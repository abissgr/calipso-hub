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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.web.ProviderSignInInterceptor;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

/**
* Adds a "display=popup" parameter so that the Facebook authorization is displayed with minimal decoration.
* @Deprecated
*/
public class FacebookConnectInterceptor implements ProviderSignInInterceptor<Facebook> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FacebookConnectInterceptor.class);

	@Override
	public void preSignIn(ConnectionFactory<Facebook> connectionFactory, MultiValueMap<String, String> parameters, WebRequest request) {
		parameters.set("display", "popup");
		LOGGER.info("topWindowDomain: " + parameters.get("topWindowDomain"));
		for (String key : parameters.keySet()) {
			LOGGER.info("param " + key + ": " + parameters.get(key));
		}
		LOGGER.info("WebRequest params...");
		for (String key : request.getParameterMap().keySet()) {

			LOGGER.info("param " + key + ": "
					+ request.getParameterMap().get(key));
		}
	}

	@Override
	public void postSignIn(Connection<Facebook> connection, WebRequest request) {
		// Nothing to do
	}

}