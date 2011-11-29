/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package economy.good;

/**
 * this is just an object holding a good type and a double representing how much
 * of that good is needed by a firm to do its job.
 * @author carrknight
 */
public class Input {
    
    final private GoodType good;
    
    final private Integer amount;

    public Input(GoodType good, Integer amount) {
        this.good = good;
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }

    public GoodType getGood() {
        return good;
    }
    
}
