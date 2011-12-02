/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package economy.market;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Semaphore;

import economy.Trader;
import economy.good.Good;
import economy.good.GoodType;
import economy.good.Input;
import economy.workers.LaborMarket;


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

	
	/**
	 * this is the list of possible goods demanded by consumers, it's set up at default by the static intializer
	 * but it can be set through the setter
	 */
	private static List<Input> possibleConsumerGoods;
	
	static{
		
		possibleConsumerGoods = new ArrayList<Input>();
		possibleConsumerGoods.add(new Input(GoodType.TOOLS, 1));
		
	}
	
	
	
	public static List<Input> getPossibleConsumerGoods() {
		return possibleConsumerGoods;
	}




    final static private CentralizedExchange[] globalInventory = new CentralizedExchange[GoodType.values().length];


    final static private LaborMarket labor = new LaborMarket(1000, new Market());


    public  LaborMarket getLabor() {
        return labor;
    }


    public String getPrice(GoodType market){
    	return globalInventory[market.ordinal()].getPrice();
    	
    }
    
    public static double getPriceDouble(GoodType market){
    	return globalInventory[market.ordinal()].getPriceDouble();
    	
    }

    public double getDelay(GoodType market){
    	double delay =  globalInventory[market.ordinal()].getAverageDelay();
    	if(market.equals(GoodType.TOOLS))
    		System.out.println("Delay in tools:" + delay);
    	
        return delay;
    }

    //static initialization is useful because no thread can touch anything in it
    //so we can fully initialize what's important
    static{
       
        for(GoodType x : GoodType.values()){
            globalInventory[x.ordinal()] = new CentralizedExchange(10, 1d, x);

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
    public double buy(GoodType goodType, int amount,Trader buyer) throws InterruptedException{

        return(globalInventory[goodType.ordinal()].acquire(amount, buyer));

    }


   /**
     * Deposit the good type
     * @param goodType the type of good you want to sell
     * @param amount how much you deposit
     */
    public void sell(Good toBeSold){

        globalInventory[toBeSold.getGoodType().ordinal()].offer(toBeSold);

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        //let only one iteratation
        synchronized(this){
            for(GoodType x :GoodType.values())
            {
                builder.append(x.name()).append("-")
                        .append(globalInventory[x.ordinal()].size())
                        .append("\n");
            }
        }

        return builder.toString();
    }
    
    

    public synchronized String[] toStringArray(){
    	String[] toReturn = new String[GoodType.values().length+1];
    	
    	toReturn[0] = Long.toString(System.currentTimeMillis());
    	
    	for(GoodType x :GoodType.values()){
    		toReturn[x.ordinal()+1] = Integer.toString(globalInventory[x.ordinal()].size());
    	}
    	
    	return toReturn;
    	
    	
    }



}
