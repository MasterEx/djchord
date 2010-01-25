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

import basic.FileNames;
import basic.SHA1;
import basic.SHAhash;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.rmi.NotBoundException;
import java.security.NoSuchAlgorithmException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import networking.FileReceiver;
import networking.FileSender;
import networking.RMIRegistry;

/**
 *
 * @author Ntanasis Periklis and Chatzipetros Mike
 */
public class Node implements RemoteNode {

    /**
     * variables
     */
    private SHAhash key;
    private String folder,pid;
    private String[] file_keys;
    private RemoteNode[] fingers = new RemoteNode[160],successors = new RemoteNode[3];
    private HashMap<String,RemoteNode> foreignfiles  = new HashMap<String,RemoteNode>();;
    private RemoteNode predecessor;
    private boolean notified = false;
    private Vector<RemoteNode> compressedFingers;
    private RemoteNode thisnode = null;
    private Check check, checkstabilize;
    private volatile boolean[] ports;
    private boolean empty_folder=true;

    /**
     * constructor
     */
    public Node() throws NoSuchAlgorithmException, UnsupportedEncodingException, RemoteException
    {
        RMIRegistry.init();
        pid = this.setPid();
        this.key = SHA1.getHash(pid);
        RMIRegistry.addNode(this,pid);
        try
        {
            thisnode = RMIRegistry.getRemoteNode(this.getAddress(), pid);
        }
        catch (NotBoundException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
        this.setFolder("downloads");
        this.setFile_keys();
        this.setPredecessor(thisnode);
        for(int u=0;u<3;u++)
        {
            this.setSuccessor(u, thisnode);
        }
        check = new Check(thisnode);
        checkstabilize = new Check(thisnode);
        check.start();
        ports = new boolean[3000];
        for(int i=0;i<3000;i++)
        {
            ports[i] = false;
        }
        System.out.println("The node :"+this.getRMIInfo()+" has been created!");
        basic.Logger.inf("The node :"+this.getRMIInfo()+" has been created!");
    }

    /**
     * Here are the Node methods that return and set basic Node attributes.
     */

     // get methods

    public SHAhash getKey() throws RemoteException
    {
        return this.key;
    }

    public String getPid() throws RemoteException
    {
        return pid;
    }

    public RemoteNode getNode() throws RemoteException
    {
        return thisnode;
    }

    public String getAddress()
    {
        return RMIRegistry.getAddress();
    }

    public String getRMIInfo() throws RemoteException
    {
        return this.pid+" "+this.getAddress();
    }

    // set methods

    public String setPid()
    {
        /**
         * the ManagementFactory.getRuntimeMXBean().getName() is JVM dependent
         * and may not always work
         */
        return this.pid = ManagementFactory.getRuntimeMXBean().getName();
    }

    // other important Node methods

    synchronized public void notified() throws RemoteException
    {
        this.notified = true;
    }

    synchronized public boolean isNotified() throws RemoteException
    {
        return notified;
    }

    public void hasFailed() throws RemoteException
    {
        return;
    }

    /**
     * The chord relative methods
     */

    public boolean exit()
    {
        try
        {
            this.joinedStabilize();
            this.getSuccessor().setPredecessor(this.getPredecessor());
            this.getPredecessor().setSuccessor(this.getSuccessor());
            this.getPredecessor().initSuccessors();
            if(!this.getPredecessor().getPredecessor().getPid().equalsIgnoreCase(this.getPid()))
            {
                this.getPredecessor().getPredecessor().initSuccessors();
            }
            if(!this.getPredecessor().getPredecessor().getPredecessor().getPid().equalsIgnoreCase(this.getPid()))
            {
                this.getPredecessor().getPredecessor().getPredecessor().initSuccessors();
            }
            this.removeFilesFromResponsibleNode();
            return true;
        }
        catch (RemoteException e)
        {
            basic.Logger.err("The successor or the predecessor has failed ");
            return false;
        }
    }

    /**
     * calls getSuccessorSuccessorsList and handles the returned array
     */
    
    public void initSuccessors() throws RemoteException
    {
        RemoteNode[] temp;
        temp = this.getSuccessor().getSuccessorSuccessorsList();
        this.setSuccessor(1, temp[0]);
        this.setSuccessor(2, temp[1]);
    }
    
    public RemoteNode[] getSuccessorSuccessorsList() throws RemoteException
    {
        this.setSuccessor(1,this.getSuccessor().getSuccessor());
        this.setSuccessor(2,this.getSuccessor().getSuccessor().getSuccessor());
        return this.successors;
    }

    public RemoteNode find_successor(SHAhash k) throws RemoteException
    {
        Node search = this;
        if (((k.compareTo(search.getKey())>0 && (k.compareTo(search.getSuccessor().getKey())<=0 || search.getKey().compareTo(search.getSuccessor().getKey())>=0))) || (k.compareTo(search.getSuccessor().getKey())<0 && search.getSuccessor().getKey().compareTo(search.getKey())<0))
        {
            return search.getSuccessor();
        }
        else if((k.compareTo(search.getKey())<0 && (k.compareTo(search.getPredecessor().getKey())>0 || search.getKey().compareTo(search.getPredecessor().getKey())<=0)) || (k.compareTo(search.getPredecessor().getKey())>0 && search.getKey().compareTo(search.getPredecessor().getKey())<=0))
        {
            return search;
        }
        else
        {
            return search.closest_preceding_node(k).find_successor(k);
        }
    }
    
    /**
     * this methos finds successors linear by checkinng the successor's successor
     */
    public RemoteNode simple_find_successor(SHAhash k) throws RemoteException
    {
        RemoteNode next = this;
        if(this.getPid().equalsIgnoreCase(this.getSuccessor().getPid()))
        {
            return this;
        }
        else
        {
            do
            {
                if(k.compareTo(next.getKey())>0 && (k.compareTo(next.getSuccessor().getKey())<=0 || next.getKey().compareTo(next.getSuccessor().getKey())>0))
                {
                    return next.getSuccessor();
                }
                else if(k.compareTo(next.getKey())<=0 && (k.compareTo(next.getPredecessor().getKey())>0 || next.getKey().compareTo(next.getPredecessor().getKey())<0))
                {
                    return next;
                }
                next = next.getSuccessor();
            }
            while(!next.getPid().equalsIgnoreCase(this.getPid()));
            return this.getSuccessor();
        }
    }
    
    public RemoteNode closest_preceding_node(SHAhash k) throws RemoteException
    {
        int i;
        if(this.compressedFingers.size()==1)
        {
            return compressedFingers.get(0);
        }
        for(i=this.compressedFingers.size()-1;i>=0;i--)
        {
            if(this.compressedFingers.get(i).getKey().compareTo(k)<0 && this.compressedFingers.get(i).getKey().compareTo(this.getKey())>0)
            {
                return this.compressedFingers.get(i);
            }
        }
        System.out.println("FTANEI EDW ENW DE PREPEI!!!!");
        return this.simple_find_successor(k);// unreachable statement(??)
    }

    public void stabilize() throws RemoteException
    {
        if(checkstabilize.isFree())
        {
            checkstabilize = new Check(this);
            checkstabilize.startStabilize();
        }
    }

    public void joinedStabilize() throws RemoteException
    {
        if(checkstabilize.isFree())
        {
            checkstabilize = new Check(this);
            checkstabilize.startStabilize();
        }
        try
        {
            checkstabilize.getThread().join();
        }
        catch (InterruptedException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
    }

    synchronized public void setSuccessor(RemoteNode next) throws RemoteException
    {
        basic.Logger.inf("Setting at successor[0] "+next.getRMIInfo());
        this.successors[0] = next;
    }

    synchronized public void setSuccessor(int i,RemoteNode next) throws RemoteException
    {
        basic.Logger.inf("Setting at successor["+i+"] "+next.getRMIInfo());
        this.successors[i] = next;
    }

    public void setPredecessor(RemoteNode previous) throws RemoteException
    {
        this.predecessor = previous;
    }

    public RemoteNode getSuccessor() throws RemoteException
    {
        return successors[0];
    }

    public RemoteNode getSuccessor(int i) throws RemoteException
    {
        return successors[i];
    }

    public RemoteNode getPredecessor() throws RemoteException
    {
        return predecessor;
    }

    public void setFingers() throws RemoteException
    {
        SHAhash temp,max = new SHAhash("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
        for(int i=0;i<160;i++)
        {
            temp = new SHAhash(this.key.add(SHAhash.power(Integer.toHexString(2), i)));
            /*
             * if hash is greater than max sha1 hash value it takes a value
             * equal to value-maxValue. Then we return a RemoteNode with
             * find_successor to the finger table
             */
            this.fingers[i] = this.simple_find_successor((temp.compareTo(max)>0)?new SHAhash((SHAhash.subtract(temp.getStringHash(), max.getStringHash())).length()==40?SHAhash.subtract(temp.getStringHash(), max.getStringHash()):(SHAhash.subtract(temp.getStringHash(), max.getStringHash())).substring(1,40)):temp);
        }
    }

    public void compressFingers() throws RemoteException
    {
        compressedFingers = new Vector<RemoteNode>();
        for(int i=0;i<=159;i++)
        {
            if(this.compressedFingers.contains(fingers[i]))
            {
                continue;
            }
            else
            {
                this.compressedFingers.add(fingers[i]);
            }
        }
        compressedFingers.trimToSize();
    }

    public void fixFingers() throws RemoteException
    {
        this.setFingers();
        this.compressFingers();
    }

    /**
     * Here are hte methods about files indexing and move.
     */

    public String getFolder()
    {
        return this.folder;
    }

    public String[] getFile_keys()
    {
        return this.file_keys;
    }

    public boolean getPort(int i) throws RemoteException
    {
        return ports[i];
    }

    public RemoteNode getFileResponsible(String filehash) throws RemoteException
    {
        return this.foreignfiles.get(filehash);
    }

    public void getFile(String filename)
    {
        boolean contin = true;
        RemoteNode responsible = null;
        try
        {
            responsible = this.simple_find_successor(SHA1.getHash(filename)).getFileResponsible(filename);
            if (responsible == null)
            {
                throw new NullPointerException();
            }
        }
        catch (NoSuchAlgorithmException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
        catch (UnsupportedEncodingException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
        catch (RemoteException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
        catch (NullPointerException ex)
        {
            contin = false;
            System.out.println("FILE DOESN'T EXIST!!!");
            basic.Logger.war("FILE DOESN'T EXIST!!!");
        }

        if(contin)
        {
            int port = 0;
            for(int i=0;i<ports.length;i++)
            {
                if(!ports[i])
                {
                    port = 50000+i;
                    break;
                }
            }
            try
            {
                while(!responsible.getAvailablePort(port))
                {
                    port++;
                }
                this.setPortBusy(port);
                FileReceiver receiver = new FileReceiver(port,"remote_files"+File.separator+filename);
                receiver.start();
                responsible.sendFile(port, this.getAddress(), filename);
                try
                {
                    receiver.getThread().join();
                    basic.Logger.inf("File "+filename+" was successfully received");
                }
                catch (InterruptedException ex)
                {
                    basic.Logger.err("File reception was interrupted");
                }
                this.unsetPortBusy(port);
            }
            catch (UnsupportedEncodingException ex)
            {
                basic.Logger.err(ex.getMessage());
            }
            catch (RemoteException ex)
            {
                basic.Logger.err(ex.getMessage());
            }
            catch (IOException ex)
            {
                basic.Logger.err(ex.getMessage());
            }
        }
    }
    public void setKey(SHAhash key) throws RemoteException
    {
         this.key = key;
    }

    public void setFolder(String folder)
    {
         this.folder = folder;
    }

    public void setFile_keys()
    {
        FileNames files = new FileNames(this.folder);
        file_keys = files.getFileNames();
        files = null;
        if(file_keys!=null)
        {
            this.empty_folder = false;
        }
    }

    public void addFile(String filename,RemoteNode node) throws RemoteException
    {
        basic.Logger.inf("Now putting "+filename+" and "+node.getPid()+" in this node");
        foreignfiles.put(filename, node);
    }

    public void rmFile(String filehash) throws RemoteException
    {
        foreignfiles.remove(filehash);
    }
    
    public void sendFiles2ResponsibleNode() throws RemoteException
    {
        this.setFile_keys();
        if(!this.empty_folder)
        {
            RemoteNode remotenode=null;
            for(int i=0;i<file_keys.length;i++)
            {
                try
                {
                    remotenode = this.simple_find_successor(SHA1.getHash(file_keys[i]));
                }
                catch (NoSuchAlgorithmException ex)
                {
                    basic.Logger.err(ex.getMessage());
                }
                catch (UnsupportedEncodingException ex)
                {
                    basic.Logger.err(ex.getMessage());
                }
                remotenode.addFile(file_keys[i], this.thisnode);
            }
        }
        else
        {
            basic.Logger.war("The folder is empty");
        }
    }

    public void removeFilesFromResponsibleNode()throws RemoteException
    {
        if(!this.empty_folder)
        {
            RemoteNode remotenode;
            for(int i=0;i<file_keys.length;i++)
            {
                remotenode = this.simple_find_successor((new SHAhash(file_keys[i])));
                remotenode.rmFile(file_keys[i]);
            }
        }
        else
        {
            basic.Logger.war("The folder is empty or doesn't exist");
        }
    }

    synchronized public void setPortBusy(int i) throws RemoteException
    {
        ports[i-50000]=true;
    }

    public void unsetPortBusy(int i) throws RemoteException
    {
        ports[i-50000]=false;
    }

    public void sendFile(int port,String address,String file) throws RemoteException
    {
        FileSender sender = new FileSender(address,port,"downloads"+File.separator+file);
        sender.start();
    }

    synchronized public boolean getAvailablePort(int port) throws RemoteException
    {
        // if port is free
        if(!ports[port-50000])
        {
            this.setPortBusy(port);
            return true;
        }
        return false;
    }
}