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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import gr.abiss.calipso.userDetails.controller.UserDetailsController;

//@Controller
@RequestMapping(value = "/api-auth", produces = { "application/json", "application/xml" })
public class CalipsoUserDetailsController extends UserDetailsController {

         // no need to override anything unless you wish to change 
         // the default method RequestMappings/URLs
 }