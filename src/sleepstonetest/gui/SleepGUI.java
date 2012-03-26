package sleepstonetest.gui;

import economy.Trader;
import economy.firm.Firm;
import economy.firm.FirmStatus;
import economy.good.GoodType;
import economy.market.Market;
import economy.workers.Consumer;
import sleepstonetest.SleepStoneTest;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class SleepGUI extends JPanel {

	//this is to make sure  we don't add animations while drawing them.
	Lock animationLock = new ReentrantLock();
	
	//
	private Hashtable<Trader, Rectangle> firmsPlace = new Hashtable<Trader, Rectangle>();
	private Hashtable<Trader, Rectangle> consumerPlace = new Hashtable<Trader, Rectangle>();
	private Hashtable<Trader, Point> tradersPlace = new Hashtable<Trader, Point>();
	private Hashtable<GoodType, Point> marketPlace = new Hashtable<GoodType, Point>(GoodType.values().length);

	static public Dimension jFrameInitialDimensions = new Dimension(1024, 720);
	
	static private Repainter repainter;

	static public boolean GUI = true;
	
	LinkedList<Animation> animations = new LinkedList<Animation>();


	private ImageIcon firmIcon;


	private double firmYStart = 0;
	private double firmFirstX = 0;
	private double firmLastX = 0;
	private double firmYEnd = 0;
	private double firmLenght=0;
	private double firmSpacing=0;
	private double workerFirstY=0;
	private double workerLastY=0;
	private double workerXStart=0;
	private double workerXEnd=0;
	private double workerHeight=0;
	private double marketYStart = 0;
	private double marketFirstX = 0;
	private double marketLastX = 0;
	private double marketSpacing=0;
	private double marketYEnd=0d;
	private double marketRadius=0;


	private ImageIcon[] icons = new ImageIcon[GoodType.values().length]; 



	public SleepGUI(){
		//this.setSize(jFrameInitialDimensions);

	}

	public static void main(String[] args){
		final SleepStoneTest test = new SleepStoneTest();
		if(GUI){
		final SleepGUI t = SleepStoneTest.gui;
		
		
		

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				// Create a JFrame, which is a Window with "decorations", i.e.
				// title, border and close-button
				JFrame  f = new JFrame("Swing Example Window");



				// "Pack" the window, making it "just big enough".
				f.pack();

				// Set the default close operation for the window, or else the
				// program won't exit when clicking close button
				//  (The default is HIDE_ON_CLOSE, which just makes the window
				//  invisible, and thus doesn't exit the app)
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				f.setTitle("Multithreaded Economy");
				f.setSize(new Dimension(1024, 720));


				f.add(t);
				t.initializeIcon();
				t.setSizes(f);
				//I am putting this one here so that I am sure the simulation starts AFTER I set up the gui
				
				test.startSimulation(SleepStoneTest.market);
				// Set the visibility as true, thereby displaying it
				f.setVisible(true);



			}
		});
		
		
		repainter = new Repainter(t, 50l);
		Thread animator = new Thread(repainter);
		animator.start();
		}
		else{
			test.startSimulation(SleepStoneTest.market);
		}

		
	}



	protected void initializeIcon() {
		icons[0] =  new ImageIcon("icons/medicine.png");
		icons[1] =  new ImageIcon("icons/wine.png");
		icons[2] =  new ImageIcon("icons/fertilizer.png");
		icons[3] =  new ImageIcon("icons/iron.png");
		icons[4] =  new ImageIcon("icons/pigiron.png");
		icons[5] =  new ImageIcon("icons/steel.png");
		icons[6] =  new ImageIcon("icons/tools.png");

		for(ImageIcon x : icons){
			if(x.getImageLoadStatus() == MediaTracker.ERRORED)
			{
				System.out.println(x + "failed");
			}
		}
		firmIcon = new ImageIcon("icons/firm.png");

	}



	
	public void goodSold(GoodType type, Trader firm){
		Point ending =  marketPlace.get(type);
		Rectangle firmRectangle =firmsPlace.get(firm);

		Point start = tradersPlace.get(firm);
		
	//	Point start = new Point((int)((firmRectangle.getMaxX()+firmRectangle.getMinX())/2d),
	//			(int)firmRectangle.getMaxY()  );
		
		GoodAnimation animation = new GoodAnimation(12,
				start,ending,icons[type.ordinal()].getImage());
				
		animationLock.lock();
		animations.add(animation);
		animationLock.unlock();
		
	}

	public void goodBought(GoodType type, Trader firm){
		//System.out.println("animation coming! " + animations.size());
		
		Point start =  marketPlace.get(type);
		Rectangle firmRectangle =firmsPlace.get(firm);
		
		Point ending = tradersPlace.get(firm);
		//Point ending = new Point((int)Math.round( firmRectangle.getX() + firmLenght/2d),
			//	(int)firmRectangle.getY()  );
		//Point ending = new Point(100,100);
		//System.out.println(start + " " + ending);
		
		
		GoodAnimation animation = new GoodAnimation(12,
				start,ending,icons[type.ordinal()].getImage());
				
				
		animationLock.lock();
		animations.add(animation);
		animationLock.unlock();

	}








	/*********************************
	 *  SIZE COMPUTATION
	 *********************************/
	protected void setSizes(JFrame f ) {
		Dimension panelDimensions = f.getSize();
		computeFirmSizes(panelDimensions);
		computeMarketsSize(panelDimensions);
		computeWorkersSizes(panelDimensions);
	}

	private void computeFirmSizes(Dimension panelDimensions){

		ArrayList<Firm> firms = SleepStoneTest.firms;

		double rowMaxLenght = 8d*panelDimensions.getWidth()/9d;

		//check when the column of of workers start
		double halfheight = panelDimensions.getHeight()/2d;
		firmYStart = halfheight/3d;
		firmYEnd = 2d*halfheight/3d;

		firmFirstX = rowMaxLenght/11d;
		firmLastX = 10d*rowMaxLenght/11d;
		double rowRealLenght = firmLastX-firmFirstX;
		firmLenght = (rowRealLenght*3d)/(4d* new Double(firms.size()));
		firmSpacing = firmLenght/3d;

		//compute the rectangles
		for(int i=0; i < firms.size(); i++){

			Rectangle location = new Rectangle((int)Math.round(firmFirstX + i * (firmLenght + firmSpacing)),
					(int)Math.round(firmYStart),(int)Math.round(firmLenght), 
					(int)Math.round(firmYEnd- firmYStart));
			System.out.println(location);
			firmsPlace.put(firms.get(i), location );
			tradersPlace.put(firms.get(i), new Point((int)location.getCenterX(),(int)location.getCenterY()));
		}
		
		

	}


	private void computeMarketsSize(Dimension panelDimensions){

		double rowMaxLenght = 8d*panelDimensions.getWidth()/9d;

		//check when the column of of workers start
		double halfheight = panelDimensions.getHeight()/2d;
		marketYStart = 2d*halfheight/5d + halfheight;
		marketYEnd = 3d*halfheight/5d + halfheight;

		marketFirstX = rowMaxLenght/9d;
		marketLastX = 8d*rowMaxLenght/9d;
		double rowRealLenght = marketLastX-marketFirstX;
		marketRadius = (rowRealLenght*3d)/(4d* new Double(GoodType.values().length));

		marketSpacing = marketRadius/3d;
		int index=0;
		for(GoodType x : GoodType.values()){
			
			Point a = new Point((int)Math.round(marketFirstX + index * (marketRadius + marketSpacing)),
					(int)Math.round((marketYStart+marketYEnd)/2d));
			marketPlace.put(x, a);
			index++;
			}

	}



	private void computeWorkersSizes(Dimension panelDimensions){

		//check when the column of of workers start
		double startWorkerColumn = 8d*panelDimensions.getWidth()/9d;

		double workerColumnWidth = panelDimensions.getWidth()-startWorkerColumn;
		workerXStart = startWorkerColumn + workerColumnWidth/5d;
		workerXEnd = panelDimensions.getWidth()-workerColumnWidth/5d;
		workerFirstY = panelDimensions.getHeight()/9d;
		workerLastY = 8d*panelDimensions.getHeight()/9d;
		workerHeight = ( workerLastY - workerFirstY )/new Double(Market.numberOfWorkers);
		assert workerXStart > 0;
		assert workerXEnd > 0;
		assert workerFirstY > 0;
		assert workerLastY > 0;
		assert workerHeight > 0;
		
		int index=0;
		for(Consumer x : SleepStoneTest.market.getLabor().workers){
			Rectangle rectangle= new 
					Rectangle((int)workerXStart,(int)( workerFirstY + index*workerHeight),
							(int)(workerXEnd-workerXStart),(int) workerHeight);
			tradersPlace.put(x, new Point((int)rectangle.getCenterX(),(int)rectangle.getCenterY()));
			consumerPlace.put(x,rectangle);
			index++;
			}
	}


	/*********************************
	 *  PAINTERS
	 *********************************/

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;


		//WORKERS
		paintWorkers(g2d);
		paintFirms(g2d);
		paintMarkets(g2d);
		paintAnimations(g2d);
	}


	private void paintFirms(Graphics2D g2d){

		Rectangle2D rec = new Rectangle2D.Double();
		ArrayList<Firm> firms = SleepStoneTest.firms;

		Stroke normalStroke = new BasicStroke();
		Stroke boldStroke = new BasicStroke(4f);

		g2d.setStroke(boldStroke);
		for(Firm x : firms){
			rec.setRect(firmsPlace.get(x));
			FirmStatus status = x.getStatus();
			
			g2d.setColor( Color.black);
			
			g2d.drawString(x.getFirmName(), 
					Math.round(rec.getMinX()), Math.round(rec.getMaxY()+15d));
			
			g2d.drawString(Integer.toString(x.getWorkers().size()), 
					Math.round(rec.getMinX()), Math.round(rec.getMinY()-15d));
			//g2d.setColor(Color.RED);
			g2d.drawImage(firmIcon.getImage(),(int)rec.getX(), 
					(int)rec.getY(),(int)rec.getWidth(),(int)rec.getHeight(),
					FirmStatus.getStatusColor(status),null);
			
			
			//draw the jobs done indicator
			if(status==FirmStatus.PRODUCING){
				g2d.setColor(Color.black);
				g2d.setStroke(boldStroke);
				Rectangle jobsIndicator = new Rectangle((int)rec.getMaxX()+5, (int)rec.getMinY(),(int)(firmSpacing/2d) , (int)rec.getHeight());
				g2d.draw(jobsIndicator);
				float jobsToDo = x.getJobsToDo();
				float jobsDone = x.getJobsDone().availablePermits();
				float percentComplete =  jobsDone/jobsToDo;
				g2d.setColor(Color.BLUE);
				jobsIndicator.setBounds((int)rec.getMaxX()+5, (int)rec.getMinY(),(int)(firmSpacing/2d) ,
						(int)(((float)(rec.getHeight()))*percentComplete));
				g2d.fill(jobsIndicator);
				g2d.setStroke(normalStroke);
			}
			//index++;
		}

	}

	private void paintMarkets(Graphics2D g2d){


		int index = 0;
		for(GoodType x : GoodType.values()){
		
	

			g2d.drawImage(icons[index].getImage(), (int)Math.round(marketFirstX + index * (marketRadius + marketSpacing)),
					(int)Math.round(marketYStart), (int)Math.round(marketRadius), 
					(int)Math.round(marketYEnd- marketYStart), (ImageObserver)null );
			g2d.drawString(Integer.toString(Market.getMarketSize(x)), 
					(int)Math.round(marketFirstX + index * (marketRadius + marketSpacing)),
					(int)Math.round(marketYEnd) +10);
			index++;
		}

	}



	private void paintWorkers(Graphics2D g2d){

		Rectangle rec;
		int index = 0;
		g2d.setStroke(new BasicStroke());
		for(Consumer x : SleepStoneTest.market.getLabor().workers){
			rec = consumerPlace.get(x);
			index++;
			if(x.getEmployer() == null)
				g2d.setColor(Color.BLACK);
			else
				g2d.setColor(Color.ORANGE);
			g2d.draw(rec);
			g2d.fill(rec);
		}

	}

	private void paintAnimations(Graphics2D g2d){

		LinkedList<Animation> toRemove = new LinkedList<Animation>();
		//get all the animations you can, forget about the ones that are about to be added
		animationLock.lock();
		List<Animation> toPaint = (List<Animation>) animations.clone();
		animationLock.unlock();
		for(Animation x : toPaint){
			if(x.isOver())
				toRemove.add(x);
			else
				x.step(g2d);
		}

		animations.removeAll(toRemove);

	}

	
	

}
