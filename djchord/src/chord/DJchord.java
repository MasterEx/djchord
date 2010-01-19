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
        
package chord;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import networking.MulticastReceiver;
import networking.MulticastSender;

/**
 *
 * @author Ntanasis Periklis and Chatzipetros Mike
 */
public class DJchord implements Runnable {

    private Node node;
    private MulticastSender sendmulticast;
    private Thread runner;

    /*
     * is invoked by start()
     */
    public void run()
    {
        try
        {
            node = new Node();
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(DJchord.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (NoSuchAlgorithmException ex)
        {
            Logger.getLogger(DJchord.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(DJchord.class.getName()).log(Level.SEVERE, null, ex);
        }
        try
        {
            sendmulticast = new MulticastSender(1101, "224.1.1.1", node.getPid().getBytes(), node);
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(DJchord.class.getName()).log(Level.SEVERE, null, ex);
        }
        sendmulticast.start();
        for(;;)
        {

            System.out.println("FTANEI EDW! 2.4");
            synchronized (sendmulticast)
            {
                System.out.println("FTANEI EDW! 2.5");
                try
                {
                    sendmulticast.wait();
                }
                catch (InterruptedException ex)
                {
                    Logger.getLogger(DJchord.class.getName()).log(Level.SEVERE, null, ex);
                }
                try
                {
                    if (node.isNotified())
                    {
                        System.out.println("FTANEI EDW! 2.6 BGAINEI");
                        break;
                    }
                }
                catch (RemoteException ex1)
                {
                    Logger.getLogger(DJchord.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
            System.out.println("FTANEI EDW! 4");
            try
            {
                node.setSuccessor(1, node.getSuccessor().getSuccessor());
                node.getSuccessor().setSuccessor(1, node.getSuccessor().getSuccessor().getSuccessor());
                node.setSuccessor(2, node.getSuccessor().getSuccessor(1));
                node.sendFiles2ResponsibleNode();
            }
            catch (RemoteException ex)
            {
                Logger.getLogger(DJchord.class.getName()).log(Level.SEVERE, null, ex);
            }
        System.out.println("FTANEI EDW! 5");
        MulticastReceiver receivemulticast = new MulticastReceiver(1101, "224.1.1.1", node);
        receivemulticast.start();
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
        this.killNode();
        sendmulticast.stop();
        runner.interrupt();
        runner = null;
    }

    /**
     * starts receiving files!
     */
    public void getFile(String name)
    {
        this.node.getFile(name);
    }

    /**
     *
     * this method terminetes the node
     */
    public void killNode()
    {
        try
        {
            RemoteNode successor = this.node.getSuccessor();
            this.node.exit();
            successor.stabilize();
            successor.fixFingers();
            successor.fixAllFingers();
        }
        catch (RemoteException remoteException)
        {
            System.out.println("Couldn't exit properly!");
        }
    }

    /**
     * This method return the node successor
     */
    public String getRMIInfo()
    {
        String returnval = "FAIL!!!";
        try
        {          
            returnval = node.getSuccessor().getRMIInfo();
        }
        catch (RemoteException ex)
        {
            System.err.println("The successors seems down... :(");
            try
            {
                node.stabilize();
                node.fixFingers();
                node.fixAllFingers();
            }
            catch (RemoteException remoteException)
            {
                System.out.println("------- HERE -------");
            }
            Logger.getLogger(DJchord.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returnval;
    }
}
