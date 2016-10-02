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
			.body(new Friendship.Builder()
				.requestSender(new User(adminLoginContext.userId))
				.requestRecipient(new User(operatorLoginContext.userId))
				.build())
			.post("/calipso/api/rest/" + Friendship.API_PATH)
			.then().assertThat()
			// test assertions
			.body("id", notNullValue())
			// get model
			.extract().as(Friendship.class);
		
		// test operator user queue
	    Assert.assertEquals(FriendshipStatus.PENDING, operatorFriendshipsQueueBlockingQueue.poll(5, SECONDS).getStatus());

		LOGGER.info("Accept request");
		// accept request
		friendship.setStatus(FriendshipStatus.ACCEPTED);
		friendship = given().spec(operatorRequestSpec)
			.body(friendship)
			.put("/calipso/api/rest/" + Friendship.API_PATH + "/" + friendship.getId())
			// get model
			.then().extract().as(Friendship.class);
		
		// test admin user queue
	    Assert.assertEquals(FriendshipStatus.ACCEPTED, adminFriendshipsQueueBlockingQueue.poll(5, SECONDS).getStatus());

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
	
	public static class HeartBeatStompSessionHandler extends DefaultStompSessionHandler{

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			// TODO Auto-generated method stub
			super.handleFrame(headers, payload);
		}
		
	}
	

	
}
