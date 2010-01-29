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

import basic.SHA1;
import basic.SHAhash;
import chord.Node;
import chord.RemoteNode;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class contains the methods that handle the incoming multicast. When a new node
 * is added to chord it transmits multicast and this method handles the incoming
 * multicast.
 *@author Ntanasis Periklis and Chatzipetros Mike
 */
public class PacketHandling implements Runnable{

    private Thread runner;
    private DatagramPacket packet;
    private String pid;
    private Node node;
    private RemoteNode successor;
    private SHAhash sha1;

    /**
     * Is invoked by start().
     */
    public void run()
    {
        //here we will learn next nodes' ip and name
        pid = new String(this.packet.getData());
        try 
        {
            sha1 = SHA1.getHash(pid.trim());
        } 
        catch (NoSuchAlgorithmException ex) 
        {
            basic.Logger.err(ex.getMessage());
        } 
        catch (UnsupportedEncodingException ex) 
        {
            basic.Logger.err(ex.getMessage());
        }
        Socket socket = null;
        OutputStream outstream = null;
        try
        {
            successor = this.node.find_successor(sha1);
            try
            {
                Thread.sleep(2000); //sleep for 2 secs
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(PacketHandling.class.getName()).log(Level.SEVERE, null, ex);
            }
            socket = new Socket(packet.getAddress(),1100);
            outstream = socket.getOutputStream();
            basic.Logger.inf("The successor of "+pid.trim()+" is "+successor.getPid());
            outstream.write(new String(successor.getRMIInfo()+" "+node.getRMIInfo()).getBytes());

        }
        catch (RemoteException ex)
        {
            basic.Logger.war("successor is dead");
        }
        catch (UnknownHostException ex)
        {
            basic.Logger.err("The IP address of the host could not be determined.");
        }
        catch (IOException ex)
        {
            basic.Logger.war("Sorry, but somenone else was quicker...Better luck next time! ;)");
        }
        finally
        {
            try
            {
                outstream.close();
                socket.close();
            }
            catch (NullPointerException ex)
            {
                
            }
            catch (IOException ex)
            {
                basic.Logger.err(ex.getMessage());
            }
        }
    }

    /**
     * 
     * @param packet The received DatagramPacket.
     * @param node The node that received it.
     */
    PacketHandling(DatagramPacket packet,Node node)
    {
        this.node = node;
        this.packet = packet;
        if(runner == null)
        {
            runner = new Thread(this);
            runner.setDaemon(true);
            runner.start();
        }
    }

}
