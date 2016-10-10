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
package gr.abiss.calipso.friends.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gr.abiss.calipso.friends.model.Friendship;
import gr.abiss.calipso.friends.model.FriendshipId;
import gr.abiss.calipso.friends.model.FriendshipStatus;
import gr.abiss.calipso.friends.service.FriendshipService;
import gr.abiss.calipso.model.dto.UserDTO;
import gr.abiss.calipso.model.dto.UserInvitationResultsDTO;
import gr.abiss.calipso.model.dto.UserInvitationsDTO;
import gr.abiss.calipso.service.UserService;
import gr.abiss.calipso.tiers.controller.BuildPageable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(tags = "Friends", description = "Friend searches")
@RequestMapping(value = "/api/rest/friends", produces = { "application/json", "application/xml" })
public class FriendsController implements BuildPageable{

	private static final Logger LOGGER = LoggerFactory.getLogger(FriendsController.class);

	@Autowired
	protected HttpServletRequest request;
	
	@Inject
	@Qualifier(FriendshipService.BEAN_ID)
	FriendshipService friendshipService;

	@Inject
	@Qualifier("userService")
	UserService userService;


	@RequestMapping(value = { "", "my" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Find all friends (paginated)", notes = "Find all friends of the current user. Returns paginated results")
	public Page<UserDTO> findMyFriendsPaginated(
			@ApiParam(name = "status", required = false, allowableValues = "SENT, PENDING, CONFIRMED, BLOCK", allowMultiple = true, defaultValue = "CONFIRMED")
			@RequestParam(value = "status", required = false, defaultValue = "CONFIRMED") String[] status,
			@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
			@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
			@RequestParam(value = "properties", required = false, defaultValue = "id") String sort,
			@RequestParam(value = "direction", required = false, defaultValue = "ASC") String direction) {
		
		return this.findFriendsPaginated(this.friendshipService.getPrincipal().getId(), page, size, sort, direction);
	}	
	
	@RequestMapping(value = { "", "{friendId}" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Find all friends of a friend (paginated)", notes = "Find all friends of a friend. Returns paginated results")
	public Page<UserDTO> findAFriendsFriendsPaginated(
			@ApiParam(name = "friendId", required = true, value = "string") @PathVariable String friendId,
			@ApiParam(name = "status", required = false, allowableValues = "SENT, PENDING, CONFIRMED, BLOCK", allowMultiple = true, defaultValue = "CONFIRMED")
				@RequestParam(value = "status", required = false, defaultValue = "CONFIRMED") String[] status,
			@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
			@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
			@RequestParam(value = "properties", required = false, defaultValue = "id") String sort,
			@RequestParam(value = "direction", required = false, defaultValue = "ASC") String direction) {
		
		// validate targget friend
		FriendshipId friendshipId = new FriendshipId(this.friendshipService.getPrincipal().getId(), friendId);
		Friendship friendship = this.friendshipService.findById(friendshipId);
		if(friendship == null 
				|| !(FriendshipStatus.CONFIRMED.equals(friendship.getStatus())
						|| FriendshipStatus.PENDING.equals(friendship.getStatus()))){
			throw new IllegalArgumentException("Unauthorized");
		}
		
		return this.findFriendsPaginated(friendId, page, size, sort, direction);
	}	

	
	protected Page<UserDTO> findFriendsPaginated(
			String ownerId,
			@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
			@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
			@RequestParam(value = "properties", required = false, defaultValue = "id") String sort,
			@RequestParam(value = "direction", required = false, defaultValue = "ASC") String direction) {
		
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.put("id.owner.id", new String[]{ownerId});
		
		String[] status = getValidatedStatus();
		parameters.put("status", status);

		Pageable pageable = buildPageable(page, size, sort, direction, parameters);
		Page<Friendship> friendshipPage = this.friendshipService.findAll(pageable);
		
		// TODO: move DTO selection to query
		List<UserDTO> frieds = new ArrayList<UserDTO>(friendshipPage.getNumberOfElements());
		for(Friendship friendship : friendshipPage){
			frieds.add(UserDTO.fromUser(friendship.getId().getFriend()));
		}
		
		PageImpl<UserDTO> friends = new PageImpl<UserDTO>(frieds, pageable, friendshipPage.getTotalElements());
		return friends;
	}


	/**
	 * @return
	 */
	protected String[] getValidatedStatus() {
		String[] status = request.getParameterValues("status");
		// validate status
		if(status == null){
			status = new String[]{FriendshipStatus.CONFIRMED.toString()};
		}
		else{
			for(String stat : status){
				if(stat.equals(FriendshipStatus.BLOCK_INVERSE) || FriendshipStatus.valueOf(stat) == null){
					throw new IllegalArgumentException("Invalid status value: "+ stat);
				}
			}
		}
		return status;
	}
}
