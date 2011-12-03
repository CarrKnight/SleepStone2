package economy.good;

import java.text.DecimalFormat;

import economy.Trader;


/**
 * This is the class representing a single good. It's used mostly for accounting
 * @author carrknight
 *
 */
public class Good implements Comparable<Good>{
	
	
	private static DecimalFormat money = new DecimalFormat("$0.00");
	
	private double priceSold; 
	
	private double costOfProduction;
	
	private double markup;
	
	GoodType goodType;
	
	Trader owner;
	
	
	
	
	public Trader getOwner() {
		return owner;
	}

	public static DecimalFormat getMoney() {
		return money;
	}

	public double getPriceSold() {
		return priceSold;
	}

	public double getCostOfProduction() {
		return costOfProduction;
	}

	public double getMarkup() {
		return markup;
	}

	public GoodType getGoodType() {
		return goodType;
	}

	/**
	 * Use this when the factory finish its production
	 * @param good
	 * @param costOfProduction
	 * @param markup
	 */
	public Good(GoodType good, Trader owner, double costOfProduction, double markup){
		goodType = good;
		this.owner = owner;
		this.markup = markup;
		this.costOfProduction = costOfProduction;
		this.priceSold = costOfProduction *(1d+markup);
	}
	
	/**
	 * This automatically sets also the new markup
	 * @param newPrice
	 */
	public void setPriceSold(double newPrice){
		priceSold = newPrice;
		markup = (priceSold-costOfProduction)/costOfProduction;
		
	}
	


	public synchronized void  trade(Trader buyer){
		buyer.buy(this);
		if(owner!=null)
			owner.sell(this);
		this.owner = buyer;





		
	}

	@Override
	public int compareTo(Good o) {
		double difference = this.priceSold-o.priceSold;
		if(difference>0)
			return 1;
		if(difference<0)
			return -1;
		else 
			return 0;
	}
	

	public String toString(){
		return
				"Good " + goodType + "at price" + getPriceSold() + " owned by:" + owner;
		
	}


}
