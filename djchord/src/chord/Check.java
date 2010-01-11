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

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;

/**
 *
 * @author Ntanasis Periklis and Chatzipetros Mike
 */
public class Check implements Runnable{

    private Thread runner;
    private RemoteNode node;
    private boolean stabilize = false, fixfingers = false;

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
                node.stabilize();
            }
            else if(fixfingers)
            {
                node.fixFingers();
                node.fixAllFingers();
            }
            else
            {
                while(true)
                {
                    node.stabilize();
                    node.fixFingers();
                    node.fixAllFingers();
                    Thread.sleep(60000); // 1 min
                }
            }
        }
        catch (RemoteException ex)
        {
            throw new RuntimeException(ex);
        }
        catch (InterruptedException ex)
        {

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

    public void startFixFIngers()
    {
        fixfingers = true;
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
        for( int i=0;i<3;i++)
        {
            try
            {
                node.getSuccessor(i).hasFailed();
            }
            catch (NoSuchObjectException e)
            {
                if(i==0)
                {
                    node.setSuccessor(0,node.getSuccessor(1));
                    node.setSuccessor(1,node.getSuccessor(2));
                    node.setSuccessor(2,node.getSuccessor(2).getSuccessor());
                    node.getSuccessor().setPredecessor(node);
                    node.getPredecessor().getPredecessor().stabilize();
                    node.getPredecessor().stabilize();
                }
                else if(i==1)
                {
                    node.setSuccessor(1,node.getSuccessor(2));
                    node.setSuccessor(2,node.getSuccessor(2).getSuccessor());
                    node.getSuccessor(1).setPredecessor(node.getSuccessor());
                    node.getSuccessor().stabilize();
                    node.getPredecessor().stabilize();

                }
                else
                {
                    node.setSuccessor(2,node.getSuccessor().getSuccessor(1));
                    node.getSuccessor(0).stabilize();
                    node.getSuccessor(1).stabilize();
                }
            }
        }

    }
    
    public void fixAllFingers() throws RemoteException
    {
        for(RemoteNode tempnode=node.getSuccessor();tempnode.equals(node);tempnode=tempnode.getSuccessor())
        {
            tempnode.fixFingers();
        }
    }

    synchronized boolean isFree()
    {
        return true;
    }

}