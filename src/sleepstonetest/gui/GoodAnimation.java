package sleepstonetest.gui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Point2D;

public class GoodAnimation implements Animation {
	
	
	Image image;
	int stepSpeed;
	int dimension = 20;
	Point2D start;
	Point2D ending;
	Point2D currentPosition;
	boolean xIsOver = false;
	boolean yIsOver = false;
	
	
	
	
	public GoodAnimation(int speed, Point start, Point ending, Image image) {
		super();
		this.stepSpeed = speed;
		this.start = start;
		this.ending = ending;
		this.image = image;
	
		currentPosition = (Point2D)start.clone();
	}
	
	@Override
	public void cycle() {
		int distanceStepX = (int)Math.round(ending.getX() - currentPosition.getX());
		if(distanceStepX>stepSpeed)
			distanceStepX=stepSpeed;
		if(distanceStepX<-stepSpeed)
			distanceStepX=-stepSpeed;
		
		int distanceStepY = (int)Math.round(ending.getY() - currentPosition.getY());
		if(distanceStepY>stepSpeed)
			distanceStepY=stepSpeed;
		if(distanceStepY<-stepSpeed)
			distanceStepY=-stepSpeed;
		
		
		if(distanceStepX == 0)
			xIsOver = true;
		if(distanceStepY == 0)
			yIsOver = true;
		
		currentPosition.setLocation(currentPosition.getX() + distanceStepX, 
				currentPosition.getY() + distanceStepY);
		
		
		
	}
	@Override
	public boolean isOver() {
		
		return xIsOver && yIsOver;
	}
	
	
	@Override
	public void step(Graphics2D g2d) 
	{
		g2d.drawImage(image,(int) currentPosition.getX(), (int) currentPosition.getY(), 
				this.dimension, this.dimension, null);
		
		this.cycle();
		
		
	}
	
	

}
