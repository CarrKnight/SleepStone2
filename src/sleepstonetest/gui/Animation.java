package sleepstonetest.gui;

import java.awt.Graphics2D;

public interface Animation {

	
	public void cycle();
	
	public boolean isOver();
	
	public void step(Graphics2D g2d);
	
	
}
