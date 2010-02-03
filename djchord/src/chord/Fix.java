/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package chord;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Periklis
 */
public class Fix extends Thread{
    
    private Node node;

    public Fix(Node node)
    {
        this.node = node;
        this.setDaemon(true);
        this.start();
    }

    @Override
    public void run()
    {
        System.out.println("In the fix!!!");
        try
        {
            while(true)
            {
                try
                {
                    if(this.node.joinedStabilize())
                    {
                        this.node.initSuccessors();
                        this.node.fixFingers();
                        try
                        {
                            node.sendFiles2ResponsibleNode();
                        }
                        catch(RemoteException ex)
                        {
                            basic.Logger.war("Can't send files right now");
                        }
                        break;
                    }
                }
                catch (NullPointerException ex)
                {
                    try 
                    {
                        //stay in the loop
                        Thread.sleep(200);
                    }
                    catch (InterruptedException ex1)
                    {
                        Logger.getLogger(Fix.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }
        }
        catch (RemoteException ex)
        {
            Logger.getLogger(Fix.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("OUT the fix!!!");
    }

}
