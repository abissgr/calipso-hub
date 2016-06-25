package gr.abiss.calipso.controller;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import gr.abiss.calipso.test.AbstractControllerIT;

@Test(/*singleThreaded = true, */description = "Test dynamic JPA specifications used in default search stack")
@SuppressWarnings("unused")
public class SpecificationsControllerIT extends AbstractControllerIT {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpecificationsControllerIT.class);

	@Test(description = "Test simple type properties")
	public void testSimpleTypeProperty() throws Exception {
		 given().param("name", "Greece")
			.get("/calipso/api/rest/countries").
		then().assertThat()
			.body("content[0].name", equalTo("Greece"));
	}

	@Test(description = "Test related entity properties")
	public void testRelatedEntityTypeProperty() throws Exception {
		 given().
		 	param("parent", "AS").
		 get("/calipso/api/rest/countries").
		 then().
		 	assertThat().
			body("content[0].parent.id", equalTo("AS"));
	}


	@Test(description = "Test related entity IDs")
	public void testRelatedEntityId() throws Exception {
		 given().
		 	param("parent.id", "AS").
		 get("/calipso/api/rest/countries").
		 then().
		 	assertThat().
			body("content[0].parent.id", equalTo("AS"));
	}

	@Test(description = "Test path to related entities simple type property")
	public void testPathToRelatedSimpleTypeProperty() throws Exception {
		 given().
		 	param("parent.name", "Oceania").
		 get("/calipso/api/rest/countries").
		then().
			assertThat().
			body("content[0].parent.name", equalTo("Oceania"));
	}
}
