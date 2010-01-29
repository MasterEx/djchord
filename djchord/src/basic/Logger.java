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

package basic;

import chord.RemoteNode;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import java.util.logging.Level;

/**
 * Logger contains static methods for writting in files.
 * @author Ntanasis Periklis and Chatzipetros Mike
 */
public class Logger {

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

    /**
     * Appends a string to Log.txt in our current directory with a date/time prefix.
     * @param msg The string that we want to append.
     * @param filename The file that we will write.
     */
    synchronized public static void println(String msg,String filename)
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);

        try
        {
            FileWriter fwriter = new FileWriter(filename,true);
            BufferedWriter bwriter = new BufferedWriter(fwriter);
            bwriter.write("["+sdf.format(cal.getTime())+"]: "+msg);
            bwriter.newLine();
            bwriter.close();
            fwriter.close();
        }
        catch (IOException ex)
        {
            basic.Logger.err(ex.getMessage());
        }
    }

    /**
     * Calls the println() method with te Warning prefix.
     * @param msg The string that we want to append.
     */
    public static void war(String msg)
    {
        Logger.println("Warning:     "+msg,"Log.txt");
    }

    /**
     * Calls the println() method with te Information prefix.
     * @param msg The string that we want to append.
     */
    public static void inf(String msg)
    {
        Logger.println("Information: "+msg,"Log.txt");
    }

    /**
     * Calls the println() method with te Error prefix.
     * @param msg The string that we want to append.
     */
    public static void err(String msg)
    {
        Logger.println("Error:       "+msg,"Log.txt");
    }

    /**
     * Prints the compressed finger to the FingerLog.txt ,
     * located to our local directory.
     * @param fingers The compressed finger vector.
     */
    public static void fingerLog(Vector<RemoteNode> fingers)
    {
        Logger.println("The fingers are:","FingerLog.txt");
        for(int i=0;i<fingers.size();i++)
        {
            try
            {
                Logger.println("The finger " + i + " is:" + fingers.get(i).getKey().getStringHash(), "FingerLog.txt");
            }
            catch (RemoteException ex)
            {
                java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Logger.println("******** End Of Finger Print ********","FingerLog.txt");
    }

}
