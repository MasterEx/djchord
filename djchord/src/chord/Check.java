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

import basic.SHAhash;
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
    private boolean stabilize = false, fixfingers = false, findfirst = false;

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
            else if(fixfingers)
            {
                node.fixFingers();
                //this.fixAllFingers();
            }
            else if(findfirst)
            {
                this.findFirst();
            }
            else
            {
                while(true)
                {
                    this.stabilize();
                    node.fixFingers();
                    //this.fixAllFingers();
                    this.findFirst();
                    node.sendFiles2ResponsibleNode();
                    Thread.sleep(60000); // 1 min
                }
            }
        }
        catch (RemoteException ex)
        {

            Logger.getLogger(Check.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(Check.class.getName()).log(Level.SEVERE, null, ex);
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

    public void startFindFirst()
    {
        findfirst = true;
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
        System.out.println("stabilizing...");
        RemoteNode pred=null,pred_succ0=null,pred_succ1=null,pred_succ2=null,succ=null,succ1=null,succ2=null;
        try 
        {
            pred = node.getPredecessor();
        }
        catch (RemoteException ex) 
        {
            System.err.println("My predecessor has failed");
        }
        try 
        {
            pred_succ0 = pred.getSuccessor(0);
        }
        catch (RemoteException ex) 
        {
            System.err.println("My predecessor's 1st successor has failed");
        }
        try 
        {
            pred_succ1 = pred.getSuccessor(1);
        }
        catch (RemoteException ex)
        {
            System.err.println("My predecessor's 2nd successor has failed");
        }
        try 
        {
            pred_succ2 = pred.getSuccessor(2);
        }
        catch (RemoteException ex) 
        {
            System.err.println("My predecessor's 3rd successor has failed");
        }
        try 
        {
            succ = node.getSuccessor(0);
        }
        catch (RemoteException ex) 
        {
            System.err.println("My successor has failed");
        }
        try
        {
            succ1 = node.getSuccessor(1);
        }
        catch (RemoteException ex)
        {
            System.err.println("My 2nd successor has failed");
        }
        try
        {
            succ2 = node.getSuccessor(2);
        }
        catch (RemoteException ex)
        {
            System.err.println("My 3rd successor has failed");
        }
        if(!pred_succ0.getPid().equalsIgnoreCase(node.getPid()))
        {
            pred.setSuccessor(node);
        }
        if(!pred_succ1.getPid().equalsIgnoreCase(succ.getPid()))
        {
            pred.setSuccessor(1,succ);
        }
        if(!pred_succ2.getPid().equalsIgnoreCase(succ1.getPid()))
        {
            pred.setSuccessor(2,succ1);
        }
        if(!succ1.getPid().equalsIgnoreCase(succ.getSuccessor().getPid()))
        {
            node.setSuccessor(1,succ.getSuccessor());
        }
        if(!succ2.getPid().equalsIgnoreCase(succ1.getSuccessor().getPid()))
        {
            node.setSuccessor(2,succ1.getSuccessor());
        }
        for( int i=0;i<3;i++)
        {
            try
            {
                node.getSuccessor(i).hasFailed();
            }
            catch (RemoteException e)
            {
                System.err.println("Successor "+i+" failed");
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
        System.out.println("ended stabilizing.");
    }
    
    public void fixAllFingers() throws RemoteException
    {
        /*for(RemoteNode tempnode=node.getSuccessor();!tempnode.getPid().equalsIgnoreCase(node.getPid());tempnode=tempnode.getSuccessor())
        {
        tempnode.fixFingers();
        }*/
        //node.sendFiles2ResponsibleNode();
    }

    public void findFirst() throws RemoteException
    {
        RemoteNode first = node.simple_find_successor((new SHAhash("0000000000000000000000000000000000000000")));
        if(!first.isFirst())
        {
            first.setFirst();
        }
        if(node.isFirst() && !node.getPid().equalsIgnoreCase(first.getPid()))
        {
            node.unsetFirst();
        }
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