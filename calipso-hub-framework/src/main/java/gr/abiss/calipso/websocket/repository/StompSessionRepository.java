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
package gr.abiss.calipso.websocket.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import gr.abiss.calipso.tiers.repository.ModelRepository;
import gr.abiss.calipso.websocket.model.StompSession;


public interface StompSessionRepository extends ModelRepository<StompSession, String> {

	@Modifying
	@Query("UPDATE User AS u SET u.stompSessionCount = u.stompSessionCount + 1 WHERE u.id = ?1")
	public void addUserStompSession(String userId);
	
	@Modifying
	@Query("UPDATE User AS u SET u.stompSessionCount = u.stompSessionCount - 1 WHERE u.id = ?1")
	public void removeUserStompSession(String userId);
}
