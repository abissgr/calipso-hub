/**
 * calipso-hub-framework - A full stack, high level framework for lazy application hackers.
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
package gr.abiss.calipso.web.filters;

import gr.abiss.calipso.userDetails.integration.UserDetailsConfig;
import gr.abiss.calipso.userDetails.util.SimpleUserDetailsConfig;
import gr.abiss.calipso.utils.Constants;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("restRequestNormalizerFilter")
public class RestRequestNormalizerFilter extends OncePerRequestFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(RestRequestNormalizerFilter.class);
	private static final String X_HTTP_METHOD_OVERRIDE = "X-HTTP-Method-Override";
	private static final String JSON_UTF8 = "application/json; charset=UTF-8";

	/** Default method parameter: {@code _method} */
	public static final String DEFAULT_METHOD_PARAM = "_method";

	private String methodParam = DEFAULT_METHOD_PARAM;

	protected UserDetailsConfig userDetailsConfig = new SimpleUserDetailsConfig();

	@Autowired(required = false)
	public void setUserDetailsConfig(UserDetailsConfig userDetailsConfig) {
		this.userDetailsConfig = userDetailsConfig;
	}

	/**
	 * Set the parameter name to look for HTTP methods.
	 * 
	 * @see #DEFAULT_METHOD_PARAM
	 */
	public void setMethodParam(String methodParam) {
		Assert.hasText(methodParam, "'methodParam' must not be empty");
		this.methodParam = methodParam;
	}
	
	

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String requestMethodOverride = getMethodOverride(request);
		String authToken = getSecurityToken(request);
		response.setContentType(JSON_UTF8);
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("doFilterInternal, path: " + request.getContextPath() + ", method override: " + requestMethodOverride  + ", authToken: " + authToken );
		}
		if (!StringUtils.isEmpty(requestMethodOverride) || !StringUtils.isEmpty(authToken) ) {
			HttpServletRequest wrapper = new RestRequestNormalizerRequestWrapper(request, requestMethodOverride, authToken);
			filterChain.doFilter(wrapper, response);
		} else {
			filterChain.doFilter(request, response);
		}
		
	}
	
	protected String getSecurityToken(HttpServletRequest httpRequest) {
		String authToken = null;
		Cookie[] cookies = httpRequest.getCookies();
		String ssoCookieName = userDetailsConfig.getCookiesBasicAuthTokenName();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if(LOGGER.isDebugEnabled() ){
					LOGGER.debug("Found cookie '" + cookie.getName() + "', secure:  " + cookie.getSecure() + ", comment: " + cookie.getComment()
					+ ", domain: " + cookie.getDomain() + ", value: " + cookie.getValue());
				}
				if (cookie.getName().equalsIgnoreCase(ssoCookieName)) {
					if(LOGGER.isDebugEnabled() ){
						LOGGER.debug("Matched calipso SSO cookie'" + cookie.getName() + "', secure:  " + cookie.getSecure() + ", comment: " + cookie.getComment()
								+ ", domain: " + cookie.getDomain() + ", value: " + cookie.getValue());
					}
					authToken = cookie.getValue();
					break;
				}
			}
			if(LOGGER.isDebugEnabled() && authToken == null){
				LOGGER.debug("Found no calipso SSO cookie with name: " + ssoCookieName);
				
			}
		}
		return authToken;
	}

	protected String getMethodOverride(HttpServletRequest httpRequest) {
		String requestMethodOverride = httpRequest.getParameter(this.methodParam);
		if (StringUtils.isEmpty(requestMethodOverride)) {
			requestMethodOverride = httpRequest.getHeader(X_HTTP_METHOD_OVERRIDE);
			LOGGER.debug("HTTP Method Override from header: " + requestMethodOverride);
		} else {
			LOGGER.debug("HTTP Method Override from param: " + requestMethodOverride);
		}
		return requestMethodOverride;
	}

}
