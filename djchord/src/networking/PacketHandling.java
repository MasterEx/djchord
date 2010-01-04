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
    public void run() {
        //here we will learn next nodes ip and name
        pid = new String(this.packet.getData());
        try 
        {
            sha1 = SHA1.getHash(pid);
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
        OutputStream outstream = null;
        try
        {
            successor = this.node.simple_find_successor(sha1);
            socket = new Socket(packet.getAddress(),1100);
            outstream = socket.getOutputStream();
            outstream.write(successor.getKey().getByteHash());
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (UnknownHostException ex)
        {
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            try
            {
                outstream.close();
                socket.close();
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
