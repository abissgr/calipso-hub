/**
 * calipso-hub-utilities - A full stack, high level framework for lazy application hackers.
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
package com.restdude.util;

public class Constants {

	public static final String REQUEST_AUTHENTICATION_TOKEN_COOKIE_NAME = "calipso-sso";

	public static final String BASE_URL_KEY = Constants.class.getName() + "#BASE_URL";
	public static final String DOMAIN_KEY = Constants.class.getName() + "#DOMAIN";

	public static final String GRAVATAR_BASE_IMG_URL = "https://www.gravatar.com/avatar/";

	public static final String DEFAULT_AVATAR_URL = ConfigurationFactory.getConfiguration().getString(ConfigurationFactory.BASE_URL) + "/img/avatars/default.jpg";
	public static final String DEFAULT_BANNER_URL = ConfigurationFactory.getConfiguration().getString(ConfigurationFactory.BASE_URL) + "/img/banners/default.png";
	public static final String HEADER_AUTHORIZATION =  "Authorization";
	
	
	
}
