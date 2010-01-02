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
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *@author Ntanasis Periklis and Chatzipetros Mike
 */
public class PacketHandling implements Runnable{

    Thread runner;
    DatagramPacket packet;
    String[] temp;
    Node node;

    /*
     * is invoked by start()
     */
    public void run() {
        //here we will learn next nodes ip and name
        temp = new String(this.packet.getData()).split(" ");
        Socket socket = null;
        PrintWriter outstream = null;
        try
        {
            socket = new Socket(temp[0], Integer.valueOf(temp[1]));
            outstream = new PrintWriter(socket.getOutputStream(),true);
            outstream.write(node.getPid());
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
            outstream.close();
            try
            {
                socket.close();
            }
            catch (IOException ex)
            {
                Logger.getLogger(PacketHandling.class.getName()).log(Level.SEVERE, null, ex);
            }
            runner.interrupt();
        }
    }

    /*
     * constructor
     */
    PacketHandling(DatagramPacket packet,Node node)
    {
        this.node = node;
        this.packet = packet;
        if(runner == null)
        {
            runner = new Thread(this);
            runner.setDaemon(true);
            runner.start();
        }
    }

}
