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

import basic.SHAhash;

/**
 *
 * @author Ntanasis Periklis and Chatzipetros Mike
 */
public class node {
    /**
     * variables
     */
    private SHAhash key;
    private String[] files;//paths for files? path for folder? names of files only?
    private SHAhash[] file_keys;

    /**
     * empty constructor
     */
    public node()
    {

    }
    /**
     * get methods
     */
    public SHAhash getKey()
    {
        return this.key;
    }
    public String[] getFiles()
    {
        return this.files;
    }
    public SHAhash[] getFile_keys()
    {
        return this.file_keys;
    }
    /**
     * set methods
     */
    public void setKey(SHAhash key)
    {
         this.key = key;
    }
    public void setFiles(String[] files)
    {
         this.files = files;
    }
    public void setFile_keys(SHAhash[] file_keys)
    {
         this.file_keys = file_keys;
    }
}
