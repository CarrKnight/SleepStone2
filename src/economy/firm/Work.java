/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package economy.firm;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This just waits 10 milliseconds. This way it "wastes time" without actually
 * taking CPU resources
 * @author carrknight
 */
public class Work implements Runnable {


    Firm firm;

    public Work(Firm firm) {
        this.firm = firm;
    }



    
    public void run() {
        try {
            //sleep 10 milli-seconds!
            Thread.sleep(10);
        } catch (InterruptedException ex) {
           System.out.println(ex);
           System.exit(0);
        }

        firm.getJobsDone().release();
    }




}
