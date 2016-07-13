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

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

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

	protected static final String JSON_UTF8 = "application/json; charset=UTF-8";

	@BeforeClass
	public static void setup() {

		// configure our object mapper
		RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
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

		RestAssured.requestSpecification = new RequestSpecBuilder()
			.setAccept(JSON_UTF8)
			.setContentType(JSON_UTF8)
			.build();
		
//		String port = System.getProperty("server.port");
//		if (port == null) {
//			RestAssured.port = Integer.valueOf(8080);
//		} else {
//			RestAssured.port = Integer.valueOf(port);
//		}
//
//		String basePath = System.getProperty("server.base");
//		if (basePath == null) {
//			basePath = "/rest-garage-sample/";
//		}
//		RestAssured.basePath = basePath;
//
//		String baseHost = System.getProperty("server.host");
//		if (baseHost == null) {
//			baseHost = "http://localhost";
//		}
//		RestAssured.baseURI = baseHost;

	}

	/** 
	 * Login using the given credentials and return the Single Sign-On token
	 * @param username
	 * @param password
	 * @return
	 */
	protected Loggedincontext getLoggedinContext(String username, String password) {
		Loggedincontext lctx = new Loggedincontext();
		// create a login request body
		Map<String, String>  loginSubmission = new HashMap<String, String>();
		loginSubmission.put("username", username);
		loginSubmission.put("password", password);
		
		// attempt login and test for a proper result
		Response rs = given().//log().all().
		 	body(loginSubmission).
		 when().
			post("/calipso/apiauth/userDetails");
		
		// validate login
		rs.then().//log().all().
			assertThat().
				statusCode(200).
				content("id", notNullValue());
		
		// Get result cookie and user id
		lctx.ssoToken = rs.getCookie(Constants.REQUEST_AUTHENTICATION_TOKEN_COOKIE_NAME);
		lctx.userId = rs.jsonPath().getString("id");
		
		// extend the global spec we have already set to add the SSO token
		RequestSpecification requestSpec = new RequestSpecBuilder()
			.addRequestSpecification(RestAssured.requestSpecification)
			.addCookie(Constants.REQUEST_AUTHENTICATION_TOKEN_COOKIE_NAME, lctx.ssoToken)
			.build();
		lctx.requestSpec = requestSpec;
		
		return lctx;
	}

	public static class Loggedincontext{
		public String userId;
		public String ssoToken;
		public RequestSpecification requestSpec;
	}
}
