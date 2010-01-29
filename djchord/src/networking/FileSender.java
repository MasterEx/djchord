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

import chord.RemoteNode;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class contains the methods for sending Files. The methods use
 * TCP for sending files and create new thread for this purpose.
 *@author Ntanasis Periklis and Chatzipetros Mike
 */
public class FileSender implements Runnable{

    private FileInputStream input;
    private String source;
    private String address;
    private int port;
    private Socket socket;
    private OutputStream outstream;
    private Thread runner;
    private boolean status = false;
    private RemoteNode node = null;
    private final int BYTE_BUFFER_SIZE = 65536;
    private byte[] buffer = new byte[BYTE_BUFFER_SIZE];

    /**
     * Is invoked by start().
     */
    public void run()
    {
        try
        {
            socket = new Socket(address, port);
            input = new FileInputStream(source);
            outstream = socket.getOutputStream();
            int currentbyte = 0;
            while(true)
            {
                currentbyte = input.read(buffer);
                if(currentbyte == -1)
                {
                    status = true;
                    break;
                }
                outstream.write(buffer,0,currentbyte);
            }
            outstream.close();
            input.close();
            socket.close();
        }
        catch (UnknownHostException ex)
        {
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            if (node!=null)
            {
                try
                {
                    System.out.println("Trying to unset port "+port+" as busy");
                    node.unsetPortBusy(port);
                } 
                catch (RemoteException ex)
                {
                    Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     *
     * @param address The ip destination address.
     * @param port the port that we will use.
     * @param source Our local files.
     */
    public FileSender(String address,int port, String source)
    {
        this.address = address;
        this.port = port;
        this.source = source;
    }

    /**
     *
     * @param address
     * @param port
     * @param source
     * @param node The node that sends a file.
     */
    public FileSender(String address,int port, String source,RemoteNode node)
    {
        this.node = node;
        this.address = address;
        this.port = port;
        this.source = source;               
    }

    /**
     * Starts the execution of the thread.
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
        try
        {
            socket.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        runner.interrupt();
        runner = null;
    }

    /**
     * Returns true if a file was sent
     */
    public boolean getstatus()
    {
        return status;
    }

    /**
     * Return this thread in case of join.
     * @return This thread.
     */
    public Thread getThread()
    {
        return this.runner;
    }

}
