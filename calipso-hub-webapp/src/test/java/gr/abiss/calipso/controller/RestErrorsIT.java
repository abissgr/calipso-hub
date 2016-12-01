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
import com.restdude.auth.userAccount.model.UserAccountRegistration;
import com.restdude.domain.error.model.ClientError;
import com.restdude.domain.error.model.ErrorLog;
import com.restdude.domain.error.model.SystemError;
import com.restdude.domain.friends.model.Friendship;
import com.restdude.domain.users.model.User;
import com.restdude.util.Constants;
import gr.abiss.calipso.test.AbstractControllerIT;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Test(/*singleThreaded = true, */description = "Test REST error responses")
@SuppressWarnings("unused")
public class RestErrorsIT extends AbstractControllerIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestErrorsIT.class);

    @Test(description = "Test duplicate username subscription attempt", priority = 10)
    public void testDuplicateUsername() throws Exception {

        RequestSpecification spec = this.getRequestSpec(null);
        SystemError error = given().spec(spec)
                .log().all()
                .body(new UserAccountRegistration.Builder()
                        .username("admin")
                        .firstName("Firstname")
                        .lastName("LastName")
                        .registrationEmail("testDuplicateUsername@" + this.getClass().getSimpleName() + ".com")
                        .build())
                .post("/calipso/api/auth/account")
                .then()
                .log().all()
                .assertThat()
                // test assertions
                .statusCode(400)
                .body("httpStatusCode", is(400))
                // get model
                .extract().as(SystemError.class);

    }

    @Test(description = "Test duplicate email subscription attempt", priority = 20)
    public void testDuplicateEmail() throws Exception {

        RequestSpecification spec = this.getRequestSpec(null);
        SystemError error = given().spec(spec)
                .log().all()
                .body(new UserAccountRegistration.Builder()
                        .registrationEmail("operator@abiss.gr")
                        .build())
                .post("/calipso/api/auth/account")
                .then()
                .log().all()
                .assertThat()
                // test assertions
                .statusCode(400)
                .body("httpStatusCode", is(400))
                // get model
                .extract().as(SystemError.class);

    }

    @Test(description = "Test invalid credentials", priority = 30)
    public void testInvalidCredentials() throws Exception {

        Loggedincontext lctx = new Loggedincontext();
        // create a login request body
        Map<String, String> loginSubmission = new HashMap<String, String>();
        loginSubmission.put("username", "admin");
        loginSubmission.put("password", "invalid");

        // attempt login and test for a proper result
        Response rs = given().accept(JSON_UTF8).contentType(JSON_UTF8).body(loginSubmission).when()
                .post("/calipso/api/auth/userDetails");

        // validate login
        rs.then().log().all().assertThat()
                .statusCode(401)
                .body("httpStatusCode", is(401));

        Assert.assertFalse(StringUtils.isNotBlank(rs.getCookie(Constants.REQUEST_AUTHENTICATION_TOKEN_COOKIE_NAME)));

    }

    @Test(description = "Test not remembered", priority = 40)
    public void testNotRemembered() throws Exception {
        LOGGER.info("testNotRemembered");
        Response rs = given().spec(getRequestSpec("invalid")).log().all()
                .get("/calipso/api/auth/userDetails");
        rs.then().log().all().assertThat()
                // test assertions
                .statusCode(401)
                .body("httpStatusCode", is(401));

        Assert.assertFalse(StringUtils.isNotBlank(rs.getCookie(Constants.REQUEST_AUTHENTICATION_TOKEN_COOKIE_NAME)));

    }

    @Test(description = "Test not found", priority = 50)
    public void testNotFound() throws Exception {

        Loggedincontext adminLoginContext = this.getLoggedinContext("admin", "admin");
        RequestSpecification adminRequestSpec = adminLoginContext.requestSpec;
        // select user
        SystemError error = given().spec(adminRequestSpec)
                .get("/calipso/api/rest/users/invalid")
                .then().assertThat()
                // test assertions
                .statusCode(404)
                .body("httpStatusCode", is(404))
                .extract().as(SystemError.class);
    }

    @Test(description = "Test invalid target ", priority = 60)
    public void testInvalidTarget() throws Exception {

        Loggedincontext adminLoginContext = this.getLoggedinContext("admin", "admin");
        RequestSpecification adminRequestSpec = adminLoginContext.requestSpec;
        // select user
        SystemError error = given().spec(adminRequestSpec)
                .log().all()
                .body(new Friendship(new User(adminLoginContext.userId), new User("3c1cd4dc-05fb-49ef-b929-f08d0f0b7c73")))
                .post("/calipso/api/rest/" + Friendship.API_PATH)
                .then().assertThat()
                // test assertions
                .statusCode(500)
                .body("httpStatusCode", is(500))
                .extract().as(SystemError.class);
    }

    @Test(description = "Test invalid target ", priority = 70)
    public void testSearchSystemErrors() throws Exception {

        Loggedincontext adminLoginContext = this.getLoggedinContext("admin", "admin");
        RequestSpecification adminRequestSpec = adminLoginContext.requestSpec;
        // select user
        JsonNode errorsPage = given().spec(adminRequestSpec)
                .log().all()
                .get("/calipso/api/rest/" + SystemError.API_PATH)
                .then().log().all().assertThat()
                // test assertions
                .statusCode(200)
                .body("content[0].message", notNullValue())
                .body("content[1].message", notNullValue())
                .extract().as(JsonNode.class);
    }

    @Test(description = "Test client error submission", priority = 80)
    public void testClientErrorSubmission() throws Exception {

        Loggedincontext adminLoginContext = this.getLoggedinContext("admin", "admin");
        RequestSpecification adminRequestSpec = adminLoginContext.requestSpec;

        for (int i = 0; i < 11; i++) {

            ClientError error = new ClientError();
            ErrorLog log = new ErrorLog();
            log.setRootCauseMessage("Section 1.10.32 of \"de Finibus Bonorum et Malorum\", written by Cicero in 45 BC");
            log.setStacktrace("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur");

            error.setMessage("Client Error #" + i + "!");
            error.setDescription("Created by " + this.getClass().getName());
            error.setErrorLog(log);

            error = given().spec(adminRequestSpec)
                    .log().all()
                    .body(error)
                    .post("/calipso/api/rest/" + ClientError.API_PATH)
                    .then().log().all().assertThat()
                    // test assertions
                    .statusCode(201)
                    .body("message", notNullValue())
                    .extract().as(ClientError.class);
        }
    }
}
