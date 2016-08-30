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

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import gr.abiss.calipso.test.AbstractControllerIT;
import gr.abiss.calipso.test.AbstractControllerIT.Loggedincontext;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@Test(/*singleThreaded = true, */description = "Test dynamic JPA specifications used in default search stack")
@SuppressWarnings("unused")
public class SpecificationsControllerIT extends AbstractControllerIT {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpecificationsControllerIT.class);

	Loggedincontext adminLoginContext;
	RequestSpecification adminRequestSpec;
    @BeforeClass
	public void setup() {
    	super.setup();
		// parse JSON by default
		// RestAssured.defaultParser = Parser.JSON;

		adminLoginContext = this.getLoggedinContext("admin", "admin");
		adminRequestSpec = adminLoginContext.requestSpec;
    }

	@Test(description = "Test simple type properties")
	public void testSimpleTypeProperty() throws Exception {
		 given().spec(adminRequestSpec).param("name", "Greece")
			.get("/calipso/api/rest/countries").
		then().assertThat()
			.body("content[0].name", equalTo("Greece"));
	}

	@Test(description = "Test related entity properties")
	public void testRelatedEntityTypeProperty() throws Exception {
		 given().spec(adminRequestSpec).
		 	param("parent", "AS").
		 get("/calipso/api/rest/countries").
		 then().
		 	assertThat().
			body("content[0].parent.id", equalTo("AS"));
	}


	@Test(description = "Test related entity IDs")
	public void testRelatedEntityId() throws Exception {
		 given().spec(adminRequestSpec).
		 	param("parent.id", "AS").
		 get("/calipso/api/rest/countries").
		 then().
		 	assertThat().
			body("content[0].parent.id", equalTo("AS"));
	}

	@Test(description = "Test path to related entities simple type property")
	public void testPathToRelatedSimpleTypeProperty() throws Exception {
		 given().spec(adminRequestSpec).
		 	param("parent.name", "Oceania").
		 get("/calipso/api/rest/countries").
		then().
			assertThat().
			body("content[0].parent.name", equalTo("Oceania"));
	}
}
