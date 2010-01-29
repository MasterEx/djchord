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

/**
 * This class contains the methods that
 * start a TCP connection and wait after the multicast call. This class works
 * by creating a new thread.
 *@author Ntanasis Periklis and Chatzipetros Mike
 */
public class IncomingNodeMulticastAnswer implements Runnable{

    private Thread runner;
    private int port = 1100; // standar port for incoming socket connextions
    private Socket socket;
    private ServerSocket serversocket;
    private Node node;
    private RemoteNode successor;
    private boolean flag = false;

    /**
     * Is invoked by start().
     */
    synchronized public void run()
    {
        String responders_pid = null, responders_address = null, pid = null;
        try
        {
            serversocket = new ServerSocket(port);
            serversocket.setSoTimeout(5000);// 5 sec
            socket = serversocket.accept();// race condition may occur
            Scanner in = new Scanner(socket.getInputStream());
            pid = in.next();
            String address = in.next();
            responders_pid = in.next();
            responders_address = in.next();
            successor = RMIRegistry.getRemoteNode(address, pid);
            node.setSuccessor(successor);
            node.setPredecessor(successor.getPredecessor());
            node.getPredecessor().setSuccessor(node.getNode());
            successor.setPredecessor(node.getNode());
            if(successor.getSuccessor().getPid().equalsIgnoreCase(successor.getPid()))
            {
                successor.setSuccessor(node.getNode());
            }
            node.initSuccessors();
            node.getPredecessor().initSuccessors();
            if(!node.getPredecessor().getPredecessor().getPid().equalsIgnoreCase(node.getPid()))
            {
                node.getPredecessor().getPredecessor().initSuccessors();
            }
            node.fixFingers();

            in.close();
            socket.close();
            serversocket.close();
        }
        catch (NotBoundException ex)
        {
            try
            {
                RemoteNode responder = RMIRegistry.getRemoteNode(responders_address, responders_pid);
                try
                {
                    basic.Logger.war("Successor not found!");
                    responder.stabilize();
                    responder.fixFingers();
                    MulticastSender multicast = new MulticastSender(1101, "224.1.1.1", node.getPid().getBytes(), node);
                    multicast.send();
                }
                catch (RemoteException ex1)
                {
                    basic.Logger.err(ex1.getMessage());
                }
                catch (UnknownHostException ex1)
                {
                    basic.Logger.err(ex1.getMessage());
                }
                catch (UnsupportedEncodingException ex1)
                {
                    basic.Logger.err(ex1.getMessage());
                }
                catch (IOException ex1)
                {
                    basic.Logger.err(ex1.getMessage());
                }
            }
            catch (NotBoundException ex1)
            {
                basic.Logger.err(ex1.getMessage());
            }
        }
        catch (UnsupportedEncodingException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
        catch (SocketTimeoutException ex)
        {
            try
            {
                flag = true;
                serversocket.close();
            }
            catch (IOException ex1)
            {
                basic.Logger.err(ex1.getMessage());
            }
        }
        catch (IOException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
    }

    /**
     * Starts the execution of the thread.
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

    /**
     * Stops the execution of the thread.
     */
    public void stop()
    {
        try
        {
            socket.close();
        }
        catch (IOException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
        runner.interrupt();
        runner = null;
    }

    /**
     * Sets the node if it wasn't set in the constructor.
     * @param node The node who just did multicast.
     */
    public void setNode(Node node)
    {
        this.node = node;
    }

    /**
     * Returns true if nobody have answered.
     * @return True or False.
     */
    public synchronized boolean isAlone()
    {
        return flag;
    }

    /**
     * Returns this thread in case of join.
     * @return This thread.
     */
    public Thread returnThread()
    {
        return this.runner;
    }
}