package economy.workers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import economy.Trader;
import economy.firm.Firm;
import economy.good.Good;
import economy.good.Input;
import economy.market.Market;


/**
 * 
 * @author carrknight
 *
 */
public class Consumer implements Runnable, Trader {

	/**
	 * So sometimes consumers get paid too little to make money, this allows them to ask a bit more than the real wage
	 * (as long as adapting is true in the laborMarket) to prevent that
	 */
	public static double priceExpectations = 1.2d;
	
	private String name;
	
	ArrayList<Good> inventory= new ArrayList<Good>();
	
	/**
	 * This is used to check when the consumer ask for more
	 */
	private Random randomizer;
	
	private Market market; 
	
	public Market getMarket() {
		return market;
	}


	private double money = 0d;
	
	private void setMoney(double newmoney){
		this.money=newmoney;
	}
	
	public synchronized double getMoney(){
		return money;
	}
	

	private Firm employer = null;
	
	
	public Firm getEmployer() {
		return employer;
	}

	public synchronized void setEmployer(Firm employer) {
		this.employer = employer;
	}

	public Consumer(Market market, String id) {
		super();
		randomizer = new Random();
		this.market = market;
		name = "Consumer " + id;
		
	}
	
	public Consumer(Random randomizer, Market market) {
		super();
		this.randomizer = randomizer;
		this.market = market;
	}

	/**
	 * When this is called (which should be right after being paid)
	 * the consumer goes to buy stuff
	 */
	@Override
	public void run() {
		//check if you are used
		if(! LaborMarket.consumers)
			return;
		if(employer==null)
			throw new RuntimeException("Running a consumer with no employer!");

		//select a good at random
		List<Input> possibleConsumerGoods = Market.getPossibleConsumerGoods(); 
		Input toBuy = possibleConsumerGoods.get(randomizer.nextInt(possibleConsumerGoods.size()));

		
		
		try {
			
			market.buy(toBuy.getGood(), toBuy.getAmount(), this);
			

		/*	if(money>= new Double(Market.getPriceDouble(toBuy.getGood()) * toBuy.getAmount())){

				priceExpectations = Math.max(1d, priceExpectations - 0.025);
				market.buy(toBuy.getGood(), toBuy.getAmount(), this);
				System.out.println("Consumer bought a good");
				System.out.println("expectations" + priceExpectations);

				if(LaborMarket.adaptive)
					money=0;

			}
			else{

				if(money>0)
					priceExpectations += .05d;
				//					System.out.println("Consumer with " +money+ " couldn't afford to pay " 
				//							+ Market.getPriceDouble(toBuy.getGood()) * toBuy.getAmount());
				System.out.println("expectations" + priceExpectations);


				if(LaborMarket.adaptive)
					money=0;
*/
			
			
			


		} catch (InterruptedException e) {
			System.out.println("fired");
		}
}


	/**
	 * this is called by the employee to pay this consumer whatever wage. 
	 * Also it creates its own thread to spend it!
	 */
	public void earnWage(double paycheck){
		money += paycheck;
		Thread thread = new Thread(this);
		thread.start();
		
	}

	@Override
	public void buy(Good bought) {
		//object immediately consumed
		//inventory.add(bought);
		money -= bought.getPriceSold();
		
	}

	@Override
	public void sell(Good sold) {
		boolean valid = inventory.remove(sold);
		money += sold.getPriceSold();
		
		if(valid==false)
			throw new RuntimeException(this + " sold a good it doesn't have!");
		
	}

	@Override
	protected Consumer clone() throws CloneNotSupportedException {
		Consumer clone = new Consumer(market,"toRename");
		// TODO Auto-generated method stub
		clone.setMoney(money);
		clone.name = this.name;
		return clone;
	}
	
	
	
	
	
	
}
