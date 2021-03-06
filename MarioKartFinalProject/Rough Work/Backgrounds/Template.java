
//FP game class



//=======================MOOSEY-O KART FINAL PROJECT====================================


//New to this version...

//trying to get rotation working using scanlines
//hopefully using scaling of lines to achieve a perspective effect
//try shrinking the bitmap
//try making the "viewrange" bigger


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


class FP extends JPanel implements KeyListener, Runnable{
	
	static int[][] map; //basic graphics interface
	static int rows;
	static int cols;
	static int gridratio;
	private Graphics dbg;
	private Image dbImage;
	private Thread th;
	private Image picture = new ImageIcon("blue.PNG").getImage();
	private Image picture2 = new ImageIcon("blue.PNG").getImage();
	
	private int p_width = 5120;
	
	private int s_width = 800;
	private int s_height = 600;
	private MainGame top;
	private int period;
	
	private int px=0;
	private int py=0;
	

	
	 
	 boolean []keys = new boolean[256]; 
	

	public FP(MainGame game, int period)throws IOException{
		super();
		
		requestFocus(); //for the key stuff
		setFocusable(true);
		addKeyListener(this);		
		top = game;
		this.period = period;
		setPreferredSize (new Dimension (s_width,s_height));
		setBackground(Color.black);


		
	}

	
	
	public void move(){
		if (keys[37]==true){ //left
			px+=50;
		}
		if (keys[39]==true){ //right 
			px-=50;
			
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
	
	public void run(){
		
		long lastUpdate, sleepLen;
		lastUpdate = System.currentTimeMillis ();

		

		while (true){
			//move(); //move the kart 
		//	System.out.println("running");
			move();
			render(); //draw stuff to offscreen buffer
			paintScreen(); //paint the screen
	    	sleepLen = lastUpdate + period - System.currentTimeMillis ();
	    	sleepLen = Math.max (5, sleepLen);
	    	delay (sleepLen);
	    	lastUpdate = System.currentTimeMillis ();

			
			
		}
	}
	

	
	
	public void render(){ ///Draw everything offscreen first
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
	    
	    dbg.setColor(Color.green); //reset screen
	    dbg.fillRect(0,0,s_width,s_height); //reset screen
		
		System.out.println(px);	
		dbg.drawImage(picture,px,py,null);
		if (px>0){
			int mod = px/p_width;
			dbg.drawImage(picture2,px-p_width*(mod+1),py,null);
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

