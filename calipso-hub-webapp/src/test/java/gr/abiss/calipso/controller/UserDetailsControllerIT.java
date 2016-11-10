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

import gr.abiss.calipso.test.AbstractControllerIT;
import gr.abiss.calipso.utils.Constants;
import io.restassured.response.Response;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Test(/*singleThreaded = true, */description = "Test dynamic JPA specifications used in default search stack")
@SuppressWarnings("unused")
public class UserDetailsControllerIT extends AbstractControllerIT {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsControllerIT.class);

    @Test(description = "Test login attempt with correct credentials")
    public void testCorrectLogin() throws Exception {
		this.getLoggedinContext("admin", "admin");
    }

    @Test(description = "Test login attempt with incorrect credentials")
    public void testIncorrectLogin() throws Exception {
        LOGGER.info("testIncorrectLogin");
        Loggedincontext lctx = new Loggedincontext();
        // create a login request body
        Map<String, String> loginSubmission = new HashMap<String, String>();
        loginSubmission.put("username", "foo");
        loginSubmission.put("password", "bar^%Y%#DC");

        // attempt login and test for a proper result
        Response rs = given().accept(JSON_UTF8).contentType(JSON_UTF8).body(loginSubmission).when()
                .post("/calipso/api/auth/userDetails");

        // validate login
        rs.then().log().all().assertThat().statusCode(401).content("code", equalTo(401));

        // ensure cookie is cleared
        Assert.assertNull(rs.getCookie(Constants.REQUEST_AUTHENTICATION_TOKEN_COOKIE_NAME));
    }

}
