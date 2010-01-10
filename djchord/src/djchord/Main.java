/**
 * Ntanasis Periklis - A.M.:3070130
 * Chatzipetros Mike - A.M.:3070175
 *
 * check LICENSE.txt in the parent directory
 *
 * The MIT License
 *
 * Copyright (c) 2009 Ntanasis Periklis and Chatzipetors Mike
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package djchord;

import basic.SHA1;
import basic.SHAhash;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
/**
 *
 *@author Ntanasis Periklis and Chatzipetros Mike
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws RemoteException, NoSuchAlgorithmException, UnsupportedEncodingException, Exception
    {
        if(args.length>0)
        {
            Arguments.handler(args);
        }
        //test code goes here!
        //SHA1.getHash("123456789");
        //System.out.println(SHA1.getHash("12346789").getStringHash()+"\n"+SHA1.getHash("223456789").getStringHash());
        //System.out.println(SHAhash.add(SHA1.getHash("123456789").getStringHash() ,SHA1.getHash("223456789").getStringHash()));
        //System.out.println(SHAhash.add("a7ae3441" ,"7f5220ae"));
        if(SHAhash.add(SHA1.getHash("12346789").getStringHash() ,SHA1.getHash("223456789").getStringHash()).length()>40)
        {

            System.out.println(" "+SHAhash.add(SHA1.getHash("12346789").getStringHash() ,SHA1.getHash("223456789").getStringHash())+"\n- ffffffffffffffffffffffffffffffffffffffff");
            System.out.println(" "+SHAhash.subtract(SHAhash.add(SHA1.getHash("12346789").getStringHash() ,SHA1.getHash("223456789").getStringHash()),"ffffffffffffffffffffffffffffffffffffffff"));
        }
        //System.out.println(SHAhash.add(SHA1.getHash("12346789").getStringHash() ,SHA1.getHash("223456789").getStringHash()));
        //http://www.wolframalpha.com/input/?i=0xfad+%2B+0xfad
        System.out.println();
    }
}