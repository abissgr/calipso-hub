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

	/** Default method parameter: {@code _method} */
	public static final String DEFAULT_METHOD_PARAM = "_method";

	private String methodParam = DEFAULT_METHOD_PARAM;

	protected UserDetailsConfig userDetailsConfig;// = new SimpleUserDetailsConfig();

	@Autowired//(required = false)
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
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		LOGGER.info("doFilterInternal, path: " + httpRequest.getContextPath());
		
		String requestMethodOverride = getMethodOverride(httpRequest);
		String authToken = getSecurityToken(httpRequest);
		
		if (!StringUtils.isEmpty(requestMethodOverride) || !StringUtils.isEmpty(authToken) ) {
			HttpServletRequest wrapper = new RestRequestNormalizerRequestWrapper(request, requestMethodOverride, authToken);
			filterChain.doFilter(wrapper, response);
		} else {
			filterChain.doFilter(request, response);
		}
	}
	
	private String getSecurityToken(HttpServletRequest httpRequest) {
		String authToken = null;
		Cookie[] cookies = httpRequest.getCookies();
		String ssoCookieName = userDetailsConfig.getCookiesBasicAuthTokenName();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				LOGGER.info("Found cookie '" + cookie.getName() + "', secure:  " + cookie.getSecure() + ", comment: " + cookie.getComment()
						+ ", domain: " + cookie.getDomain() + ", value: " + cookie.getValue());
				if (cookie.getName().equalsIgnoreCase(ssoCookieName)) {
					authToken = cookie.getValue();
				}
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
