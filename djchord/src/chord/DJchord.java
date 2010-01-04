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

import exceptions.NotInitializedVariablesException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import networking.MulticastSender;

/**
 *
 * @author Ntanasis Periklis and Chatzipetros Mike
 */
public class DJchord implements Runnable {

    Node node;
    byte buffer;
    MulticastSender sendmulticast;
    Thread runner;

    /*
     * is invoked by start()
     */
    public void run()
    {
        try
        {
            node = new Node();
        }
        catch (NoSuchAlgorithmException ex)
        {
            Logger.getLogger(DJchord.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(DJchord.class.getName()).log(Level.SEVERE, null, ex);
        }
        sendmulticast = new MulticastSender(1101,"224.1.1.1",node.getPid().getBytes());
        sendmulticast.start();
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
        sendmulticast.stop();
        runner.interrupt();
        runner = null;
    }
}
