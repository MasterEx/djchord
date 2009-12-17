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

import exceptions.NotInitializedVariablesException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *@author Ntanasis Periklis and Chatzipetros Mike
 */
public class MulticastSender extends Multicast implements Runnable{

    private int ttl = 1; // time to live
    private byte[] buffer; //the message tha we will send
    private Thread runner; //the thread that we will use

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
     *constructor
     */
    MulticastSender(int port,String group,byte buffer[])
    {
        super(port,group);
        this.buffer = buffer;
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

    /*
     * sender
     */
    public void send() throws UnknownHostException, IOException
    {
        DatagramPacket packet = new DatagramPacket(this.buffer,this.buffer.length,
                InetAddress.getByName(group),port);
        this.socket.setTimeToLive(this.ttl);
        this.socket.send(packet);
    }

    /*
     * is invoked by start()
     */
    public void run()
    {
        try
        {
            openconnection();
            send();
            closeconnection();

        }
        catch (NotInitializedVariablesException ex)
        {
                Logger.getLogger(MulticastSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (UnknownHostException ex)
        {
            Logger.getLogger(MulticastSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(MulticastSender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     * starts the execution of the thread
     */
    public void start()
    {
        if (runner == null)
        {
            runner = new Thread(this);
            runner.setDaemon(true);
            runner.start();
        }
    }

    /*
     * stops the execution of the thread
     */
    public void stop()
    {
        try
        {
            closeconnection();
        }
        catch (UnknownHostException ex)
        {
            Logger.getLogger(MulticastSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(MulticastSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (NotInitializedVariablesException ex)
        {
            Logger.getLogger(MulticastSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        runner.interrupt();
        runner = null;
    }

    /*
     * set value to buffer
     */
    public void setbuffer(byte buffer[])
    {
        this.buffer = buffer;
    }

}
