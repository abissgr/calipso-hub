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

import com.fasterxml.jackson.databind.JsonNode;
import com.restdude.auth.userAccount.model.UserAccountRegistration;
import gr.abiss.calipso.test.AbstractControllerIT;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@Test(/*singleThreaded = true, */description = "Unique constraint tests")
@SuppressWarnings("unused")
public class UniqueConstraintControllerIT extends AbstractControllerIT {

	private static final Logger LOGGER = LoggerFactory.getLogger(UniqueConstraintControllerIT.class);

	
	@Test(description = "Test unique constraints handling")
	public void testUniqueConstraintsn() throws Exception {
		RequestSpecification spec = this.getRequestSpec(null);
		JsonNode json = given().spec(spec)
                .body(new UserAccountRegistration.Builder()
                        .registrationEmail("system@abiss.gr")
                        .username("system")
                        .build())
                .post("/calipso/api/auth/account")
                .then().assertThat().statusCode(400)
				// test assertions
				.body("status", notNullValue())
				// get model
				.extract().as(JsonNode.class);
		
//		LOGGER.info("Invalid registration JSON: \n{}", JacksonUtils.prettyPrint(json));
	}

	
}
