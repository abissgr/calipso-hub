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
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
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
//@RequestMapping(value = "/apiauth", produces = { "application/json", "application/xml" })
public abstract class AbstractUserDetailsController<S extends UserDetailsService> extends
		ServiceBasedRestController<ICalipsoUserDetails, String, S> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractUserDetailsController.class);

	protected UserDetailsConfig userDetailsConfig = new SimpleUserDetailsConfig();

	@Override
	public abstract void setService(S service);

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
	@RequestMapping(value = "confirmation", method = {RequestMethod.POST })
	@ResponseBody
	public ICalipsoUserDetails confirmRegistrationAndLogin(HttpServletRequest request,
			HttpServletResponse response, @RequestBody UserDetails resource) {
		String key = resource.getConfirmationToken();
		LOGGER.info("Confirmation key: " + key);
		ICalipsoUserDetails userDetails = this.service.confirmPrincipal(key);
		SecurityUtil.login(request, response, userDetails, userDetailsConfig);
		return userDetails;
	}
	
	/**
	 * Logins the given user after confirming his email address. Triggered by token HTML form (POST) or email confirmation link (GET)
	 * @param request
	 * @param response
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "accountConfirmations/{token}", method = {RequestMethod.GET})
	@ResponseBody
	public ICalipsoUserDetails confirmRegistrationAndLoginFromEmailLink(HttpServletRequest request,
			HttpServletResponse response, @PathVariable String token) {
		ICalipsoUserDetails userDetails = this.service.confirmPrincipal(token);
		SecurityUtil.login(request, response, userDetails, userDetailsConfig);
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
	public ICalipsoUserDetails resetPasswordAndLogin(
HttpServletRequest request,
			HttpServletResponse response, @RequestBody UserDetails user,
			@PathVariable String token) {

		String userNameOrEmail = user.getUsername() != null ? user
				.getUsername() : user.getEmail();
				ICalipsoUserDetails userDetails = service.resetPassword(
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
	public ICalipsoUserDetails getRemembered(HttpServletRequest request,
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
	public ICalipsoUserDetails create(HttpServletRequest request, HttpServletResponse response, @RequestBody UserDetails resource) {
		return this.create(request, response, resource, true);
	}

	/**
	 * Update the user details object, i.e. change the user password
	 * @param request
	 * @param response
	 * @param resource
	 * @return
	 */
	@RequestMapping(value = "userDetails", method = RequestMethod.PUT)
	@ResponseBody
	public UserDetails update(HttpServletRequest request, HttpServletResponse response, @RequestBody UserDetails resource) {
		LOGGER.info("updatePassword, resource: " + resource);
		resource = (UserDetails) this.service.update(resource);
		SecurityUtil.login(request, response, resource, userDetailsConfig);
		return resource;
	}


	@RequestMapping(value = "verifyPassword", method = RequestMethod.POST)
	@ResponseBody
	public ICalipsoUserDetails verifyPassword(HttpServletRequest request, HttpServletResponse response, @RequestBody UserDetails resource) {
		return this.create(request, response, resource, false);
	}
	
	protected ICalipsoUserDetails create(HttpServletRequest request, HttpServletResponse response, @RequestBody UserDetails resource, boolean apply) {
		ICalipsoUserDetails userDetails = null;

		LOGGER.info("Trying to " + (apply?"login":"confirm password") + " with: "+resource);
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("Trying to " + (apply?"login":"confirm password") + " with: "+resource);
		}
		try {
			userDetails = resource != null ? this.service.create(resource) : null;

			LOGGER.info("create userDetails: " + userDetails);
			if (userDetails != null && userDetails.getId() != null) {
				if(apply){
					SecurityUtil.login(request, response, userDetails, userDetailsConfig);
				}
			} else {
				if(apply){
					SecurityUtil.logout(request, response, userDetailsConfig);
				}
				userDetails = new UserDetails();
			}
		}
		catch (Throwable e) {
			StackTraceElement[] elems = e.getStackTrace();
			LOGGER.error("printing stacktrace...");
			for(int i=0;i<elems.length;i++){
				StackTraceElement elem = elems[i];
				LOGGER.error(elem.getFileName() + ", line "
						+ elem.getLineNumber());
			}
			LOGGER.error(
					"UserDetailsController failed creating new userDetails",
					e);
		}

		return userDetails;
	}

	/**
	 * @deprecated use {@link #delete(HttpServletRequest,HttpServletResponse)} instead.  
	 */
	@Deprecated
	@RequestMapping(value = "userDetails/logout", method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST})
	@ResponseBody
	public void logout(HttpServletRequest request,
			HttpServletResponse response) {
		LOGGER.warn("@RequestMapping \"userDetails/logout\" is deprecated");
		this.delete(request, response);
	}

	/**
	 * Log out the user
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "userDetails", method = RequestMethod.DELETE)
	@ResponseBody
	public void delete(HttpServletRequest request,
			HttpServletResponse response) {
		// logout
		SecurityUtil.logout(request, response, userDetailsConfig);
		
	}
	

}
