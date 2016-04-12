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

import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gr.abiss.calipso.service.UserService;
import gr.abiss.calipso.userDetails.integration.UserDetailsConfig;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.userDetails.model.UserDetails;
import gr.abiss.calipso.userDetails.service.UserDetailsService;
import gr.abiss.calipso.userDetails.util.SecurityUtil;
import gr.abiss.calipso.userDetails.util.SimpleUserDetailsConfig;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.resthub.web.controller.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//import com.mangofactory.swagger.annotations.ApiIgnore;
//import com.wordnik.swagger.annotations.Api;

//@Controller
//@Api(value = "Logged-in user details")
//@ApiIgnore
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
	// somehow required for CDI to work on 64bit JDK?
	public void setService(UserDetailsService service) {
		this.service = service;
	}
	
	//
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ICalipsoUserDetails login(@RequestBody UserDetails resource) {

		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("Login");
		}
		return this.create(resource, true);
	}

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
		return this.login(resource);

	}
	
	

	public ICalipsoUserDetails resetPasswordAndLogin(@RequestBody UserDetails user,
			@PathVariable String token) {

		String userNameOrEmail = user.getEmailOrUsername();
		ICalipsoUserDetails userDetails = service.resetPassword(
				userNameOrEmail, token, user.getPassword());
		SecurityUtil.login(request, response, userDetails, userDetailsConfig, this.service);
		
		
		return userDetails;
	}
	
	public void handlePasswordResetRequest(@PathVariable String userNameOrEmail) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("handlePasswordResetRequest: Trying to reset password");
		}
		service.handlePasswordResetRequest(userNameOrEmail);
	}

	protected ICalipsoUserDetails create( UserDetails resource, boolean apply) {
		ICalipsoUserDetails userDetails = null;

		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("protected create");
		}
		if(resource != null){

			if(BooleanUtils.isTrue(resource.getIsResetPasswordReguest())){
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("Forgotten password request, will createg token");
				}
				this.handlePasswordResetRequest(resource.getEmailOrUsername());
				userDetails = resource;
				
			}
			else if(resource.getResetPasswordToken() != null){
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("Trying to change password ewith reset token anf login");
				}
				userDetails = this.resetPasswordAndLogin(resource, resource.getResetPasswordToken());
				
			}
			else{
				if(LOGGER.isDebugEnabled()){
					LOGGER.debug("Trying to " + (apply?"login":"confirm password") + " with: "+resource);
				}
				userDetails = login(request, response, resource, apply, userDetails);
			}
			
		}
		return userDetails;
	}
	protected ICalipsoUserDetails login(HttpServletRequest request,
			HttpServletResponse response, UserDetails resource, boolean apply,
			ICalipsoUserDetails userDetails) {
		try {
			userDetails = resource != null ? this.service.create(resource) : null;

			LOGGER.info("create userDetails: " + userDetails);
			if (userDetails != null && userDetails.getId() != null) {
				if(apply){
					SecurityUtil.login(request, response, userDetails, userDetailsConfig, this.service);
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

}
