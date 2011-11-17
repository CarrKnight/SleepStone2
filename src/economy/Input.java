/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package economy;

/**
 * this is just an object holding a good type and a double representing how much
 * of that good is needed by a firm to do its job.
 * @author carrknight
 */
public class Input {
    
    final private Good good;
    
    final private Integer amount;

    public Input(Good good, Integer amount) {
        this.good = good;
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }

    public Good getGood() {
        return good;
    }
    
}
