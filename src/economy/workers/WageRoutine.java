package economy.workers;

import java.util.ConcurrentModificationException;

import javax.management.RuntimeErrorException;

import economy.firm.Firm;
import economy.market.Market;

import sleepstonetest.SleepStoneTest;


/**
 * This routine make firms pay wages every x seconds, where the period is in SleepStoneTest
 * @author carrknight
 *
 */
public class WageRoutine extends Thread {

	Market market;
	
	boolean cancelled = false;
	
	
	
	public WageRoutine(Market market) {
		super();
		this.market = market;
	}



	public void run() {
		
		while(!cancelled){
			
			//wait the agreed time!
			try {
				Thread.sleep(LaborMarket.wagePeriod);
			} catch (InterruptedException e) {
				throw new RuntimeException("What's going on? abort abort abort!");
			}
			
			System.out.println("Paytime!");
			
		/*	for(Firm f : SleepStoneTest.firms)
			{
				for(Consumer c : f.getWorkers())
				{	
					try{
					f.payWage(c);}
					catch(ConcurrentModificationException e){
						throw new RuntimeException("Noooooooooooooo");
					}
				}
			}
			*/
			int i=0;
			for(Consumer c : market.getLabor().workers)
				if(c.getEmployer() != null){
					i++;
					Thread t = new Thread(c);
					t.run();
				}
			System.out.println("I ACTIVATE " + i + " CONSUMERS!");
					
			
			
		}
		
		
	}


}
