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
@Api(tags = "Invitations", description = "Invite new users")
@RequestMapping(value = "/api/rest/invitations", produces = { "application/json", "application/xml" })
public class InvitationsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(InvitationsController.class);

	@Inject
	@Qualifier(FriendshipService.BEAN_ID)
	FriendshipService friendshipService;
	
	@Inject
	@Qualifier("userService")
	UserService userService;

	@RequestMapping(value = "invites", method = RequestMethod.POST)
	@ResponseBody
    @ApiOperation(value = "Invite users", notes = "Invite users by email")
	public UserInvitationResultsDTO inviteUsers(@RequestBody UserInvitationsDTO invitations) {
		LOGGER.debug("INVITE USERS: " + invitations);
		return this.userService.inviteUsers(invitations);
	}
}
