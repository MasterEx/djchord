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

import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;

/**
 *
 *@author Ntanasis Periklis and Chatzipetros Mike
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException, UnknownHostException {
        //test code goes here!
     
        byte[] buffer = new byte[10];
        buffer = "192.168.1.15 10120".getBytes();
        DatagramPacket packet = new DatagramPacket(buffer,buffer.length,
                InetAddress.getByName("123.1.2.3"),1000);
        System.out.println(new String(packet.getData()));
        String[] s = new String(packet.getData()).split(" ");
        System.out.println("adrr:"+s[0]+" post:"+s[1]);

    }

}