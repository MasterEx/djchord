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
import chord.RemoteNode;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *@author Ntanasis Periklis and Chatzipetros Mike
 */
public class IncomingNodeMulticastAnswer implements Runnable{

    private Thread runner;
    private int port = 1100; // standar port for incoming socket connextions
    private Socket socket;
    private ServerSocket serversocket;
    private Node node;
    private RemoteNode successor;
    private byte[] buffer = new byte[20];
    private boolean flag = false;

    /*
     * is invoked by start()
     */
    synchronized public void run()
    {
        String responders_pid = null, responders_address = null, pid = null;
        try
        {
            System.out.println("Socket opened<--------------");
            serversocket = new ServerSocket(port);
            serversocket.setSoTimeout(5000);// 5 sec
            socket = serversocket.accept();// race condition may occur
            Scanner in = new Scanner(socket.getInputStream());
            pid = in.next();
            String address = in.next();
            responders_pid = in.next();
            responders_address = in.next();
            successor = RMIRegistry.getRemoteNode(address, pid);
            //successor.setKey(new SHAhash(buffer));
            node.setSuccessor(successor);
            node.setPredecessor(successor.getPredecessor());
            node.getPredecessor().setSuccessor(node.getNode());
            successor.setPredecessor(node.getNode());
            if(successor.getSuccessor().getPid().equalsIgnoreCase(successor.getPid()))
            {
                successor.setSuccessor(node.getNode());
            }
            //here we set this node First in chord if it is
            if(successor.isFirst() && node.getKey().compareTo(successor.getKey())<0)
            {
                successor.unsetFirst();
                node.setFirst();
            }
            node.initSuccessors();
            node.fixFingers();
            //node.fixAllFingers();

            in.close();
            socket.close();
            serversocket.close();
            System.out.println("Socket closed<--------------");
        }
        catch (NotBoundException ex)
        {
            try
            {
                RemoteNode responder = RMIRegistry.getRemoteNode(responders_address, responders_pid);
                try
                {
                    System.out.println("Successor not found!");
                    responder.stabilize();
                    responder.fixAllFingers();
                    MulticastSender multicast = new MulticastSender(1101, "224.1.1.1", node.getPid().getBytes(), node);
                    multicast.send();
                }
                catch (RemoteException ex1)
                {
                    Logger.getLogger(IncomingNodeMulticastAnswer.class.getName()).log(Level.SEVERE, null, ex1);
                }
                catch (UnknownHostException ex1)
                {
                    Logger.getLogger(IncomingNodeMulticastAnswer.class.getName()).log(Level.SEVERE, null, ex1);
                }
                catch (UnsupportedEncodingException ex1)
                {
                    Logger.getLogger(IncomingNodeMulticastAnswer.class.getName()).log(Level.SEVERE, null, ex1);
                }
                catch (IOException ex1)
                {
                    Logger.getLogger(IncomingNodeMulticastAnswer.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            catch (NotBoundException ex1)
            {
                Logger.getLogger(IncomingNodeMulticastAnswer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(IncomingNodeMulticastAnswer.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SocketTimeoutException ex)
        {
            try
            {
                flag = true;
                serversocket.close();
                System.out.println("Socket closed<--------------");
            }
            catch (IOException ex1)
            {
                Logger.getLogger(IncomingNodeMulticastAnswer.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     * starts the execution of the thread
     */
    synchronized public void start()
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
            socket.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        runner.interrupt();
        runner = null;
    }

    public void setNode(Node node)
    {
        this.node = node;
    }

    public synchronized boolean isAlone()
    {
        return flag;
    }

    public Thread returnThread()
    {
        return this.runner;
    }
}