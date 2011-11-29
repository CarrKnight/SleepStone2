/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import economy.firm.Firm;
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

    public ReentrantLock lock = new ReentrantLock();

    /**
     * Queues of all workers ready for work!
     * KEEP PRIVATE! 
     */
    private BlockingQueue<Consumer> freeWorkers = new LinkedBlockingQueue<Consumer>();
    
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
    	for(int i = 0; i<totalWorkers; i++)
    	{
    	
    		freeWorkers.add(new Consumer(market));
    			
    	}
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
    public Consumer hire(Firm employer, boolean startTheConsumer){
    	//it has to be locked!
    	assert lock.isLocked();
    	if(freeWorkers.isEmpty()){
    		throw new RuntimeException("Trying to hire non-existant worker!");
    		}

    	Consumer hired = freeWorkers.poll();
    	employedWorkers.add(hired);
    	hired.setEmployer(employer);
    	
    	if(startTheConsumer)
    		hired.start();
    	
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
        fired.interrupt();
        //put it among the freeagents!
        freeWorkers.add(new Consumer(fired.getMarket()));
        
    }
    
    /**
     * When it's time, start the workers
     */
    public void startWorkers(){
    	for (Consumer e : employedWorkers)
    		e.start();
    		
    	
    }

}




