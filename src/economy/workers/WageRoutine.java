package economy.workers;

import java.util.ConcurrentModificationException;

import javax.management.RuntimeErrorException;

import economy.firm.Firm;

import sleepstonetest.SleepStoneTest;


/**
 * This routine make firms pay wages every x seconds, where the period is in SleepStoneTest
 * @author carrknight
 *
 */
public class WageRoutine extends Thread {

	boolean cancelled = false;
	
	public void run() {
		
		while(!cancelled){
			
			//wait the agreed time!
			try {
				Thread.sleep(LaborMarket.wagePeriod);
			} catch (InterruptedException e) {
				throw new RuntimeException("What's going on? abort abort abort!");
			}
			
			for(Firm f : SleepStoneTest.firms)
			{
				for(Consumer c : f.getWorkers())
				{	
					try{
					f.payWage(c);}
					catch(ConcurrentModificationException e){
						
					}
				}
			}
			
			
			
		}
		
		
	}


}
