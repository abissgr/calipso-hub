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
package gr.abiss.calipso.model.dto;

import java.io.Serializable;

import javax.persistence.Column;

import org.apache.commons.lang3.builder.ToStringBuilder;

import gr.abiss.calipso.friends.model.Friendship;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.User.Builder;
import gr.abiss.calipso.model.types.FriendshipStatus;
import io.swagger.annotations.ApiModel;

@ApiModel(value = "FriendshipDTO", description = "FriendshipDTO is a lightweight DTO version of Friendship")
public class FriendshipDTO implements Serializable {

	private UserDTO requestSender;
	private UserDTO requestRecepient;
	private FriendshipStatus status = FriendshipStatus.PENDING;
	
	public FriendshipDTO() {
	}
	
	public FriendshipDTO(Friendship friendship){
		this.requestSender = UserDTO.fromUser(friendship.getRequestSender());
		this.requestRecepient = UserDTO.fromUser(friendship.getRequestRecipient());
		this.status = friendship.getStatus();
	}

	public UserDTO getRequestSender() {
		return requestSender;
	}

	public void setRequestSender(UserDTO requestSender) {
		this.requestSender = requestSender;
	}

	public UserDTO getRequestRecepient() {
		return requestRecepient;
	}

	public void setRequestRecepient(UserDTO requestRecepient) {
		this.requestRecepient = requestRecepient;
	}

	public FriendshipStatus getStatus() {
		return status;
	}

	public void setStatus(FriendshipStatus status) {
		this.status = status;
	}
	

}