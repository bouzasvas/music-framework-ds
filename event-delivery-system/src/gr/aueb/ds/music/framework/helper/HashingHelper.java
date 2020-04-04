package gr.aueb.ds.music.framework.helper;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingHelper {

    public static final String MD5_ALGORITHM = "MD5";

    public static BigInteger hashText(String text) {
        BigInteger hashedNo;
        try {
            MessageDigest md = MessageDigest.getInstance(MD5_ALGORITHM);
            byte[] messageDigest = md.digest(text.getBytes());
            hashedNo = new BigInteger(1, messageDigest);
        }
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            LogHelper.errorWithParams(PropertiesHelper.getProperty("hashing.failed"), text);
            throw new RuntimeException(e);
        }

        return hashedNo;
    }
}
