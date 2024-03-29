/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sleepstonetest;

import economy.Data;
import economy.firm.Firm;
import economy.good.GoodType;
import economy.good.Input;
import economy.market.Market;
import economy.workers.LaborMarket;
import economy.workers.WageRoutine;
import sleepstonetest.gui.SleepGUI;

import java.util.ArrayList;

/**
 *
 * @author carrknight
 */
public class SleepStoneTest {

	public final static ArrayList<Firm> firms = new ArrayList<Firm>();;
	public static Market market;

	public static SleepGUI gui = new SleepGUI();


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
	
	static{
		
		market = new Market();
		Firm firmOne;
		if(LaborMarket.consumers){
			firmOne = new Firm(300, "Iron Mine", GoodType.IRON, 10, 2,new Input(GoodType.TOOLS, 0)); firms.add(firmOne);
			
		}
		else{
			firmOne = new Firm(300, "Iron Mine", GoodType.IRON, 10, 2,new Input(GoodType.TOOLS, 10)); firms.add(firmOne);
		}
		
		Firm firmTwo = new Firm(600, "Smelters", GoodType.PIG_IRON, 10, 2,new Input(GoodType.IRON, 10));firms.add(firmTwo);
		Firm firmThree = new Firm(900, "Steel Mill", GoodType.STEEL, 10, 2,new Input(GoodType.PIG_IRON, 10)); firms.add(firmThree);
		Firm firmFour = new Firm(1200, "Equipment Manufactory", GoodType.TOOLS, 10, 2,new Input(GoodType.STEEL, 10)); firms.add(firmFour);

	//	gui= new SleepGUI();
	}

	//public static void main(String[] args) {

	
	//	startSimulation(market);
		//	firmOne.start(); firmTwo.start();firmThree.start();firmFour.start();

	//}

	public void startSimulation(Market market){
		System.out.println("start");
		Data.initializeData();


		for(Firm x : firms)
		{
			x.start();
		}

		market.getLabor().startWorkers();
		if(LaborMarket.consumers)
		{
			WageRoutine wageRoutine = new WageRoutine(market);
			wageRoutine.start();
		}
	}

}
