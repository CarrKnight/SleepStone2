/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package economy.firm;

import economy.Data;
import economy.Trader;
import economy.good.Good;
import economy.good.GoodType;
import economy.good.Input;
import economy.market.CentralizedExchange;
import economy.market.Market;
import economy.workers.Consumer;
import economy.workers.LaborMarket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import javax.management.RuntimeErrorException;


import static org.junit.Assert.*;

import org.junit.Test;

/**
 *
 * @author carrknight
 */
public class Firm extends Thread implements Trader{


	double money=100;

	private FirmStatus status = FirmStatus.PRODUCING;



	public synchronized FirmStatus getStatus() {
		return status;
	}



	public synchronized void setStatus(FirmStatus status) {
		this.status = status;
	}



	public double getMoney() {
		return money;
	}

	private double markup=.05d;

	private double productionCost;



	@SuppressWarnings("unused")
	private List<Good>[] inventory = (ArrayList<Good>[])new ArrayList[GoodType.values().length];



	public List<Good> getInventory(GoodType type) {
		return inventory[type.ordinal()];
	}

	/**
	 * Whenever a job is completed workers "add" one to the semaphore.
	 * The firm wait at the semaphore until all jobs are done
	 */
	private Semaphore jobsDone = new Semaphore(0);
	/**
	 * This is the "pool of work to be done"
	 */
	private ExecutorService workpool;

	public Semaphore getJobsDone() {
		return jobsDone;
	}

	//one market for all
	//Although everyone has its own reference the market itself is just a wrapper
	//around a single static semaphore
	private Market market = new Market();




	public Market getMarket() {
		return market;
	}

	/**
	 * Does it hire/fire workers?
	 */
	private boolean isAdaptive = true;

	final static private int defaultWorkers=2;

	private long maxInputWaitingTime = 500l;
	private long maxOutputWaitingTime = 250l;

	//  private int workers= defaultWorkers;
	//  public int getWorkers() {return workers;}

	final private ArrayList<Consumer> workers = new ArrayList<Consumer>();

	/**
	 * This returns an unmodifiable list of workers, used to pay wages by the wage routine!
	 * @return
	 */
	synchronized public List<Consumer> getWorkers(){
		return Collections.unmodifiableList(workers);

	}

	public int getFirmSize(){
		return workers.size();
	}

	final private String firmName;

	final private int jobsToDo;
	
	

	public synchronized int getJobsToDo() {
		return jobsToDo;
	}

	final private List<Input> inputs;

	final private GoodType outputType;

	final private Integer outputQuantity;

	public List<Input> getInputs() {
		return inputs;
	}


	public Integer getOutputQuantity() {
		return outputQuantity;
	}

	public GoodType getOutputType() {
		return outputType;
	}

	public String getFirmName(){
		return firmName;
	}

	public Firm(int workToBeDone, String name,
			GoodType outputType, Integer outputQuantity, Input... inputs) {
		super();
		this.jobsToDo = workToBeDone;
		this.firmName = name;
		this.outputType = outputType;
		this.outputQuantity = outputQuantity;
		this.inputs = Arrays.asList(inputs);

		for(int i=0; i< defaultWorkers; i++)
			hire(true);

		//build the arraylist of inventories
		for(int i=0; i< inventory.length ; i++)
			inventory[i] = new ArrayList<Good>();
	}

	public Firm(int workToBeDone, String name,
			GoodType outputType, Integer outputQuantity, int workers,
			Input... inputs) {
		super();
		this.jobsToDo = workToBeDone;
		this.firmName = name;
		this.outputType = outputType;
		this.outputQuantity = outputQuantity;
		this.inputs = Arrays.asList(inputs);
		for(int i=0; i< workers; i++)
			hire(true);

		//build the arraylist of inventories
		for(int i=0; i< inventory.length ; i++)
			inventory[i] = new ArrayList<Good>();
	}


	volatile boolean cancelled = false;



