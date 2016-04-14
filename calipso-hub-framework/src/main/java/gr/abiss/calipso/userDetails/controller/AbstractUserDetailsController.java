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

import org.apache.commons.lang.BooleanUtils;
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


}
