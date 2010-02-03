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

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class contains the methods of the periodical check and fix of chord.
 * @author Ntanasis Periklis and Chatzipetros Mike
 */
public class Check implements Runnable{

    private Thread runner;
    private Node node;
    private boolean stabilize = false,stop = false;

    /**
     *
     * @param node A RemoteNode object.
     */
    public Check(Node node)
    {
        this.node = node;
    }

    /**
     * The standar run method.
     */
    synchronized public void run()
    {
        try
        {
            if(stabilize)
            {
                this.stabilize();
            }
            else
            {
                while(true)
                {
                    if(stop)
                    {
                        break;
                    }
                    if(node.getPort(2995))
                    {
                        for(int i=0;i<2900;i++)
                        {
                            node.unsetPortBusy(i);
                        }
                        new Thread(new Runnable()
                        {
                            public void run()
                            {
                                try
                                {
                                    Thread.sleep(60000);
                                }
                                catch (InterruptedException ex)
                                {
                                    Logger.getLogger(Check.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                try
                                {
                                    node.unsetPortBusy(2995);
                                }
                                catch (RemoteException ex)
                                {
                                    Logger.getLogger(Check.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }).start();
                    }
                    this.stabilize();
                    Thread.sleep(20000); // 20 sec
                }
            }
        }
        catch (RemoteException ex)
        {
            runner = null;
            this.start();
            basic.Logger.err(ex.getMessage());
        }
        catch (InterruptedException ex)
        {
            basic.Logger.err(ex.getMessage());
        }

    }

    /**
     * Starts the periodical execution of the thread.
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

    /**
     * Starts a tread that onlly runs once the stabilize.
     */
    public void startStabilize()
    {
        stabilize = true;
        if (runner==null)
        {
            runner = new Thread(this);
            runner.setDaemon(true);
            runner.start();
        }
    }

    /**
     * stops the execution of the thread
     */
    public void stop()
    {
        stop = true;
    }

    /**
     * The stabilize method xhexks and fixes the next 3 successors of this node.
     * @throws RemoteException
     */
    public void stabilize() throws RemoteException
    {
        basic.Logger.inf("stabilizing...");
        try
        {
           node.getSuccessor().hasFailed(); 
        }
        catch(RemoteException e)
        {
            basic.Logger.war("My successor has failed :(");
            node.setSuccessor(node.getSuccessor(1));
            node.setSuccessor(1,node.getSuccessor(0).getSuccessor());
            node.setSuccessor(2,node.getSuccessor(1).getSuccessor());
            node.getSuccessor().setPredecessor(node.getNode());
            networking.MulticastSender sendmulticast = new networking.MulticastSender(1101, "224.1.1.1", ("fix "+node.getPid()).getBytes(), this.node);
            sendmulticast.start();
        }
        try
        {
           node.getSuccessor(1).hasFailed(); 
        }
        catch(RemoteException e)
        {
            basic.Logger.war("My 2nd successor has failed :(");
            node.setSuccessor(1,node.getSuccessor(2));
            node.setSuccessor(2,node.getSuccessor(1).getSuccessor());
            node.getSuccessor(1).setPredecessor(node.getSuccessor());
            networking.MulticastSender sendmulticast = new networking.MulticastSender(1101, "224.1.1.1", ("fix "+node.getPid()).getBytes(), node);
            sendmulticast.start();
        }
        try
        {
           node.getSuccessor(2).hasFailed(); 
        }
        catch(RemoteException e)
        {
            basic.Logger.war("My 3rd successor has failed :(");
            try
            {
                node.setSuccessor(2,node.getSuccessor(1).getSuccessor());
            }
            catch(RemoteException ex)
            {
                node.setSuccessor(2,node.getSuccessor(1).getSuccessor(1));
            }
            node.getSuccessor(2).setPredecessor(node.getSuccessor(1));
            networking.MulticastSender sendmulticast = new networking.MulticastSender(1101, "224.1.1.1", ("fix "+node.getPid()).getBytes(), node);
            sendmulticast.start();
        }
        basic.Logger.inf("ended stabilizing.");
    }

    /**
     * Returns the current status of the thread.
     * @return True if the thread had stoped it's execution.
     */
    synchronized boolean isFree()
    {
        return true;
    }

    /**
     * Returns this thread in case we want to join it.
     * @return This thread.
     */
    public Thread getThread()
    {
        return runner;
    }

}