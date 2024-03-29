/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;


import au.com.bytecode.opencsv.CSVWriter;
import economy.firm.Firm;
import economy.good.GoodType;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import sleepstonetest.SleepStoneTest;


/**
 *
 * @author carrknight
 */
public class Data {

    /**
     * this is the buffer to the HD writer
     */
    private static  CSVWriter firmWriter;
    private static  CSVWriter marketWriter;
    private static  CSVWriter workerWriter;
    private static CSVWriter delayWriter;
    private static CSVWriter priceWriter;
    private static CSVWriter moneyWriter;
    
    /**
     * This is time zero
     */
    private final static Long initialTime = System.currentTimeMillis();


    //I need this to catch the exception!
    static{
        try {
            firmWriter = new CSVWriter(new FileWriter("production.csv"));
            
            marketWriter = new CSVWriter(new FileWriter("market.csv"));
            //write the good names as header!
            String[] goodTitles = new String[GoodType.values().length+1];
            goodTitles[0] = "Time";
            for(GoodType x : GoodType.values())
            {
            	goodTitles[x.ordinal()+1] = x.name();
            }
            marketWriter.writeNext(goodTitles);
            
            //recycle the headers for the delay writer
            delayWriter = new CSVWriter(new FileWriter("delay.csv"));
            delayWriter.writeNext(goodTitles);
            
            priceWriter = new CSVWriter(new FileWriter("price.csv"));
            priceWriter.writeNext(goodTitles);
            
            moneyWriter  = new CSVWriter(new FileWriter("money.csv"));
            String[] firmTitles = new String[SleepStoneTest.firms.size()+1];
            firmTitles[0] = "Time";
            int i=1;
            for(Firm x :SleepStoneTest.firms)
            {
            	firmTitles[i++] = x.getName();
            }
            moneyWriter.writeNext(firmTitles);
            
            
        } catch (IOException ex) {
        	System.err.println("writer not initialized!");
        }

    }

    private static void registerDelays(Firm firm, Long time) throws IOException{
    	
    	  String[] delays = firm.getMarket().toStringArray();
    	  delays[0] = Long.toString(time - getInitialTime());
    	  for(GoodType x : GoodType.values())
    	  {
    		  delays[x.ordinal()+1] = Double.toString(firm.getMarket().getDelay(x));
    	  }
          delayWriter.writeNext(delays);
          delayWriter.flush();
    	
    }
    
    /**
     * After all firms have been created, please call this!
     */
    public static void initializeData(){
    	try {
    		workerWriter = new CSVWriter(new FileWriter("worker.csv"));
    		//go through firms and write their name
    		int numberOfFirms = SleepStoneTest.firms.size();
    		String[] headers = new String[numberOfFirms+1];
    		headers[0] = "time";
    		for(int i=0; i < numberOfFirms; i++)
    		{
    			headers[i+1] = SleepStoneTest.firms.get(i).getFirmName();
    		}
    		workerWriter.writeNext(headers);
    		headers=null;
    		
    		//now write the initial workers
    		String[] workers = new String[numberOfFirms+1];
    		workers[0] = Integer.toString(0);
    		for(int i=0; i < numberOfFirms; i++)
    		{
    			workers[i+1] = Integer.toString(SleepStoneTest.firms.get(i).getFirmSize());
    		}
    		workerWriter.writeNext(workers);
    		
    		workerWriter.flush();

    	} catch (IOException e) {
			System.err.println("worker writer not initialized!");
		}
    	 
    	
    	
    }

    /**
     * This just writes down that the firm has started production. It is synchronized
     * @param firm
     * @param goodType
     */
    private static synchronized void registerProduction(Firm firm,
            GoodType goodType, long time, boolean begin){

        String[] toWrite = new String[4];
        toWrite[0] = Long.toString(time - initialTime);
        if(begin)
            toWrite[1] = "begin";
        else
            toWrite[1] = "end";
        toWrite[2] = firm.getFirmName();
        toWrite[3] = goodType.toString();

        try {
            firmWriter.writeNext(toWrite);
            firmWriter.flush();
            
            
            String[] marketStatus = firm.getMarket().toStringArray();
            marketStatus[0] = Long.toString(Long.parseLong(marketStatus[0]) - getInitialTime());
            marketWriter.writeNext(marketStatus);
            marketWriter.flush();
            registerDelays(firm, time);

            /*************************************
             * 
             * Prices
             * 
             *************************************/
            String[] prices = firm.getMarket().toStringArray();
            prices[0] = Long.toString(time - getInitialTime());
            for(GoodType x : GoodType.values())
            {
            	prices[x.ordinal()+1]=firm.getMarket().getPrice(x);
            }
           // System.out.println("prices: " + prices);
            priceWriter.writeNext(prices);
            priceWriter.flush();


            /***************************************
             * Money
             ***************************************/
        	DecimalFormat formatter = new DecimalFormat("$0.00");

            String[] money = new String[SleepStoneTest.firms.size()+1];
            money[0] = prices[0];
            int i=1;
            for(Firm x :SleepStoneTest.firms)
            {
            	money[i++] = formatter.format(x.getMoney());
            }
            moneyWriter.writeNext(money);
            moneyWriter.flush();
            
        } catch (IOException ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
    }




    /**
     * Writes down that production has started on a different thread (no wait)
     * @param firm
     * @param goodType
     */
    public static void beginProduction(final Firm firm, final GoodType goodType,
            final Long time){

        Thread thread = new Thread(new Runnable() {

            
            public void run() {
                Data.registerProduction(firm, goodType,time,true);
            }
        });

        thread.start();
    }




    public static void endProduction(final Firm firm, final GoodType goodType,
            final Long time){

        Thread thread = new Thread(new Runnable() {

            
            public void run() {
                Data.registerProduction(firm, goodType,time,false);
            }
        });

        thread.start();
    }


    public static void countWorkers(final Long time){

    	//update on a separate thread!
    	
        Thread thread = new Thread(new Runnable() {

            
            public void run() {
                
            	//now write the initial workers
            	int numberOfFirms = SleepStoneTest.firms.size();
        		String[] workers = new String[numberOfFirms+1];
        		workers[0] = Long.toString(time - getInitialTime());
        		for(int i=0; i < numberOfFirms; i++)
        		{
        			workers[i+1] = Integer.toString(SleepStoneTest.firms.get(i).getFirmSize());
        		}
        		workerWriter.writeNext(workers);
        		try {
					workerWriter.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
            	
            	
            }
        });

        thread.start();
    }
    
    

    public static Long getInitialTime() {
        return initialTime;
        
        
        
    }



}
