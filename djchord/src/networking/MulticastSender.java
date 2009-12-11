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

package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 *@author Ntanasis Periklis and Chatzipetros Mike
 */
public class MulticastSender extends Multicast{

    private int ttl = 1; // time to live

    /*
     *constructor
     */
    MulticastSender()
    {
        super();
    }

    /*
     *constructor
     */
    MulticastSender(int port)
    {
        super(port);
    }

    /*
     *constructor
     */
    MulticastSender(String group)
    {
        super(group);
    }

    /*
     *constructor
     */
    MulticastSender(int port,String group)
    {
        super(port,group);
    }

    /*
     * set time to live
     */
    public void setttl(int ttl)
    {
        this.ttl = ttl;
    }

    /*
     * sender
     */
    public void send(byte buffer[]) throws UnknownHostException, IOException
    {
        DatagramPacket packet = new DatagramPacket(buffer,buffer.length,
                InetAddress.getByName(group),port);
        this.socket.setTimeToLive(this.ttl);
        this.socket.send(packet);
    }

}