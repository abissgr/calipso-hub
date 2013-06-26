package gr.abiss.calipso.utils;

import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class MD5Utils {

	private static final Logger LOGGER = LoggerFactory.getLogger(MD5Utils.class);

	public static String hex(byte[] array) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; ++i) {
			sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString();
	}

	public static String md5Hex(String message) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return hex(md.digest(message.getBytes("UTF-8")));
		} catch (Exception e) {
			LOGGER.error("Failed to create hash for message \"" + message + "\"", e);
		}
		return null;
	}
}

