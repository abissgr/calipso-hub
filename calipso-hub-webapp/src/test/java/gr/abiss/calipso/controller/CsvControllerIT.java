/**
 * calipso-hub-webapp - A full stack, high level framework for lazy application hackers.
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gr.abiss.calipso.controller;

import com.fasterxml.jackson.databind.JsonNode;
import gr.abiss.calipso.test.AbstractControllerIT;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@Test(/*singleThreaded = true, */description = "Test dynamic JPA specifications used in default search stack")
@SuppressWarnings("unused")
public class CsvControllerIT extends AbstractControllerIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvControllerIT.class);

    @Test(description = "Test CSV output")
    public void testCorrectLogin() throws Exception {

        // login as admin
        Loggedincontext lctx = this.getLoggedinContext("admin", "admin");

        // get initial data batch id
        JsonNode batches = given().spec(lctx.requestSpec)
                .log().all()
                .param("status", "SENT")
                .get("/calipso/api/rest/registrationCodeBatches")
                .then().assertThat()
                .body("content[0].id", notNullValue())
                .extract().as(JsonNode.class);
        String id = batches.get("content").get(0).get("id").asText();

        // export code batch to CSV
        RequestSpecification reqSpec = this.getRequestSpec(lctx.ssoToken, "text/csv", "text/csv");
        String csv = RestAssured.given().spec(reqSpec)
                .log().all().get("/calipso/api/rest/registrationCodeBatches/" + id + "/csv").then().log().all().statusCode(200).extract().response().getBody().print();

        // verify multiple lines
        Assert.assertTrue(csv.split("\\r?\\n").length > 1);
    }

}
