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
package gr.abiss.calipso.notification.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.Query;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.notification.model.BaseNotification;
import gr.abiss.calipso.tiers.repository.ModelRepository;

public interface BaseNotificationRepository extends ModelRepository<BaseNotification, String> {
	
	@Query("select count(n) from BaseNotification n where n.seen = false and n.recepient.id = ?1")
	Long countUnseen(Serializable recepientId);

}
