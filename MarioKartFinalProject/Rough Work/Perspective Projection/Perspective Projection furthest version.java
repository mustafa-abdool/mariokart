
//FP game class



//=======================MOOSEY-O KART FINAL PROJECT====================================
//Ideas....

//make a kind of grid thingy, incase you go outside 



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


	
	private int s_width = 	600;
	private int s_height =  300;
	private int period;
	private int count; //used for testing
	private BufferedImage bitmap; //map thingy
	private Image real_map = new ImageIcon("new map.png").getImage();
	
	
	private int map_width = 1024;
	private int	map_height = 1024;	

	
	private int actual_height = (int)(s_height*.75); //height of actual screen in which you see stuff
	

	private Image random_pic = new ImageIcon("test thing.png").getImage();
	private Image norm = new ImageIcon("mushroom map2.png").getImage();
	private Image grass = new ImageIcon("bigger background green.PNG").getImage();
	
	private Kart[] players; //represents all the players in the game
	
	 
	 boolean []keys = new boolean[256];  //list of keys	
	char[][] terrain_grid = new char[128][128]; //grid for the terrain
	private String bg_file = "forest.gif";
	
	private ImageIcon whatever = new ImageIcon(bg_file);
	private Image picture = new ImageIcon(bg_file).getImage();
	private Image picture2 = new ImageIcon(bg_file).getImage();
	
	private int p_width = whatever.getIconWidth(); //dimensions of the picture itself 
	private double p_height = whatever.getIconHeight();
	

	
	private int px=0;
	private int py=0;
	
	private int bg_height = 150; //the height that you want the backgroundt o be 
	
	public void moveBackground(Graphics dbg){ //function that handles the background moving
	
	
		BufferedImage buffer = new BufferedImage(s_width,bg_height,BufferedImage.TYPE_INT_ARGB); //buffered image used to scale the iamge
		Graphics2D g2d = buffer.createGraphics();
		

	

		
		double yscale =  bg_height/p_height;
		
		g2d.scale(1,yscale); //scale appropriately 

		if (px<p_width && px>0){ //going left - using 2 images
			g2d.drawImage(picture,px-p_width,py,null);
		}
		
		if (px>p_width){ //reset - going left 
			px=0;
		}
		
		if (px*-1+s_width>p_width){
			int newX = p_width+px;
			g2d.drawImage(picture,newX,py,null);
		}
		
		
		if (px*-1>=p_width){ // reset - going right
			px=0;
		}
		
		

		
		
		g2d.drawImage(picture,px,py,null);

		dbg.drawImage(buffer,0,0,null); 

	}		
	
	
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
		
		double rotate = 90 - player.angle;
		px=(int)rotate*5;
		
		
		moveBackground(dbg);
	//	player.drawKart(dbg);
		

	//	System.out.println("Done");

	   // dbg.drawImage(viewrange,0,0,null); //finally, draw everything to the real screen
	    

	}
	
	
	public void drawMap(Graphics dbg,Kart player){ //player is the perspective that you see stuff from
		

		BufferedImage map = new BufferedImage(map_width*2,map_height*2,BufferedImage.TYPE_INT_ARGB); //hollds the map picture
		BufferedImage screen = new BufferedImage(s_width,s_height,BufferedImage.TYPE_INT_ARGB); //holds a copy of the screen
		
		//x and yposition of camera
		int camX;
		int camY; 
		
		//(because you cant use get RGB for normal images)
		
	//	player.angle = 90;
		
		double rotate_ang = player.angle - 90;



		double cam_height = 95; //height of camera ===> also determines length of vertical...viwepoint, I guess 
		double yaw = 69; // angle camera is looking down at ===> determines length 
		double cam_dist = 35; //dist of camera to player ==> determines how "close" you can get to things =P
		double FOV =  90; //field of view parameters ===> determines how far you can see 
		
		int xpos = 0; //x position on the screen
		int ypos = 0; //y position on the screen
		
		//distance to pixel line ==> cam_height/tan(Math.toRadians(cam_orietnation)
		
		double straight_dist = cam_height*Math.tan(Math.toRadians(yaw));
		double increment = FOV/s_width; //increase the angle by this much everytime
		
		
		
		double start_angle = player.angle-FOV/2;
		double end_angle = player.angle+FOV/2;
		
		int strip_h = (int)(straight_dist-cam_dist); //height of each "strip"
		double scale_factor = (double)(s_height/2)/strip_h; //factor by which everything is scaled	



		
		Graphics2D g = map.createGraphics();
		
		g.drawImage(grass,0,0,null); //fill in the map w/ green
		g.drawImage(grass,0,1024,null);
		g.drawImage(grass,1024,0,null);
		g.drawImage(grass,1024,1024,null);
		
		
		g.translate((int)player.xpos,(int)player.ypos);
		g.rotate(Math.toRadians(rotate_ang));
		g.scale(scale_factor,scale_factor);
		
		
		g.drawImage(norm,-(int)player.xpos,-(int)player.ypos,null);
	
		g.translate(-(int)player.xpos,-(int)player.ypos);
		
		
		
		double height_start = s_height/2+cam_dist/scale_factor;
		double height_end = cam_dist/scale_factor+1;
		
		
		System.out.println(height_start-height_end);
		
//===============================problem with this is that it might go out of bounds, we need to fix this by making sure px,py


		for (double ang = start_angle; ang<=end_angle; ang+=increment){
			ypos = s_height/2;
			
			if (xpos==s_width){ //why do I have this...*yawns*...so lame 
				break;
			}
			
		//	System.out.println(xpos);
			
			for (double height=height_start;height>=height_end;height-=1){ //loop thru each height
				double dx = height*Math.tan(Math.toRadians(player.angle - ang)); //subtract your angle from the viewing angle 
				
				int newX = (int)dx+(int)player.xpos;
				int newY = (int)player.ypos-(int)height;

				int pixelvalue; //value of the pixel at that location
					
				if (newX<=0 || newX>=2000 || newY<=0 || newY>=2000){ //if out of bounds use a certain value? 
			//		System.out.println("out of range");
					pixelvalue = -16754688; 
				}
				
				else{
				  pixelvalue = map.getRGB(newX,newY);
				}
				
			//	System.out.println(xpos + " "+ypos);
				
				screen.setRGB(xpos,ypos,pixelvalue);
				ypos++; //increase y pos 
			}
			xpos++; //increase x pos 

			
		}
		

		
		dbg.drawImage(screen,0,0,null);


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
	double handling; //how sharply you can turn your kart 
	String name;
	boolean[] keys = new boolean[256];
	Image[] frames;
	int currentframe;
	
	char[][] terrain;
	
	public Kart(char[][] terrain_grid){
		xpos = 960;//960;// 1441;
		ypos = 683;//1173;
		angle = 90;
		
		velocity = 0;
		acceleration = 0;
		max_velocity = 30;
		min_velocity = -5;
		max_acceleration = 5;
		min_acceleration = -1;
		handling = 10;
		
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
		

	}
	
	public void updateAngle(){ //updates hte speed and angle 
		if (keys[37]==true){ //left turn 
			angle-=handling;
			currentframe = 2;
			
		}
		if (keys[39]==true){ //right turn
			angle+=handling;
			currentframe = 1;
	}
		if (keys[37]==false && keys[39]==false){ //if you aren't turning, your frame is the normal one
			currentframe = 0;
		}	
	
}
	
	public void updateSpeed(){
		
		if (keys[38]==true){
			xpos+=25*Math.cos(Math.toRadians(angle));
			ypos+=25*Math.sin(Math.toRadians(angle))*-1;	
		}
		
		if (keys[40]==true){
			xpos+=-25*Math.cos(Math.toRadians(angle));
			ypos+=-25*Math.sin(Math.toRadians(angle))*-1;	
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



