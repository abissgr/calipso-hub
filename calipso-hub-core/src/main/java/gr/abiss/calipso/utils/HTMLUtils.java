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
