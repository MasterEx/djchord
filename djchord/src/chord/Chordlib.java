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

import basic.*;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Ntanasis Periklis and Chatzipetros Mike
 */
public class Chordlib {

    /*
     * variables
     */
    private Node djnode;
    private String folder;
    private FileNames files;
    private String[] fileNames;
    private SHAhash[] keys;

    /*
     * methods
     */
    public SHAhash find_succesor(SHAhash k)
    {
        Node search = djnode;
        while(k.compareTo(search.getKey())==1)
        {
            search = search.getNext();
        }
        return search.getKey();
    }
    
    public SHAhash find_succesor(String k) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        return find_succesor(SHA1.getHash(k));
    }

    public void map_insert(SHAhash hash, String name)
    {
        djnode.mapAdd(hash, name);
    }

    public SHAhash[] hashFiles() throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        this.folder = djnode.getFolder();
        files = new FileNames(this.folder);
        fileNames = files.getFileNames();
        for(int i=0;i<fileNames.length;i++)
        {
            keys[i] = SHA1.getHash(fileNames[i]/*,keys*/);
        }
        return keys;
    }

}
