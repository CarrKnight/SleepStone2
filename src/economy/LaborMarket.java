/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is basically the simplest Labor Market Possible. It's an integer having the number of free workers
 * available. There is a lock you must close when using the public functions or it will throw an exception <br>
 * You can check if the market is empty, you can try to hire or try to fire somebody.
 * Remember to unlock when done!
 *
 * @author carrknight
 */
public class LaborMarket {

    public ReentrantLock lock = new ReentrantLock();


    AtomicInteger freeWorkers;

    public LaborMarket(int freeWorkers) {
        this.freeWorkers = new AtomicInteger(freeWorkers);
    }

    public boolean isThereAFreeWorker(){
        //it has to be locked!
        assert lock.isLocked();
        if(freeWorkers.intValue()>0)
        {
            return true;
        }
        else {
            return false;
        }


    }

    public void hire(){
        //it has to be locked!
        assert lock.isLocked();
        if(freeWorkers.intValue()<=0)
            throw new RuntimeException("Trying to hire non-existant worker!");
        else
            freeWorkers.decrementAndGet();
    }


    public void fire(){
        //it has to be locked!
        assert lock.isLocked();
        freeWorkers.incrementAndGet();
    }

}




