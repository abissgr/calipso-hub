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

import com.restdude.auth.userAccount.model.UserAccountRegistration;
import com.restdude.domain.users.model.User;
import gr.abiss.calipso.test.AbstractControllerIT;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@Test(/*singleThreaded = true, */description = "Test REST error responses")
@SuppressWarnings("unused")
public class RestErrorsIT extends AbstractControllerIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestErrorsIT.class);

    @Test(description = "Test duplicate usename subscription attempt")
    public void testDuplicateUsername() throws Exception {

        RequestSpecification spec = this.getRequestSpec(null);
        User user = given().spec(spec)
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
                .body("code", is(400))
                // get model
                .extract().as(User.class);

    }

}
