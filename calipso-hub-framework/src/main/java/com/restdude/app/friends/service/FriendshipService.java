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
package com.restdude.app.friends.service;

import com.restdude.app.friends.model.Friendship;
import com.restdude.app.friends.model.FriendshipId;
import gr.abiss.calipso.model.dto.UserDTO;
import gr.abiss.calipso.tiers.service.ModelService;
import gr.abiss.calipso.websocket.message.ActivityNotificationMessage;

public interface FriendshipService extends ModelService<Friendship, FriendshipId> {

	public static final String BEAN_ID = "friendshipService";

	public Iterable<UserDTO> findAllMyFriends();

//	public Page<UserDTO> findAllMyFriendsPaginated(Pageable pageRequest);

	public void sendStompActivityMessageToOnlineFriends(ActivityNotificationMessage msg);
}