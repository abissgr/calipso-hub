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
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import org.testng.annotations.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JacksonUtils;

import gr.abiss.calipso.friends.model.Friendship;
import gr.abiss.calipso.friends.model.FriendshipId;
import gr.abiss.calipso.friends.model.FriendshipStatus;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.dto.FriendshipDTO;
import gr.abiss.calipso.model.dto.UserDTO;
import gr.abiss.calipso.model.dto.UserInvitationResultsDTO;
import gr.abiss.calipso.model.dto.UserInvitationsDTO;
import gr.abiss.calipso.test.AbstractControllerIT;
import gr.abiss.calipso.test.AbstractControllerIT.DefaultStompFrameHandler;
import gr.abiss.calipso.test.AbstractControllerIT.Loggedincontext;
import gr.abiss.calipso.userDetails.model.LoginSubmission;
import gr.abiss.calipso.utils.Constants;
import gr.abiss.calipso.websocket.Destinations;
import gr.abiss.calipso.websocket.client.DefaultStompSessionHandler;
import gr.abiss.calipso.websocket.message.StateUpdateMessage;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Test(/*singleThreaded = true, */description = "Test dynamic JPA specifications used in default search stack")
@SuppressWarnings("unused")
public class FriendsControllerIT extends AbstractControllerIT {

	private static final Logger LOGGER = LoggerFactory.getLogger(FriendsControllerIT.class);

	@Test(description = "Test logging in with correct credentials")
	public void testFindMy() throws Exception {

		// --------------------------------
		// Login
		// --------------------------------

		// login and get a request spec for stateless auth
		// essentially this posts to "/calipso/apiauth/userDetails"
		// with request body: {username: "admin", password: "admin"}
		Loggedincontext adminLoginContext = this.getLoggedinContext("admin", "admin");
		RequestSpecification adminRequestSpec = adminLoginContext.requestSpec;
		
		Loggedincontext operatorLoginContext = this.getLoggedinContext("operator", "operator");
		RequestSpecification operatorRequestSpec = operatorLoginContext.requestSpec;
		

		// --------------------------------
		// subscribe to friendship websocket updates
		// --------------------------------
		// 
		StompSession adminSession = getStompSession(WEBSOCKET_URI, adminLoginContext);
		StompSession operatorSession = getStompSession(WEBSOCKET_URI, operatorLoginContext);
		// subsscribe to updates
		BlockingQueue<FriendshipDTO> adminFriendshipsQueueBlockingQueue = new LinkedBlockingDeque<FriendshipDTO>();
		adminSession.subscribe("/user" + Destinations.USERQUEUE_FRIENDSHIPS, 
			new DefaultStompFrameHandler<FriendshipDTO>(adminSession, FriendshipDTO.class, adminFriendshipsQueueBlockingQueue));
		
		// subsscribe to generic state updates and verify user updateof stomsessionCount
		BlockingQueue<JsonNode> adminStateUpdatesQueueBlockingQueue = new LinkedBlockingDeque<JsonNode>();
		adminSession.subscribe("/user" + Destinations.USERQUEUE_UPDATES_STATE, 
			new DefaultStompFrameHandler<JsonNode>(adminSession, JsonNode.class, adminStateUpdatesQueueBlockingQueue));
				
		BlockingQueue<FriendshipDTO> operatorFriendshipsQueueBlockingQueue = new LinkedBlockingDeque<FriendshipDTO>();
		Subscription operatorFriendshipQueueSubscription = operatorSession.subscribe("/user/queue/friendships", 
				new DefaultStompFrameHandler<FriendshipDTO>(operatorSession, FriendshipDTO.class, operatorFriendshipsQueueBlockingQueue));

		// --------------------------------
		// Create a friendship request
		// --------------------------------
		LOGGER.info("Create a friendship request");
		Friendship friendship = given().spec(adminRequestSpec)
			.log().all()
			.body(new Friendship(new User(adminLoginContext.userId), new User(operatorLoginContext.userId)))
			.post("/calipso/api/rest/" + Friendship.API_PATH)
			.then().assertThat()
			// test assertions
			.body("status", notNullValue())
			.log().all()
			// get model
			.extract().as(Friendship.class);

		LOGGER.info("validate result");
		validateFriendship(friendship, adminLoginContext.userId, operatorLoginContext.userId, FriendshipStatus.SENT);

		LOGGER.info("Validate admin's outbox (SENT)");
	    JsonNode friendshipsNode =  given().spec(adminRequestSpec)
				.log().all()
				.param("status", "SENT")
				.get("/calipso/api/rest/friends/my")
				.then().assertThat()
				.body("content[0].id", notNullValue())
				// test assertions
//				.log().all()
				// get model
				.extract().as(JsonNode.class);
		LOGGER.debug("Outbox: \n{}", JacksonUtils.prettyPrint(friendshipsNode));
	    Assert.assertEquals(operatorLoginContext.userId, friendshipsNode.get("content").get(0).get("id").asText());
	    
	    LOGGER.info("Validate inbox (PENDING)");
	    friendshipsNode =  given().spec(operatorRequestSpec)
				.log().all()
				.param("status", "PENDING")
				.get("/calipso/api/rest/friends/my")
				.then().assertThat()
				.body("content[0].id", notNullValue())
				// test assertions
//				.log().all()
				// get model
				.extract().as(JsonNode.class);
	    Assert.assertEquals(adminLoginContext.userId, friendshipsNode.get("content").get(0).get("id").asText());
	    
	    
	    // validate oprator/inverse result
	    FriendshipDTO ioperatorFriendRequestNotification = operatorFriendshipsQueueBlockingQueue.poll(5, SECONDS);
		validateFriendship(ioperatorFriendRequestNotification, operatorLoginContext.userId, adminLoginContext.userId, FriendshipStatus.PENDING);

		// test operator user queue
		LOGGER.info("Accept request");
		// accept request by sending only the new status to the right URL
		Friendship friendshipInverse = given().spec(operatorRequestSpec).log().all()
			.body(new Friendship(FriendshipStatus.CONFIRMED))
			.put("/calipso/api/rest/" + Friendship.API_PATH + "/" + ioperatorFriendRequestNotification.getId())
			// get model
			.then().log().all().extract().as(Friendship.class);
		
		// validate result
		validateFriendship(friendshipInverse, operatorLoginContext.userId, adminLoginContext.userId, FriendshipStatus.CONFIRMED);

	    // validate admin/inverse result
	    FriendshipDTO adminFriendRequestNotification = adminFriendshipsQueueBlockingQueue.poll(5, SECONDS);
		validateFriendship(adminFriendRequestNotification, adminLoginContext.userId, operatorLoginContext.userId, FriendshipStatus.CONFIRMED);

		LOGGER.info("Get friends");
		// get friends
		given().spec(adminRequestSpec)
			.get("/calipso/api/rest/friends/my");

		// --------------------------------
		// Create bulk friendship requests (invitations
		// --------------------------------
		UserInvitationsDTO invitations = new UserInvitationsDTO.Builder()
				.addressLines("manos, info@abiss.gr\nabc@xyz.com, asd@dsa.com \nqwe@rty.com,yui@gui.com,jih@app.com,abc@xyz.com,asd@dsa.com")
				.recepient(new UserDTO.Builder().email("test@pick.com").build()).build();
		
		UserInvitationResultsDTO userInvitationResults = given().spec(adminRequestSpec)
				.body(invitations)
				.post("/calipso/api/rest/invitations")
				.then()
				//.assertThat()
				// test assertions
				//.body("id", notNullValue())
				// get model
				.extract().as(UserInvitationResultsDTO.class);
		

		// disconnect
		LOGGER.info("DISCONNECT OPERATOR");
		operatorSession.disconnect();

		JsonNode disconnectUpdate = adminStateUpdatesQueueBlockingQueue.poll(10, SECONDS);

		LOGGER.info("disconnectUpdate MESSAGE:\n{}", JacksonUtils.prettyPrint(disconnectUpdate));
		// TODO
	    Assert.assertNotNull(disconnectUpdate);

		StompSession operatorSession2 = getStompSession(WEBSOCKET_URI, operatorLoginContext);

		JsonNode reconnectUpdate = adminStateUpdatesQueueBlockingQueue.poll(10, SECONDS);

		LOGGER.info("reconnectUpdate MESSAGE: \n{}", JacksonUtils.prettyPrint(reconnectUpdate));
		// TODO
	    Assert.assertNotNull(reconnectUpdate);
	}

