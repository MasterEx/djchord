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
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Ntanasis Periklis and Chatzipetros Mike
 */
public class FileNames {

    /**
     * variables
     */
    private int count,pos;
    private File[] files;
    private File folder;
    private String[] fiiles,a,filess,b,returnedfilenames;

    /**
     * constructors
     */
    public FileNames(String folderPath)
    {
        this.returnedfilenames = this.returnFileNames(folderPath);
    }
    public String[] getFileNames()
    {
        return this.returnedfilenames;
    }
    private String[] returnFileNames(String folderPath)
    {
        this.folder= new File(folderPath);
        pos = 0;
        this.a=folderPath.split(File.separator);
        try
        {
            this.files = this.folder.listFiles();
            this.fiiles = new String[this.files.length];
        
            for(int i=0;i<this.files.length;i++)
            {
                if(!this.files[i].isHidden())
                {
                    this.fiiles[i] = this.files[i].toString();
                    this.filess=this.fiiles[i].split(File.separator);

                    for(int j=0;j<this.filess.length;j++)
                    {
                        this.count=0;
                        for(int y=0;y<this.a.length;y++)
                        {
                            if(this.filess[j].equals(this.a[y]))
                            {
                                this.count++;
                                break;
                            }
                        }
                        if(this.count==0)
                        {
                            if(this.filess[j].contains("."))
                            {
                                this.b=this.filess[j].split("\\.");
                                returnedfilenames[pos]=this.b[0];
                                pos++;
                            }
                            else 
                            {
                                returnedfilenames[pos]=this.filess[j];
                                pos++;
                            }
                        }
                    }
                }
            }
        }
        catch (NullPointerException e)
        {
            System.err.println("The folder is empty or doesn't exist");
        }
        return returnedfilenames;
    }

}
