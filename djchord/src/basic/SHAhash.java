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
public class SHAhash implements Comparable<SHAhash>{

    private byte[] bytehash = null;
    private String stringhash;

    /*
     * constructor
     */
    public SHAhash(byte[] hash)
    {
        this.bytehash = hash;
        this.stringhash = convertToHex(hash);
    }
    
    /*
     * constructor
     */
    public SHAhash(String hash)
    {
        this.stringhash = hash;
    }

    /*
     * compareTo implementation
     */
    public int compareTo(SHAhash arg0)
    {
        String this_temp = this.getStringHash();
        String arg0_temp = arg0.getStringHash();
        if(this_temp.length()>arg0_temp.length())
        {
            for(int i=arg0_temp.length();i<this_temp.length();i++)
            {
                this_temp = "0".concat(this_temp);
            }
        }
        else if(this_temp.length()<arg0_temp.length())
        {
            for(int i=this_temp.length();i<arg0_temp.length();i++)
            {
                arg0_temp = "0".concat(arg0_temp);
            }
        }
        return this_temp.compareToIgnoreCase(arg0_temp);
    }

    /*
     * returns bytehash
     */
    public byte[] getByteHash()
    {
        if(this.bytehash != null)
        {
            return this.bytehash;
        }
        return this.stringhash.getBytes();
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

    /*
     * This method rises one hexadecimal number to the given power
     */
    public static String power(String base,int exponent)
    {
        for(int i=0;i<exponent;i++)
        {
            base = SHAhash.add(base, base);
        }
        return base;
    }

    /*
     * This method adds 2 hexadecimal numbers
     */
    public String add(String hex)
    {
        return SHAhash.add(this.getStringHash(), hex);
    }

    /*
     * This method adds 2 hexadecimal numbers
     */
    public static String add(String hex,String hexx)
    {
        String outcome = "",temp,rest="0",t3 = "0",t4;
        int max,t1,t2;
        char a,b;
        if(hexx.compareTo(hex)>0)
        {
            max = hexx.length();
        }
        else
        {
            max = hex.length();
        }
        for(int i=1;i<=max;i++)
        {
            if(hexx.length()<i)
            {
                a = '0';
            }
            else
            {
                a = hexx.charAt(hexx.length()-i);
            }
            if(hex.length()<i)
            {
                b = '0';
            }
            else
            {
                b = hex.charAt(hex.length()-i);
            }
            t1 = Integer.parseInt(String.valueOf(a), 16);
            t2 = Integer.parseInt(String.valueOf(b), 16);
            temp = Integer.toHexString(t1+t2);
            if(Integer.valueOf(rest)!=0)
            {
                //is there any case of having second rest?
                t3 = String.valueOf(temp.charAt(temp.length()-1));
                t3 = Integer.toHexString(Integer.parseInt(t3, 16)+Integer.parseInt(rest, 16));
                t4 = String.valueOf(t3.charAt(t3.length()-1));
                outcome = t4.concat(outcome);
                if(t3.length()>1)
                {
                    rest = String.valueOf(t3.charAt(0));
                }
            }
            else
            {
                outcome = String.valueOf(temp.charAt(temp.length()-1)).concat(outcome);
            }
            if(temp.length()>1)
            {
                rest = String.valueOf(temp.charAt(0));
            }
            else
            {
                if(t3.length()<2)
                {
                   rest = "0"; 
                }                
            }
        }
        if(!rest.equalsIgnoreCase("0"))
        {
            outcome = rest.concat(outcome);
        }
        return outcome;
    }

    /*
     * a method that subtracts a hexadesimal String from another (first-second)
     * special thanks to mathforum.org
     * http://mathforum.org/library/drmath/view/55943.html
     */
    public static String subtract(String first, String second) throws Exception
    {
        if(first.compareTo(second)>0)
        {
            String outcome = "", borrowed="0", borrowed2="0", t3 = "0";
            int max, t1, t2;
            String a,b;
            max = first.length();
            for(int i=1;i<=max;i++)
            {
                a = String.valueOf(first.charAt(first.length()-i));
                if(i>second.length())
                {
                    b = "0";
                }
                else
                {
                    b = String.valueOf(second.charAt(second.length()-i));
                }
                if((borrowed.equalsIgnoreCase("1") || borrowed2.equalsIgnoreCase("1")) && !a.equalsIgnoreCase("0"))
                {
                    t1 = Integer.parseInt(a, 16) - Integer.parseInt((borrowed.equalsIgnoreCase("1")?borrowed:borrowed2),16);
                    borrowed = "0";
                    borrowed2 ="0";
                }
                else if(borrowed.equalsIgnoreCase("1") || borrowed2.equalsIgnoreCase("1"))
                {
                    t1 = (Integer.parseInt(a, 16) + Integer.parseInt("10", 16)) - Integer.parseInt((borrowed.equalsIgnoreCase("1")?borrowed:borrowed2),16);
                    borrowed2 = "1";
                }
                else
                {
                    t1 = Integer.parseInt(a, 16);
                }
                t2 = Integer.parseInt(b, 16);
                if(Integer.toHexString(t1).compareTo(Integer.toHexString(t2))>=0)
                {
                    t3 = Integer.toHexString(t1-t2);
                    borrowed="0";
                }
                else
                {
                    t3 = Integer.toHexString((t1+Integer.parseInt("10", 16))-t2);
                    borrowed="1";
                }
                outcome = t3.concat(outcome);
            }
            return outcome;
        }
        throw new ArithmeticException("trying to subtract much from little");
    }
    
}