	@Test(description = "Test logging in with correct credentials")
	public void testFindUserFriends() throws Exception {

		// Chapperwocky login
		Loggedincontext chapperwockyLoginContext = this.getLoggedinContext("Chapperwocky", "Chapperwocky");
		RequestSpecification chapperwockyRequestSpec = chapperwockyLoginContext.requestSpec;

		// get No-Tec's user id
		Loggedincontext noTecLoginContext = this.getLoggedinContext("No-Tec", "No-Tec");
		
		// find No-Tec's friends
		LOGGER.info("Find No-Tec's friends");
	    JsonNode friendshipsNode =  given().spec(chapperwockyRequestSpec)
				.log().all()
				.get("/calipso/api/rest/friends/" + noTecLoginContext.userId)
				.then()
				.assertThat().statusCode(200)
				.body("content[0].id", notNullValue())
				// test assertions
				.log().all()
				// get model
				.extract().as(JsonNode.class);
	}
	
	protected void validateFriendship(Friendship friendship, String one,  String other, FriendshipStatus status) {
		this.validateFriendship(new FriendshipDTO(friendship), one, other, status);
	}
	protected void validateFriendship(FriendshipDTO friendship, String one,  String other, FriendshipStatus status) {
		Assert.assertEquals(one, friendship.getOwner().getId());
	    Assert.assertEquals(other, friendship.getFriend().getId());
	    Assert.assertEquals(status, friendship.getStatus());
	}
	
	public static class HeartBeatStompSessionHandler extends DefaultStompSessionHandler{

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			// TODO Auto-generated method stub
			super.handleFrame(headers, payload);
		}
		
	}
	

	
}
