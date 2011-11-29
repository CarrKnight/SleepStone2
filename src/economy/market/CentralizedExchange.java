package economy.market;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;


import economy.Trader;
import economy.good.Good;
import economy.good.GoodType;

/**
 * This is basically a priority blocking queue that also holds the 
 * exponential average of delays in getting the stuff 
 * @author carrknight
 *
 */
public class CentralizedExchange {

	private PriorityBlockingQueue<Good> goodsToSell;
	
	private static final double delayAverageWeight = .9;
	
	Good lastGoodTraded;
	
	/**
	 * This counts the delay of getting single items.
	 * Starts at -1 waiting for the first delay
	 */
	private double averageDelay = -1; 


	public double getAverageDelay() {
		return averageDelay;
	}

	public String getPrice() {
		final DecimalFormat money = new DecimalFormat("$0.00");
		
		
		if(lastGoodTraded==null)
			return("NaN");
		else
			return(money.format(lastGoodTraded.getPriceSold()));
	}


	/**
	 * use only for debugging!
	 * @return
	 */
	PriorityBlockingQueue<Good>  getInventory(){
		
		return goodsToSell;
	}
	
	
	

	public int size() {
		return goodsToSell.size();
	}



	/**
	 * We assume initial markup to be 20%
	 * @param initialGoods
	 * @param initialPrice
	 * @param typeOfGood
	 */
	public CentralizedExchange(int initialGoods,double initialPrice,GoodType typeOfGood) {
		goodsToSell = new PriorityBlockingQueue<Good>();
		
		for(int i=0; i <initialGoods; i++)
			goodsToSell.add(new Good(typeOfGood, null, initialPrice, .2));
	}
	
	/**
	 * This is to initialize an empty market!
	 * @param initialGoods
	 * @param initialPrice
	 * @param typeOfGood
	 */
	public CentralizedExchange() {
		goodsToSell = new PriorityBlockingQueue<Good>();

	}

	/**
	 * buys the goods, return the total cost.
	 * @param amount
	 * @param buyer
	 * @throws InterruptedException
	 */
	public double acquire(int amount, Trader buyer) throws InterruptedException {
		double totalCost = 0d;
		
		//if you are here you have acquired permits! trade them!
		for(int i=0; i < amount; i++){
			Good good = take();
			good.trade(buyer);
			lastGoodTraded = good;
			totalCost +=good.getPriceSold();
		}
		
		return totalCost;
	}

	public void offer(Good onSale) {
	
		goodsToSell.add(onSale);
		
	}
	
	
	
	/**
	 * The addition is the computation of delay
	 * @throws InterruptedException
	 */
	private Good take() throws InterruptedException{
		//start timer
		long time = System.currentTimeMillis();
		//take the good
		Good taken = goodsToSell.take();
		//stop the timer
		long delay = System.currentTimeMillis()-time;
		registerDelay(delay);
//		if(taken.getGoodType()!=GoodType.IRON){
//			System.out.println("delay :" + delay);
//			System.out.println("delay :" + averageDelay);
//		}		
		//give the good back!
		return taken;
	}
	
	private synchronized void registerDelay(long delay){
		if(delay<0)
			throw new RuntimeException("Negative delay, ain't that fucked up!");
		
		if(averageDelay==-1)
			averageDelay = delay;
		else
			averageDelay  = ((double)averageDelay)  * delayAverageWeight + 
							((double) delay) * delayAverageWeight;
		
	}

	
	
	
}
