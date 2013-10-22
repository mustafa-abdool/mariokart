
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

	int view = 800;
	private int s_width = 1024;
	private int s_height = 1024;
	private int period;
	private int count; //used for testing
	private BufferedImage bitmap; //map thingy
	private Image real_map = new ImageIcon("new map.png").getImage();
	
	
	private int map_width = 1024;
	private int	map_height = 1024;	


	private Image norm = new ImageIcon("mushroom map2.PNG").getImage();
	private Image grass = new ImageIcon("bigger background green.PNG").getImage();

	
	private Kart[] players; //represents all the players in the game
	
	 
	 boolean []keys = new boolean[256];  //list of keys	
	char[][] terrain_grid = new char[128][128]; //grid for the terrain
	
	
	private String bg_file = "forest.gif"; //background pic 
	
	private ImageIcon whatever = new ImageIcon(bg_file);
	private Image picture = new ImageIcon(bg_file).getImage();
	private Image picture2 = new ImageIcon(bg_file).getImage();
	
	private int p_width = whatever.getIconWidth(); //dimensions of the picture itself 
	private double p_height = whatever.getIconHeight();
	

	
	private int px=0; //for bakcground
	private int py=0;
	
	private int bg_height = 100; //the height that you want the backgroundt o be 
	
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
		
		//update position of background
		
		double rotate = 90 - player.angle;
		px=(int)rotate*5;
		

		moveBackground(dbg);
		
		
		
	//	player.drawKart(dbg);
		

	//	System.out.println("Done");

	   // dbg.drawImage(viewrange,0,0,null); //finally, draw everything to the real screen
	    

	}
	
	public int round(double d){
		double n = d+.5;
		return (int)n;
		
	}	
	
	public void drawMap(Graphics dbg,Kart player){ //player is the perspective that you see stuff from
	
		BufferedImage map = new BufferedImage(1024,1024,BufferedImage.TYPE_INT_ARGB); //hollds the map picture
		BufferedImage screen = new BufferedImage(s_width,s_height,BufferedImage.TYPE_INT_ARGB); //holds a copy of the screen
		BufferedImage viewrange;
		double rotation_angle = Math.toRadians(90-player.angle);
		
		
		
		Graphics2D map_g = map.createGraphics();
		map_g.drawImage(norm,0,0,null);
		
		
		double y_offset = 100; 
	    double cam_height  = 60;
	    int horizon = 40; //pixels line 0 is below the horizon
	    
	    double scale_x=800; //scaling factors 
	    double scale_y=800;
	    

	    double line_dist; //dist to scanline
	    double line_scale; //horizontal scaling of this particular scanline 
	    
	    double line_dx; //increments that tell us how to move along a line
	    double line_dy; 
	    	
	    double map_x; // x, y position on the bitmap
	    double map_y;

		double px = player.xpos; //shallow (like laura) copies 
		double py = player.ypos; 
		
		double cx;
		double cy;	

		double cos_precalc = Math.cos(Math.toRadians(player.angle));
		double sin_precalc = Math.sin(Math.toRadians(player.angle));

	    
	    //extreme cases b/c...Java is dumb
	    
	    
	    if (player.angle==90){ 
	    	sin_precalc = 1;
    		cos_precalc = 0;
	    }
	    
	    if (player.angle==0){
	    	sin_precalc = 0;
	    	cos_precalc = 1;
	    }	    

	    if (player.angle==270){
	    	sin_precalc = -1;
	    	cos_precalc = 0;
	    }	    
	    
	    if (player.angle==180){
	    	sin_precalc = 0;
	    	cos_precalc = -1;
	    }	    


		//get camera pos 
		
		map_g.translate(px,py);
		map_g.rotate(rotation_angle);
		
		cx = 0;//px;
		cy = 0;//py;


		System.out.println(player.angle);

	    
	    map_g.setColor(Color.red);
	    
	    sin_precalc = 1; //assume 90 
    	cos_precalc = 0;	    
	    
	    
	    for (int screen_y = 40; screen_y<s_height;screen_y++){
	    	line_dist = (cam_height*scale_y)/(screen_y+horizon); //dist to the line 

	    	
	    	line_scale = line_dist/scale_x; //horizontal scale of the line 

	    	
	    	line_dx = -sin_precalc*line_scale; 
	    	line_dy = cos_precalc*line_scale;
	    	

	    	

	    	//starting pos
	    	map_x = cx+line_dist*cos_precalc-(double)s_width/2*line_dx; 
	    	map_y = cy+line_dist*sin_precalc-(double)s_width/2*line_dy;
	    	
	    	
			
			double map_x_end = map_x + line_dx*s_width;
			double map_y_end = map_y + line_dy*s_width;
			
			
			//reflect in the line which is the player, which the origin 
			int x1 =  round(map_x)*-1;
			int y1 = round(map_y)*-1+round(y_offset); //player dist is just like a...y offest I guess
			int x2 = round(map_x_end)*-1;
			int y2 = round(map_y_end)*-1+round(y_offset);
			
			
			
			map_g.drawLine(x1,y1,x2,y2);
	    	
/*	    	for (int screenx = 0;screenx<s_width;screenx++){
	    		
	    		int pixelvalue;
	    		
	    		int newX = round(map_x);
	    		int newY = round(map_y);
	    		
	    		
	    	//	System.out.println(newY);
	    		
	    		if (newX<0 || newY<0 || newX>=1024 || newY>=1024){
	    			pixelvalue = -16754688; 
	    		}
	    		
	    		else{
	    			pixelvalue = map.getRGB(newX,newY);
	    		}
	    		

	    		screen.setRGB(screenx,screen_y,pixelvalue);
	    		
	    		map_x+=line_dx;
	    		map_y+=line_dy;
	    	} */
	    	
	    }
	    
	    map_g.setColor(Color.black);
	    map_g.drawOval(round(cx),round(cy),5,5);	    
	  //  System.out.println("done");
	    dbg.drawImage(map,0,0,null);

		
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
		xpos = 512;//960;// 1441;
		ypos = 512;//683;//1173;
		angle = 90;
		
		velocity = 0;
		acceleration = 0;
		max_velocity = 10;
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
			angle = (angle+360)%360;
			currentframe = 2;
			
		}
		if (keys[39]==true){ //right turn
			angle-=handling;
			angle = (angle+360)%360;
			currentframe = 1;
	}
		if (keys[37]==false && keys[39]==false){ //if you aren't turning, your frame is the normal one
			currentframe = 0;
		}	
	
}
	
	public void updateSpeed(){
		
		if (keys[38]==true){
			acceleration+=.03;
		}
		
		if (keys[40]==true){
			acceleration-=.01;
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
		
		if (type=='b'){ //border 
			velocity*=-.1; //reverse velocity
			acceleration = 0;
		}
		
		if (type=='d'){ //dirt 
			velocity-=.5; //factor by which velocity is decreased
		}
		
		if (type=='g'){
			velocity-=.5; //factor by which velocity is decreased
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



