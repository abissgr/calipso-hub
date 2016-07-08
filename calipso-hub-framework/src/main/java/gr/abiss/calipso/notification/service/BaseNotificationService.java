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
package gr.abiss.calipso.notification.service;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.notification.model.BaseNotification;
import gr.abiss.calipso.tiers.service.ModelService;
import gr.abiss.calipso.userDetails.integration.LocalUser;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;

public interface BaseNotificationService extends ModelService<BaseNotification, String> {

	/**
	 * Count the unseen notifications addressed to the current principal
	 * @return the count
	 */
	Long countUnseen();

	/**
	 * Count the unseen notifications addressed to the given user 
	 * @return the count
	 */	
	Long countUnseen(User recepient);
	
	/**
	 * Count the unseen notifications addressed to the given user 
	 * @return the count
	 */	
	Long countUnseen(LocalUser recepient);
	
	/**
	 * Count the unseen notifications addressed to the given principal
	 * @return the count
	 */
	Long countUnseen(ICalipsoUserDetails recepient);


}