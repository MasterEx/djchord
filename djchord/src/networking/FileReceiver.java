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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *@author Ntanasis Periklis and Chatzipetros Mike
 */
public class FileReceiver implements Runnable{

    private ServerSocket serversocket;
    private Socket socket;
    private String destination;
    private Thread runner;
    private int port;
    private boolean echo = false;
    private boolean status = false;

    /*
     * is invoked by start()
     */
    public void run() 
    {
        try
        {
            serversocket = new ServerSocket(port);
            socket = serversocket.accept();
            InputStreamReader in = new InputStreamReader(socket.getInputStream());
            FileOutputStream out = new FileOutputStream(destination);
            int currentbyte=0;
            while(true)
            {
                out.write(currentbyte = in.read());
                if(currentbyte == -1)
                {
                    status = true;
                    break;
                }
            }
            if (echo)
            {
                System.out.println("File was successfully received:\n\t" +
                        "Size:\t"+socket.getSendBufferSize()  +"" +
                        "\n\tSender address:\t"+socket.getLocalSocketAddress());
            }
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

    /*
     * constructor
     */
    public FileReceiver(int port,String destination) throws IOException
    {
        this.port = port;
        this.destination = destination;
    }

    /*
     * constructor
     */
    public FileReceiver(int port,String destination,boolean echo)
    {
        this.port = port;
        this.destination = destination;
        this.echo = echo;
    }

    /*
     * starts the execution of the thread
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

    /*
     * stops the execution of the thread
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

    /*
     * returns true if file is received
     */
    public boolean getstatus()
    {
        return status;
    }

    public Thread getThread()
    {
        return this.runner;
    }

}
