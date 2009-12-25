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

import basic.SHA1;
import basic.SHAhash;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 *
 * @author Ntanasis Periklis and Chatzipetros Mike
 */
public class Node {

    /**
     * variables
     */
    private SHAhash key;
    private String folder;
    private SHAhash[] file_keys;
    private FingerArray[] fingers;
    private Map<SHAhash,String> index;
    private Node successor;
    
    /*
     * an array of fingers
     */
    private class FingerArray {
        public String address;
        public SHAhash key;
    }

    /**
     * empty constructor
     */
    public Node()
    {

    }

    /**
     * get methods
     */

        public SHAhash find_succesor(SHAhash k)
    {
        Node search = this;
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

    public SHAhash getKey()
    {
        return this.key;
    }

    public String getFolder()
    {
        return this.folder;
    }

    public SHAhash[] getFile_keys()
    {
        return this.file_keys;
    }
    
    public Node getNext()
    {
        return successor;
    }

    /**
     * set methods
     */
    public void setKey(SHAhash key)
    {
         this.key = key;
    }

    public void setFolder(String folder)
    {
         this.folder = folder;
    }

    public void setFile_keys(SHAhash[] file_keys)
    {
         this.file_keys = file_keys;
    }
    
    public void setNext(Node next)
    {
        this.successor = next;
    }

    public void mapAdd(SHAhash nodeHash,String fileName)
    {
        if(!this.index.containsKey(nodeHash)&&!this.index.containsValue(fileName))
        {
            this.index.put(nodeHash, fileName);        
        }
    }

}