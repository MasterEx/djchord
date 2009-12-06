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
 *@author Ntanasis Periklis and Chatzipetros Mike
 */
class djint {

    private byte[] m;

    /*
    *empty constructor
    */
    public djint()
    {
        m = new byte[160];
    }

    /*
     *byte constructor
     */
    public djint(byte[] n)
    {
        m=n;
    }

    /*
     *returns the byte value
     */
    public byte[] byteValue(){
        return this.m;
    }

    //den katafera na kanw override/overload (opws legetai :P) tous telestes :(
    /*
     *add
     */
    public djint add(djint n)
    {
        byte[] j = n.byteValue();
        for (int i=0;i<160;i++)
        {
            this.m[i]+=j[i];
        }
        return this;
    }
    /*
     *abstraction
     */
    public djint sub(djint n)
    {
        byte[] j = n.byteValue();
        for (int i=0;i<160;i++)
        {
            this.m[i]-=j[i];
        }
        return this;
    }
 
}