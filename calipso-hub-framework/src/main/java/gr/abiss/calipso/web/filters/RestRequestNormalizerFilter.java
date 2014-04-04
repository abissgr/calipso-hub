package gr.abiss.calipso.web.filters;

import gr.abiss.calipso.utils.Constants;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestRequestNormalizerFilter extends OncePerRequestFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(RestRequestNormalizerFilter.class);
	private static final String X_HTTP_METHOD_OVERRIDE = "X-HTTP-Method-Override";

	/** Default method parameter: {@code _method} */
	public static final String DEFAULT_METHOD_PARAM = "_method";

	private String methodParam = DEFAULT_METHOD_PARAM;

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
		// TODO: ['GET', 'HEAD', 'OPTIONS', 'DELETE'] must have no body

		String paramValue = request.getParameter(this.methodParam);
		if (StringUtils.isEmpty(paramValue)) {
			paramValue = request.getHeader(X_HTTP_METHOD_OVERRIDE);
		}
		if ("POST".equals(request.getMethod())
				&& StringUtils.hasLength(paramValue)) {
			String method = paramValue.toUpperCase(Locale.ENGLISH);
			HttpServletRequest wrapper = new HttpMethodRequestWrapper(request,
					method);
			filterChain.doFilter(wrapper, response);
		} else {
			filterChain.doFilter(request, response);
		}
	}

	/**
	 * Simple {@link HttpServletRequest} wrapper that returns the supplied
	 * method for {@link HttpServletRequest#getMethod()}.
	 */
	private static class HttpMethodRequestWrapper extends
			HttpServletRequestWrapper {

		private final String method;

		private Map<String, String> customHeaderMap = new HashMap<String, String>();

		public void addHeader(String name, String value) {
			this.customHeaderMap.put(name, value);
		}

		public HttpMethodRequestWrapper(HttpServletRequest request,
				String method) {
			super(request);
			this.method = method;
			String authtoken = getSecurityToken(request);

			if (org.apache.commons.lang3.StringUtils.isNotBlank(authtoken)) {
				this.addHeader("Authorization", "Basic " + authtoken);
			}
			
		}

		@Override
		public String getMethod() {
			return this.method;
		}

		@Override
		public String getHeader(String name) {
	        String header = super.getHeader(name);
	        if("accept".equalsIgnoreCase(name)){
	        	header = "application/json";
	        }
			if (header == null) {
				header = this.customHeaderMap.get(name);
			}
	        return header; // Note: you can't use getParameterValues() here.
	    }

		@Override
		public Enumeration<String> getHeaderNames() {
	        List<String> names = Collections.list(super.getHeaderNames());
	        names.add("Accept");
			names.addAll(this.customHeaderMap.keySet());
	        return Collections.enumeration(names);
	    }


		private String getSecurityToken(HttpServletRequest httpRequest) {
			String authToken = null;
			Cookie[] cookies = httpRequest.getCookies();//(REQUEST_AUTHENTICATION_TOKEN_PARAM_NAME);
			if (cookies != null) {
				for (int i = 0; i < cookies.length; i++) {
					Cookie cookie = cookies[i];
					LOGGER.info("Found cookie '" + cookie.getName() + "', secure:  " + cookie.getSecure() + ", comment: " + cookie.getComment()
							+ ", domain: " + cookie.getDomain() + ", value: " + cookie.getValue());
					if (cookie.getName().equalsIgnoreCase(Constants.REQUEST_AUTHENTICATION_TOKEN_COOKIE_NAME)) {
						authToken = cookie.getValue();
					}
				}
			}
			return authToken;
		}
	}

}
