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
 * You should have received a copy of the GNU General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
package gr.abiss.calipso.utils;

import java.io.InputStream;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(HTMLUtils.class);

	private static final String HTML_INPUT_POLICY_FILE_LOCATION = "antisamy-calipso.xml";

	private static Policy policy = null;
	static {
		try {
			InputStream policyStream = HTMLUtils.class.getClassLoader()
					.getResourceAsStream(HTML_INPUT_POLICY_FILE_LOCATION);
			policy = Policy.getInstance(policyStream);
		} catch (PolicyException e) {
			LOGGER.error("Failed creating OWASP AntiSamy policy", e);
		}
	}

	public static String sanitize(String html) {
		AntiSamy as = new AntiSamy(); // Create AntiSamy object
		String newHtml = null;
		try {
			CleanResults markupResults = as.scan(html, policy);
			newHtml = markupResults.getCleanHTML();
			//LOGGER.info("Cleaned old HTML: " + oldHtml + ", new HTML: " + comment.getMarkup());
		} catch (Exception e) {
			LOGGER.error("Failed sanitizing markup", e);
			throw new RuntimeException(e);
		}
		return newHtml;
	}
}
