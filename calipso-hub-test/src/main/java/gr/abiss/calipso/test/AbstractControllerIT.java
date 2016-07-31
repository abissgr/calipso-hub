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

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.utils.ConfigurationFactory;
import gr.abiss.calipso.utils.Constants;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.factory.Jackson2ObjectMapperFactory;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Base class for rest-assured based controller integration testing
 */
@SuppressWarnings("unused")
public class AbstractControllerIT {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractControllerIT.class);

	protected static final String JSON_UTF8 = "application/json; charset=UTF-8";

	@BeforeClass
	public void setup() {

		// parse JSON by default
//		RestAssured.defaultParser = Parser.JSON;
		// log request/response in errors
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

		// pickup from the command line if given for the jetty-maven-plugin
		String port = System.getProperty("jetty.http.port");
		RestAssured.port = port != null ? Integer.parseInt(port) : 8080;

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

		// Only initialize the RequestSpecification AFTER configuring the static
		// configuration
		// see
		// https://github.com/rest-assured/rest-assured/issues/370#issuecomment-123192038
//		RestAssured.requestSpecification = new RequestSpecBuilder()
				// .setPort(RestAssured.port)
//				.build();
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
		Response rs = given()
				.accept(JSON_UTF8)
				.contentType(JSON_UTF8)
				.body(loginSubmission).when().post("/calipso/apiauth/userDetails");

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
		RequestSpecification requestSpec = new RequestSpecBuilder()
				.setAccept(JSON_UTF8).setContentType(JSON_UTF8)
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
}
