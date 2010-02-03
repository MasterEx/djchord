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

import djchord.GUI;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import networking.MulticastReceiver;
import networking.MulticastSender;

/**
 * This class is a layer between our node and the application.
 * @author Ntanasis Periklis and Chatzipetros Mike
 */
public class DJchord implements Runnable {

    private Node node;
    private MulticastSender sendmulticast;
    private Thread runner;
    private boolean output; // true for system false for gui
    private GUI gui = null;

    /**
     * Is invoked by start()
     */
    public void run()
    {
        try
        {
            node = new Node(this.output,this.gui);
        }
        catch (RemoteException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
        catch (NoSuchAlgorithmException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
        catch (UnsupportedEncodingException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
        try
        {
            sendmulticast = new MulticastSender(1101, "224.1.1.1", node.getPid().getBytes(), node);
        }
        catch (RemoteException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
        sendmulticast.start();
        try
        {
            sendmulticast.getThread().join();
        }
        catch (InterruptedException ex)
        {
            
        }
        try
        {
            node.setSuccessor(1, node.getSuccessor().getSuccessor());
            node.getSuccessor().setSuccessor(1, node.getSuccessor().getSuccessor().getSuccessor());
            node.setSuccessor(2, node.getSuccessor().getSuccessor(1));
        }
        catch (RemoteException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
        try
        {
            node.joinedStabilize();
            node.fixFingers();
            node.sendFiles2ResponsibleNode();
        }
        catch (RemoteException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
        node.startCheck();
        MulticastReceiver receivemulticast = new MulticastReceiver(1101, "224.1.1.1", node);
        receivemulticast.setOutput(output);
        if(!output)
        {
            receivemulticast.setGUI(gui);
        }
        receivemulticast.start();
    }

    /**
     * Starts the execution of the thread
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

    /**
     * Stops the execution of the thread.
     */
    public void stop()
    {
        this.killNode();
        basic.Logger.inf("find_successor has been called "+basic.HopsAndTime.search_counter+" times");
        basic.Logger.inf("Average hops/find_successor: "+basic.HopsAndTime.getAvgHops());
        basic.Logger.inf("Average execution time/find_successor: "+basic.HopsAndTime.getAvgTime());
        sendmulticast.stop();
        runner.interrupt();
        runner = null;
    }

    /**
     * Starts receiving files!
     * @param name A file name.
     */
    public void getFile(String name)
    {
        this.node.getFile(name);
    }

    /**
     *
     * This method terminates the node.
     */
    public void killNode()
    {
        try
        {
            RemoteNode successor = this.node.getSuccessor(), predecessor = this.node.getPredecessor();
            this.node.exit();
            successor.joinedStabilize();
            successor.fixFingers();
            predecessor.joinedStabilize();
            predecessor.fixFingers();
        }
        catch (RemoteException remoteException)
        {
            basic.Logger.war("Couldn't exit properly!");
        }
    }

    /**
     * This method return the node successor.
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
            basic.Logger.war("The successors seems down... :(");
            try
            {
                node.joinedStabilize();
                node.fixFingers();
            }
            catch (RemoteException remoteException)
            {
                basic.Logger.err(remoteException.getMessage());
            }
            basic.Logger.err(ex.getMessage());
        }
        return returnval;
    }

    /**
     * This method prints all the next successors and their hashes.
     */
    public void showAllSuccessors()
    {
        try
        {
            if(output)
            {
                System.out.println("I'm "+node.getRMIInfo()+" with hash:\n"+node.getKey().getStringHash());
            }
            else
            {
                this.gui.append("I'm "+node.getRMIInfo()+" with hash:\n"+node.getKey().getStringHash());
            }
            basic.Logger.inf("I'm "+node.getRMIInfo());
            for(RemoteNode i=node.getSuccessor();!i.getPid().equalsIgnoreCase(node.getPid());i=i.getSuccessor())
            {
                if(output)
                {
                    System.out.println("My next successor is "+i.getRMIInfo()+" with hash:\n"+i.getKey().getStringHash());
                }
                else
                {
                    this.gui.append("My next successor is "+i.getRMIInfo()+" with hash:\n"+i.getKey().getStringHash());
                }
                basic.Logger.inf("My next successor is "+i.getRMIInfo());
            }
            
        }
        catch (RemoteException ex)
        {
            basic.Logger.err("The successors seems down... :(");
            try
            {
                node.joinedStabilize();
                node.fixFingers();
            }
            catch (RemoteException remoteException)
            {
                basic.Logger.err(remoteException.getMessage());
            }
            basic.Logger.err(ex.getMessage());
        }
    }

    /**
     * Constructor that sets the standar ouput.
     * @param output True for System.out , false for gui.
     */
    public DJchord(boolean output)
    {
        this.output = output;
    }

    /**
     * In case we have gui we need to append our messages to it.
     * @param gui Our gui.
     */
    public void setGui(GUI gui)
    {
        this.gui = gui;
    }

    /**
     * Prints all the chord files.
     */
    public void getFiles()
    {
        try
        {
            if(output)
            {
                System.out.println("My files are");
                String[] files = node.getFile_keys();
                for(int i=0;i<files.length;i++)
                {
                    System.out.println(files[i]);
                }
            }
            else
            {
                this.gui.append("My files are");
                String[] files = node.getFile_keys();
                for(int i=0;i<files.length;i++)
                {
                    this.gui.append(files[i]);
                }
            }
            for(RemoteNode i=node.getSuccessor();!i.getPid().equalsIgnoreCase(node.getPid());i=i.getSuccessor())
            {
                String[] files = i.getFile_keys();
                if(output)
                {
                    System.out.println("My next successor files are: ");
                    for(int j=0;j<files.length;j++)
                    {
                        System.out.println(files[j]);
                    }
                }
                else
                {
                    this.gui.append("My next successor files are: ");
                    for(int j=0;j<files.length;j++)
                    {
                        this.gui.append(files[j]);
                    }
                }
            }

        }
        catch (RemoteException ex)
        {
            basic.Logger.err("The successors seems down... :(");
            try
            {
                node.joinedStabilize();
                node.fixFingers();
            }
            catch (RemoteException remoteException)
            {
                basic.Logger.err(remoteException.getMessage());
            }
            basic.Logger.err(ex.getMessage());
        }
    }
}
