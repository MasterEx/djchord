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
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.rmi.RemoteException;
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
    private RemoteNode[] fingers,successors;
    private Map<SHAhash,String> index;
    private RemoteNode predecessor;
    private boolean first = false, last = false;

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
        this.setSuccessor(this);
        this.setPredecessor(this);
    }

    //public

    public RemoteNode find_successor(SHAhash k) throws RemoteException
    {
        Node search = this;
        if (k.compareTo(search.getKey())==1 && k.compareTo(search.getSuccessor().getKey())<=0)
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
        if (k.compareTo(fingers[159].getKey())==1 || k.compareTo(this.getKey())==-1)
        {
            return fingers[159].closest_preceding_node(k);
        }
        for(int i=158;i>=0;i--)
        {
            if (k.compareTo(fingers[i].getKey())==1 && (k.compareTo(fingers[i].getSuccessor().getKey())==-1 || fingers[i].getSuccessor().isFirst()))
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

    public boolean isFirst() throws RemoteException
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

    public void setSuccessor(RemoteNode next) throws RemoteException
    {
        this.successors[0] = next;
    }

    public void setSuccessor(int i,RemoteNode next) throws RemoteException
    {
        this.successors[i] = next;
    }

    public void setPredecessor(RemoteNode previous) throws RemoteException
    {
        this.predecessor = previous;
    }

    public void setFirst() throws RemoteException
    {
        this.first = true;
    }

    public void unsetFirst() throws RemoteException
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
        for(int i=0;i<159;i++)
        {
            //this.fingers[i] = simple_find_successor(this.getKey().getStringHash());
            //2^159 is too large!!! - long is 2^64-1 - 2^159 is 41 digits
            //what about use 2^10 = 2^5*2^5 ???
            this.fingers[i] = this.find_successor(new SHAhash(this.key.add(SHAhash.power(Integer.toHexString(2), i-1))));
        }
    }

}