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
 *
 * @author Ntanasis Periklis and Chatzipetros Mike
 */
public class Check implements Runnable{

    private Thread runner;
    private RemoteNode node;
    private boolean stabilize = false;

    public Check(RemoteNode node)
    {
        this.node = node;
    }

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
                    this.stabilize();
                    try
                    {
                        node.fixFingers();
                    }
                    catch(RemoteException ex)
                    {
                        basic.Logger.war("Can't fix fingers right now");
                    }
                    basic.Logger.inf("sending files to responsible node");
                    try
                    {
                        node.sendFiles2ResponsibleNode();
                    }
                    catch(RemoteException ex)
                    {
                        basic.Logger.war("Can't send files right now");
                    }
                    Thread.sleep(40000); // 40 sec
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

    /*
     * starts the periodical execution of the thread
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

    /*
     * stops the execution of the thread
     */
    public void stop()
    {
        runner.interrupt();
        runner = null;
    }

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
        }
        basic.Logger.inf("ended stabilizing.");
    }

    synchronized boolean isFree()
    {
        return true;
    }

    public Thread getThread()
    {
        return runner;
    }

}