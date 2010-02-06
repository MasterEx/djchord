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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class contains hte methods for receiving Files. The methods use
 * TCP for sending files and create new thread for this purpose.
 *@author Ntanasis Periklis and Chatzipetros Mike
 */
public class FileReceiver implements Runnable{

    private ServerSocket serversocket;
    private Socket socket;
    private String destination;
    private Thread runner;
    private int port;
    private boolean echo = true;
    private boolean status = false;
    private RemoteNode node = null;
    private final int BYTE_BUFFER_SIZE = 65536;
    private byte[] buffer;

    /**
     * Is invoked by start().
     */
    public void run() 
    {
        int bytecounter = 0;
        try
        {
            buffer = new byte[BYTE_BUFFER_SIZE];
            serversocket = new ServerSocket(port);
            socket = serversocket.accept();
            FileOutputStream out = null;
            InputStream in = socket.getInputStream();
            try
            {
                out = new FileOutputStream(destination);
            }
            catch (FileNotFoundException ex)
            {
                boolean success = (new File("remote_files")).mkdir();
                if (success)
                {
                    basic.Logger.inf("\"remote_files\" folder was created!");
                    out = new FileOutputStream(destination);
                }

            }
            int currentbyte=0;
            long startTime = System.currentTimeMillis();
            while(true)
            {
                currentbyte = in.read(buffer);
                if(currentbyte == -1)
                {
                    status = true;
                    break;
                }
                bytecounter+=currentbyte;
                out.write(buffer,0,currentbyte);
            }
            long endTime = System.currentTimeMillis();
            if (echo)
            {
                basic.Logger.appendln("File was successfully received in "+((endTime-startTime)/1000D)+" sec:\n" +
                        "\tSize:\t"+bytecounter  +" bytes"+
                        "\n\tSender address:\t"+socket.getLocalSocketAddress());
                basic.Logger.inf("File was successfully received in: "+destination);                
            }/*if(node!=null)
            {
            node.unsetPortBusy(port);
            }*/
            in.close();
            out.close();
            socket.close();
            serversocket.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param port The port that we will use.
     * @param destination The destination folder.
     */
    public FileReceiver(int port,String destination) throws IOException
    {
        this.port = port;
        this.destination = destination;
    }

    /**
     *
     * @param port
     * @param destination
     * @param node The node that will receive the files.
     * @throws IOException
     */
    public FileReceiver(int port,String destination,RemoteNode node) throws IOException
    {
        this.node = node;
        this.port = port;
        this.destination = destination;
    }

    /**
     *
     * @param echo Prints extra messages.
     */
    public FileReceiver(int port,String destination,boolean echo)
    {
        this.port = port;
        this.destination = destination;
        this.echo = echo;
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
     * Returns true if file is received.
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
