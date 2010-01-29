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

/**
 * This class contains the hops and time static variables for statistic purposes.
 * @author Ntanasis Periklis and Chatzipetros Mike
 */
public class HopsAndTime {

    public static long TIME = 0;
    public static long HOPS = 0;
    public static long search_counter = 0;

    /**
     * This method adds the find time to total time.
     * @param time Time in millis.
     */
    synchronized public static void addTime(long time)
    {
        TIME += time;
    }

    /**
     * Adds this find number of hops to the total.
     * @param hop Number of hops.
     */
    synchronized public static void addHop(long hop)
    {
        HOPS += hop;
    }

    /**
     * It counts the times a find_successor was called.
     */
    synchronized public static void addCounter()
    {
        search_counter++;
    }

    /**
     * Returns the average execution time of find_successor.
     * @return Time in seconds with 3 decimal digits precision.
     */
    synchronized public static double getAvgTime()
    {
        return (TIME/search_counter)/1000D;
    }

    /**
     * Returns the average hops per find_successor.
     * @return Average hops (long).
     */
    synchronized public static long getAvgHops()
    {
        return (HOPS/search_counter);
    }

}
