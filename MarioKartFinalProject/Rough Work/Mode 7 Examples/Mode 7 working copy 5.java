
//=======================MOOSEY-O KART FINAL PROJECT====================================


// the translation back to xcoord, ycoord isn't right...?
//I think this is because of the whole "scale factor" thing...
//Reverse transform to find the new line points



//Kart should be a subclass of the MainGame b/c you're using keys and stuff...



//Things to do!

//revert back to the original affine matrix each time
//clean stuff up 
//in the real game make sure that the player can't have an item held and then hit an item box AGAIN
//print what the exceptions are ==> Makes things easier to see 
//make dbg just a Graphics2D thing...saves the need for a screen variable? 
//moving backwards doesn't work b/c of the moveAlogn() function
//clean up minimap thing 
//have different stats for AI handling and velocity ===> so it doesn't look like they're all
//following the same path, you know...
//don't draw time on dbg...do it on map or something else 

//Get start time only once ==> Make a flag and get the time at the call 

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
import java.awt.Rectangle;
import java.applet.AudioClip;

class MainGame extends JFrame{
	
	private static int fps = 60;
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


//==========================Stuff that Java Needs=======================

	private MainGame top; //interface of main game
	private Graphics dbg;
	private Image dbImage;
	private Thread th;	
	boolean []keys = new boolean[256];  //list of keys	
	
	

	
//==========================Dimensions of Screen and stuff =======================	
	
	int view = 800; //what is this for...O_o
	private int s_width = 1024;
	private int s_height = 1024;
	private int period;
	private int map_width = 1024;
	private int	map_height = 1024;	
	
	private int minimap_width = 500; //2d flat representation 
	private int minimap_height = 500;
	
	private int view_height = 500; //3d representation 
	private int view_width = 500;

	private int v_height = 500; //3d representation 
	private int v_width = 500;
	
	private int HUD_height = 1000; //s height
	private int HUD_width = 200; //1000 - 800 s_width - view_width 
	

//======================More Graphics Stuff (Dimensions etc) ====================


	BufferedImage map = new BufferedImage(map_width,map_height,BufferedImage.TYPE_INT_ARGB); //hollds the map picture
	Graphics2D map_g = map.createGraphics();		
		
	BufferedImage screen = new BufferedImage(view_width,view_height,BufferedImage.TYPE_INT_ARGB); //holds a copy of the screen
	
	BufferedImage mini_map = new BufferedImage(minimap_width,minimap_height,BufferedImage.TYPE_INT_ARGB);		
	Graphics2D minimap_g = mini_map.createGraphics();
	
	double mini_xscale = (double)minimap_width/map_width;
	double mini_yscale = (double)minimap_height/map_height;
	
	BufferedImage view_range; //everything infront of the player 
	BufferedImage hstrip; //holds the current strip
	
	BufferedImage scaled_strip = new BufferedImage(v_width,1,BufferedImage.TYPE_INT_ARGB);
	Graphics2D sg = scaled_strip.createGraphics();
	
	
//======================Mode 7 Specific Stuff====================================

	double[] strip_lengths;
	double[] strip_heights; 
	
	double y_offset = 120; 
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
    
	double cx; //this is always 0,0 O_o
	double cy;		    
	
	double px;
	double py; 
	
	double cos_precalc; //precalculations for sine and cos of a given angle...
	double sin_precalc; 
		
	double max_width; //max width of strip ==> use for rectangle ==> remember to divide by 2
	double max_height; //max height of strip ==> use for rectangle ==> remember to divide by 2
	
	double obj_xscale;
	double obj_yscale; 
		
	
//====================================Background stuff===============================	
	private String bg_file = "forest.gif"; //background pic 
	private ImageIcon whatever = new ImageIcon(bg_file);
	private Image picture = new ImageIcon(bg_file).getImage();
	private Image picture2 = new ImageIcon(bg_file).getImage();	
	private int p_width = whatever.getIconWidth(); //dimensions of the picture itself 
	private double p_height = whatever.getIconHeight();
	private int bx=0; //for bakcground
	private int by=0;	
	private int bg_height = 100; //the height that you want the backgroundt o be 


//==================In Game stuff==============================================	

	private ItemBox[] items; //holds where the items are 
	private Kart[] players; //represents all the players in the game
	char[][] terrain_grid = new char[128][128]; //grid for the terrain
	private ArrayList<GreenShell> green_shells = new ArrayList<GreenShell>();
	private ArrayList<RedShell> red_shells = new ArrayList<RedShell>();
	
