/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;

import java.text.DecimalFormat;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.Semaphore;


/**
 * The market is actually just an array of Semaphores. When a firm needs a good it will
 * ask for permission. As many permissions as goods needed.
 * Whenever somebody produces something what they are really doing is giving
 * permissions to the semaphore where consumers are waiting
 * <br>
 * Market itself is just a wrapper around a final static Semaphore.
 * <br>
 * Because semaphore is nothing but an integer counter we'll have to keep goods as
 * indivisible integers.
 * @author carrknight
 */
public class Market {



    final static private Semaphore[] globalInventory = new Semaphore[Good.values().length];


    final static private LaborMarket labor = new LaborMarket(1000, new Market());

    final static private Double[] delays = new Double[Good.values().length];

    public  LaborMarket getLabor() {
        return labor;
    }

    /**
     * the delays will be computed with exponential average L=1/2
     */

    public void registerDelay(Long delay, Good market){
        delays[market.ordinal()] = delays[market.ordinal()] * .5 + ((double) delay) * .5;
        if(market == Good.TOOLS)
     //   	System.out.println("delay in " + market + " is now " + delays[market.ordinal()]);
        	System.out.println("Consumers Waiting : " + globalInventory[market.ordinal()].getQueueLength());

    }

    public double getDelay(Good market){
        return delays[market.ordinal()];
    }

    //static initialization is useful because no thread can touch anything in it
    //so we can fully initialize what's important
    static{
       
        for(Good x : Good.values()){
            globalInventory[x.ordinal()] = new Semaphore(10, true);
            delays[x.ordinal()] = new Double(0);
        }
    }

    /**
     * Initializes the market with n free workers to hire
     * Starts up the market with 10 goods of each
     */
    public Market(){
        //everything else is done in the static initializer
    }


    /**
     * Try to acquire stuff and WAIT until you can.aa
     * @param goodType the type of good you want to buy
     * @param amount how much you want
     * @throws InterruptedException 
     */
    public void buy(Good goodType, int amount) throws InterruptedException{

        globalInventory[goodType.ordinal()].acquire(amount);

    }


   /**
     * Deposit the good type
     * @param goodType the type of good you want to sell
     * @param amount how much you deposit
     */
    public void sell(Good goodType, int amount){

        globalInventory[goodType.ordinal()].release(amount);

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        //let only one iteratation
        synchronized(this){
            for(Good x :Good.values())
            {
                builder.append(x.name()).append("-")
                        .append(globalInventory[x.ordinal()].availablePermits())
                        .append("\n");
            }
        }

        return builder.toString();
    }
    
    

    public synchronized String[] toStringArray(){
    	String[] toReturn = new String[Good.values().length+1];
    	
    	toReturn[0] = Long.toString(System.currentTimeMillis());
    	
    	for(Good x :Good.values()){
    		toReturn[x.ordinal()+1] = Integer.toString(globalInventory[x.ordinal()].availablePermits());
    	}
    	
    	return toReturn;
    	
    	
    }



}
