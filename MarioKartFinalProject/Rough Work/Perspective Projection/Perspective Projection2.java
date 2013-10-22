
//FP game class



//=======================MOOSEY-O KART FINAL PROJECT====================================


//New to this version...

//adding in acceleration/velocity/grid squares/collisions
//still need to fix perspective


//Problems...

//can't turn probably with perspective and an image
//no line scaling ==> no depth =(



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
	
	private static int fps = 60;
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

	private MainGame top; //interface of main game
	private Graphics dbg;
	private Image dbImage;
	private Thread th;	


	
	private int s_width = 	800;
	private int s_height =  800;
	private int period;
	private int count; //used for testing
	private BufferedImage bitmap; //map thingy
	private Image real_map = new ImageIcon("new map.png").getImage();
	
	
	private int map_width = 1024;
	private int	map_height = 1100;	
	private int real_width = map_width*2;  //width and height of the acutal bitmap that you're using
	private int real_height = map_height*2;		
	private int view_width = 300; //width and height of the portion of the image that you see
	private int view_height = 500;
	private int view = 900 ; //this represents the TOTAL screenshot that is gotten, and the position is taken from that
	
	private int actual_height = (int)(s_height*.75); //height of actual screen in which you see stuff
	
	private double scale_x = s_width/view_width;
	private double scale_y = actual_height/view_height;
	private Image random_pic = new ImageIcon("test thing.png").getImage();
	private Image norm = new ImageIcon("mushroom map.png").getImage();
	private Image grass = new ImageIcon("grass2.PNG").getImage();
	
	private Kart[] players; //represents all the players in the game
	
	 
	 boolean []keys = new boolean[256];  //list of keys	
	char[][] terrain_grid = new char[128][128]; //grid for the terrain
	
	
	
	public void loadMapData(){
		
		
		try{
			Scanner infile = new Scanner(new File("outfile0.txt"));
			int j=0;
			while (infile.hasNextLine()){
				String line = infile.nextLine();
				for (int i=0;i<128;i++){
					char letter = line.charAt(i);
					terrain_grid[j][i] = letter;
				}
				j++;
			}
			
		}
		
		catch(Exception e){
			
		}
	}
	

	public FP(MainGame game, int period)throws IOException{
		super();
		
		requestFocus(); //for the key stuff
		addKeyListener(this);
		setFocusable(true);		
		top = game;
		this.period = period;
		setPreferredSize (new Dimension (s_width,s_height));
		setBackground(Color.black);
	//	makeMap(); //make the map

		
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
		players = new Kart[8];
		loadMapData(); //load the data for that map
		
		players[0] = new Kart(terrain_grid);
		
		

		

		while (true){
			
			moveKarts();
			render(players[0]); //draw stuff to offscreen buffer ==> karts[0] means everything is relative to the [0]th kart
			paintScreen(); //paint the screen
	    	sleepLen = lastUpdate + period - System.currentTimeMillis ();
	    	sleepLen = Math.max (5, sleepLen);
	    	delay (sleepLen);
	    	lastUpdate = System.currentTimeMillis ();
	    	count++;
			
			
		}
	}
	

	public void moveKarts(){
		for (Kart player:players){
			if (player!=null && player.isAI()==false){
			//	System.out.println(player);
				player.move();
			//	player.checkCollisions();
				
			}
		}
	}
	
	public void drawBackground(Graphics dbg,int s_width,int s_height){
		for (int i=0;i<s_height;i+=8){
			for (int j=0;j<s_width;j+=8){
				dbg.drawImage(grass,j,i,null);
			}
		}
	}
	
	public void render(Kart player){ ///Draw everything offscreen first
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
	     
	    //reset screen 
	   // drawBackground(dbg);
	    dbg.setColor(Color.black);
	    dbg.fillRect(0,0,s_width,s_height);
	    //draw minimap
		drawMap(dbg,player);
	//	player.drawKart(dbg);
		

	//	System.out.println("Done");

	   // dbg.drawImage(viewrange,0,0,null); //finally, draw everything to the real screen
	    

	}
	
	
	public void drawMap(Graphics dbg,Kart player){ //player is the perspective that you see stuff from
		
		//assume start angle is 90
		//use trig to calculate real angels
		
		//ROTATE ACCORDING TO THE VIEWING ANGLE OF THE PLAYER (ASSUME ITS 90 FOR THE FIRST TIME) 
		
		BufferedImage map = new BufferedImage(1024,1000,BufferedImage.TYPE_INT_ARGB); //hollds the map picture
		BufferedImage screen = new BufferedImage(s_width,s_height,BufferedImage.TYPE_INT_ARGB); //holds a copy of the screen
		//(because you cant use get RGB for normal images)
		
		Graphics2D g = map.createGraphics();
		
		double rotation_angle = 90 - player.angle;	
		g.rotate(rotation_angle);
				
		g.drawImage(norm,0,0,null); //draw map to it

		double cam_height = 40; //height of camera
		double yaw = 70; // angle camera is looking down at
		double cam_dist = 50; //dist of camera to player ==> we wont' use this yet
		double FOV = 60; //field of view parameters
		
		int xpos = 0; //x position on the screen
		int ypos = 0; //y position on the screen
		
		//distance to pixel line ==> cam_height/tan(Math.toRadians(cam_orietnation)
		
		double straight_dist = cam_height*Math.tan(Math.toRadians(yaw));
		double increment = FOV/s_width; //increase the angle by this much everytime
		
		System.out.println(straight_dist);
		System.out.println(increment);
		
		
		double start_angle = player.angle-FOV/2;
		double end_angle = player.angle+FOV/2;
		
		
		System.out.println(player.angle);
	
		
		for (double ang = start_angle; ang<end_angle; ang+=increment){
			ypos = s_height/2;
			for (double height=straight_dist; height>0; height-=1){ //loop thru each height
				double dx = height*Math.tan(Math.toRadians(player.angle - ang)); //subtract your angle from the viewing angle 
				
				int newX = (int)dx+(int)player.xpos;
				int newY = (int)player.ypos-(int)height;
				
			//	System.out.println(newX+" "+newY);
				
				int pixelvalue = map.getRGB(newX,newY);
				screen.setRGB(xpos,ypos,pixelvalue);
				ypos++; //increase y pos 
			}
			xpos++; //increase x pos 

			
		}
		
		
		
		dbg.drawImage(screen,0,0,null);
//	int pixelvalue = map.getRGB(newX,newY); //get pixel value				
//screen.setRGB(screenpos,screenypos,pixelvalue); //set RGB...

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





class Kart implements KeyListener{
	double xpos;
	double ypos;
	double angle; //angle that this kart is facing
	boolean AI; //if this kart is AI or not...
	double acceleration;
	double velocity;
	double max_acceleration;
	double max_velocity;
	double min_velocity;
	double min_acceleration;
	int handling; //how sharply you can turn your kart 
	String name;
	boolean[] keys = new boolean[256];
	Image[] frames;
	int currentframe;
	
	char[][] terrain;
	
	public Kart(char[][] terrain_grid){
		xpos = 960;// 1441;
		ypos = 683;//1173;
		angle = 90;
		
		velocity = 0;
		acceleration = 0;
		max_velocity = 30;
		min_velocity = -5;
		max_acceleration = 5;
		min_acceleration = -1;
		handling = 5;
		
		AI=false;
		requestFocus(); //for the key stuff
		addKeyListener(this);		
			
		frames = new Image[3];
		frames[0] = new ImageIcon("karts16 copy.png").getImage();
		frames[1] = new ImageIcon("karts10 copy.png").getImage();
		frames[2] = new ImageIcon("karts4 copy.png").getImage();
		
		terrain = terrain_grid;
					
	}
	
	public int getGridX(){
		double screenX = xpos;
		
		return (int)screenX/8;
	}
	
	public int getGridY(){
		
		double screenY = ypos-13-77;
		
		return (int)(screenY)/8;
	}
	
	public boolean isAI(){
		return AI==true;
	}
	
	public void move(){
		updateAngle();
		updateSpeed();
		

		
		xpos+=velocity*Math.cos(Math.toRadians(angle));
		ypos+=velocity*Math.sin(Math.toRadians(angle))*-1;		
		
	}
	
	public void updateAngle(){ //updates hte speed and angle 
		if (keys[37]==true){ //left turn 
			angle+=handling;
			currentframe = 2;
			
		}
		if (keys[39]==true){ //right turn
			angle-=handling;
			currentframe = 1;
	}
		if (keys[37]==false && keys[39]==false){ //if you aren't turning, your frame is the normal one
			currentframe = 0;
		}	
	
}
	
	public void updateSpeed(){
		
		if (keys[38]==true){
			acceleration+=.3;
		}
		
		if (keys[40]==true){
			acceleration-=.1;
		}
		
		if (keys[38]==false && keys[40]==false){ //not moving
			acceleration-=.2;
		}
		
		

		
		if (velocity<=0 && keys[38]==false && keys[40]==false){
			acceleration = 0;
			velocity = 0;
		}
		
		
		velocity+=acceleration;
		
		if (velocity>max_velocity){
			velocity = max_velocity;
			acceleration = 0;
		}
		
		if (velocity<min_velocity){
			velocity = min_velocity;
			acceleration = 0;
		}		
		
	}
	
	
	public void drawKart(Graphics dbg){
		dbg.drawImage(frames[currentframe],500-109/2,600-81,null);
	}
	
	
	public void checkCollisions(){
		int gx = this.getGridX();
		int gy = this.getGridY();
		
		char type = terrain[gy][gx];
		
		if (type=='b'){
			velocity = 0;
			acceleration = 0;
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
	
	

	
}

}



