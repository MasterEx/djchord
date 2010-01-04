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

import basic.SHAhash;
import chord.Node;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *@author Ntanasis Periklis and Chatzipetros Mike
 */
public class IncomingNodeMulticastAnswer implements Runnable{

    private Thread runner;
    private int port = 1100; // standar port for incoming socket connextions
    private Socket socket;
    private ServerSocket serversocket;
    private Node node,successor;
    private byte[] buffer = new byte[20];

    /*
     * is invoked by start()
     */
    public void run() {
         try
        {
            serversocket = new ServerSocket(port);
            socket = serversocket.accept();
            InputStream in = socket.getInputStream();
            for(int i=0;i<19;i++)
            {
                this.buffer[i] = (byte) in.read();
            }
            successor.setKey(new SHAhash(buffer));
            node.setSuccessor(successor);
            in.close();
            socket.close();
            serversocket.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public void setNode(Node node)
    {
        this.node = node;
    }

}
