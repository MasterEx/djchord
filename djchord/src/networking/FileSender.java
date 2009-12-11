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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *@author Ntanasis Periklis and Chatzipetros Mike
 */
public class FileSender implements Runnable{

    FileInputStream input;
    String source;
    String address;
    int port;
    Socket socket;
    PrintWriter outstream;
    Thread runner;

    public void run()
    {
        try
        {
            socket = new Socket(address, port);
            input = new FileInputStream(address);
            outstream = new PrintWriter(socket.getOutputStream(),true);
            int x = 0;
            while(true)
            {
                if(x == -1)
                {
                    break;
                }
                outstream.write(x);
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
    }

    FileSender(String address,int port, String source)
    {
        this.address = address;
        this.port = port;
        this.source = source;
        runner = new Thread(this);
        runner.setDaemon(true);
        runner.start();
    }

}