	private CheckPoint half; //half lap checkpoint 
	private CheckPoint finish_line; //finish line checkpoint 
	
	private int lap_no; //how many laps there are in this race
	private String[] rankings; 
	
	
	
	//indicates if you finish a lap or not 


//==========================HUD Stuff=============================

	private Font gameFont = new Font("Mario Kart Font",20,20);
		
		
//====================Pictures===================

	private Image norm = new ImageIcon("mushroom map2.PNG").getImage();



//=========================Time=========================

		long start_time;
		long millisecond=0;
		long second = 0;
		long min = 0;	

//=================Music==========================
		
		private HashMap<String,AudioClip> songs = new HashMap<String,AudioClip>();
		
	
	public void generateStrips(){ //generate strip descriptions...
		
		strip_lengths = new double[view_height-horizon]; //as many strips as scanlines/pixels on the screen
		strip_heights = new double[view_height-horizon];
	    sin_precalc = 1; //assume 90 
    	cos_precalc = 0;
    	boolean first_flag = true; 	    
	    
	    
	    for (int screen_y = horizon; screen_y<view_height;screen_y++){
	    	line_dist = (cam_height*scale_y)/(screen_y+horizon); //dist to the line
	   	
	    	line_scale = line_dist/scale_x; //horizontal scale of the line 
	    	
	    	line_dx = -sin_precalc*line_scale; 
	    	line_dy = cos_precalc*line_scale;


			double len = line_dx*view_width*-1; //*-1 b/c its negative for some reason...? O_o
			double height = ((line_dist*sin_precalc-(double)view_width/2*line_dy)*-1+y_offset)*-1;
			
			strip_lengths[screen_y - horizon] = len; //related by indices
			strip_heights[screen_y - horizon] = height; 
			
					
			if (first_flag==true){
				max_width = strip_lengths[0];
				max_height = strip_heights[0];
				view_range = new BufferedImage(round(max_width),round(max_height),BufferedImage.TYPE_INT_ARGB);
				first_flag=false; 
			}
	    	
	    }		
		
	}

	public void loadCheckPoints(){
		
		try{
			Scanner infile = new Scanner(new File("checkpoint.txt"));
			for (int i=0;i<2;i++){
				String line = infile.nextLine();
				String[] data = line.split(" ");
				int xcoord = Integer.parseInt(data[0]); //xcoord
				int ycoord = Integer.parseInt(data[1]); //y coord
				int width = Integer.parseInt(data[2]);
				int height = Integer.parseInt(data[3]);
				
				if (i==0){ //halfway 
					half = new CheckPoint(xcoord,ycoord,width,height);
				}
				if (i==1){ //finish line 
					finish_line = new CheckPoint(xcoord,ycoord,width,height);
				}	
							
			}
			

		}
		
		catch(Exception e){
			System.out.println(e);
		}
	}


	public void checkItemCollisions(Kart player){
		Rectangle r = new Rectangle(round(player.xpos),round(player.ypos),8,8); //get the rectangle made
		//by the player 
		for (ItemBox item:items){ //for each item box check if it collides w/ the player 
			if (item.collide(r)==true && player.hasItem()==false && item.isUsed()==false){
				item.setUsed();
				player.setItem();
				
			}
		}
	}

	public void drawItems(){
		for (ItemBox item: items){
			item.draw(map_g);
		}
	}
	
