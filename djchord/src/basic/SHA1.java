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

public class SHA1 {   

    public SHAhash SHA1(String text,SHAhash output)
    throws NoSuchAlgorithmException, UnsupportedEncodingException  {
    MessageDigest md;
    md = MessageDigest.getInstance("SHA-1");
    md.update(text.getBytes("iso-8859-1"), 0, text.length());
    output = new SHAhash(md.digest());
    return output;
    }
}
