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

import java.io.File;

/**
 *
 * @author Ntanasis Periklis and Chatzipetros Mike
 */
public class FileNames {

    /**
     * variables
     */
    private String folderPath;
    private int count,pos;
    private File[] files;
    private File folder;
    private String[] filepaths,temp1,tempstringarray,temp2,retval;
    String separator = "";

    /**
     * constructor
     */
    public FileNames(String folderPath)
    {
        pos = 0;
        this.folderPath = "" + folderPath;
        this.folder = new File(folderPath);
	if(File.separator.equals("\\"))
        {
            separator = "\\\\";
        }
	if(File.separator.equals("/"))
        {
            separator = "/";
        }
    }

    /**
     * methods
     */
    public String[] getFileNames()
    {
        return this.files();
    }

    private String[] files()
    {
        temp1=folderPath.split(separator);
        try
        {
            files = folder.listFiles();
            filepaths = new String[files.length];
            retval = new String[files.length];
            for(int i=0;i<files.length;i++)
            {
                if(!files[i].isHidden())
                {
                    filepaths[i] = files[i].toString();
                    tempstringarray=filepaths[i].split(separator);

                    for(int j=0;j<tempstringarray.length;j++)
                    {
                        count=0;
                        for(int y=0;y<temp1.length;y++)
                        {
                            if(tempstringarray[j].equals(temp1[y]))
                            {
                                count++;
                                break;
                            }
                        }
                        if(count==0)
                        {
                            retval[pos] = tempstringarray[j];
                            pos++;
                        }
                    }
                }
            }
        }
        catch (NullPointerException e)
        {
            System.err.println("The folder is empty or doesn't exist");
        }
        return retval;
    }

}
