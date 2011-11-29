package economy.market;

import static org.junit.Assert.*;

import org.junit.Test;

import economy.firm.Firm;
import economy.good.Good;
import economy.good.GoodType;
import economy.good.Input;

public class CentralizedExchangeTest {

	@Test
	public final void queueBasicTest() {
		CentralizedExchange exchange = new CentralizedExchange();
		
		Good good1 = new Good(GoodType.IRON, null, 1d, 2d);
		Good good2 = new Good(GoodType.IRON, null, 2d, 2d);
		
		exchange.offer(good1);
		exchange.offer(good2);
		
		try {
			System.out.println(exchange.getInventory());

			Good taken = exchange.getInventory().take();

			taken = exchange.getInventory().take();
			assertEquals(good2, taken);

		} catch (InterruptedException e) {
			fail("this shouldn't happen!");
		}
		
		
	}

	@Test
	public final void basicBuyTest() {
		CentralizedExchange exchange = new CentralizedExchange();
		 Firm firm1 = new Firm(300, "Firm 1", GoodType.MEDICINE, 10, 2,new Input(GoodType.WINE, 10));
	     Firm firm2 = new Firm(600, "Firm 2", GoodType.WINE, 10, 2,new Input(GoodType.MEDICINE, 10));
	
		Good good1 = new Good(GoodType.IRON, firm1, 1d, 2d); 
		firm1.getInventory(GoodType.IRON).add(good1);
		Good good2 = new Good(GoodType.IRON, firm1, 2d, 2d);
		firm1.getInventory(GoodType.IRON).add(good2);
		
		exchange.offer(good1);
		exchange.offer(good2);
		
		
		try {
			System.out.println(exchange.getInventory());
			System.out.println(firm1.getFirmName() +" " + firm1.getInventory(GoodType.IRON));
			System.out.println(firm2.getFirmName() +" " + firm2.getInventory(GoodType.IRON));
			assertTrue(firm1.getInventory(GoodType.IRON).size()==2);
			assertTrue(firm2.getInventory(GoodType.IRON).size()==0);
			assertEquals(-1d, exchange.getAverageDelay(),.1);
			
			exchange.acquire(1, firm2);
			
			System.out.println(exchange.getInventory());
			System.out.println(firm1.getFirmName() +" " + firm1.getInventory(GoodType.IRON));
			System.out.println(firm2.getFirmName() +" " + firm2.getInventory(GoodType.IRON));
			System.out.println(exchange.getAverageDelay());
			assertTrue(firm1.getInventory(GoodType.IRON).size()==1);
			assertTrue(firm2.getInventory(GoodType.IRON).size()==1);
			assertTrue(exchange.getAverageDelay()>=0);
			
			

		} catch (InterruptedException e) {
			fail("this shouldn't happen!");
		}
		
		
		
		
	}
	
}
