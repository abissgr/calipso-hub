package gr.abiss.calipso.web.filters;


import java.io.IOException;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class RestRequestNormalizerFilter extends OncePerRequestFilter {

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

		public HttpMethodRequestWrapper(HttpServletRequest request,
				String method) {
			super(request);
			this.method = method;
		}

		@Override
		public String getMethod() {
			return this.method;
		}
	}

}
