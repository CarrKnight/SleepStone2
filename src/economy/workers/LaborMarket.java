/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package economy.workers;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import economy.firm.Firm;
import economy.good.Input;
import economy.market.Market;

/**
 * This is basically the simplest Labor Market Possible. It's an integer having the number of free workers
 * available. There is a lock you must close when using the public functions or it will throw an exception <br>
 * You can check if the market is empty, you can try to hire or try to fire somebody.
 * Remember to unlock when done!
 *
 * @author carrknight
 */
public class LaborMarket {

	/**
	 * Do consumers matter
	 */
	public static final boolean consumers = false;
	
	public static final boolean adaptive = true;
	
	public final static long wagePeriod = 6000;

	
	
	public double getCurrentWage(){
		if(!consumers){
			return 0d;
		}
		else{
		double sumPrices=0d;
		
		for(Input i : Market.getPossibleConsumerGoods()){
			sumPrices = new Double(i.getAmount()) * Market.getPriceDouble(i.getGood());
		}
		sumPrices = sumPrices / new Double(Market.getPossibleConsumerGoods().size());
		System.out.println("wages are: " + sumPrices);
		if(!adaptive)
			return sumPrices;
		else
			return sumPrices * Consumer.priceExpectations;
		}
	}
	
    public ReentrantLock lock = new ReentrantLock();

    /**
     * Queues of all workers ready for work!
     * KEEP PRIVATE! 
     */
    private BlockingQueue<Consumer> freeWorkers = new LinkedBlockingQueue<Consumer>();
    
    /**
     * this is just a normal list keeping all workers in. This is useful for the GUI
     */
    public List<Consumer> workers;
    
    
    /**
     * Queues of workers that are consuming right now
     * KEEP PRIVATE! 
     */
    private BlockingQueue<Consumer> employedWorkers = new LinkedBlockingQueue<Consumer>();
    
    
    //AtomicInteger totalWorkers;
  //  AtomicInteger freeWorkers;

    /**
     * Labor market instantiate also all the consumers. You need to call laborStart() to make them start consuming
     * @param totalWorkersNum
     * @param market
     */
    public LaborMarket(int totalWorkers, Market market) {

    	//here we keep another list of workers, we are going to make it immutable and searchable by the GUI
    	LinkedList<Consumer> workers = new LinkedList<Consumer>();
    	
    	
    	for(int i = 0; i<totalWorkers; i++)
    	{
    		Consumer c = new Consumer(market, ""+i);
    		freeWorkers.add(c);
    		workers.add(c);
    		
    	}
    	this.workers = Collections.unmodifiableList(workers);
        //this.freeWorkers = new AtomicInteger(freeWorkers);
        //this.totalWorkers = new AtomicInteger(totalWorkers);
    }

    public boolean isThereAFreeWorker(){
        //it has to be locked!
        assert lock.isLocked();
        return(!freeWorkers.isEmpty());
    
    }

    /**
     * Call this (after locking) to hire a new worker
     * @param employer firm hiring
     * @param startTheConsumer if this is not in the initialization of the firm, make this true
     */
    //TODO remove the boolean argument
    public Consumer hire(Firm employer, boolean startTheConsumer){
    	//it has to be locked!
    	assert lock.isLocked();
    	if(freeWorkers.isEmpty()){
    		throw new RuntimeException("Trying to hire non-existant worker!");
    		}

    	Consumer hired = freeWorkers.poll();
    	employedWorkers.add(hired);
    	hired.setEmployer(employer);
    	
   // 	if(startTheConsumer)
   // 		hired.start();
    	
    	return hired;
    }


    public void fire(Firm employer, Consumer fired){
        //it has to be locked!
        assert lock.isLocked();
        //remove it from the work
        if(!employedWorkers.remove(fired))
        {
        	throw new RuntimeException("Trying to fire an unemployed worker!");
        }

        //stop it
        fired.setEmployer(null);
        // fired.interrupt();
        //put it among the freeagents!
        //try {
        freeWorkers.add(fired);
        //	freeWorkers.add(fired.clone());
        //}
        /*catch (CloneNotSupportedException e) {
			throw new RuntimeException("Clone error when firing, what happened?");
		}*/

    }

    /**
     * When it's time, start the workers
     */
    public void startWorkers(){
    	//useless
    	
    	//for (Consumer e : employedWorkers)
    		//e.start();
    		
    	
    }

}




