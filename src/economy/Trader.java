package economy;

import economy.good.Good;
import economy.good.GoodType;

public interface Trader {

	/**
	 * this is called by a good when it's bought.
	 * @param bought
	 */
	public void buy(Good bought);
	
	public void sell(Good sold);
	
}
