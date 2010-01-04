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
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Ntanasis Periklis and Chatzipetros Mike
 */
public interface RemoteNode extends Remote{

    public RemoteNode find_successor(SHAhash k) throws RemoteException;
    public RemoteNode getPredecessor()throws RemoteException;
    public RemoteNode getSuccessor()throws RemoteException;
    public RemoteNode getSuccessor(int i) throws RemoteException;
    public boolean isFirst() throws RemoteException;
    public boolean isLast() throws RemoteException;
    public void setFirst() throws RemoteException;
    public void setLast() throws RemoteException;
    public void setSuccessor(int i,RemoteNode next) throws RemoteException;
    public void setPredecessor(RemoteNode previous) throws RemoteException;
    public void setKey(SHAhash key) throws RemoteException;
    public void setSuccessor(RemoteNode next) throws RemoteException;
    public SHAhash getKey() throws RemoteException;
    public String getPid() throws RemoteException;
    public String getRMIInfo() throws RemoteException;
    public RemoteNode closest_preceding_node(SHAhash k) throws RemoteException;
    public void redistribute_keys(SHAhash k) throws RemoteException;
}
