package economy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import economy.firm.Firm;
import economy.good.Input;
import economy.market.Market;


/**
 * 
 * @author carrknight
 *
 */
public class Consumer extends Thread {

	
	
	
	/**
	 * This is used to check when the consumer ask for more
	 */
	private Random randomizer;
	
	private Market market; 
	
	public Market getMarket() {
		return market;
	}




	private Firm employer = null;
	
	
	public Firm getEmployer() {
		return employer;
	}

	public synchronized void setEmployer(Firm employer) {
		this.employer = employer;
	}

	public Consumer(Market market) {
		super();
		randomizer = new Random();
		this.market = market;
	}
	
	public Consumer(Random randomizer, Market market) {
		super();
		this.randomizer = randomizer;
		this.market = market;
	}

	@Override
	public void run() {
		
		while(employer != null)
		{
			
			long sleepTime = Math.round(randomizer.nextGaussian()*300d + 2800d);
			//select a good at random
			List<Input> possibleConsumerGoods = Market.getPossibleConsumerGoods(); 
			Input toBuy = possibleConsumerGoods.get(randomizer.nextInt(possibleConsumerGoods.size()));

			try {
				Thread.sleep(sleepTime);

				long waitTime = System.currentTimeMillis();
			
				//FIXME todo
		//		market.buy(toBuy.getGood(), toBuy.getAmount(), this);
		//		market.buy(toBuy.getGood(), toBuy.getAmount());
			//	System.out.println("consumer bought " + toBuy.getAmount() + " " + toBuy.getGood());
			} catch (InterruptedException e) {
				System.out.println("fired");
				break;
					
			}

		}
		System.out.println("Out of the loop");
	}
	
	
	
	
	
	
	
	
}
