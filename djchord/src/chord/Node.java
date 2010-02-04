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
import djchord.GUI;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.rmi.NotBoundException;
import java.security.NoSuchAlgorithmException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Vector;
import networking.FileReceiver;
import networking.FileSender;
import networking.RMIRegistry;

/**
 * A node of the chord.
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
    private Check check, checkstabilize = null;
    private boolean[] ports;
    private boolean empty_folder=true;
    private GUI gui; // true for system false for gui
    private boolean output;

    /**
     * Constructor.
     * @param output Sets the messages outpput.
     * @param gui Sets the gui, if any.
     */    
    public Node(boolean output,GUI gui) throws NoSuchAlgorithmException, UnsupportedEncodingException, RemoteException
    {
        this.output = output;
        this.gui = gui;
        pid = this.setPid();
        this.key = SHA1.getHash(pid);
        RMIRegistry.init();
        RMIRegistry.addNode(this,pid);
        try
        {
            thisnode = RMIRegistry.getRemoteNode(this.getAddress(), pid);
        }
        catch (NotBoundException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
        this.setFolder("local_files");
        this.setFile_keys();
        this.setPredecessor(thisnode);
        for(int u=0;u<3;u++)
        {
            this.setSuccessor(u, thisnode);
        }
        check = new Check(this);
        ports = new boolean[3000];
        for(int i=0;i<3000;i++)
        {
            ports[i] = false;
        }
        if(output)
        {
            System.out.println("The node: "+this.getRMIInfo()+" has been created!");            
        }
        else
        {
            this.gui.append("The node: "+this.getRMIInfo()+" has been created!");
        }
        basic.Logger.inf("The node: "+this.getRMIInfo()+" has been created!");
    }

    /**
     * Here are the Node methods that return and set basic Node attributes.
     */

     // get methods

    /**
     * Returns this node hash key.
     * @return A SHAhash object.
     * @throws RemoteException
     */
    public SHAhash getKey() throws RemoteException
    {
        return this.key;
    }

    /**
     * Returns this node process id.
     * @return PID in string.
     * @throws RemoteException
     */
    public String getPid() throws RemoteException
    {
        return pid;
    }

    /**
     * Returns the RemoteNode object of this node.
     * @return A RemoteNode object.
     * @throws RemoteException
     */
    public RemoteNode getNode() throws RemoteException
    {
        return thisnode;
    }

    /**
     * Returns this node ip address.
     * @return ip address in string for.
     */
    public String getAddress()
    {
        return RMIRegistry.getAddress();
    }

    /**
     * Returns the RMI informations of this node.
     * @return The RMI information in string form.
     * @throws RemoteException
     */
    public String getRMIInfo() throws RemoteException
    {
        return this.pid+" "+this.getAddress();
    }

    // set methods

    /**
     * Sets the PID. May be JVM dependent.
     * @return Thia PID.
     */
    public String setPid()
    {
        /**
         * the ManagementFactory.getRuntimeMXBean().getName() is JVM dependent
         * and may not always work
         */
        return this.pid = ManagementFactory.getRuntimeMXBean().getName();
    }

    // other important Node methods

    /**
     * Marks this node as notified.
     * @throws RemoteException
     */
    synchronized public void notified() throws RemoteException
    {
        this.notified = true;
    }

    /**
     * Returns thue if this node is notified.
     * @return True if notified, false otherwise.
     * @throws RemoteException
     */
    synchronized public boolean isNotified() throws RemoteException
    {
        return notified;
    }

    /**
     * This method does nothing but help us know if a RemoteNode is alive.
     * @throws RemoteException
     */
    public void hasFailed() throws RemoteException
    {
        return;
    }

    /**
     * The chord relative methods
     */

    /**
     * This method exits properly this node form the chord.
     * @return True if all are ok.
     */
    public boolean exit()
    {
        try
        {
            this.joinedStabilize();
            this.getSuccessor().setPredecessor(this.getPredecessor());
            this.getPredecessor().setSuccessor(this.getSuccessor());
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
     * Calls getSuccessorSuccessorsList and handles the returned array.
     */
    
    public void initSuccessors() throws RemoteException
    {
        try
        {
            this.setSuccessor(1, this.getSuccessor(0));
            this.setSuccessor(2, this.getSuccessor(1));
        }
        catch(RemoteException ex)
        {
            this.setSuccessor(1, this.getSuccessor().getSuccessor());
            this.setSuccessor(2, this.getSuccessor().getSuccessor().getSuccessor());
        }
    }

    /**
     * Returns the successor's list.
     * @return A RemoteNode array.
     * @throws RemoteException
     */
    public RemoteNode[] getSuccessorSuccessorsList() throws RemoteException
    {
        this.setSuccessor(1,this.getSuccessor().getSuccessor());
        this.setSuccessor(2,this.getSuccessor().getSuccessor().getSuccessor());
        return this.successors;
    }

    /**
     * The find succesors method.
     * @param k A hash key in SHAhash form.
     * @return The succeding node.
     * @throws RemoteException
     */
    public RemoteNode find_successor(SHAhash K) throws RemoteException
    {
        int hop = 0;
        long startTime = System.currentTimeMillis();
        RemoteNode NODE = this.thisnode,SUCCESSOR = NODE.getSuccessor();
        SHAhash N = NODE.getKey(),S = SUCCESSOR.getKey();
        if(N.compareTo(S)==0)
        {
            basic.HopsAndTime.addCounter();
            basic.HopsAndTime.addHop(hop);
            basic.HopsAndTime.addTime(System.currentTimeMillis()-startTime);
            basic.Logger.inf("find_successor's total hops: "+hop);
            return NODE;
        }
        if(K.compareTo(N)>0 && (K.compareTo(S)<=0 || S.compareTo(N)<0))
        {
            basic.HopsAndTime.addCounter();
            basic.HopsAndTime.addHop(hop);
            basic.HopsAndTime.addTime(System.currentTimeMillis()-startTime);
            basic.Logger.inf("find_successor's total hops: "+hop);
            return SUCCESSOR;
        }
        if(K.compareTo(S)<0 && S.compareTo(N)<0)
        {
            basic.HopsAndTime.addCounter();
            basic.HopsAndTime.addHop(hop);
            basic.HopsAndTime.addTime(System.currentTimeMillis()-startTime);
            basic.Logger.inf("find_successor's total hops: "+hop);
            return SUCCESSOR;
        }
        else
        {
            basic.HopsAndTime.addTime(System.currentTimeMillis()-startTime);
            RemoteNode temp = NODE.closest_preceding_node(K);
            //System.out.println("out");
            return temp.find_successor_hops(K,hop);
        }
    }

    /**
     * As find successors but logs the total number of hops.
     * @param k A hash key in SHAhash form.
     * @param hop The previous hop number (int).
     * @return The succeding node.
     * @throws RemoteException
     */
    public RemoteNode find_successor_hops(SHAhash K,int hop) throws RemoteException
    {
        //System.out.println("PROBLIMA!!!");
        hop++;
        long startTime = System.currentTimeMillis();
        RemoteNode NODE = this.thisnode,SUCCESSOR = NODE.getSuccessor();
        SHAhash N = NODE.getKey(),S = SUCCESSOR.getKey();
        /*System.out.println("INFO:\nN= "+N.getStringHash()+"\nK= "+K.getStringHash());
        System.out.println("S= "+S.getStringHash());*/
        if(N.compareTo(S)==0)
        {
            basic.HopsAndTime.addCounter();
            basic.HopsAndTime.addHop(hop);
            basic.HopsAndTime.addTime(System.currentTimeMillis()-startTime);
            basic.Logger.inf("find_successor's total hops: "+hop);
            return NODE;
        }
        if(K.compareTo(N)>0 && (K.compareTo(S)<=0 || S.compareTo(N)<0))
        {
            basic.HopsAndTime.addCounter();
            basic.HopsAndTime.addHop(hop);
            basic.HopsAndTime.addTime(System.currentTimeMillis()-startTime);
            basic.Logger.inf("find_successor's total hops: "+hop);
            return SUCCESSOR;
        }
        if(K.compareTo(S)<0 && S.compareTo(N)<0)
        {
            basic.HopsAndTime.addCounter();
            basic.HopsAndTime.addHop(hop);
            basic.HopsAndTime.addTime(System.currentTimeMillis()-startTime);
            basic.Logger.inf("find_successor's total hops: "+hop);
            return SUCCESSOR;
        }
        else
        {
            basic.HopsAndTime.addTime(System.currentTimeMillis()-startTime);
            return NODE.closest_preceding_node(K).find_successor_hops(K,hop);
        }
    }
    
    /**
     * This methd finds successors linear by checkinng the successor's successor.
     * @param k A hash key in SHAhash form.
     * @return A the succesding node.
     */
    public RemoteNode simple_find_successor(SHAhash k) throws RemoteException
    {
        RemoteNode next = this.thisnode;
        if(this.getPid().equalsIgnoreCase(this.getSuccessor().getPid()))
        {
            return this.thisnode;
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

    /**
     * It is used by find successor.
     * @param k A hash key in SHAhash form.
     * @return The preceding node.
     * @throws RemoteException
     */
    synchronized public RemoteNode closest_preceding_node(SHAhash K) throws RemoteException
    {
        SHAhash LF = this.compressedFingers.get(this.compressedFingers.size()-1).getKey();
        RemoteNode LAST_FINGER = this.compressedFingers.get(this.compressedFingers.size()-1);
        RemoteNode NODE = this.thisnode;
        SHAhash N = NODE.getKey();
        int length = compressedFingers.size()-1;
        if(K.compareTo(LF)==0)
        {
            return LAST_FINGER.getPredecessor();
        }
        if(N.compareTo(LF)==0)
        {
            return NODE.getPredecessor();
        }
        if(K.compareTo(N)>0)
        {
            if(K.compareTo(LF)>0)
            {
                if(LF.compareTo(N)<0)
                {
                    for(int i=length;i>=0;i--)
                    {
                        if(compressedFingers.get(i).getKey().compareTo(LF)<0)
                        {
                            continue;
                        }
                        if(compressedFingers.get(i).getKey().compareTo(N)<0)
                        {
                            return compressedFingers.get(i);
                        }
                    }
                    return NODE.getSuccessor();
                }
                return LAST_FINGER;
            }
            if(K.compareTo(LF)<0)
            {
                for(int i=length;i>=0;i--)
                {
                    if(compressedFingers.get(i).getKey().compareTo(K)<0)
                    {
                        return compressedFingers.get(i);
                    }
                }
            }
        }
        if(K.compareTo(N)<0)
        {
            if(K.compareTo(LF)>0)
            {
                return LAST_FINGER;
            }
            if(K.compareTo(LF)<0)
            {
                if(LF.compareTo(N)<0)
                {
                    for(int i=length;i>=0;i--)
                    {
                        if(compressedFingers.get(i).getKey().compareTo(N)>0 || compressedFingers.get(i).getKey().compareTo(K)<0)
                        {
                            return compressedFingers.get(i);
                        }
                    }
                }
                return LAST_FINGER;
            }
        }

        System.out.println("NEVER COMES HERE ");
        return this.simple_find_successor(K);// unreachable statement
    }

    public void startCheck()
    {
        check.start();
    }

    /**
     * Runs the stabilize thread once for this node.
     * @throws RemoteException
     */
    public void stabilize() throws RemoteException
    {
        if(checkstabilize.isFree())
        {
            checkstabilize = new Check(this);
            checkstabilize.startStabilize();
        }
    }

    /**
     * As stabilize but waits for the stabilize to end.
     * @throws RemoteException
     */
    public boolean joinedStabilize() throws RemoteException
    {
        synchronized(this)
        {
            if(checkstabilize == null)
            {
                checkstabilize = new Check(this);
            }
        }
        if(checkstabilize.isFree())
        {
            checkstabilize.startStabilize();
        }
        try
        {
            checkstabilize.getThread().join();
            synchronized(this)
            {
                checkstabilize = null;
            }
            return true;
        }
        catch (InterruptedException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
        catch (NullPointerException ex)
        {
            return false;
        }
        return false;
    }

    /**
     * Set this node next successor.
     * @param next A RemoteNode.
     * @throws RemoteException
     */
    synchronized public void setSuccessor(RemoteNode next) throws RemoteException
    {
        basic.Logger.inf("Setting at successor[0] "+next.getRMIInfo());
        this.successors[0] = next;
    }

    /**
     * Set this node one of the next 3 successors.
     * @param i Which successor.
     * @param next The next node.
     * @throws RemoteException
     */
    synchronized public void setSuccessor(int i,RemoteNode next) throws RemoteException
    {
        basic.Logger.inf("Setting at successor["+i+"] "+next.getRMIInfo());
        this.successors[i] = next;
    }

    /**
     * Sets this node predecessor.
     * @param previous The previous node.
     * @throws RemoteException
     */
    public void setPredecessor(RemoteNode previous) throws RemoteException
    {
        this.predecessor = previous;
    }

    /**
     * Returns the next node.
     * @return The next node.
     * @throws RemoteException
     */
    public RemoteNode getSuccessor() throws RemoteException
    {
        return successors[0];
    }

    /**
     * Returns one of the 3 next nodes.
     * @param i
     * @return One next node.
     * @throws RemoteException
     */
    public RemoteNode getSuccessor(int i) throws RemoteException
    {
        return successors[i];
    }

    /**
     * Returns the previous node.
     * @return The previous node.
     * @throws RemoteException
     */
    public RemoteNode getPredecessor() throws RemoteException
    {
        return predecessor;
    }

    /**
     * Creates the finger table.
     * @throws RemoteException
     */
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

    /**
     * A new finger vector without nodes' doubles.
     * @throws RemoteException
     */
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
        basic.Logger.fingerLog(compressedFingers);
    }

    /**
     * It fixes faster the fingers.
     * @throws RemoteException
     */
    public void fastFix() throws RemoteException
    {
        compressedFingers = new Vector<RemoteNode>();
        SHAhash temp,max = new SHAhash("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
        RemoteNode temp1 , temp2=null;
        for(int i=0;i<160;i++)
        {
            temp = new SHAhash(this.key.add(SHAhash.power(Integer.toHexString(2), i)));
            /*
             * if hash is greater than max sha1 hash value it takes a value
             * equal to value-maxValue. Then we return a RemoteNode with
             * find_successor to the finger table
             */
            temp1 = this.simple_find_successor((temp.compareTo(max)>0)?new SHAhash((SHAhash.subtract(temp.getStringHash(), max.getStringHash())).length()==40?SHAhash.subtract(temp.getStringHash(), max.getStringHash()):(SHAhash.subtract(temp.getStringHash(), max.getStringHash())).substring(1,40)):temp);
            if(temp2==null || !temp1.getPid().equalsIgnoreCase(temp2.getPid()))
            {
                this.compressedFingers.add(temp1);
                temp2 = temp1;
            }
        }
        compressedFingers.trimToSize();
        basic.Logger.fingerLog(compressedFingers);
    }

    /**
     * It calls setFingers and compressFingers.
     * @throws RemoteException
     */
    public void fixFingers() throws RemoteException
    {
        //this.setFingers();
        //this.compressFingers();
        this.fastFix();
    }

    /**
     * Here are hte methods about files indexing and move.
     */

    /**
     * It returns the folder that our files are located.
     * @return The folder in string form.
     */
    public String getFolder()
    {
        return this.folder;
    }

    /**
     * Returns an array with our files.
     * @return String array with file names.
     * @throws RemoteException
     */
    public String[] getFile_keys() throws RemoteException
    {
        return this.file_keys;
    }

    /**
     * Return if the specific port is available.
     * @param i An integer from 0 to 3000.
     * @return True if is in use or false if not.
     * @throws RemoteException
     */
    public boolean getPort(int i) throws RemoteException
    {
        return ports[i];
    }

    /**
     * Let us know who has localy this file.
     * @param filehash Hash key in string.
     * @return The responsible node.
     * @throws RemoteException
     */
    public RemoteNode getFileResponsible(String filehash) throws RemoteException
    {
        return this.foreignfiles.get(filehash);
    }

    /**
     * It downloads the specified file.
     * @param filename Filename in string.
     */
    public boolean getFile(String filename)
    {
        boolean contin = true;
        RemoteNode responsible = null;
        try
        {
            responsible = this.find_successor(SHA1.getHash(filename)).getFileResponsible(filename);
            if (responsible == null)
            {
                throw new NullPointerException();
            }
        }
        catch (NoSuchAlgorithmException ex)
        {
            basic.Logger.err(ex.getMessage());
            return false;
        }
        catch (UnsupportedEncodingException ex)
        {
            basic.Logger.err(ex.getMessage());
            return false;
        }
        catch (RemoteException ex)
        {
            if(output)
            {
                System.out.println("File was unable to be sent, please try again later.");
            }
            else
            {
                this.gui.append("File was unable to be sent, please try again later.");
            }
            basic.Logger.err(ex.getMessage());
            return false;
        }
        catch (NullPointerException ex)
        {
            contin = false;
            if(output)
            {
                System.out.print("FILE DOESN'T EXIST!!!");
            }
            else
            {
                this.gui.append("FILE DOESN'T EXIST!!!");
            }
            basic.Logger.war("FILE DOESN'T EXIST!!!");
            return false;
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
                FileReceiver receiver = new FileReceiver(port,"remote_files"+File.separator+filename,this.thisnode);
                receiver.setOutput(output);
                if(!output)
                {
                    receiver.setGUI(gui);
                }
                receiver.start();
                responsible.sendFile(port, this.getAddress(), filename);
                return receiver.getstatus();
            }
            catch (UnsupportedEncodingException ex)
            {
                basic.Logger.err(ex.getMessage());
                return false;
            }
            catch (RemoteException ex)
            {
                basic.Logger.err(ex.getMessage());
                return false;
            }
            catch (IOException ex)
            {
                basic.Logger.err(ex.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Sets this node hash key.
     * @param key A SHAhash key.
     * @throws RemoteException
     */
    public void setKey(SHAhash key) throws RemoteException
    {
         this.key = key;
    }

    /**
     * Sets the folder where we have our local files.
     * @param folder Folder name in string.
     */
    public void setFolder(String folder)
    {
         this.folder = folder;
    }

    /**
     * Uses FileNames to specify our files.
     */
    public void setFile_keys()
    {
        this.empty_folder = true;
        file_keys = (new FileNames(this.folder)).getFileNames();
        if(file_keys!=null)
        {
            this.empty_folder = false;
        }
    }

    /**
     * Sets a file with hash less or equals to ours to our responsibility.
     * @param filename String file name.
     * @param node The node that owns the file.
     * @throws RemoteException
     */
    public void addFile(String filename,RemoteNode node) throws RemoteException
    {
        basic.Logger.inf("Now putting "+filename+" and "+node.getPid()+" in this node");
        foreignfiles.put(filename, node);
    }

    /**
     * Removes a file from our responsibility.
     * @param filehash String file name.
     * @throws RemoteException
     */
    public void rmFile(String filehash) throws RemoteException
    {
        foreignfiles.remove(filehash);
    }

    /**
     * Sends our files to responsible nodes.
     * @throws RemoteException
     */
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
                    remotenode.addFile(file_keys[i], this.thisnode);
                }
                catch (NoSuchAlgorithmException ex)
                {
                    basic.Logger.err(ex.getMessage());
                    continue;
                }
                catch (UnsupportedEncodingException ex)
                {
                    basic.Logger.err(ex.getMessage());
                    continue;
                }
                catch (NullPointerException ex)
                {
                    continue;
                }
                catch(Exception ex)
                {
                    continue;
                }
            }
        }
        else
        {
            basic.Logger.war("The folder is empty");
        }
    }

    /**
     * Remove our files from the responsible nodes.
     * @throws RemoteException
     */
    public void removeFilesFromResponsibleNode()throws RemoteException
    {
        if(!this.empty_folder)
        {
            RemoteNode remotenode;
            for(int i=0;i<file_keys.length;i++)
            {
                remotenode = this.find_successor((new SHAhash(file_keys[i])));
                remotenode.rmFile(file_keys[i]);
            }
        }
        else
        {
            basic.Logger.war("The folder is empty or doesn't exist");
        }
    }

    /**
     * Set a port as busy.
     * @param i Integer from 50000 to 53000.
     * @throws RemoteException
     */
    synchronized public void setPortBusy(int i) throws RemoteException
    {
        ports[i-50000]=true;
    }

    /**
     * Sets a port available again.
     * @param i Integer from 50000 to 53000.
     * @throws RemoteException
     */
    public void unsetPortBusy(int i) throws RemoteException
    {
        ports[i-50000]=false;
    }

    /**
     * Sends a file to the node that requested it.
     * @param port Port that we will use.
     * @param address IP address of our destination.
     * @param file The file that we will send.
     * @throws RemoteException
     */
    public void sendFile(int port,String address,String file) throws RemoteException
    {
        FileSender sender = new FileSender(address,port,"local_files"+File.separator+file);
        sender.start();
    }

    /**
     * Returns true if port is available.
     * @param port The port that we check.
     * @return True if port's available or false otherwise.
     * @throws RemoteException
     */
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