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

import java.util.Vector;

/**
 *
 *@author Ntanasis Periklis and Chatzipetros Mike
 */
public class DetailsTable {

    static private Vector<String> address = new Vector<String>();
    static private Vector<String> name = new Vector<String>();
    static private Vector<Integer> port = new Vector<Integer>();

    static private void setAddress(String address)
    {
        DetailsTable.address.addElement(address);
    }

    static private void setName(String name)
    {
        DetailsTable.name.addElement(name);
    }

    static private void setPort(int port)
    {
        DetailsTable.port.addElement(port);
    }

    static public void setNodeDetails(String address,String name, int port)
    {
        DetailsTable.address.addElement(address);
        DetailsTable.name.addElement(name);
        DetailsTable.port.addElement(port);
    }

    static public String getAddress(int i)
    {
        return DetailsTable.address.get(i);
    }

    static public String getName(int i)
    {
        return DetailsTable.name.get(i);
    }

    static public int getPort(int i)
    {
        return DetailsTable.port.get(i);
    }

    static public int getLength()
    {
        return DetailsTable.port.size();
    }

    static public int searchByAddress(String address)
    {
        for(int i=0;i<DetailsTable.getLength();i++)
        {
            if(address.equals(DetailsTable.address.get(i)))
            {
                return i;
            }
        }
        return DetailsTable.getLength()-1;
    }

    static public int searchByName(String name)
    {
        for(int i=0;i<DetailsTable.getLength();i++)
        {
            if(name.equals(DetailsTable.name.get(i)))
            {
                return i;
            }
        }
        return DetailsTable.getLength()-1;
    }

    static public int searchByPort(int port)
    {
        for(int i=0;i<DetailsTable.getLength();i++)
        {
            if(port==DetailsTable.port.get(i))
            {
                return i;
            }
        }
        return DetailsTable.getLength()-1;
    }

    static public void trim()
    {
        DetailsTable.address.trimToSize();
        DetailsTable.name.trimToSize();
        DetailsTable.port.trimToSize();
    }

}
