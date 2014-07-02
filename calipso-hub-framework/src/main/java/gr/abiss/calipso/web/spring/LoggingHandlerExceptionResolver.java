/**
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
package gr.abiss.calipso.web.spring;

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