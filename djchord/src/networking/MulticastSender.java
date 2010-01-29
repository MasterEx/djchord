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

import chord.Node;
import exceptions.NotInitializedVariablesException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This class contains all the methods for sending multicasts. It is used as a separate thread.
 *@author Ntanasis Periklis and Chatzipetros Mike
 */
public class MulticastSender extends Multicast implements Runnable{

    private int ttl = 1; // time to live
    private byte[] buffer; //the message tha we will send
    private Thread runner; //the thread that we will use
    private Node node; //the current node

    /**
     * Default constructor.
     */
    public MulticastSender()
    {
        super();
    }

    /**
     *
     * @param port Port used for multicast.
     * @param group D class address.
     * @param buffer The byte array that we will transmit.
     * @param node The node that transmits the multicast.
     */
    public MulticastSender(int port,String group,byte buffer[],Node node)
    {
        super(port,group);
        this.buffer = buffer;
        this.node = node;
    }

    /**
     * Set time to live.
     * @param ttl Time To Live.
     */
    public void setttl(int ttl)
    {
        this.ttl = ttl;
    }

    /**
     * The method that starts the transmission.
     * @param buffer The message.
     * @throws UnknownHostException
     * @throws IOException
     */
    public void send(byte buffer[]) throws UnknownHostException, IOException
    {
        DatagramPacket packet = new DatagramPacket(buffer,buffer.length,
                InetAddress.getByName(group),port);
        this.socket.setTimeToLive(this.ttl);
        this.socket.send(packet);
    }

    /**
     * Sends an empty DatagramPacket.
     * @throws UnknownHostException
     * @throws IOException
     */
    public void send() throws UnknownHostException, IOException
    {
        DatagramPacket packet = new DatagramPacket(this.buffer,this.buffer.length,
                InetAddress.getByName(group),port);
        this.socket.setTimeToLive(this.ttl);
        this.socket.send(packet);
    }

    /**
     * Is invoked by start().
     */
    public void run()
    {
        try
        {
            openconnection();
            send();
            IncomingNodeMulticastAnswer answer = new IncomingNodeMulticastAnswer();
            answer.setNode(node);
            answer.start();
            try
            {
                answer.returnThread().join();
            }
            catch (InterruptedException ex)
            {
                basic.Logger.war("IncomingNodeMulticastAnswer was unable to be terminated");
            }
            closeconnection();
            node.notified();
            synchronized(this)
            {
                this.notifyAll();
            }
        }
        catch (NotInitializedVariablesException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
        catch (UnknownHostException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
        catch (IOException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
    }

    /**
     * Starts the execution of the thread.
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

    /**
     * Stops the execution of the thread.
     */
    public void stop()
    {
        try
        {
            closeconnection();
        }
        catch (UnknownHostException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
        catch (IOException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
        catch (NotInitializedVariablesException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
        runner.interrupt();
        runner = null;
    }

    /**
     * Set value to buffer.
     * @param buffer The message if it's not set in the costructor.
     */
    public void setbuffer(byte buffer[])
    {
        this.buffer = buffer;
    }

    /**
     *
     * @return Returns this thread in case of join
     */
    public Thread getThread()
    {
        return this.runner;
    }

}
