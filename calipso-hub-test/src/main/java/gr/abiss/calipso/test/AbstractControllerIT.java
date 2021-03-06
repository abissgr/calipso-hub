/**
 * calipso-hub-test - A full stack, high level framework for lazy application hackers.
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gr.abiss.calipso.test;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.restdude.domain.misc.model.Host;
import com.restdude.domain.users.model.User;
import com.restdude.util.ConfigurationFactory;
import com.restdude.util.Constants;
import com.restdude.websocket.Destinations;
import com.restdude.websocket.client.DefaultStompSessionHandler;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.factory.Jackson2ObjectMapperFactory;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.testng.annotations.BeforeClass;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Base class for rest-assured based controller integration testing
 */
@SuppressWarnings("unused")
public class AbstractControllerIT {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractControllerIT.class);

	protected static final String JSON_UTF8 = "application/json; charset=UTF-8";

	protected static final Configuration CONFIG = ConfigurationFactory.getConfiguration();

	protected String WEBSOCKET_URI;
	
	protected static Configuration getConfig() {
		return CONFIG;
	}
	
	protected StompSession getStompSession(String url, Loggedincontext loginContext) {
		return getStompSession(url, loginContext, new DefaultStompSessionHandler());
	}

	protected StompSession getStompSession(String url, Loggedincontext loginContext, StompSessionHandler sessionHandler) {
		return getStompSession(url, loginContext, sessionHandler, null, null);
		
	}

	protected StompSession getStompSession(String url, Loggedincontext loginContext, StompSessionHandler sessionHandler, 
		WebSocketHttpHeaders handshakeHeaders, StompHeaders connectHeaders) {
		LOGGER.debug("Creating STOMP session for user {}:{}", loginContext.userId, loginContext.ssoToken);
		if(sessionHandler == null){
			sessionHandler = new DefaultStompSessionHandler();
		}
		StompSession ownerSession = null;
		
		// add auth
		if(handshakeHeaders == null){
			handshakeHeaders = new WebSocketHttpHeaders();
		}
		handshakeHeaders.add("Authorization", "Basic " + loginContext.ssoToken);
		
		
		try {
			ownerSession = getWebSocketStompClient().connect(url, handshakeHeaders, connectHeaders, sessionHandler).get(5, SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new RuntimeException(e);
		}
		
		return ownerSession;
	}

	
	protected WebSocketStompClient getWebSocketStompClient() {
    	// setup websocket
		StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
		WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);
         // support JSON messages
         stompClient.setMessageConverter(new MappingJackson2MessageConverter());
         return stompClient;
    }

	protected BlockingQueue<JsonNode> getActivityQueue(StompSession stompSession) {
		BlockingQueue<JsonNode> queue = new LinkedBlockingDeque<JsonNode>();
		StompSession.Subscription subscription = stompSession.subscribe("/user" + Destinations.USERQUEUE_UPDATES_ACTIVITY,
				new DefaultStompFrameHandler<JsonNode>(stompSession, JsonNode.class, queue));
		return queue;
	}

	protected BlockingQueue<JsonNode> getStatusChangeQueue(StompSession stompSession) {
		BlockingQueue<JsonNode> queue = new LinkedBlockingDeque<JsonNode>();
		StompSession.Subscription subscription = stompSession.subscribe("/user" + Destinations.USERQUEUE_UPDATES_STATE,
				new DefaultStompFrameHandler<JsonNode>(stompSession, JsonNode.class, queue));
		return queue;
	}

	@BeforeClass
	public void setup() {

		// log request/response in errors
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

		// pickup from the jetty port
		RestAssured.port = CONFIG.getInt("jetty.http.port", 8080);
		this.WEBSOCKET_URI = new StringBuffer("ws://localhost:")
				.append(RestAssured.port)
				.append("/calipso/ws")
				.toString();
		LOGGER.info("Using websocket URL {}", this.WEBSOCKET_URI );
		// TODO:
		// String basePath = System.getProperty("server.base");
		// if (basePath == null) {
		// basePath = "/rest-garage-sample/";
		// }
		// RestAssured.basePath = basePath;
		//
		// String baseHost = System.getProperty("server.host");
		// if (baseHost == null) {
		// baseHost = "http://localhost";
		// }
		// RestAssured.baseURI = baseHost;

		// configure our object mapper
		RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
				// config object mapper
				new ObjectMapperConfig().jackson2ObjectMapperFactory(new Jackson2ObjectMapperFactory() {
					@Override
					public ObjectMapper create(Class aClass, String s) {
						ObjectMapper objectMapper = new ObjectMapper()
								.registerModule(new ParameterNamesModule())
								.registerModule(new Jdk8Module())
								.registerModule(new JavaTimeModule());

						// Disable features
						objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
						objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
						objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
						objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

						// enable features
						objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);

						return objectMapper;
					}
				}));

	}


	/**
	 * Login using the given credentials and return the Single Sign-On token
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	protected Loggedincontext getLoggedinContext(String username, String password) {
		Loggedincontext lctx = new Loggedincontext();
		// create a login request body
		Map<String, String> loginSubmission = new HashMap<String, String>();
		loginSubmission.put("username", username);
		loginSubmission.put("password", password);

		// attempt login and test for a proper result
		Response rs = given().accept(JSON_UTF8).contentType(JSON_UTF8).body(loginSubmission).when()
				.post("/calipso/api/auth/userDetails");

		// validate login
		rs.then().log().all().assertThat().statusCode(200).content("id", notNullValue());

		// Get result cookie and user id
		lctx.ssoToken = rs.getCookie(Constants.REQUEST_AUTHENTICATION_TOKEN_COOKIE_NAME);
		lctx.userId = rs.jsonPath().getString("id");

		RequestSpecification requestSpec = getRequestSpec(lctx.ssoToken);
		lctx.requestSpec = requestSpec;

		return lctx;
	}

	protected RequestSpecification getRequestSpec(String ssoToken) {
        return this.getRequestSpec(ssoToken, JSON_UTF8, JSON_UTF8);
    }

    protected RequestSpecification getRequestSpec(String ssoToken, String accept, String contentType) {
        // extend the global spec we have already set to add the SSO token
        RequestSpecification requestSpec;
        RequestSpecBuilder b = new RequestSpecBuilder().setAccept(accept).setContentType(contentType);
        if(ssoToken != null){
			b.addCookie(Constants.REQUEST_AUTHENTICATION_TOKEN_COOKIE_NAME, ssoToken);
		}
		requestSpec = b.build();
		return requestSpec;
	}

	protected User getUserByUsernameOrEmail(String userNameOrEmail) {
		return get("/calipso/api/rest/users/byUserNameOrEmail/{userNameOrEmail}", userNameOrEmail).as(User.class);
	}
	
	protected Host getRandomHost(RequestSpecification someRequestSpec) {
		// obtain a random C2 id
		String id = given().spec(someRequestSpec)
				.get("/calipso/api/rest/hosts").then()
				.assertThat().body("content[0].id", notNullValue()).extract().path("content[0].id");
		// use the public C2
		Host host = new Host();
		host.setId(id);
		return host;
	}

	public static class Loggedincontext {
		public String userId;
		public String ssoToken;
		public RequestSpecification requestSpec;
	}

	/**
	 * Handle stream connection info updates by adding them to a local queue storage
	 */
	public static class DefaultInitialDataStompFrameHandler<T> implements StompFrameHandler {

	    private Class datumClass;
	    private StompSession session;
	    public List<T>  initialData;
	    
	    private DefaultInitialDataStompFrameHandler() {
	    }
	    
	    public DefaultInitialDataStompFrameHandler(StompSession session, Class datumClass) {
	        this.session = session;
	        this.datumClass = datumClass;
	    }

	    @Override
	    public Type getPayloadType(StompHeaders headers) {
	        try {
				return this.getClass().getField("initialData").getType();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
	    }

	    @SuppressWarnings("unchecked")
		@Override
	    public void handleFrame(StompHeaders headers, Object payload) {
	        LOGGER.info("handleFrame: payload: " + payload);
	        this.initialData = (List<T>) payload;
	    }

	}
	/**
	 * Handle stream connection info updates by adding them to a local queue storage
	 */
	public static class DefaultStompFrameHandler<T> implements StompFrameHandler {

		private static final Logger LOGGER = LoggerFactory.getLogger(DefaultStompFrameHandler.class);

	    private Class messageClazz;
	    private StompSession session;
	    private BlockingQueue<T> blockingQueue;
	    
	    private DefaultStompFrameHandler() {
	    }
	    
	    public DefaultStompFrameHandler(StompSession session, Class messageClazz, BlockingQueue<T> blockingQueue) {
	        this.session = session;
	        this.blockingQueue = blockingQueue;
	        this.messageClazz = messageClazz;
	    }

	    @Override
	    public Type getPayloadType(StompHeaders headers) {
	        return this.messageClazz;
	    }

	    @SuppressWarnings("unchecked")
		@Override
	    public void handleFrame(StompHeaders headers, Object payload) {
	        LOGGER.info("handleFrame, headers: {}, payload: {}", headers, payload);
	        this.blockingQueue.offer((T) payload);
	    }

	}
}
