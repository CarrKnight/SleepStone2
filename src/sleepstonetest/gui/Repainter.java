package sleepstonetest.gui;

import java.awt.Graphics2D;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class Repainter implements Runnable {

	boolean cancel = false;
	
	
	
	JComponent toAnimate;
	long updateTime;
	
	public void stopAnimating(){
		cancel=true;
		
	}
	
	
	
	



	public Repainter(JComponent toAnimate, long updateTime) {
		super();
		this.toAnimate = toAnimate;
		this.updateTime = updateTime;
	}







	@Override
	public void run() {

		long startTime = System.currentTimeMillis();

		while(!cancel)
		{
			while (true) {

				SwingUtilities.invokeLater(new Runnable() 
				{
					public void run() {
						toAnimate.repaint();
						
					}
				});
				
				long timePassed = System.currentTimeMillis() - startTime;
				long sleep = updateTime - timePassed;

				if (sleep < 0)
					sleep = 2;
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					System.out.println("interrupted");
				}

				startTime = System.currentTimeMillis();
			}
			
			
			
			
		}
	}

}
