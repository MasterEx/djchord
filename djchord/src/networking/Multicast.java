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
import java.io.IOException;
import java.net.*;
import exceptions.NotInitializedVariablesException;

/**
 * It is inheritted by MulticastReceiver and MulticastSender.
 *@author Ntanasis Periklis and Chatzipetros Mike
 * @see MulticastReceiver
 * @see MulticastSender
 */
public class Multicast {

    protected int port = -1; // port
    protected String group; // d class group address
    protected MulticastSocket socket = null; // socket

    /*
     *constructor
     */
    public Multicast()
    {
        //empty
    }

    /*
     *constructor
     */
    public Multicast(int port)
    {
        setport(port);
    }

    /*
     *constructor
     */
    public Multicast(String group)
    {
        setgroup(group);
    }

    /*
     *constructor
     */
    public Multicast(int port,String group)
    {
        setport(port);
        setgroup(group);
    }


    /*
     *sets port number
     */
    public void setport(int port)
    {
        this.port = port;
    }

    /*
     *sets group address
     */
    public void setgroup(String group)
    {
        this.group = group;
    }

    /*
     *binds port to socket and joins the mullticast group
     */
    public void openconnection() throws NotInitializedVariablesException, IOException
    {
        if(group.equalsIgnoreCase("") || port==-1)
        {
            throw (new NotInitializedVariablesException(this.getClass()+": " +
                    "NotInitializedVariablesException:\n port or group address " +
                    "are not initialized"));
        }

        socket = new MulticastSocket(port);
    }

    /*
     *closes the open connnections
     */
    public void closeconnection() throws UnknownHostException, IOException, NotInitializedVariablesException
    {
        if(socket == null)
        {
            throw (new NotInitializedVariablesException(this.getClass()+" : " +
                    "NotInitializedVariablesException:\n socket is not initialized"));
        }
        socket.close();
    }


}
