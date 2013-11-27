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
