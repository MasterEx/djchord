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
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *@author Ntanasis Periklis and Chatzipetros Mike
 */
public class PacketHandling implements Runnable{

    private Thread runner;
    private DatagramPacket packet;
    private String pid;
    private Node node;
    private RemoteNode successor;
    private SHAhash sha1;

    /*
     * is invoked by start()
     */
    public void run()
    {
        //here we will learn next nodes ip and name
        pid = new String(this.packet.getData());
        try 
        {
            sha1 = SHA1.getHash(pid.trim());
        } 
        catch (NoSuchAlgorithmException ex) 
        {
            Logger.getLogger(PacketHandling.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (UnsupportedEncodingException ex) 
        {
            Logger.getLogger(PacketHandling.class.getName()).log(Level.SEVERE, null, ex);
        }
        Socket socket = null;
        PrintWriter outstream = null;
        try
        {
            successor = this.node.find_successor(sha1);

            //socket.bind(new Socket(responders_address));
            socket = new Socket(packet.getAddress(),1100);
            if (socket.getChannel()!=null)
            {
                throw new IOException();
            }
            //socket.setSoTimeout(6000); // 6 sec
            outstream = new PrintWriter(socket.getOutputStream());
            System.out.println("The successor of "+pid.trim()+" is "+successor.getPid());
            outstream.write(successor.getRMIInfo()+" "+node.getRMIInfo());
        }
        catch (RemoteException ex)
        {
            System.err.println("successor is dead");
        }
        catch (UnknownHostException ex)
        {
            System.err.println("The IP address of the host could not be determined.");
        }
        catch (IOException ex)
        {
            System.err.println("Sorry, but somenone else was quicker...Better luck next time! ;)");
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
                Logger.getLogger(PacketHandling.class.getName()).log(Level.SEVERE, null, ex);
            }
            runner.interrupt();
        }
    }

    /*
     * constructor
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
