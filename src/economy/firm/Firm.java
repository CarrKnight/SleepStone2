/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package economy.firm;

import economy.Data;
import economy.Good;
import economy.Input;
import economy.Market;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;



/**
 *
 * @author carrknight
 */
public class Firm extends Thread {

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
    //around a single static semaphor
    private Market market = new Market();

    /**
     * Does it hire/fire workers?
     */
    private boolean isAdaptive = true;

    final static private int defaultWorkers=2;

    private long maxInputWaitingTime = 500l;
    private long maxOutputWaitingTime = 250l;

    private int workers= defaultWorkers;

    final private String firmName;

    final private int jobsToDo;

    final private List<Input> inputs;

    final private Good outputType;

    final private Integer outputQuantity;

    public List<Input> getInputs() {
        return inputs;
    }


    public Integer getOutputQuantity() {
        return outputQuantity;
    }

    public Good getOutputType() {
        return outputType;
    }

    public String getFirmName(){
        return firmName;
    }

    public Firm(int workToBeDone, String name,
            Good outputType, Integer outputQuantity, Input... inputs) {
        super();
        this.jobsToDo = workToBeDone;
        this.firmName = name;
        this.outputType = outputType;
        this.outputQuantity = outputQuantity;
        this.inputs = Arrays.asList(inputs);
    }

      public Firm(int workToBeDone, String name,
            Good outputType, Integer outputQuantity, int workers,
            Input... inputs) {
        super();
        this.jobsToDo = workToBeDone;
        this.firmName = name;
        this.outputType = outputType;
        this.outputQuantity = outputQuantity;
        this.inputs = Arrays.asList(inputs);
        this.workers = workers;
    }


    volatile boolean cancelled = false;



    @Override
    public void run() {

        while(!cancelled){



            /********************************
             * PRELIM
             ********************************/
            long wait = System.currentTimeMillis();
            //check for inputs!
            gatherSupplies();

            System.out.println(this.firmName + " Finished gathering supplies after " + (System.currentTimeMillis()-wait));
            /********************************
             * PRODUCTION
             ********************************/
             wait = System.currentTimeMillis();
            //set up the work to be done
            //"hire the workers"
            workpool = Executors.newFixedThreadPool(workers);
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
            System.out.println(this.firmName + " ended production in ms :" + (System.currentTimeMillis()-wait) 
            		+ " with " + workers + " workers");

            //clean up
            workpool.shutdownNow();
            workpool=null;
       //      numbersToCrunch=null;
       //     System.out.println(this.name + " cleaned up at time" + System.currentTimeMillis());

            /********************************
             * RETAIL
             ********************************/
            //we are done working if we are here!

            //tell big data about it
            Data.endProduction(this, outputType,System.currentTimeMillis());
            //sell to the market!
            market.sell(outputType, outputQuantity);


            //how's the market today?
      //      System.out.println(market.toString());
    //        System.out.println(this.name + " finished selling at time" + System.currentTimeMillis());

            /***********************************
             * HIRING
             ***********************************/
            //if there is high demand and free workers: hire somebody!
            if(isAdaptive && market.getDelay(outputType) > maxOutputWaitingTime)
            {
            market.getLabor().lock.lock();
            try{
                if(market.getLabor().isThereAFreeWorker())
                {
                    market.getLabor().hire();
                    workers++;
                    System.out.println("hire: now "+ firmName + " has " + workers + " workers");
                }
            }finally{
            market.getLabor().lock.unlock();
            }

            }
        }

    }


    /**
     *  This gathers supplies and ammo but also checks whether it's time to fire somebody
     */
    private void gatherSupplies(){

        //
        long totalWait = System.currentTimeMillis();
        for(Input input : inputs){
            //TODO change this to multi-threaded
            long inputWait = System.currentTimeMillis();
            market.buy(input.getGood(), input.getAmount());
            market.registerDelay(System.currentTimeMillis()-inputWait, input.getGood());
        }
        totalWait = System.currentTimeMillis()- totalWait ;
        //if you are here all inputs have been bought!
        Data.beginProduction(this, outputType,System.currentTimeMillis());

        //Fire somebody
        if(isAdaptive && totalWait > maxInputWaitingTime && workers > 1)
        {

            market.getLabor().lock.lock();
            try{
                market.getLabor().fire();
                workers--;
                System.out.println("fire: now "+ firmName + " has " + workers + " workers");

            }finally{
                market.getLabor().lock.unlock();
            }


        }


    }

    @Override
    public String toString() {
        return firmName;
    }



}