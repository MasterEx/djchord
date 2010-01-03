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
import java.lang.management.ManagementFactory;
import java.util.Map;
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
    private Node[] fingers,successors;
    private Map<SHAhash,String> index;
    private Node predecessor;
    private boolean first = false, last = false;

    /**
     * constructor
     */
    public Node()
    {
        //the ManagementFactory.getRuntimeMXBean().getName() is JVM dependent
        //and may not always work
        pid = this.setPid();
        RMIRegistry.addNode(this,pid);
    }

    //public

    public Node find_successor(SHAhash k)
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
    
    public Node simple_find_successor(SHAhash k)
    {
        for(Node tempnode=this.getSuccessor();tempnode==this;tempnode=tempnode.getSuccessor())
        {
            if((k.compareTo(tempnode.getKey())<0 || tempnode.isFirst())&& k.compareTo(tempnode.getPredecessor().getKey())>=0)
            {
                return tempnode;
            }
        }
        return this;
    }
    
    public Node closest_preceding_node(SHAhash k)
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
        return null; // no reachable statement
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

    public Node getSuccessor()
    {
        return successors[0];
    }

    public Node getSuccessor(int i)
    {
        return successors[i];
    }

    public Node getPredecessor()
    {
        return predecessor;
    }

    public String getPid()
    {
        return pid;
    }

    public boolean isFirst()
    {
        return first;
    }

    public boolean isLast()
    {
        return last;
    }    

    /**
     * set methods
     */
    public void setKey(SHAhash key)
    {
         this.key = key;
    }

    public void setFolder(String folder)
    {
         this.folder = folder;
    }

    public void setFile_keys(SHAhash[] file_keys)
    {
         this.file_keys = file_keys;
    }

    public void setSuccessor(Node next)
    {
        this.successors[0] = next;
    }

    public void setSuccessor(int i,Node next)
    {
        this.successors[i] = next;
    }

    public void setPredecessor(Node previous)
    {
        this.predecessor = previous;
    }

    public void setFirst()
    {
        this.first = true;
    }

    public void setLast()
    {
        this.last = true;
    }
    
    private String setPid() 
    {
        return this.pid = ManagementFactory.getRuntimeMXBean().getName(); 
    }

}