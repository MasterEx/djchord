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
 *
 * @author Ntanasis Periklis and Chatzipetros Mike
 */
public class SHAhash implements Comparable{

    private byte[] bytehash = new byte[40];
    private String stringhash;

    /*
     * constructor
     */
    SHAhash(byte[] hash)
    {
        this.bytehash = hash;
        this.stringhash = convertToHex(hash);
    }

    /*
     * compareTo implementation
     */
    public int compareTo(Object arg0)
    {
        SHAhash hash = (SHAhash) arg0;
        for(int i=0;i<40;i++)
        {
            if (bytehash[i]>hash.getByte(i))
            {
                return 1;
            }
            else if (bytehash[i]<hash.getByte(i))
            {
                return -1;
            }
        }
        return 0;
    }

    /*
     * returns bytehash
     */
    public byte[] getByteHash()
    {
        return bytehash;
    }

    /*
     * returns specific byte ofbytehash
     */
    public byte getByte(int position)
    {
        return bytehash[position];
    }

    /*
     * returns stringhash
     */
    public String getStringHash()
    {
        return stringhash;
    }

    private String convertToHex(byte[] data)
    {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++)
        {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do
            {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                {
                    buf.append((char) ('0' + halfbyte));
                }
                else
                {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            }
            while(two_halfs++ < 1);
        }
        return buf.toString();
    }

    /*
     * overides toString()
     */
    @Override
    public String toString()
    {
        return stringhash;
    }

}
