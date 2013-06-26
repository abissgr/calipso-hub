package gr.abiss.calipso.spring;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * In a standard web application, the authentication process may be
 * automatically triggered when the client tries to access a secured resource
 * without being authenticated – this is usually done by redirecting to a login
 * page so that the user can enter credentials. However, for a RESTful Web
 * Service this behavior doesn’t make much sense – authentication should only be
 * done by a request to the correct URI and all other requests should simply
 * fail with a 401 UNAUTHORIZED status code if the user is not authenticated.
 * 
 * Spring Security handles this automatic triggering of the authentication
 * process with the concept of an entry point; the entry point is a required
 * part of the configuration, and can be injected via the entry-point-ref
 * attribute of the <http> element. Keeping in mind that this functionality
 * doesn’t make sense in the context of the RESTful web service, the new custom
 * entry point is defined:
 * 
 */
@Component("restAuthenticationEntryPoint")
public final class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
			throws IOException {
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
	}
}