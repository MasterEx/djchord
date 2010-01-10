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

package djchord;

import chord.DJchord;
import java.util.Scanner;
import networking.RMIRegistry;

/**
 *
 *@author Ntanasis Periklis and Chatzipetros Mike
 */
public class Arguments {

    /**
     * This method is the cli of djchord application
     */
    public static void handler(String args[])
    {
        System.out.println("djchord -- a distributed filesystem based on chord written in java\n");
        System.out.println("authors : Ntanasis Periklis and Chatzipetros Mike");
        System.out.println("This application is licensed under the MIT license," +
                "see license.txt for further information");

        if(args.length == 1 && args[0].equalsIgnoreCase("-h"))
        {
            System.out.println("this is the help");
        }
        else if(args.length%2 != 0)
        {
            System.out.println("Syntax error: Unexpected number of arguments, use -h for help");
            System.exit(1);
        }

        int counter = 0;
        
        while(true)
        {
            if (counter == args.length)
            {
                break;
            }

            if (args[counter].equalsIgnoreCase("-startrmi"))
            {
                if (args.length==2)
                {
                    RMIRegistry.createRegistry(Integer.valueOf(args[++counter]));
                }
                else
                {
                    RMIRegistry.createRegistry();
                }
            }

            if (args[counter].equalsIgnoreCase("-createnode"))
            {
                DJchord chord = new DJchord();
                chord.start();
                System.out.println("A node was created." +
                        "\nUse help to see the available options");
                Scanner in = new Scanner(System.in);
                //submenu
                while (true)
                {
                    if(in.next().equalsIgnoreCase("help"))
                    {
                        System.out.println("exit\nhelp\ngetfile\nquit");
                    }
                    else if(in.next().equalsIgnoreCase("quit") || in.next().equalsIgnoreCase("exit"))
                    {
                        System.out.println("The process is terminating...");
                        chord.stop();
                        System.exit(0);
                    }
                    else if(in.next().equalsIgnoreCase("getfile"))
                    {
                        if(in.hasNext())
                        {
                            chord.getFile(in.next());
                        }
                        else
                        {
                            System.out.print("Give file name:");
                            chord.getFile(in.next());
                            System.out.println();
                        }
                    }
                    in.reset();
                }
            }
            counter++;
        }
    }

}
