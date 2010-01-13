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

package networking;

import chord.Node;
import chord.RemoteNode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
*
*@author Ntanasis Periklis and Chatzipetros Mike
*/
public class RMIRegistry {

    private static boolean init = false;
    private static Registry registry;
    private static int port = 1099; //this is the default port of java RMI
    private static String address;

    /*
* This method works like java RMI tutorial example
* http://java.sun.com/docs/books/tutorial/rmi/overview.html
* and as the example we have to start RMI registry before the
* execution of our applications.
* It is start rmiregistry fow windows (or javaw if start not available)
* and rmiregistry & for linux.
* We also use a security manager, so we need to specify a security policy
* file so that the code is granted the security permissions it needs to run.
*/
    static public boolean init(boolean security)
    {
        if(init)
        {
            return init;
        }
        try
        {
            address = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException ex)
        {
            Logger.getLogger(RMIRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }
        //SecurityManager is optional
        if (System.getSecurityManager() == null && security)
        {
            System.setSecurityManager(new SecurityManager());
        }

        try
        {
            RMIRegistry.registry = LocateRegistry.getRegistry(port);
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(RMIRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }
        return init=true;
    }

    static public boolean init()
    {
        return init(false);
    }

    static public void addNode(Node node,String name)
    {
        RemoteNode rnode = (RemoteNode) node;
        try
        {
            RemoteNode stub = (RemoteNode) UnicastRemoteObject.exportObject(rnode, 0);
            registry.rebind(name, stub);
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(RMIRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    static public int getPort()
    {
        return RMIRegistry.port;
    }

    static public void setPort(int port)
    {
        RMIRegistry.port = port;
    }

    /*
    * This method creates an RMIRegistry. It's like
    * start rmiregistry in windows or
    * rmiregistry & in linux
    */
    static public void createRegistry(int port)
    {
        try
        {
            registry = LocateRegistry.createRegistry(port);
            System.out.println("RMIRegistry was created @ port: "+port);
            synchronized(Thread.currentThread())
            {
                Thread.currentThread().wait();
            }
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(RMIRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (InterruptedException ex)
        {
            System.out.println("RMIRegistry is terminating.");
            Logger.getLogger(RMIRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static public RemoteNode getRemoteNode(String address,int port,String name) throws NotBoundException
    {
        try
        {
            registry = LocateRegistry.getRegistry(address, port);
            return (RemoteNode)(registry.lookup(name));
        }
        catch (AccessException ex)
        {
            Logger.getLogger(RMIRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(RMIRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }


    static public RemoteNode getRemoteNode(String address,String name) throws NotBoundException
    {
        return RMIRegistry.getRemoteNode(address,1099, name);
    }

    static public void createRegistry()
    {
        createRegistry(1099);
    }

    static public String getAddress()
    {
        return address;
    }
}
 