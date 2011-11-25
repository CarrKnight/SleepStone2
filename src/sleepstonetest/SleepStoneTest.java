/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sleepstonetest;

import economy.Data;
import economy.Good;
import economy.Input;
import economy.Market;
import economy.firm.Firm;

import java.util.ArrayList;
import java.util.concurrent.Executor;

/**
 *
 * @author carrknight
 */
public class SleepStoneTest {

	public final static ArrayList<Firm> firms = new ArrayList<Firm>();;
	



	/**
     * @param args the command line arguments
     */
/*    public static void main(String[] args) {

        @SuppressWarnings("unused")
		Market market = new Market();

        Firm firmOne = new Firm(300, "Firm 1", Good.MEDICINE, 10, 2,new Input(Good.WINE, 10));
        Firm firmTwo = new Firm(600, "Firm 2", Good.WINE, 10, 2,new Input(Good.MEDICINE, 10));
        firmOne.start(); firmTwo.start();

    }
    
    */
	
	public static void main(String[] args) {

		@SuppressWarnings("unused")
		Market market = new Market();
		Firm firmOne = new Firm(300, "Iron Mine", Good.IRON, 10, 2,new Input(Good.TOOLS, 0)); firms.add(firmOne);
		Firm firmTwo = new Firm(600, "Smelters", Good.PIG_IRON, 10, 2,new Input(Good.IRON, 10));firms.add(firmTwo);
		Firm firmThree = new Firm(900, "Steel Mill", Good.STEEL, 10, 2,new Input(Good.PIG_IRON, 10)); firms.add(firmThree);
		Firm firmFour = new Firm(1200, "Equipment Manufactory", Good.TOOLS, 10, 2,new Input(Good.STEEL, 10)); firms.add(firmFour);



		startSimulation(market);
	//	firmOne.start(); firmTwo.start();firmThree.start();firmFour.start();

	}

	private static void startSimulation(Market market){
		Data.initializeData();
		for(Firm x : firms)
		{
			x.start();
		}
		market.getLabor().startWorkers();

	}

}
