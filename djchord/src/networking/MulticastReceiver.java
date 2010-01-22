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
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *@author Ntanasis Periklis and Chatzipetros Mike
 */
public class MulticastReceiver extends Multicast implements Runnable{

    private Thread runner;
    private boolean run = true; //terminates the loop
    private Node node;

    /*
     * constructor
     */
    public MulticastReceiver(Node node)
    {
        this.node = node;
    }

    /*
     * constructor
     */
    public MulticastReceiver(int port,Node node)
    {
        super(port);
        this.node = node;
    }

    /*
     *constructor
     */
    public MulticastReceiver(String group,Node node)
    {
        super(group);
        this.node = node;
    }

    /*
     *constructor
     */
    public MulticastReceiver(int port,String group,Node node)
    {
        super(port,group);
        this.node = node;
    }

    /*
     *binds port to socket and joins the mullticast group
     */
    @Override
    public void openconnection()
    {
        try 
        {
            if (group.equalsIgnoreCase("") || port == -1) {
                throw (new NotInitializedVariablesException(this.getClass() + ": " +
                        "NotInitializedVariablesException:\n port or group address " +
                        "are not initialized"));
            }

            socket = new MulticastSocket(port);
            socket.joinGroup(InetAddress.getByName(group));
        } 
        catch (NotInitializedVariablesException ex) 
        {
            System.err.println(ex.getMessage());
        } 
        catch (IOException ex) 
        {
            if (socket==null) 
            {
                System.err.println("I/O exception occurred while creating the MulticastSocket " + ex);
            } 
            else 
            {
                System.err.println("there is an error joining or when the address is not a multicast address "+ex);
            }
        }
    }

    /*
     *closes the open connnections
     */
    @Override
    public void closeconnection()
    {
        try 
        {
            if(socket == null)
            {
                throw (new NotInitializedVariablesException(this.getClass() + " : " +
                        "NotInitializedVariablesException:\n socket is not initialized"));
            }
            socket.leaveGroup(InetAddress.getByName(group));
            socket.close();
        }
        catch (NotInitializedVariablesException ex) 
        {
            System.err.println(ex.getMessage());
        }
        catch (IOException e)
        {
            System.err.println("An error occured while leaving or the address is not a multicast address\n"+e);
        }
    }

    /*
     * receiver that returns the received datagram packet
     */
    public DatagramPacket receive(byte buffer[]) throws IOException
    {
        DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
        this.socket.receive(packet);
        System.out.println("received packet here<-------");
        return packet;
    }

    /*
     * is invoked by start()
     */
    public void run()
    {        
        try
        {
            openconnection();
            System.out.println("Started waiting for multicast calls");
            while(run)
            {
                new PacketHandling(receive(new byte[1024]),this.node);
                System.out.println("received packet");
            }
            System.out.println("closed connection");
            closeconnection();
        }
       /*catch (NotInitializedVariablesException ex)
        {
        Logger.getLogger(MulticastReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        catch (IOException ex)
        {
            Logger.getLogger(MulticastReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     * starts the execution of the thread
     */
    public void start()
    {
        if (runner==null)
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
        closeconnection();
        runner.interrupt();
        runner = null;
    }

    /*
     * Terminates the loop - thread
     */
    public void terminate()
    {
        run = false;
    }

}
