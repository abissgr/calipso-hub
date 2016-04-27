package gr.abiss.calipso.util;

import com.lambdaworks.crypto.SCryptUtil;

/**
 * @author emil.bostijancic@novofina.com
 */
public class PasswordHasher {

    /**
     * Method will hash a password using scrypt.
     * @param password Plain text password.
     * @return scrypt hashed password.
     */
    public static String hashPassword(String password) {
        return SCryptUtil.scrypt(password, 16384, 3, 1);
    }

    public static boolean checkPassword(String plainTextPassword, String hashed) {
        return SCryptUtil.check(plainTextPassword, hashed);
    }
}
