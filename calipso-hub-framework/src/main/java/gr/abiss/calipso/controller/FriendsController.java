/**
 * calipso-hub-framework - A full stack, high level framework for lazy application hackers.
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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gr.abiss.calipso.model.dto.UserDTO;
import gr.abiss.calipso.model.dto.UserInvitationResultsDTO;
import gr.abiss.calipso.model.dto.UserInvitationsDTO;
import gr.abiss.calipso.service.FriendshipService;
import gr.abiss.calipso.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@Api(tags = "Friends", description = "Friend searches")
@RequestMapping(value = "/api/rest/friends", produces = { "application/json", "application/xml" })
public class FriendsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FriendsController.class);

	@Inject
	@Qualifier(FriendshipService.BEAN_ID)
	FriendshipService friendshipService;
	
	@Inject
	@Qualifier("userService")
	UserService userService;


	@RequestMapping(value = "my", method = RequestMethod.GET, params = "page=no")
	@ResponseBody
	@ApiOperation(value = "Find all friends", notes = "Find all friends of the current user")
	public Iterable<UserDTO> findAllMyFriends() {
		return this.friendshipService.findAllMyFriends();
	}

	@RequestMapping(value = "my", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Find all friends (paginated)", notes = "Find all friends of the current user. Returns paginated results")
	public Page<UserDTO> findAllMyFriendsPaginated(
			@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
			@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
			@RequestParam(value = "properties", required = false, defaultValue = "id") String sort,
			@RequestParam(value = "direction", required = false, defaultValue = "ASC") String direction) {

		return this.friendshipService.findAllMyFriendsPaginated(this.buildPageable(page, size, sort, direction));
	}
	
	protected Pageable buildPageable(Integer page, Integer size, String sort,
			String direction) {
		Assert.isTrue(page >= 0, "Page index must be greater than, or equal to, 0");

		List<Order> orders = null;
		Sort pageableSort = null;
		if(sort != null && direction != null){
			Order order = new Order(
					direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC
							: Sort.Direction.DESC, sort);
			orders = new ArrayList<Order>(1);
			orders.add(order);
			pageableSort = new Sort(orders);
		}

		return new PageRequest(page, size, pageableSort);
	}
	

	@RequestMapping(value = "invites", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "Invite users", notes = "Invite users by email")
	public UserInvitationResultsDTO inviteUsers(@RequestBody UserInvitationsDTO invitations) {
		LOGGER.debug("INVITE USERS: " + invitations);
		return this.userService.inviteUsers(invitations);
	}
}
