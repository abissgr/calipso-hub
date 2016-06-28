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

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import gr.abiss.calipso.userDetails.integration.UserDetailsConfig;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.userDetails.model.UserDetails;
import gr.abiss.calipso.userDetails.service.UserDetailsService;
import gr.abiss.calipso.userDetails.util.SecurityUtil;
import gr.abiss.calipso.userDetails.util.SimpleUserDetailsConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Auth", description = "Authentication operations")
@RequestMapping(value = "/apiauth/userDetails", produces = {"application/json", "application/xml" })
public class UserDetailsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UserDetailsController.class);
	
	private UserDetailsService service;

	protected UserDetailsConfig userDetailsConfig = new SimpleUserDetailsConfig();

    @Autowired
    protected HttpServletRequest request;
    
    @Autowired
    protected HttpServletResponse response;
	
	@Autowired(required = false)
	public void setUserDetailsConfig(UserDetailsConfig userDetailsConfig) {
		this.userDetailsConfig = userDetailsConfig;
	}
	
	@Inject
	@Qualifier("userDetailsService")
	public void setService(UserDetailsService service) {
		this.service = service;
	}
	
	//
	@RequestMapping(method = RequestMethod.POST)
	@ApiOperation(value = "Login", 
		notes = "Login using a JSON object with email/password properties.") 
	@ResponseBody
	public ICalipsoUserDetails create(@RequestBody ICalipsoUserDetails resource) {

		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("create");
		}
		if(resource.getEmailOrUsername() != null){
			// change password
			if(resource.getResetPasswordToken() != null){
				resource = this.resetPasswordAndLogin(resource);
			}
			// if login
			else if(resource.getPassword() != null){
				resource = this.login(resource, true);
			}
			// forgot password
			else{
				this.service.handlePasswordResetRequest(resource.getEmailOrUsername());
			}
		}
		return resource;
	}

	@ApiOperation(value = "Remember", 
		notes = "Login remembered user") 
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ICalipsoUserDetails remember() {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("remember");
		}

		UserDetails resource = new UserDetails();

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
		return this.create(resource);

	}

	@ApiOperation(value = "Logout", 
			notes = "Logout and forget user") 
	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseBody
	public ICalipsoUserDetails delete() {
		// logout
		SecurityUtil.logout(request, response, userDetailsConfig);
		return new UserDetails();
	}
	

	public ICalipsoUserDetails resetPasswordAndLogin(@RequestBody ICalipsoUserDetails userDetails) {

		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("resetPasswordAndLogin");
		}
		userDetails = service.resetPassword(userDetails);
		SecurityUtil.login(request, response, userDetails, userDetailsConfig, this.service);
		
		
		return userDetails;
	}
	
	protected ICalipsoUserDetails login(ICalipsoUserDetails userDetails, boolean apply) {

		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("login");
		}
		try {
			userDetails = this.service.create(userDetails);
			if (userDetails != null && userDetails.getId() != null) {
				if(apply){
					LOGGER.info("login, applying: " + userDetails);
					SecurityUtil.login(request, response, userDetails, userDetailsConfig, this.service);
				}
				else{

					LOGGER.info("login, skipping: " + userDetails);
				}
			} else {
				if(apply){
					LOGGER.info("login, logging out: " + userDetails);
					SecurityUtil.logout(request, response, userDetailsConfig);
				}
				LOGGER.info("login, anew: " + userDetails);
				userDetails = new UserDetails();
			}
		}
		catch (Throwable e) {
			LOGGER.error("login: failed creating new userDetails",	e);
		}
		return userDetails;
	}

}
