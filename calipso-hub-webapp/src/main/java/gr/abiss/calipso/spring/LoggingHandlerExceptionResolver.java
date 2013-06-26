package gr.abiss.calipso.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * Custom HandlerExceptionResolver that actually prints the stacktrace VS
 * spring's default behavior of
 * swallowing it
 * 
 * @author manos
 * 
 */
public class LoggingHandlerExceptionResolver implements HandlerExceptionResolver, Ordered {
	@Override
	public int getOrder() {
		return Integer.MIN_VALUE; // we're first in line, yay!
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest aReq, HttpServletResponse aRes, Object aHandler, Exception anExc) {
		anExc.printStackTrace(); // again, you can do better than this ;)
		return null; // trigger other HandlerExceptionResolver's
	}
}