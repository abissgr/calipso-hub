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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;


/**
*/
public class SecurityUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityUtil.class);
	// TODO: move to config
	private static final String COOKIE_NAME_SESSION = "JSESSIONID";

	public static void login(HttpServletRequest request, HttpServletResponse response, LocalUser user, UserDetailsConfig userDetailsConfig) {
		UserDetails userDetails = UserDetails.fromUser(user);
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("login,  userDetails: "+userDetails);
		}
		login(request, response, userDetails, userDetailsConfig);
	}

	public static void login(HttpServletRequest request, HttpServletResponse response, UserDetails userDetails,
			UserDetailsConfig userDetailsConfig) {
		if(LOGGER.isDebugEnabled()){
			if (userDetails != null){
				LOGGER.debug("login, userDetails un: "+userDetails.getUsername()+", non-blank pw: "+StringUtils.isNotBlank(userDetails.getPassword()));
			}
		}
		if (userDetails != null
				&& StringUtils.isNotBlank(userDetails.getUsername())
				&& StringUtils.isNotBlank(userDetails.getPassword())) {
			String token = new String(Base64.encode((userDetails.getUsername()
					+ ":" + userDetails.getPassword()).getBytes()));
			addCookie(request, response, userDetailsConfig.getCookiesBasicAuthTokenName(), token, false, userDetailsConfig);
		} else{
			throw new BadCredentialsException("The provided user details are incomplete");
		}
		
	}


	public static void logout(HttpServletRequest request, HttpServletResponse response, UserDetailsConfig userDetailsConfig) {
		addCookie(request, response, userDetailsConfig.getCookiesBasicAuthTokenName(), null, true, userDetailsConfig);
		addCookie(request, response, COOKIE_NAME_SESSION, null, true, userDetailsConfig);
		HttpSession session = request.getSession();
		if (session == null) {
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("logout, no session to clear");
			}
		} else {
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("logout, invalidating session");
			}
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
	private static void addCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, boolean allowClear, UserDetailsConfig userDetailsConfig) {
		if (StringUtils.isBlank(cookieValue) && !allowClear) {
			throw new RuntimeException("Was given a blank cookie value but allowClear is false for cookie name: " + cookieName);
		}

		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("addCookie, cookieName: " + cookieName + 
				", cookie value: " + cookieValue+
				", domain: "+userDetailsConfig.getCookiesDomain() +
				", secure: "+userDetailsConfig.isCookiesSecure() +
				", http-only: "+userDetailsConfig.isCookiesHttpOnly() +
				", path: "+userDetailsConfig.getCookiesContextPath());
		}
		Cookie cookie = new Cookie(cookieName, cookieValue);
		
		// set the cookie domain
		if (StringUtils.isNotBlank(userDetailsConfig.getCookiesDomain())) {
			cookie.setDomain('.' + userDetailsConfig.getCookiesDomain());
		}
		// maybe not a good idea unless you can trust the proxy
//		else if (StringUtils.isNotBlank(request.getHeader("X-Forwarded-Host"))) {
//			cookie.setDomain('.' + request.getHeader("X-Forwarded-Host"));
//		}
//		else{
//			cookie.setDomain('.' + request.getLocalName());
//			
//		}
		// set the cookie path
		if (StringUtils.isNotBlank(userDetailsConfig.getCookiesContextPath())) {
			cookie.setPath(userDetailsConfig.getCookiesContextPath());
		} 
//		else {
//			cookie.setPath("/");
//		}
		
		cookie.setSecure(userDetailsConfig.isCookiesSecure());
		cookie.setHttpOnly(userDetailsConfig.isCookiesHttpOnly());
		
		if (StringUtils.isBlank(cookieValue)) {
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("addCookie, setting max-age to 0 to clear cookie: " + cookieName);
			}
			cookie.setMaxAge(0);
		}
		response.addCookie(cookie);
	}

	public static UserDetails getPrincipal() {
		Object principal = null;
		if (SecurityContextHolder.getContext() != null
				&& SecurityContextHolder.getContext().getAuthentication() != null) {
			principal = SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
		}
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("getPrincipal, principal: " + principal);
		}
		if (principal != null
				&& UserDetails.class.isAssignableFrom(principal.getClass())) {
			return (UserDetails) principal;
		} else {
			return null;
		}
	}

}