	@Override
	public void run() {

		while(!cancelled){



			/********************************
			 * PRELIM
			 ********************************/
			this.setStatus(FirmStatus.SUPPLYING);
			long startTime = System.currentTimeMillis();
			//check for inputs!
			gatherSupplies();
			consumeSupplies();
			//System.out.println(this.firmName + " Finished gathering supplies after " + (System.currentTimeMillis()-wait));
			/********************************
			 * PRODUCTION
			 ********************************/
			//  wait = System.currentTimeMillis();
			//set up the work to be done
			//"hire the workers"
			workpool = Executors.newFixedThreadPool(workers.size());
			this.setStatus(FirmStatus.PRODUCING);
			//"set up and start the jobs!"
			for(int i=0; i<jobsToDo; i++)
			{
				//give 'em jobs to execute
				workpool.execute(new Work(this));
			}

			//start waiting till we are done!
			try {

				jobsDone.acquire(jobsToDo);
			} catch (InterruptedException ex) {
				throw new RuntimeException(" We have been interrupted while waiting for job to end!");
			}

			//we are done!
			System.out.println(this.firmName + " ended production in ms :" + (System.currentTimeMillis()-startTime) 
					+ " with " + workers.size() + " workers");

			//clean up
			workpool.shutdownNow();
			workpool=null;
			//      numbersToCrunch=null;

			//add labor cost to the production cost
			//this is just number of workers * (time of production / wagePeriod)
			productionCost = productionCost + workers.size()* market.getLabor().getCurrentWage()
					*(((double) (System.currentTimeMillis()-startTime))/ ((double) LaborMarket.wagePeriod)); 
			//divide production cost over the total output
			productionCost = (productionCost / new Double(outputQuantity));


			//now the goods appear in your inventory
			ArrayList<Good> toSell = new ArrayList<Good>();
			for(int i=0; i < outputQuantity; i++)
				toSell.add(new Good(outputType, this, productionCost, markup));

			inventory[outputType.ordinal()].addAll(toSell);

			/********************************
			 * RETAIL
			 ********************************/
			//we are done working if we are here!

			this.setStatus(FirmStatus.RETAILING);
			//tell big data about it
			Data.endProduction(this, outputType,System.currentTimeMillis());
			//sell to the market!
			for(Good output : toSell)
				market.sell(output);


					//how's the market today?
					//       System.out.println(market.toString());
					//        System.out.println(this.name + " finished selling at time" + System.currentTimeMillis());

					/***********************************
					 * HIRING
					 ***********************************/
					//if there is high demand and free workers: hire somebody!
					if(isAdaptive && market.getDelay(outputType) > maxOutputWaitingTime)
					{

						hire(false);
					}
		}

	}


	/**
	 *  This gathers supplies and ammo but also checks whether it's time to fire somebody
	 *  Finally, it computes the cost of production
	 */
	private void gatherSupplies(){

		productionCost = 0d;

		long totalWait = System.currentTimeMillis();
		for(Input input : inputs){
			//TODO change this to multi-threaded
			//		long inputWait = System.currentTimeMillis();
			if(input.getAmount()>0){
				try {
					productionCost += market.buy(input.getGood(), input.getAmount(), this);
				} catch (InterruptedException e) {
					// this shouldn't happen. Only consumers can be interrupted!
					throw new RuntimeException("Interrupted when buying!");
				}

				//	market.registerDelay(System.currentTimeMillis()-inputWait, input.getGood());
			}
		}
		totalWait = System.currentTimeMillis()- totalWait ;
		//System.out.println("total wait:" + totalWait);


		//if you are here all inputs have been bought!
		Data.beginProduction(this, outputType,System.currentTimeMillis());

		//Fire somebody
		if(isAdaptive && (totalWait > maxInputWaitingTime || 
				( outputType != GoodType.TOOLS && Market.getMarketSize(outputType)>30 )) 
				&& workers.size() > 1)
		{

			this.fire();

		}


	}

	/**
	 * Simply remove supplies from the inventory
	 */
	private void consumeSupplies(){
		for(Input input : inputs){
			for(int i=0; i< input.getAmount(); i++){
				inventory[input.getGood().ordinal()].remove(0);
			}
		}


	}

	@Override
	public String toString() {
		return firmName;
	}

	private void fire(){

		market.getLabor().lock.lock();
		try{
			Consumer toFire = workers.remove(0);
			market.getLabor().fire(this, toFire);
			System.out.println("fire: now "+ firmName + " has " + workers.size() + " workers");
			Data.countWorkers(System.currentTimeMillis());
		}finally{
			market.getLabor().lock.unlock();
		}

	}


	private void hire(boolean isItInitialWorker){
		market.getLabor().lock.lock();
		try{
			if(market.getLabor().isThereAFreeWorker())
			{
				Consumer employee = market.getLabor().hire(this,!isItInitialWorker);
				workers.add(employee);
				System.out.println("hire: now "+ firmName + " has " + workers.size() + " workers");
				if(!isItInitialWorker)
					Data.countWorkers(System.currentTimeMillis());
			}
		}finally{
			market.getLabor().lock.unlock();
		}


	}


	/*******************************
	 * Implementing trader
	 * 
	 * These are not the command to call for the firm to buy and sell. 
	 * If you need the firm to buy something call the market methods
	 * 
	 * These public methods instead are called by the good itself when it changes hands
	 *****************************/


	@Override
	public void buy(Good bought) {

		inventory[bought.getGoodType().ordinal()].add(bought);
		money -= bought.getPriceSold();

	}


	@Override
	public void sell(Good sold) {

		boolean valid = inventory[sold.getGoodType().ordinal()].remove(sold);
		money += sold.getPriceSold();

		if(valid==false)
			throw new RuntimeException(this.getFirmName() + " sold a good it doesn't have!" + sold);

	}

	public void payWage(Consumer c){
		double wage =  market.getLabor().getCurrentWage();
		money = money - wage;
		c.earnWage(wage);

	}





}
