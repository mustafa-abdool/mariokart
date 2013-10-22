
//FP game class



//=======================MOOSEY-O KART FINAL PROJECT====================================


//New to this version...

//scanlines working now
//basic sprite on screen moving 
//try scaling the y by using decimal values? [not sure if this'll work though]
//try scaling the x by using decimal values? [using decimal increments for dx] //trying using (int)dx + 1 FOR THIS


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
	//	setSize(800,600);	
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
	
	private int s_width = 800;
	private int s_height = 600;
	private int period;
	private int count; //used for testing
	private BufferedImage bitmap; //map thingy
	private Image real_map = new ImageIcon("new map.png").getImage();
	MainGame top;
	private int real_width = 2000;  //width and height of the acutal bitmap that you're using
	private int real_height = 2000;
	private Player player;
	
	private int view_width = 200; //width and height of the portion of the image that you see
	private int view_height = 200;
	private int view = (int)(view_height*4); //this represents the TOTAL screenshot that is gotten, and the position is taken from that
	
	private int actual_height = (int)(s_height*.75); //height of actual screen in which you see stuff
	
	private double scale_x = s_width/view_width;
	private double scale_y = actual_height/view_height;
	private Image random_pic = new ImageIcon("test thing.png").getImage();
	private Image norm = new ImageIcon("new map.png").getImage();
	private Image kart = new ImageIcon("karts10 copy.png").getImage();
	
	 
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
		makeMap(); //make the map

		
	}

	
	
	public void move(){
		
		if (keys[37]==true){ //turning left
			player.angle+=10;
			System.out.println("Turn left");
			player.setFrame(2); //change 
		}
		if (keys[39]==true){ //turning right 
			player.angle-=10;
			System.out.println("Turn right");
			player.setFrame(1); //change frame
		}

		
		if (keys[37]==false && keys[39]==false){ //if you aren't turning, your frame is the normal one
			player.setFrame(0);
		}
		
		
		if (keys[38]==true){ //if up arrow is held - you speed up 
			player.changeSpeed(false,true);
		}
		
		if (keys[40]==true){ //if the down arrow is held - you slow down
			player.changeSpeed(true,false);
		}
		
		if (keys[38]==false && keys[40]==false){ //if niether arrow is held you gradually slow down
			player.changeSpeed(false,false);
		}
		
		//player.updateSpeed();
		
		player.xpos+=player.velocity*Math.cos(Math.toRadians(player.angle)); //move the player
		player.ypos+=player.velocity*Math.sin(Math.toRadians(player.angle))*-1;				
		
		
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
		player = new Player();
		

		while (true){
			move(); //move the kart 
			render(); //draw stuff to offscreen buffer
			paintScreen(); //paint the screen
	    	sleepLen = lastUpdate + period - System.currentTimeMillis ();
	    	sleepLen = Math.max (5, sleepLen);
	    	delay (sleepLen);
	    	lastUpdate = System.currentTimeMillis ();
	    	count++;
			
			
		}
	}
	
	
	public void drawMap(Graphics dbg){
	    BufferedImage map; //this map holds the whole bitmap
	    BufferedImage map2; //this map holds the portion of the bitmap that is AROUND your kart at all times
	    BufferedImage viewrange;
	    BufferedImage h_strip; //holds horizontal strips used for scanline

	    
	    
	    map = new BufferedImage(real_width,real_height,BufferedImage.TYPE_INT_ARGB);
	    map2 = new BufferedImage(real_width,real_height,BufferedImage.TYPE_INT_ARGB);
	    
	    Graphics2D map2_g =map2.createGraphics(); //graphics context for map that holds view range
	    Graphics2D map_g = map.createGraphics();
	    
	    map_g.drawImage(norm,0,0,null); //draw actual map to this image
	    
	    
	    viewrange = map.getSubimage((int)player.xpos-view/2,(int)player.ypos-view/2,view,view); //get subImage
	    
	    
	    double rotation_angle = Math.toRadians(player.angle) - Math.PI/2; //angle which to rotate - subtract 90 - relative to player

	    map2_g.translate(real_width/2,real_height/2); //translate the axis to the middle
	    map2_g.rotate(rotation_angle); //rotate according to angle
	    
	    map2_g.drawImage(viewrange,view/2*-1,view/2*-1,null); //draw the whole image to the new map, its -150,-150 b/c you want mario's position 
	     //to be the center of the new axis!
	    
	    
	    
	    
	    
		
		map2_g.translate(real_width/2*-1,real_height/2*-1); //translate back
		
		int kartX = real_width/2; //0 + 10000;
		int kartY = real_height/2; //no OFFSET ON THIS ONE  (0+1000)
		
		int newxpos = kartX;
		int newypos = kartY-view_height/2;
		
		viewrange = map2.getSubimage(newxpos - view_width/2,newypos - view_height/2,view_width,view_height); //get the image of what mario actually SEES


//drawing on a scanline by scanline basis
		
		
		int strip_no = 300; //number of strips to have 
		int dx = 90; //horitzontal length of each strip
		int yscalefactor = 3; //amount that each strip is scaled by horizontally
		int scalechange = 15;
		int[] strips = {20,60,120,200};
		int[] scalefactor= {4,3,2,1};
		
		for (int i=0;i<strip_no;i++){
			
			
			h_strip = map2.getSubimage(kartX - dx/2,kartY,dx,1); //get subimage of what you can see
			BufferedImage scaled_ver = new BufferedImage(s_width,yscalefactor,BufferedImage.TYPE_INT_ARGB); //BufferedImage used to scale the strip
			Graphics2D scalever_g = scaled_ver.createGraphics(); //graphics context for scaled buffered image
			//has to be width of screen
			double xscale = s_width/(double)(dx); //get xscale - must be a double
			

	/*		if (i%scalechange==0 && yscalefactor!=1){ //derease how much things are scaled as you get farther away 
				yscalefactor--;
				scalechange+=15;
			}		*/						
			
			int yscale = yscalefactor;
			
			
			scalever_g.scale(xscale,yscale); //scale image
			scalever_g.drawImage(h_strip,0,0,null); //draw to scaled image
			int screen_ypos = s_height - i*yscalefactor;
			dbg.drawImage(scaled_ver,0,screen_ypos,null);  //draw to real screen
	//		System.out.println("Looping");
	
			if (i%2==0){ //increase how far you can see in the xdirection 
				dx+=1;
			}
			
			if (i>=0 && i<120){ //depending on how close the strip is to the focal point (ie you) represents the scaling 
				kartY-=1;
			}
			if (i>=120 && i<180){
				kartY-=2;
			}
			if (i>=180 && i<195){
				kartY-=3;
			}
			if (i>=195 && i<strip_no){
				kartY-=5;
			}			
			
			
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
	    
	    
	    
	    dbg.setColor(Color.black); //reset screen
	    dbg.fillRect(0,0,s_width,s_height); //reset screen
	


	 	drawMap(dbg); //draws the basic background 
	    player.drawKart(dbg); //draw the player's kart onto the screen 

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
			
	public void makeMap()throws IOException {
		Scanner datafile = new Scanner(new File("grid.txt"));
		rows = datafile.nextInt();
		cols = datafile.nextInt();
		map = new int[rows][cols];
		datafile.nextLine();
		for (int i=0;i<rows;i++){
			String line = datafile.nextLine();
			char[] whatever = line.toCharArray();
			for (int j=0;j<cols;j++){
				map[i][j]=Integer.parseInt(whatever[j]+"");
			}
		}
		
		
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


class Player{
	double FOV;
	double xpos;
	double ypos;
	double angle;
	double velocity;
	double max_velocity;
	double min_velocity;
	double acceleration;
	Image[] frames;
	int currentframe;
	
	public Player(){
		//angle and location stuff
		xpos = 1441;
		ypos = 1173;
		angle = 90;
		
		//image stuff -  only for player 
		frames = new Image[3];
		frames[0] = new ImageIcon("karts16 copy.png").getImage();
		frames[1] = new ImageIcon("karts10 copy.png").getImage();
		frames[2] = new ImageIcon("karts4 copy.png").getImage();
		currentframe = 0;
		
		//speed stuff
		acceleration = 0;
		velocity = 0;
		max_velocity = 30;
		min_velocity = -10;
		
		
		
	}
	
	
	public void setFrame(int newFrame){
		currentframe = newFrame;
	}
	
	public double getX(){
		return this.xpos;
	}
	
	
	public double getY(){
		return this.ypos;
	}
	
	public double getAngle(){
		return this.angle;
	}
	
	public void drawKart(Graphics dbg){
		dbg.drawImage(frames[currentframe],400-109/2,600-120,null);
	}
	

	
	public void changeSpeed(boolean downArrow,boolean upArrow){
		System.out.println("Loop");
		
		if (downArrow==true){
			acceleration-=.25;
			updateSpeed(this.acceleration);
		}
		
		if (downArrow==false && upArrow==false){
			acceleration-=.25;
			if (velocity-acceleration<0){
				System.out.println("here");
				velocity=0;
				acceleration=0;
				return;
			}
			else{
				updateSpeed(acceleration);
			}
		}
		
		if (upArrow==true){
			acceleration+=.5;
			updateSpeed(acceleration);
			
		}
		
		
	}
	
	public void updateSpeed(double inc){
		if (velocity+inc>min_velocity && velocity+inc<max_velocity){
			velocity+=inc;
		}
		
	}
	
	
}

