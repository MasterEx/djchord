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
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.rmi.NotBoundException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private SHAhash[] file_keys;
    private RemoteNode[] fingers,successors = new RemoteNode[3];
    private Map<SHAhash,String> index;
    private RemoteNode predecessor;
    private boolean first = false, last = false, notified = false;
    private Vector<RemoteNode> compressedFingers;
    private RemoteNode thisnode = null;

    /**
     * constructor
     */
    public Node() throws NoSuchAlgorithmException, UnsupportedEncodingException, RemoteException
    {
        //the ManagementFactory.getRuntimeMXBean().getName() is JVM dependent
        //and may not always work
        pid = this.setPid();
        RMIRegistry.addNode(this,pid);
        file_keys = setFile_keys();
        this.setSuccessor(thisnode);
        this.setPredecessor(thisnode);
        this.fixFingers();
        try
        {
            thisnode = RMIRegistry.getRemoteNode(this.getAddress(), pid);
        }
        catch (NotBoundException ex)
        {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(int u=0;u<3;u++)
        {
            this.setSuccessor(u, thisnode);
        }
    }

    /*
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
        int counter = 0; // how many times this node is in successors list
        for(int i=0;i<successors.length;i++)
        {
            if(successors[i].getPid().equalsIgnoreCase(this.getPid()))
            {
                counter++;
            }
        }
        switch (counter)
        {
            case 1:this.setSuccessor(1,this.getPredecessor());
            try
            {
                this.setSuccessor(2, RMIRegistry.getRemoteNode(this.getAddress(), pid));
            }
            catch (NotBoundException ex)
            {
                Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
            }
            break;
            case 3:this.setSuccessor(1, this.getPredecessor()); break;
        }
        return this.successors;
    }

    public void stabilize() throws RemoteException
    {
        int i = 0;
        try
        {
            for(;i<3;i++)
            {
                this.getSuccessor(i).getKey();
            }
        }
        catch (RemoteException e)
        {

        }
    }

    public RemoteNode find_successor(SHAhash k) throws RemoteException
    {
        Node search = this;
        if (k.compareTo(search.getKey())>0 && k.compareTo(search.getSuccessor().getKey())<=0)
        {
            return search.getSuccessor();
        }
        else
        {
            return search.closest_preceding_node(k).find_successor(k);
        }
    }
    
    public RemoteNode simple_find_successor(SHAhash k) throws RemoteException
    {
        for(RemoteNode tempnode=this.getSuccessor();tempnode==this;tempnode=tempnode.getSuccessor())
        {
            if((k.compareTo(tempnode.getKey())<0 || tempnode.isFirst())&& k.compareTo(tempnode.getPredecessor().getKey())>=0)
            {
                return tempnode;
            }
        }
        return this;
    }
    
    public RemoteNode closest_preceding_node(SHAhash k) throws RemoteException
    {
        if (k.compareTo(fingers[159].getKey())>0 || k.compareTo(this.getKey())<0)
        {
            return fingers[159].closest_preceding_node(k);
        }
        for(int i=158;i>=0;i--)
        {
            if (k.compareTo(fingers[i].getKey())>0 && (k.compareTo(fingers[i].getSuccessor().getKey())<0 || fingers[i].getSuccessor().isFirst()))
            {
                 return fingers[i];
            }
        }
        return null; // unreachable statement
    }

    public void redistribute_keys(SHAhash k) throws RemoteException
    {
        //this.setPredecessor(this.find_successor(k).getPredecessor());
    }

    public void mapAdd(SHAhash nodeHash,String fileName)
    {
        if(!this.index.containsKey(nodeHash)&&!this.index.containsValue(fileName))
        {
            this.index.put(nodeHash, fileName);
        }
    }
    
    public void compressFingers() throws RemoteException
    {
        compressedFingers = new Vector<RemoteNode>();
        int j=0, i=0;
        for(;i<159;i++)
        {
            for(;j<159;j++)
            {
                if(!fingers[i].getPid().equalsIgnoreCase(fingers[j].getPid()))
                {
                    break;
                }
            }
            if(j!=159)
            {
                this.compressedFingers.add(fingers[i]);
                this.compressedFingers.add(fingers[j]);
                i = j;
            }
            else
            {
                this.compressedFingers.add(fingers[i]);
                break;
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
     * get methods
     */
    public SHAhash getKey()
    {
        return this.key;
    }

    public String getFolder()
    {
        return this.folder;
    }

    public SHAhash[] getFile_keys()
    {
        return this.file_keys;
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

    public String getPid() throws RemoteException
    {
        return pid;
    }

    synchronized public boolean isFirst() throws RemoteException
    {
        return first;
    }

    public boolean isLast() throws RemoteException
    {
        return last;
    }

    public String getAddress()
    {
        return RMIRegistry.getAddress();
    }

    public String getRMIInfo() throws RemoteException
    {
        return this.pid+" "+this.getAddress();
    }
    
    synchronized public boolean isNotified() throws RemoteException
    {
        return notified;
    }

    /**
     * set methods
     */
    public void setKey(SHAhash key) throws RemoteException
    {
         this.key = key;
    }

    public void setFolder(String folder)
    {
         this.folder = folder;
    }

    public SHAhash[] setFile_keys() throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        FileNames files = new FileNames(this.folder);
        String[] filenames = files.getFileNames();
        for(int i=0;i<filenames.length;i++)
        {
            file_keys[i] = SHA1.getHash(filenames[i]);
            this.mapAdd(file_keys[i], filenames[i]);
        }
        return file_keys;
    }

    synchronized public void setSuccessor(RemoteNode next) throws RemoteException
    {
        this.successors[0] = next;
    }

    synchronized public void setSuccessor(int i,RemoteNode next) throws RemoteException
    {
        this.successors[i] = next;
    }

    public void setPredecessor(RemoteNode previous) throws RemoteException
    {
        this.predecessor = previous;
    }

    synchronized public void setFirst() throws RemoteException
    {
        this.first = true;
    }

    synchronized public void unsetFirst() throws RemoteException
    {
        this.first = false;
    }

    public void setLast() throws RemoteException
    {
        this.last = true;
    }
    
    public String setPid()
    {
        return this.pid = ManagementFactory.getRuntimeMXBean().getName(); 
    }

    public void setFingers() throws RemoteException
    {
        SHAhash temp,max = new SHAhash("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
        for(int i=0;i<159;i++)
        {
            temp = new SHAhash(this.key.add(SHAhash.power(Integer.toHexString(2), i-1)));
            /*
             * if hash is greater than max sha1 hash value it takes a value
             * equal to value-maxValue. Then we return a RemoteNode with
             * find_successor to the finger table
             */
            this.fingers[i] = this.simple_find_successor((temp.compareTo(max)>0)?new SHAhash((SHAhash.subtract(temp.getStringHash(), max.getStringHash())).length()==40?SHAhash.subtract(temp.getStringHash(), max.getStringHash()):(SHAhash.subtract(temp.getStringHash(), max.getStringHash())).substring(1,40)):temp);
        }
    }
    
    synchronized public void notified() throws RemoteException
    {
        this.notified = true;
    }

}