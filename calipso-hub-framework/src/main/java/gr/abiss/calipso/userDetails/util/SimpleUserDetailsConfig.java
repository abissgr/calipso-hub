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

import gr.abiss.calipso.userDetails.integration.UserDetailsConfig;

public class SimpleUserDetailsConfig implements UserDetailsConfig {

	private String cookiesBasicAuthTokenName = "Authorization";
	private String cookiesDomain = null;
	private String cookiesContextPath = null;
	private boolean cookiesSecure = false;
	private boolean cookiesHttpOnly = false;

	public SimpleUserDetailsConfig() {

	}

	public SimpleUserDetailsConfig(String cookiesBasicAuthTokenName) {
		this(cookiesBasicAuthTokenName, null, null, false, false);
	}
	public SimpleUserDetailsConfig(String cookiesBasicAuthTokenName, String cookiesDomain, String cookiesContextPath) {
		this(cookiesBasicAuthTokenName, cookiesDomain, cookiesContextPath, false, false);
	}
	
	public SimpleUserDetailsConfig(String cookiesBasicAuthTokenName, String cookiesDomain, String cookiesContextPath, boolean cookiesSecure, boolean cookiesHttpOnly) {
		super();
		this.cookiesBasicAuthTokenName = cookiesBasicAuthTokenName;
		this.cookiesDomain = cookiesDomain;
		this.cookiesContextPath = cookiesContextPath;
		this.cookiesSecure = cookiesSecure;
		this.cookiesHttpOnly = cookiesHttpOnly;
	}

	public String getCookiesBasicAuthTokenName() {
		return cookiesBasicAuthTokenName;
	}

	public String getCookiesDomain() {
		return cookiesDomain;
	}

	public String getCookiesContextPath() {
		return cookiesContextPath;
	}

	public boolean isCookiesSecure() {
		return cookiesSecure;
	}

	public boolean isCookiesHttpOnly() {
		return cookiesHttpOnly;
	}


}
