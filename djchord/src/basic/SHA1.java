/*
 * Ntanasis Periklis - A.M.:3070130
 * Chatzipetros Mike - A.M.:3070175
 *
 * The original class was found here:
 * http://www.anyexample.com/programming/java/java_simple_class_to_compute_sha_1_hash.xml
 * For further informations about the operators look here:
 * http://java.sun.com/docs/books/tutorial/java/nutsandbolts/op3.html
 *
 */

package basic;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class was originally found here : <a href='http://www.anyexample.com/programming/java/java_simple_class_to_compute_sha_1_hash.xml'>here</a> .
 * It was modified by us and convertToHex method is used in SHAhash class.
 *
 */
public class SHA1 {   

    /**
     * Static method that returns a SHAhash object.
     * @param text Any text (string).
     * @return A SHAhash object.
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static SHAhash getHash(String text)
    throws NoSuchAlgorithmException, UnsupportedEncodingException  {
    MessageDigest md;
    md = MessageDigest.getInstance("SHA-1");
    md.update(text.getBytes("iso-8859-1"), 0, text.length());
    return new SHAhash(md.digest());
    }
}
