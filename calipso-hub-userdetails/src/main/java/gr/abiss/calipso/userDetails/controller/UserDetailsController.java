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
package gr.abiss.calipso.userDetails.controller;

import gr.abiss.calipso.userDetails.integration.UserDetailsConfig;
import gr.abiss.calipso.userDetails.model.UserDetails;
import gr.abiss.calipso.userDetails.service.UserDetailsService;
import gr.abiss.calipso.userDetails.util.SecurityUtil;
import gr.abiss.calipso.userDetails.util.SimpleUserDetailsConfig;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.resthub.web.controller.ServiceBasedRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;




//@Controller
@RequestMapping(value = "/apiauth", produces = { "application/json", "application/xml" })
public abstract class UserDetailsController extends
		ServiceBasedRestController<UserDetails, String, UserDetailsService> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsController.class);

	private UserDetailsConfig userDetailsConfig = new SimpleUserDetailsConfig();

	@Override
	@Autowired
	public void setService(UserDetailsService service) {
		this.service = service;
	}

	@Autowired(required = false)
	public void setUserDetailsConfig(UserDetailsConfig userDetailsConfig) {
		this.userDetailsConfig = userDetailsConfig;
	}

	/**
	 * Logins the given user after confirming his email address. Triggered by token HTML form (POST) or email confirmation link (GET)
	 * @param request
	 * @param response
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "confirmation/{token}", method = {
			RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public UserDetails confirmRegistrationAndLogin(HttpServletRequest request,
			HttpServletResponse response, @PathVariable String token) {
		UserDetails userDetails = this.service.confirmPrincipal(token);
		SecurityUtil.login(request, response, userDetails, userDetailsConfig);
		if ("GET".equalsIgnoreCase(request.getMethod())) {
			// check for a comment to redirect to exists
			if (StringUtils.isNotEmpty(userDetails.getRedirectUrl())) {
				try {
					response.sendRedirect(userDetails.getRedirectUrl());
				} catch (IOException e) {
					LOGGER.error("Could not redirect to contribution URL", e);
				}
			}
			else{
				LOGGER.warn("Asked to confirm registration by GET but no comment was found as the one registration origin for user "
						+ userDetails.getUsername() + ", redirecting to manager area");
				try {
					response.sendRedirect(request.getContextPath());
				} catch (IOException e) {
					LOGGER.error("Could not redirect to manager area", e);
				}
			}
		}
		return userDetails;
	}

	/**
	 * Logins the given user after reseting his password
	 * @param request
	 * @param response
	 * @param user
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "password-reset/{token}", method = RequestMethod.POST)
	@ResponseBody
	public UserDetails resetPasswordAndLogin(
HttpServletRequest request,
			HttpServletResponse response, @RequestBody UserDetails user,
			@PathVariable String token) {

		String userNameOrEmail = user.getUsername() != null ? user
				.getUsername() : user.getEmail();
		UserDetails userDetails = service.resetPassword(
				userNameOrEmail, token, user.getPassword());
		SecurityUtil.login(request, response, userDetails, userDetailsConfig);
		return userDetails;
	}

	@RequestMapping(value = "password-reset-request/{userNameOrEmail}", method = RequestMethod.POST)
	@ResponseBody
	public void handlePasswordResetRequest(@PathVariable String userNameOrEmail) {
		service.handlePasswordResetRequest(userNameOrEmail);
	}

	@RequestMapping(value = "userDetails/remembered", method = RequestMethod.GET)
	@ResponseBody
	public UserDetails getRemembered(HttpServletRequest request,
			HttpServletResponse response) {
		UserDetails resource = null;

		Cookie tokenCookie = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {

			for (int i = 0; i < cookies.length; i++) {
				tokenCookie = cookies[i];
				if (tokenCookie.getName().equals(
						this.userDetailsConfig.getCookiesBasicAuthTokenName())) {
					String token = tokenCookie.getValue();
					if (StringUtils.isNotBlank(token)) {
						token = new String(Base64.decode(token.getBytes()));
						LOGGER.info("Request contained token: " + token);
						if (token.indexOf(':') > 0) {
							String[] parts = token.split(":");
							if (StringUtils.isNotBlank(parts[0])
									&& StringUtils.isNotBlank(parts[1])) {
								resource = new UserDetails();
								resource.setUsername(parts[0]);
								resource.setPassword(parts[1]);
							}
						} else {
							LOGGER.warn("Invalid token received: " + token);
						}
					}
					break;
				}
			}
		}
		return this.create(request, response, resource);
	}




	@RequestMapping(value = "userDetails", method = RequestMethod.POST)
	@ResponseBody
	public UserDetails create(HttpServletRequest request, HttpServletResponse response, @RequestBody UserDetails resource) {

		UserDetails userDetails = resource != null ? this.service
				.create(resource) : null;

		if(userDetails != null){
			SecurityUtil.login(request, response, userDetails, userDetailsConfig);
			resource = userDetails;
		}
		else{
			resource = new UserDetails();
		}

		return resource;
	}

	@RequestMapping(value = "userDetails/logout", method = RequestMethod.POST)
	@ResponseBody
	public void logout(HttpServletRequest request,
			HttpServletResponse response, @RequestBody UserDetails resource) {
		// logout
		this.delete(request, response, resource);
	}
	
	@RequestMapping(value = "userDetails", method = RequestMethod.DELETE)
	@ResponseBody
	public void delete(HttpServletRequest request,
			HttpServletResponse response, @RequestBody UserDetails resource) {
		// logout
		SecurityUtil.logout(request, response, userDetailsConfig);
	}


}
