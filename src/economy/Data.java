/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;


import au.com.bytecode.opencsv.CSVWriter;
import economy.firm.Firm;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author carrknight
 */
public class Data {

    /**
     * this is the buffer to the HD writer
     */
    private static  CSVWriter writer;

    /**
     * This is time zero
     */
    private final static Long initialTime = System.currentTimeMillis();


    //I need this to catch the exception!
    static{
        try {
            writer = new CSVWriter(new FileWriter("test.csv"));
        } catch (IOException ex) {
            System.err.println("writer not initialized!");

        }

    }

    /**
     * This just writes down that the firm has started production. It is synchronized
     * @param firm
     * @param goodType
     */
    private static synchronized void registerProduction(Firm firm,
            Good goodType, long time, boolean begin){

        String[] toWrite = new String[4];
        toWrite[0] = Long.toString(time - initialTime);
        if(begin)
            toWrite[1] = "begin";
        else
            toWrite[1] = "end";
        toWrite[2] = firm.getFirmName();
        toWrite[3] = goodType.toString();

        try {
            writer.writeNext(toWrite);
            writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
    }




    /**
     * Writes down that production has started on a different thread (no wait)
     * @param firm
     * @param goodType
     */
    public static void beginProduction(final Firm firm, final Good goodType,
            final Long time){

        Thread thread = new Thread(new Runnable() {

            
            public void run() {
                Data.registerProduction(firm, goodType,time,true);
            }
        });

        thread.start();
    }




    public static void endProduction(final Firm firm, final Good goodType,
            final Long time){

        Thread thread = new Thread(new Runnable() {

            
            public void run() {
                Data.registerProduction(firm, goodType,time,false);
            }
        });

        thread.start();
    }



    public static Long getInitialTime() {
        return initialTime;
    }



}
