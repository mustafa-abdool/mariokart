
//FP game class



//=======================MOOSEY-O KART FINAL PROJECT====================================


//Level Editor


import java.awt.geom.AffineTransform;
import java.awt.*;
import java.awt.image.*;
import java.net.*;

import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.text.DecimalFormat;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;


class MainGame extends JFrame{
	
	private static int fps = 30;
	private int period;
	
	
	public MainGame(int period)throws IOException{
		super("FP Test");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		makeGUI(period);
		pack();
		setResizable(false);
		setVisible(true);		
		setVisible(true);	
	}
	

    private void makeGUI(int period)throws IOException   	
    {
		FP fp = new FP(this, period);
		add(fp);
	//	pack();
		setVisible(true);

    }  // end of makeGUI()
    
 	public static void main(String args[])throws IOException
    { 
	int period = (int) 1000.0/fps;
	new MainGame(period);
    }
    
    	
}


class FP extends JPanel implements KeyListener, MouseListener, MouseMotionListener, Runnable{
	


	private Graphics dbg; //set up stuff
	private Image dbImage;
	private Thread th;
	private MainGame top;
	private int period;	
		
	private Image picture = new ImageIcon("mushroom map2.png").getImage();
	
	private int s_width = 1000; //height and width of screen
	private int s_height = 1000;
	private int gridratio = 8; //how many pixels each "tile" is 
	
	private int[][] terrain_grid = new int[s_width/gridratio][s_height/gridratio];
	

	
	private int mx=0; //double mx and my position
	private int my=0;
	
	private int rx=0;
	private int ry=0;
	
	private boolean mbDown=false; //tells if the mouse button is up or down 
	
	 
	 boolean []keys = new boolean[256]; 
	

	public FP(MainGame game, int period)throws IOException{
		super();
		
		requestFocus(); //for the key stuff
		setFocusable(true);
		addKeyListener(this);
		addMouseListener(this);		
		addMouseMotionListener(this);	
		top = game;
		this.period = period;
		setPreferredSize (new Dimension (s_width,s_height));
		setBackground(Color.black);


		
	}


    public void mouseDragged(MouseEvent e) {
	if(mbDown){
		
		rx = e.getX();
		ry = e.getY();

    }
    }
    
    public void mouseMoved(MouseEvent e) {        
	if(mbDown){

		rx = e.getX();
		ry = e.getY();
    }
    }


    public void mouseReleased(MouseEvent e) {
		mbDown = false;
		rx=-1;
		ry= -1;
    }
    
    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e){} 


    public void mouseClicked(MouseEvent e) {}
        
    public void mousePressed(MouseEvent e) {
    	if (e.getButton()==1){
    		mbDown = true;
    	}
    	mx = e.getX();
    	my = e.getY();
    }	
	
	public void move(){
		if (keys[37]==true){ //left

		}
		if (keys[39]==true){ //right 

			
		}
		if (keys[38]==true){ //up

			
		}
		if (keys[40]==true){ //down
	
			
		}		
				
				
		
		
	}
	
    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;

    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }	
	
	public void run()throws IOException {
		
		long lastUpdate, sleepLen;
		lastUpdate = System.currentTimeMillis ();

		

		while (true){
			//move(); //move the kart 

		//	System.out.println(mx + " "+my);
			render(); //draw stuff to offscreen buffer
			paintScreen(); //paint the screen
	    	sleepLen = lastUpdate + period - System.currentTimeMillis ();
	    	sleepLen = Math.max (5, sleepLen);
	    	delay (sleepLen);
	    	lastUpdate = System.currentTimeMillis ();
	    //	System.out.println(lastUpdate);

			
			
		}
	}
	

	public void drawRectangle(int mx,int my, int rx, int ry,Graphics g){
		int width = Math.abs((mx - rx));
		int height = Math.abs((my - ry));
		
		if (mbDown==true){
			if (mx<rx && my<ry){
				g.drawRect(mx,my,width,height);
			}
			if (mx>rx && my<ry){
				g.drawRect(rx,ry-height,width,height);
			}
			if (mx>rx && my>ry){
				g.drawRect(rx,ry,width,height);
			}
			if (mx<rx && my>ry){
				g.drawRect(mx,my-height,width,height);
			}
			
		}
		


		
		
	}
	
	public void render()throws IOException{ ///Draw everything offscreen first
		if (dbImage == null)
		{
	    dbImage = createImage (s_width,s_height);
		}
	    if (dbImage == null)
	    {
			System.out.println ("dbImage is null");
			return;
	    }
	    else{
	    	dbg = dbImage.getGraphics ();
	    }
	    
	    dbg.setColor(Color.black); //reset screen
	    dbg.fillRect(0,0,s_width,s_height); //reset screen
	    
	    pictureTest(dbg);
	    dbg.setColor(Color.black);
	    drawGrid(dbg);
	    dbg.setColor(Color.blue);
	    drawRectangle(mx,my,rx,ry,dbg);


	    

	}		
		
		
	public void pictureTest(Graphics g)throws IOException{
		BufferedImage buffer = new BufferedImage(1024,1024,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = buffer.createGraphics();
		g2d.drawImage(picture,0,0,null);
	/*	ArrayList<Integer> pixels = new ArrayList<Integer>();

		for (int i = 0;i<1024;i+=8){
			
			int value = buffer.getRGB(i,16);
			if (pixels.contains(value)==false){
				pixels.add(value);
				System.out.println(value);
			}
		}	
		System.out.println(pixels); */
		
		PrintWriter outfile=new PrintWriter(new BufferedWriter(new FileWriter("outfile.txt")));
		
		for (int i=0;i<1024;i+=8){
			for (int j=0;j<1024;j++){
				int pixel = buffer.getRGB(i,j);
				if (pixel==-505784 || pixel==-10422176 || pixel ==-460656 || pixel==-9934660){
					outfile.println("b");
				}
				if (pixel==-16754688 || pixel==-16734208){
					outfile.println("g");
				}
				
				if (pixel==-10465240 || pixel==-10422176){
					outfile.println("d");
				}
				
				if (pixel==-6254496){
					outfile.println("d");
				}
				
				else{
					outfile.println("r");
				}
				
				
				
			}
			outfile.println("\n"); //new line
		}
			
		
		g.drawImage(buffer,0,0,null);
	}	
		
	public void drawGrid(Graphics g){
		for (int i=0;i<=s_width;i+=gridratio){
			g.drawLine(i,0,i,s_height);
			g.drawLine(0,i,s_width,i);
		}
		
		
	}	
	
	public void paintScreen(){
		Graphics g;
		g = this.getGraphics();
		if ((g != null) && (dbImage != null)){
			g.drawImage (dbImage, 0, 0, null);
		}

	}
		
	private void startGame(){
		th = new Thread(this);
		th.start();
	}


    public void addNotify ()
	// wait for the JPanel to be added to the JFrame before starting
    {
    	
	super.addNotify ();    // creates the peer
	startGame ();   // start the thread
	
    }

		
		

    public void delay (long len)
    {
	try
	{
	    Thread.sleep (len);
	}
	catch (InterruptedException ex)
	{
	}

    }		


}

