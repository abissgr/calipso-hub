/**
 *
 *
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gr.abiss.calipso.userDetails.util;

import gr.abiss.calipso.userDetails.integration.LocalUser;
import gr.abiss.calipso.userDetails.integration.UserDetailsConfig;
import gr.abiss.calipso.userDetails.model.UserDetails;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.codec.Base64;


/**
*/
public class SecurityUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityUtil.class);
	// TODO: move to config
	private static final String COOKIE_NAME_SESSION = "JSESSIONID";

	public static void login(HttpServletRequest request, HttpServletResponse response, LocalUser user, UserDetailsConfig userDetailsConfig) {
		LOGGER.debug("login S1, Logging in user: " + user);
		UserDetails userDetails = UserDetails.fromUser(user);

		//        		ExampleUserDetails.getBuilder()
		//                .firstName(user.getFirstName())
		//                .id(user.getId())
		//                .lastName(user.getLastName())
		//                .password(user.getPassword())
		//                .role(user.getRole())
		//                .socialSignInProvider(user.getSignInProvider())
		//                .username(user.getEmail())
		//                .build();
		LOGGER.debug("login S1, Logging in principal: " + userDetails);
		//		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		//		SecurityContextHolder.getContext().setAuthentication(authentication);
		login(request, response, userDetails, userDetailsConfig);
		LOGGER.debug("login S1, Logged in user: " + userDetails);
	}

	public static void login(HttpServletRequest request, HttpServletResponse response, UserDetails userDetails,
			UserDetailsConfig userDetailsConfig) {
		if (userDetails != null
				&& StringUtils.isNotBlank(userDetails.getUserName()) 
				&& StringUtils.isNotBlank(userDetails.getUserPassword())) {
			String token = new String(Base64.encode((userDetails.getUserName() + ":" + userDetails.getUserPassword()).getBytes()));
			addCookie(response, userDetailsConfig.getCookiesBasicAuthTokenName(), token, false, userDetailsConfig);
		} else{
			throw new BadCredentialsException("The provided user details are incomplete");
		}
		
	}


	public static void logout(HttpServletRequest request, HttpServletResponse response, UserDetailsConfig userDetailsConfig) {
		addCookie(response, userDetailsConfig.getCookiesBasicAuthTokenName(), null, true, userDetailsConfig);
		addCookie(response, COOKIE_NAME_SESSION, null, true, userDetailsConfig);
		HttpSession session = request.getSession();
		if (session != null) {
			LOGGER.debug("logout, no session to clear");
		} else {
			LOGGER.debug("logout, invalidating session");
			session.invalidate();
		}
	}

	/**
	 * Writes a cookie to the response. In case of a blank value the method will 
	 * set the max age to zero, effectively marking the cookie for immediate 
	 * deletion by the client if the <code>allowClear</code> is true or throw an exception if false.
	 * Blank value strings mark cookie deletion. If 
	 * @param response
	 * @param cookieName
	 * @param cookieValue
	 * @param allowClear
	 */
	private static void addCookie(HttpServletResponse response, String cookieName, String cookieValue, boolean allowClear, UserDetailsConfig userDetailsConfig) {
		if (StringUtils.isBlank(cookieValue) && !allowClear) {
			throw new RuntimeException("Was given a blank cookie value but allowClear is false for cookie name: " + cookieName);
		}
		// TODO: use own config
		//Configuration config = null;//Config.getConfiguration();

		LOGGER.debug("addCookie, cookieName: " + cookieName + 
				", cookie value: " + cookieValue+
				", Domain: "+userDetailsConfig.getCookiesDomain() +
				", path: "+userDetailsConfig.getCookiesContextPath());
		Cookie cookie = new Cookie(cookieName, cookieValue);
		if (StringUtils.isNotBlank(userDetailsConfig.getCookiesDomain())) {
			cookie.setDomain('.' + userDetailsConfig.getCookiesDomain());
		} else {
			cookie.setDomain(".localhost");
		}

		if (StringUtils.isNotBlank(userDetailsConfig.getCookiesContextPath())) {
			cookie.setPath(userDetailsConfig.getCookiesContextPath());
		} else {
			cookie.setPath("/");
		}

		if (StringUtils.isBlank(cookieValue)) {
			LOGGER.debug("addCookie, setting max-age to 0 to clear cookie: " + cookieName);
			cookie.setMaxAge(0);
		}
		response.addCookie(cookie);
	}

}