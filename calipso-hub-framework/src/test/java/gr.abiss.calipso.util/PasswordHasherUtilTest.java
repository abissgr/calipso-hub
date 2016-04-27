package gr.abiss.calipso.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author emil.bostijancic@novofina.com
 */
public class PasswordHasherUtilTest {
    private static final String PLAIN_TEXT_PASSWORD = "some_password";

    @Test
    public void shouldHashPassword() {
        final String hashed = PasswordHasher.hashPassword(PLAIN_TEXT_PASSWORD);

        Assert.assertNotNull(hashed);
        Assert.assertNotEquals(hashed, PLAIN_TEXT_PASSWORD);
    }

    @Test
    public void shouldCheckHashedPassword() {
        final String hashed = PasswordHasher.hashPassword(PLAIN_TEXT_PASSWORD);

        Assert.assertTrue(PasswordHasher.checkPassword(PLAIN_TEXT_PASSWORD, hashed));
        Assert.assertFalse(PasswordHasher.checkPassword(hashed, hashed));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnInvalidHashedValue() {
        PasswordHasher.checkPassword(PLAIN_TEXT_PASSWORD, PLAIN_TEXT_PASSWORD);
    }
}