	public void loadItems(){ //load where all the items are from the datafile 
		try{
			Scanner infile = new Scanner(new File("Item Box Locations.txt"));
			int no = Integer.parseInt(infile.nextLine());
			items = new ItemBox[no];
			for (int i=0;i<no;i++){
				String line = infile.nextLine();

				
				String[] data = line.split(" ");
				int xcoord = Integer.parseInt(data[0]); //xcoord
				int ycoord = Integer.parseInt(data[1]); //y coord
				items[i] = new ItemBox(xcoord,ycoord);

			}
			
		}
		
		catch(Exception ILoveAlvee){
			System.out.println("Item Data File Not Found");
			}
	}
	
	
	public void moveBackground(Graphics dbg){ //function that handles the background moving
	
	
		BufferedImage buffer = new BufferedImage(s_width,bg_height,BufferedImage.TYPE_INT_ARGB); //buffered image used to scale the iamge
		Graphics2D g2d = buffer.createGraphics();
		

	

		
		double yscale =  bg_height/p_height;
		
		g2d.scale(1,yscale); //scale appropriately 

		if (bx<p_width && bx>0){ //going left - using 2 images
			g2d.drawImage(picture,bx-p_width,by,null);
		}
		
		if (bx>p_width){ //reset - going left 
			px=0;
		}
		
		if (bx*-1+view_width>p_width){
			int newX = p_width+bx;
			g2d.drawImage(picture,newX,by,null);
		}
		
		
		if (bx*-1>=p_width){ // reset - going right
			bx=0;
		}
		
		

		
		
		g2d.drawImage(picture,bx,by,null);

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
			System.out.println("Map Data File Not Found");
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
    
    public String getTime(){
    	
		long diff = System.currentTimeMillis ()-start_time;
		long milli = diff%1000;
		second = (diff/1000)%60;
		min = diff/60000;
		return "Time: "+ min+" "+second+" "+milli+" s";
    				//mm ss mmm
			//mm seconds milliseconds 
			
    }	
	
	public void run(){
		
		long lastUpdate, sleepLen;
		lastUpdate = System.currentTimeMillis ();
		start_time = System.currentTimeMillis ();
	
		
		players = new Kart[8];
		rankings = new String[8];
		
		lap_no = 1; //number of laps in the race 
		
		loadMapData(); //load the data for that map
		loadItems(); //make item box stuff
		loadCheckPoints(); //load checkpoint information 
		minimap_g.scale(mini_xscale,mini_yscale);
		players[0] = new Kart("Mario",false,512,512);
		players[1] = new Kart("Peach",true,918,577);
		
		generateStrips();
		System.out.println("Max width is: " + max_width);
		System.out.println("Max Height is: "+ max_height);
		
		
		
	//	players[2] = new Kart("Wario",true,919,627);
		
		
		
		while (true){
			
			moveKarts();
			moveObjects();
			render(players[0]); //draw stuff to offscreen buffer ==> karts[0] means everything is relative to the [0]th kart
			paintScreen(); //paint the screen
			
			
	    	sleepLen = lastUpdate + period - System.currentTimeMillis ();
	    	sleepLen = Math.max (5, sleepLen);
	    	delay (sleepLen);
	    	lastUpdate = System.currentTimeMillis ();

			}
			
			
		}

	

	public void moveKarts(){
		for (Kart player:players){
			if (player!=null && player.isAI()==false){
				player.move();
				player.useItemCheck();
				checkItemCollisions(player);
				player.lapCheck();
			}
			if (player!=null && player.isAI()==true){
//				System.out.println("AI Char in Game tenete");
				player.AImove(); //move according to AI rules
				player.lapCheck(); //check laps for update 
			}
			
		}
	}
	
	public void moveObjects(){
		moveGreenShells();
	}
	
	public void moveGreenShells(){
		
		for (int i=0;i<green_shells.size();i++){
			GreenShell shell = green_shells.get(i);
			if (shell.isAvailable()){
				shell.move();
			}
			if (shell.isAvailable()==false){ //remove if lifespan is over 
				green_shells.remove(i);
			}
			
		}
	}
	
	
	public void drawObjects(){
		drawGreenShells();
	}
	
	public void drawGreenShells(){
		for (GreenShell shell:green_shells){
			shell.draw(map_g);
		}		
	}

	
	
	public void drawPlayers(Kart player){ //this won't be used in 3d rendering...only for debugging
		for (Kart character:players){
			if (character!=null){
				if (character.equals(player)==false){
					character.draw(map_g);
					character.drawAIPoints(map_g);					
				}

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
	    	dbg.setFont(gameFont);
	    }
	     
	    //reset screen 
	   // drawBackground(dbg);
	    dbg.setColor(Color.black);
	    dbg.fillRect(0,0,s_width,s_height);
		
		
		
		
		
		
		//update position of background
		
		double rotate = 90 - player.angle;
		bx=(int)rotate*5;
		

		moveBackground(dbg);



	    
		//draw original map and stuff...
	    map_g.drawImage(norm,0,0,null);

	    //render floor ==> draw red lines 
		renderFloor(dbg,player);	    
	    
	    drawItems(); //see if the item boxes should be drawn on the map...
	    drawObjects();
		drawPlayers(player); //this won't be used in 3d rendering  ==> only for debugging
	
		 
		//draw player 
	    map_g.setColor(Color.black);
	    map_g.fillRect(round(player.xpos)-4,round(player.ypos)-4,8,8);	    
	    
	    //draw minimap pl0x	
		minimap_g.drawImage(map,0,0,null); //draw in order to scale...(minimap) 		
	    dbg.drawImage(mini_map,0,0,null); //draw map to screen - USED ONLY FOR DEBUGGING 
	    
	    
	    		
		drawHUD(dbg, player);
		
		
		renderFloor3D(dbg,player);

	}
	
	
	
	public void drawHUD(Graphics g, Kart player ){
		drawLapCount(g,player);
		drawTime(g);
	}
	
	public void drawTime(Graphics g){
		g.drawString(this.getTime(),500,100);
	}
	
	
	public void drawLapCount(Graphics g, Kart player ){
		g.drawString("Lap: "+player.getLap(),100,100);	
	}
	
	public int round(double d){
		double n = d+.5;
		return (int)n;
		
	}	
		
	
	public void renderFloor32D(Graphics dbg, Kart player){ //second try...following example this time
	
		
	
		
	}
	
	
		
	public void renderFloor3D(Graphics dbg,Kart player){
		double rotation_angle = Math.toRadians(90-player.angle);
		
		double px = player.xpos; //shallow (like laura) copies 
		double py = player.ypos; 	
		
		double cos_precalc = Math.cos(rotation_angle);
		
		if (rotation_angle==0){
			cos_precalc = 0;
		}
		
		int drawstart = horizon;
		
	  /*  	//using rotation transform...
	   *
	   *	double tempX = (strip_lengths[i]/2)*Math.cos(r_angle) + strip_height*Math.sin(r_angle)
	   *    double tempY = -1 * (strip_lengths[i]/2)*Math.sin(r_angle) + strip_height * Math.sin(r_angle)
	   *    startX = px - tempX
	   *    startY = py - tempY
	   *    hscale = distance/xscale;
	   *    line_dx = 
	   *
	   
			
	    	
	    	
	    	 */
	    			
		
		for (int i = 0; i <(strip_heights.length-1);i++){
			
			double len = strip_lengths[i];
			double startX = px - len/2;
			double startY = py - strip_heights[i];
			double dx = len/v_width;
			


			
			for (int j = 0 ; j<v_width;j++){
				
				int pixelvalue;
				
				int newX = round(startX);
				int newY = round(startY);
				
	    		if (newX<0 || newY<0 || newX>=1024 || newY>=1024){
	    			pixelvalue = -16754688; 
	    		}
	    		
	    		else{
	    			pixelvalue = map.getRGB(newX,newY);
	    		}				
				
				screen.setRGB(j,drawstart,pixelvalue);
				
				startX+=dx;

				
			}
			
			drawstart++; //increase y coord  
			
		} 
		
		

		
		dbg.drawImage(screen,500,0,null);
		


	}	
	
	public void renderFloor(Graphics dbg,Kart player){ //player is the perspective that you see stuff from
	
	

		double rotation_angle = Math.toRadians(90-player.angle);



		px = player.xpos; //shallow (like laura) copies 
		py = player.ypos; 
		

		cos_precalc = Math.cos(Math.toRadians(player.angle));
		sin_precalc = Math.sin(Math.toRadians(player.angle));

	    
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

		map_g.translate(px,py);
		map_g.rotate(rotation_angle);
		
		cx = 0;
		cy = 0;

	    map_g.setColor(Color.red);
	    
	    sin_precalc = 1; //assume 90 
    	cos_precalc = 0;	    
	    
	    
	    for (int screen_y = horizon; screen_y<view_height;screen_y++){
	    	line_dist = (cam_height*scale_y)/(screen_y+horizon); //dist to the line
	   	
	    	line_scale = line_dist/scale_x; //horizontal scale of the line 
	    	
	    	line_dx = -sin_precalc*line_scale; 
	    	line_dy = cos_precalc*line_scale;

	    	//starting pos
	    	map_x = cx+line_dist*cos_precalc-(double)view_width/2*line_dx; 
	    	map_y = cy+line_dist*sin_precalc-(double)view_width/2*line_dy;
			
		//	System.out.println(map_x);
			
			double map_x_end = map_x + line_dx*view_width;
			double map_y_end = map_y + line_dy*view_width;
			
					
			//reflect in the line which is the player, which the origin 
			int x1 =  round(map_x)*-1;
			int y1 = round(map_y)*-1+round(y_offset); //player dist is just like a...y offest I guess
			int x2 = round(map_x_end)*-1;
			int y2 = round(map_y_end)*-1+round(y_offset);

			map_g.drawLine(x1,y1,x2,y2);
	   
	    	
	    }
	    
	    
	    
	    //revert back to the original affine matrix  - THIS IS IMPORTANT 
	    map_g.rotate(-rotation_angle);
		map_g.translate(-px,-py);
	    

		
	}
	
/*	public void renderFloor32d(Graphics dbg, Kart player){

		
		double rotation_angle = Math.toRadians(90-player.angle);
	
		double map_x;
		double map_y;
	    
	    double r_cos_precalc;
	    double r_sin_precalc; 
	    
	    //extreme cases b/c...Java is dumb
	    
		px = player.xpos; //shallow (like laura) copies 
		py = player.ypos; 
				    
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

		
		cx = px; 
		cy = py;

	    
	    sin_precalc = 1; //assume 90 
    	cos_precalc = 0;	    
	    
	    r_sin_precalc = Math.sin(rotation_angle);
	    r_cos_precalc = Math.cos(rotation_angle);
	    
	    
	   // System.out.println(player.angle);
	    
	    for (int screen_y = horizon; screen_y<view_height;screen_y++){


	    			    	
	    	
	    	line_dist = (cam_height*scale_y)/(screen_y+horizon); //dist to the line 

	    	
	    	line_scale = line_dist/scale_x; //horizontal scale of the line 

	    	
	    	line_dx = -sin_precalc*line_scale; 
	    	line_dy = cos_precalc*line_scale;
	    	

	    	

	    	//starting pos
	    	map_x = cx+line_dist*cos_precalc-(double)v_width/2*line_dx; 
	    	map_y = cy+line_dist*sin_precalc-(double)v_width/2*line_dy;
	    	

			
			//reflect in the line which is the player
			double x1 =  map_x*-1;
			double y1 = map_y*-1+y_offset; //player dist is just like a...y offest I guess
			

	    	for (int screenx = 0;screenx<v_width;screenx++){
	    		
	    		int pixelvalue;
	    		

	    		
				double tempX = (x1*r_cos_precalc + r_sin_precalc*-1*y1 + px);
	    		double tempY = (r_sin_precalc*x1+ r_cos_precalc*y1 + py);
	    		
	    		int newX = round(tempX);
	    		int newY = round(tempY);
	    		

	    		
	    		if (newX<0 || newY<0 || newX>=1024 || newY>=1024){
	    			pixelvalue = -16754688; 
	    		}
	    		
	    		else{
	    			pixelvalue = old.getRGB(newX,newY);
	    		}
	    		

	    		screen.setRGB(screenx,screen_y,pixelvalue);
	    		
	    		x1+=line_dx;
	    		y1+=line_dy;
	    	} 
	    	
	    }
	    

	    dbg.drawImage(screen,500,0,null);	
		
	} */
	
	
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



	public void updateRankings(Kart player){
		String name = player.getName();

		for (int i = 1; i < rankings.length; i ++){

			if (rankings[i]==null){
				rankings[i] = name;
				//System.out.println("Player called "+ name + " is rank " + i );
				break; 
			}
		}
		
	}


class Kart implements KeyListener{
	double xpos;
	double ypos;
	double angle; //angle that this kart is facing

	
//=====================Constant Stat Values===============
	
	
	private double star1_accel = 0.02;
	private double star2_accel = 0.03;
	private double star3_accel = 0.04;
	private double star1_speed = 7; //max velocity
	private double star2_speed = 10 ;
	private double star3_speed = 13;
	private double star1_offroad = 3; //max offroad velocity 
	private double star2_offroad = 4;
	private double star3_offroad = 5; 



	/*	velocity = 0;
		acceleration = 0;
		max_velocity = 10;
		min_velocity = -5;
		
		accel_increment = 0.03;
		brake_increment = 0.01;
		
		offroad_accel_increment = 0.01;
		offroad_max_velocity = 5; //max velocity offroad  	*/

	
//=====================AI Stuff===========================	
	boolean AI; //if this kart is AI or not...
	double opponent_deaccel = 0.02; //slow down when turning corners...
	
	double x_leftpoint;
	double y_leftpoint;
	
	double x_rightpoint;
	double y_rightpoint;
	
	double x_forwardpoint;
	double y_forwardpoint;
	
	double angle_dev = 20; 
	double point_dist = 55;
	double fpoint_dist = 55;
	
	String last_turn = "left"; //depends on level y'know, laura luo is...a meanie =(
	
	
	
//==============for when you're on the road - STATS ===============	
	double acceleration;
	double velocity;
	
	double accel_increment;  //how quickly you speed up 
	double brake_increment; //how fast your brakes slow you down
	
	double max_velocity; //top speed
	double min_velocity; //min reverse speed...who cares about this though *yawns*
	//maybe I'll just make this the same for every character 

	String max_speed;
	String offroad_speed;
	String accel_const;
	String item_const; 
	
//====================Items==========================

	boolean hasItem; //tells if you have an item or not 
	String item; //string representing what item you have 
	int item_timer; // how long you have a mushroom/star for
	boolean starFlag;


//==========for when you're on the dirt/ice/grass/death whatever=============

	double offroad_accel_increment;
	double offroad_max_velocity;


//=============CheckPoint and Lap Data================


//	private boolean finLap;
	private boolean halfWay;
	private int lapCount; 
		
//=====================================================	
	
	int handling; //how sharply you can turn your kart 
	String name; //name of character - used to determine stats later on...
	Image[] frames;
	int currentframe;
	
	
	public double getStatValue(String stat, String stars){
		if (stars.equals("*")){
			
		}
		if (stars.equals("**")){
			
		}
		if (stars.equals("***")){
			
		}
		
		return -1; //change this later 
	
	}
		
		

	
	
	public Kart(String name,boolean AIflag,double startx, double starty){
		
		AI = AIflag;
		this.name = name;
		xpos = startx;//960;// 1441;
		ypos = starty;//683;//1173;
		
		
		angle = 90;

		
		try{
			Scanner infile = new Scanner(new File("Character Stats.txt"));
			while (infile.hasNextLine()){
				String line = infile.nextLine();
				String[] temp; 
				if (line.equals(name)){
					System.out.println("okay");
					max_speed = infile.nextLine().split(" ")[1];
					offroad_speed = infile.nextLine().split(" ")[1];
					accel_const = infile.nextLine().split(" ")[1];
					item_const = infile.nextLine();
				}
			}
		}
		
		catch (Exception lauraLuoisMean){
			System.out.println(lauraLuoisMean);
		}
		
		

		velocity = 0;
		acceleration = 0;
		max_velocity = 10;
		min_velocity = -5;
		
		accel_increment = 0.03;
		brake_increment = 0.01;
		
		offroad_accel_increment = 0.01;
		offroad_max_velocity = 5; //max velocity offroad  		

		handling = 5;
		
		requestFocus(); //for the key stuff
		addKeyListener(this);		
			
			
//		finLap = false;
		halfWay = false; 
			
		hasItem = true;
		item = "Green Shell";
		item_timer = 0; 	
			
		frames = new Image[3];
		frames[0] = new ImageIcon("karts16 copy.png").getImage();
		frames[1] = new ImageIcon("karts10 copy.png").getImage();
		frames[2] = new ImageIcon("karts4 copy.png").getImage();
					
	}
	
	
	public int getLap(){ //return what lap you're on 
		return lapCount;
	}
	
	public void lapCheck(){
		
		Rectangle r = new Rectangle(round(xpos),round(ypos),8,8);
		
		if (half.collide(r)==true && halfWay == false){ //halfWay check 
			halfWay = true;
		//	System.out.println("halfway mark passed"); 
		}
		
		if (finish_line.collide(r)==true && halfWay==true){ //Lap Complete check 
			lapCount++; //increase lap
		//	System.out.println(lapCount);
		//	System.out.println(lap_no);
			
			halfWay = false;
			if (lapCount==lap_no){
				updateRankings(this);
			}
		//	System.out.println("lap complete");
		}

		
	}
	
	
	public String getName(){
		return this.name; 
	}
	
	public boolean isAI(){
		return AI==true;
	}
	
	
	public void mushroomCheck(){
		if (item_timer>0){ //if you have a mushroom...put this in a function later 
			velocity = 15;
			item_timer--;
		}		
	}
	
	
	public void move(){
		updateAngle();
		updateSpeed();
		
		mushroomCheck();
		
		
		moveAlong();		
		
	}
	
	public void updateAngle(){ //updates the speed and angle 
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
	
	public void moveAlong(){
		boolean stopflag = false;
		for (int v=0;v<velocity;v++){ //loop as many "velocities" as he has...
			if (velocity>0){ //moving forward
				xpos+=1*Math.cos(Math.toRadians(angle));
				ypos+=1*Math.sin(Math.toRadians(angle))*-1;					
			}
			if (velocity<0){ //moving backward
				xpos+=-1*Math.cos(Math.toRadians(angle));
				ypos+=-1*Math.sin(Math.toRadians(angle))*-1;					
			}			
			
		
		 	stopflag = allBorderCheck();
		 	if (stopflag==true){ //if you hit a barrier move back a bit ...with added velocity?
		 	//	System.out.println("collide");
				xpos-=(2+velocity*.25)*Math.cos(Math.toRadians(angle));
				ypos-=(2+velocity*.25)*Math.sin(Math.toRadians(angle))*-1;			 				 		
		 		break;
		 	}	
		}		
	}
	
	public boolean allBorderCheck(){
		return (pointBorderCheck(xpos+4,ypos+4) || pointBorderCheck(xpos+4,ypos-4) || pointBorderCheck(xpos-4,ypos-4) || pointBorderCheck(xpos-4,ypos+4)); 
		
	}
	
	public boolean offRoadCheck(){

		int newx = round(xpos);
		int newy = round(ypos);		

		int gridx = newx/8; //divide by grid ratio
		int gridy = newy/8;
		
		return terrain_grid[gridy][gridx]=='d';
		
	}

	public boolean pointBorderCheck(double px,double py){ //checks to see if this specific point lies on a border
		
		int newx = (int)px;
		int newy = (int)py;
		
		int gridx = newx/8; //divide by grid ratio
		int gridy = newy/8;
		
		return terrain_grid[gridy][gridx]=='b';
		
		
		
	}
	
	public boolean pointDirtCheck(double px,double py){
		int newx = (int)px;
		int newy = (int)py;
		
		int gridx = newx/8; //divide by grid ratio
		int gridy = newy/8;
		
		return terrain_grid[gridy][gridx]=='d';		
	}
	
	public void updateSpeed(){
		
		//System.out.println(offRoadCheck());
		
		if (keys[38]==true && offRoadCheck()==false){ //up arrow 
			acceleration+=accel_increment; //0.03
		}

		if (keys[38]==true && offRoadCheck()==true){ //up arrow ==> on dirt 
			acceleration+=offroad_accel_increment; //0.03
		}


		
		if (keys[40]==true){ //down arrow 
			acceleration-=brake_increment; //0.01
		}
		
		if (keys[38]==false && keys[40]==false){ //not moving - no arrow 
			acceleration-=.2; //constant 
		}
		
		

		
		if (velocity<=0 && keys[38]==false && keys[40]==false){ //if you aren't moving at ALL
			acceleration = 0;
			velocity = 0;
		}
		
		
		velocity+=acceleration;
		

		if (velocity>offroad_max_velocity && offRoadCheck()==true){ // on dirt 
			if (velocity+2>offroad_max_velocity){
				velocity-=1;
			}
			else{
				velocity = offroad_max_velocity;
			}
			acceleration = 0;
		}		
		
		if (velocity>max_velocity && offRoadCheck()==false){
			velocity = max_velocity;
			acceleration = 0;
		}
		
		if (velocity<min_velocity){
			velocity = min_velocity;
			acceleration = 0;
		}		
		
	}
	
	public void setItem(){ //set to true
		this.hasItem = true;
		item = "Green Shell";
	}
	
	public boolean hasItem(){
		return this.hasItem==true;
	}

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        //System.out.println(e.getKeyCode());

    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
    
    public void useItemCheck(){
		
		
		if (keys[70]==true && hasItem){
			
		
		    	
    	if (item.equals("Red Shell")){
    		
    	}
     	if (item.equals("Green Shell")){
     		System.out.println("Green Shell Used");
     		item = ""; //don't need to do this
     		hasItem = false;     		
    		GreenShell temp = new GreenShell(xpos,ypos,angle,terrain_grid); //make it 
    		green_shells.add(temp); //add it 
    	}
    	
     	if (item.equals("Mushroom")){
     		System.out.println("Mushroom Used");
     		item = ""; //don't need to do this
     		hasItem = false;
     		item_timer = 20; //some timer...indicates how long your velocity increases for     		
    	}
    	
    	if (item.equals("Star")){
    		
    	}    	
     	if (item.equals("Banana")){
    		
    	}
    	
		}    	
    	   	   	
    }
    
    public double getAngle(){
    	return this.angle;
    }
    
    public double getX(){
    	return this.xpos;
    }
    
    public double getY(){
    	return this.ypos; 
    }
    
    public void draw(Graphics2D g){
		

		int x = round(xpos);
		int y = round(ypos);
		g.setColor(Color.blue);
		g.fillRect(x-4,y-4,8,8);
		
    }
    
    public void AImove(){
    	
    	x_leftpoint = xpos + point_dist*Math.cos(Math.toRadians(angle + angle_dev));
    	y_leftpoint = ypos + point_dist*Math.sin(Math.toRadians(angle + angle_dev))*-1;
    	
     	x_rightpoint = xpos + point_dist*Math.cos(Math.toRadians(angle - angle_dev));
    	y_rightpoint = ypos + point_dist*Math.sin(Math.toRadians(angle - angle_dev))*-1;   	
    	
 
     	x_forwardpoint = xpos + fpoint_dist*Math.cos(Math.toRadians(angle));
    	y_forwardpoint = ypos + fpoint_dist*Math.sin(Math.toRadians(angle))*-1;   
    	
  //  	System.out.println(x_leftpoint+" "+x_rightpoint);
    	
    	acceleration+=accel_increment; //0.03
 
		velocity+=acceleration;
		

		if (velocity>offroad_max_velocity && offRoadCheck()==true){ // on dirt 
			if (velocity+2>offroad_max_velocity){
				velocity-=1;
			}
			else{
				velocity = offroad_max_velocity;
			}
			acceleration = 0;
		}		
		
		if (velocity>max_velocity && offRoadCheck()==false){
			velocity = max_velocity;
			acceleration = 0;
		}
		
		///basic stuff    
		
		//==================AI Stuff==================	

    	if (pointDirtCheck(x_forwardpoint,y_forwardpoint)){
    		if (last_turn.equals("left")){
    			angle-=20;
    			velocity*=.9;
    		}
    		if (last_turn.equals("right")){
    			angle+=20;
    			velocity*=.9;
    		}
    	}

    	
    	if (pointDirtCheck(x_leftpoint,y_leftpoint)){
    	//	System.out.println("left on dirt");
    		angle-=20;
    		velocity*=.9;
    		last_turn = "left";
    	}
     	if (pointDirtCheck(x_rightpoint,y_rightpoint)){
     	//	System.out.println("right on dirt");
    		angle+=20;
    		velocity*=.9;
    		last_turn = "right";    		
    	}
    	

    	
   // 	velocity = 2;   	
    	
    	moveAlong(); //last thing you doo...
    	
    }
    
    public void drawAIPoints(Graphics2D g){
    	
    	x_leftpoint = xpos + point_dist*Math.cos(Math.toRadians(angle + angle_dev));
    	y_leftpoint = ypos + point_dist*Math.sin(Math.toRadians(angle + angle_dev))*-1;
    	
     	x_rightpoint = xpos + point_dist*Math.cos(Math.toRadians(angle - angle_dev));
    	y_rightpoint = ypos + point_dist*Math.sin(Math.toRadians(angle - angle_dev))*-1;
    	
    	x_forwardpoint = xpos + fpoint_dist*Math.cos(Math.toRadians(angle));
    	y_forwardpoint = ypos + fpoint_dist*Math.sin(Math.toRadians(angle))*-1;   
    	
    	g.setColor(Color.pink);
    	
    	g.fillOval(round(x_leftpoint),round(y_leftpoint),5,5);
    	
    	g.setColor(Color.yellow);
    	
    	g.fillOval(round(x_rightpoint),round(y_rightpoint),5,5);
    	
    	g.setColor(Color.gray);
    	
    	g.fillOval(round(x_forwardpoint),round(y_forwardpoint),5,5);
    	
    }
    

    	
	
	
	
}



}



