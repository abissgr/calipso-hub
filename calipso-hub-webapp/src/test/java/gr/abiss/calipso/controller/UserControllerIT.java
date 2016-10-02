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

import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.notNullValue;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.test.AbstractControllerIT;
import gr.abiss.calipso.utils.Constants;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.specification.RequestSpecification;

@Test(/*singleThreaded = true, */description = "User entity tests")
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
	
	@Test(priority = 10, description = "Test C2 use cases")
	public void testUploadsPreserveOtherProperties() throws Exception {

		// --------------------------------
		// Login
		// --------------------------------

		// login and get a request spec for stateless auth
		// essentially this posts to "/calipso/apiauth/userDetails"
		// with request body: {username: "admin", password: "admin"}
		Loggedincontext adminLoginContext = this.getLoggedinContext("admin", "admin");
		RequestSpecification adminRequestSpec = adminLoginContext.requestSpec;
		
		// --------------------------------
		// Upload user images
		// --------------------------------
		String file1 = "user_banner.png";
		String file2 = "user_avatar.jpg";
        final byte[] bytes1 = IOUtils.toByteArray(getClass().getResourceAsStream("/"+file1)); 
        final byte[] bytes2 = IOUtils.toByteArray(getClass().getResourceAsStream("/"+file2)); 
        
        // select user 	
        User user = given().spec(adminRequestSpec)
			.get("/calipso/api/rest/users/" + adminLoginContext.userId)
			.then().assertThat()
			// test assertions
			.body("id", notNullValue())
			// get model
			.extract().as(User.class);
        LOGGER.info("User before uploading files: {}", user);
        		
        		
        User userAfterUploading = given()
			.contentType("multipart/form-data")
			.cookies(Constants.REQUEST_AUTHENTICATION_TOKEN_COOKIE_NAME, adminLoginContext.ssoToken)
	        .multiPart(new MultiPartSpecBuilder(bytes1)
	                .fileName(file1)
	                .controlName("bannerUrl")
	                .mimeType("image/png").build())
	        .multiPart(new MultiPartSpecBuilder(bytes2)
	                .fileName(file1)
	                .controlName("avatarUrl")
	                .mimeType("image/jpeg").build())
			.when().post("/calipso/api/rest/users/" + user.getId() + "/files")
			.then().extract().as(User.class);
        

        LOGGER.info("User after uploading files: {}", userAfterUploading);

	    Assert.assertEquals(user, userAfterUploading);
	}


	
}
