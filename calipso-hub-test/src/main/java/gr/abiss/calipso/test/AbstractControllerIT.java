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

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;
import org.testng.annotations.BeforeClass;

import com.amazonaws.util.Base64;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.test.AbstractControllerIT.Loggedincontext;
import gr.abiss.calipso.utils.ConfigurationFactory;
import gr.abiss.calipso.utils.Constants;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.factory.Jackson2ObjectMapperFactory;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

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
		StompSession ownerSession = null;
		
		WebSocketHttpHeaders ownerHandshakeHeaders = new WebSocketHttpHeaders();
		ownerHandshakeHeaders.add("Authorization", "Basic " + loginContext.ssoToken);
		try {
			ownerSession = getWebSocketStompClient().connect(url, ownerHandshakeHeaders, new DefaultStompSessionHandler() {}).get(1, SECONDS);
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

	@BeforeClass
	public void setup() {

		// parse JSON by default
		// RestAssured.defaultParser = Parser.JSON;
		// log request/response in errors
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

		// pickup from the command line if given for the jetty-maven-plugin
		String port = System.getProperty("jetty.http.port");
		RestAssured.port = port != null ? Integer.parseInt(port) : 8080;

		this.WEBSOCKET_URI = new StringBuffer("ws://localhost:")
				.append(RestAssured.port)
				.append("/calipso/ws")
				.toString();
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
						ObjectMapper objectMapper = new ObjectMapper();
						// support joda classes<->JSON
						objectMapper.registerModule(new JodaModule());
						// ignore unknown properties
						objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
				.post("/calipso/apiauth/userDetails");

		// validate login
		rs.then().assertThat().statusCode(200).content("id", notNullValue());

		// Get result cookie and user id
		lctx.ssoToken = rs.getCookie(Constants.REQUEST_AUTHENTICATION_TOKEN_COOKIE_NAME);
		lctx.userId = rs.jsonPath().getString("id");

		RequestSpecification requestSpec = getRequestSpec(lctx.ssoToken);
		lctx.requestSpec = requestSpec;

		return lctx;
	}

	protected RequestSpecification getRequestSpec(String ssoToken) {
		// extend the global spec we have already set to add the SSO token
		RequestSpecification requestSpec = new RequestSpecBuilder().setAccept(JSON_UTF8).setContentType(JSON_UTF8)
				.addCookie(Constants.REQUEST_AUTHENTICATION_TOKEN_COOKIE_NAME, ssoToken).build();
		return requestSpec;
	}

	protected User getUserByUsernameOrEmail(String userNameOrEmail) {
		return get("/calipso/api/rest/users/byUserNameOrEmail/{userNameOrEmail}", userNameOrEmail).as(User.class);
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

		private static final Logger LOGGER = LoggerFactory.getLogger(DefaultStompFrameHandler.class);

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
	        return datumClass;
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
	        LOGGER.info("handleFrame: payload: " + payload);
	        this.blockingQueue.offer((T) payload);
	    }

	}
	
	public static class DefaultStompSessionHandler implements StompSessionHandler {

		private static final Logger LOGGER = LoggerFactory.getLogger(DefaultStompSessionHandler.class);
		/**
		 * This implementation is empty.
		 */
		@Override
		public void afterConnected(StompSession session, StompHeaders connectedHeaders) {

	        LOGGER.info("afterConnected: session: " + session + ". connectedHeaders: " + connectedHeaders);
		}

		/**
		 * This implementation returns String as the expected payload type
		 * for STOMP ERROR frames.
		 */
		@Override
		public Type getPayloadType(StompHeaders headers) {
	        LOGGER.info("getPayloadType: headers: " + headers);
			return String.class;
		}

		/**
		 * This implementation is empty.
		 */
		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
	        LOGGER.info("handleFrame: headers: " + headers + ", payload: " + payload);
		}

		/**
		 * This implementation is empty.
		 */
		@Override
		public void handleException(StompSession session, StompCommand command, StompHeaders headers,
				byte[] payload, Throwable exception) {

	        LOGGER.info("handleException: session: " + session + ", command: " + command + ", headers: " + headers);
		}

		/**
		 * This implementation is empty.
		 */
		@Override
		public void handleTransportError(StompSession session, Throwable exception) {
	        LOGGER.error("Transport ERROR for session: " + session.getSessionId(), exception);
		}

	}
}
