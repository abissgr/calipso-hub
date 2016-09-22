/**
 * calipso-hub-webapp - A full stack, high level framework for lazy application hackers.
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
package gr.abiss.calipso.controller;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import gr.abiss.calipso.friends.model.Friendship;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.test.AbstractControllerIT;
import gr.abiss.calipso.userDetails.model.LoginSubmission;
import gr.abiss.calipso.utils.Constants;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Test(/*singleThreaded = true, */description = "Test dynamic JPA specifications used in default search stack")
@SuppressWarnings("unused")
public class UserControllerIT extends AbstractControllerIT {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserControllerIT.class);

	@Test(description = "Test logging in with correct credentials")
	public void testCorrectLogin() throws Exception {
		this.getLoggedinContext("admin", "admin");
	}
	
	@Test(description = "Test registration")
	public void testRegistration() throws Exception {
		RequestSpecification spec = this.getRequestSpec(null);
		User user = given().spec(spec)
				.body(new User.Builder()
					.firstName("Firstname")
					.lastName("LastName")
					.email("ittestreg@UserControllerIT.evasyst.com")
					.build())
				.post("/calipso/api/rest/users")
				.then().assertThat()
				// test assertions
				.body("id", notNullValue())
				// get model
				.extract().as(User.class);
	}

	
}
