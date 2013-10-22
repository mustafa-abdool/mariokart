
//=======================MOOSEY-O KART FINAL PROJECT====================================


//CHANGE IMAGE TO IMAGEICON...doesn't make a difference 

//CROP ALL PICTURES  ===> GET RID OF THE EXTRA FLUFF AND THEN CHANGE THE HEIGHT/WIDTH IN THE
// "TOBUFFEREDIMAGE" FUNCTION 

//if you CHANGE THE WIDTH AND HEIGHT CHANGE THE COLLISION STUFF TOO!

//Kart should be a subclass of the MainGame b/c you're using keys and stuff...

//Things to do!

//revert back to the original affine matrix each time
//clean stuff up 
//in the real game make sure that the player can't have an item held and then hit an item box AGAIN

//make dbg just a Graphics2D thing...saves the need for a screen variable? 
//moving backwards doesn't work b/c of the moveAlogn() function

//have different stats for AI handling and velocity ===> so it doesn't look like they're all
//following the same path, you know...


//Get start time only once ==> Make a flag and get the time at the call 
//To get sprites find which screen coordinate corresponds to (x,y) on the screen
//find relative distance...O_o...then scale...then draw
//zomg so much work...

//give characters item based on item_const variable 

//make getter methods TOO!


//getAudioClip(getCodeBase(),"laser.wav"); ===> How to get audioclips 


//fix shell collisiosn ===> DELETE THE SHELL AFTER COLLISION
//add spinning frames in 
//shell bouncing is a bit off
//make shell appear a bit further up from player 



//red shells should act like a player ==> home when in a certain radius
//add in sprites + working mode 7
//add in different ai things...for borders etc 


//why does the other human player move in the same way as you do? O_o (weird) 


//ALL ITEM NAMES MUST BE IN LOWERCASE LIKE THE FILE NAMES 

//change some names..like Koopa Troop should BE KT when loading images..caues I was lazy lololol


//player scanline shouldn't be so low ===> subtract width/2 
//should you be able to see players in the horizon or not?

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
import java.applet.*; //Import all the stuff we need...
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;             
import java.awt.event.*;
import java.awt.font.*;
import java.applet.AudioClip;
import javax.sound.midi.*; //for the music 
import java.io.*;

class MainGame extends JFrame{
	
	private static int fps = 600;
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


	int screenx_offset = 500;//x offset of the 3d portion
	int screeny_offset = 0;//y offset of the 3d portion 	

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
	double[] line_scales;
	
	//y_off = 100
	//height = 60
	
	//or...
	
	//y off = 70
	//c height = 40
	
	double y_offset = 100; //how close cam is to player 
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
	
	double min_height; //min height and min width of strips 
	double min_width; 
		
	double player_scanline; //scanline that the player is ALWAYS centered on
	//therefore, no objects/sprites can be drawn before this point 
	
	double obj_xscale = 200;
	double obj_yscale = 200; 
		
	
//====================================Background stuff===============================	

	private HashMap<String,String> map_horizons = new HashMap<String,String>();
	
	private String bg_file; // = "dark mountain.PNG"; //background pic 
	private ImageIcon bg_pic; // = new ImageIcon(bg_file);
	private Image picture; // = new ImageIcon(bg_file).getImage();
	private Image picture2; // = new ImageIcon(bg_file).getImage();	
	private int p_width; // = whatever.getIconWidth(); //dimensions of the picture itself 
	private double p_height; // = whatever.getIconHeight();
	
	private int bx=0; //for bakcground
	private int by=0;	
	private int bg_height = horizon; //the height that you want the backgroundt o be 


//==================In Game stuff==============================================	

	private ItemBox[] items; //holds where the items are 
	private Kart[] players; //represents all the players in the game
	char[][] terrain_grid = new char[128][128]; //grid for the terrain
	private ArrayList<GreenShell> green_shells = new ArrayList<GreenShell>();
	private ArrayList<RedShell> red_shells = new ArrayList<RedShell>();
	private ArrayList<Banana> bananas = new ArrayList<Banana>(); //bananas...
	private ArrayList<Pipe> pipes = new ArrayList<Pipe>();
	
	private CheckPoint half; //half lap checkpoint 
	private CheckPoint finish_line; //finish line checkpoint 
	
	private int lap_no; //how many laps there are in this race
	private String[] rankings; 
	private HashMap<Integer,Integer> frameMap = new HashMap<Integer,Integer>();
	private HashMap<Integer,Integer> reverse_frameMap = new HashMap<Integer,Integer>();
	private ArrayList<Rectangle> boosts = new ArrayList<Rectangle>();
	private ArrayList<Rectangle> oil_spills = new ArrayList<Rectangle>();
	
	//indicates if you finish a lap or not 


//==========================HUD Stuff=============================

	private Font gameFont = new Font("Mario Kart Font",20,20);
	//pictures for items on HUD
	
	private HashMap<String,Image> item_pics = new HashMap<String,Image>();
	
		
		
		
//====================Map Pictures===================
	
	private HashMap<String,ImageIcon> maps = new HashMap<String,ImageIcon>();
	
		
	private Image level_map; //variable which holds the map of the level 



//=========================Time=========================

		long start_time;
		long millisecond=0;
		long second = 0;
		long min = 0;	

//=================Music==========================
		
		
		Sequencer bg_music;


	//	String[] song_names = new String[8];
		private HashMap<String,String> songs = new HashMap<String,String>();


//==================Level Things=========================

		String level; //name of level 
		ArrayList<String> green_background = new ArrayList<String>();
		ArrayList<String> white_background = new ArrayList<String>();
		ArrayList<String> black_background = new ArrayList<String>();
				
		ArrayList<String> brown_background = new ArrayList<String>();		
		
	
//=================Options==========================

		int ai_difficultly = 1; 
			
		boolean two_player = false;
		int player_no = 2;
		ArrayList<String> player_names = new ArrayList<String>();
		String player1_name;
		boolean race_endflag = false;


//==========Loading Screens=============

		CharSelectScreen char_select;
		MapSelectScreen map_select;

		
//===================Methods========================
	
	public boolean raceOver(){
		return race_endflag==true;
	}
	
	public void getMapHorizonData(){
		map_horizons.put("Choco Mountain","mountain");
		map_horizons.put("Mario Circuit","hilly");
		map_horizons.put("Mario Circuit2","cloud");
		map_horizons.put("Mario Circuit3","hilly");
		map_horizons.put("Ghost Valley","ghosts");
		map_horizons.put("Vanilla Lake","cloud hills");
		map_horizons.put("Rainbow Road","dark mountain");
	}
	
	public void setUpHorizon(){
		
		String name = map_horizons.get(level);
		String ext = ".PNG";
		bg_file = name + ext;
		bg_pic = new ImageIcon(bg_file);
		picture = new ImageIcon(bg_file).getImage();
		picture2  = new ImageIcon(bg_file).getImage();
		p_width = bg_pic.getIconWidth(); //dimensions of the picture itself 
		p_height  = bg_pic.getIconHeight();				
			
	
			
	}
	
	public void loadPlayerNames(){
		player_names.add("mario");
		player_names.add("KT");
		player_names.add("Luigi");
		
	}
	
	public void setMapBackgrounds(){
		
		green_background.add("Mario Circuit2");
		green_background.add("Mario Circuit");
		green_background.add("Mario Circuit3");
		brown_background.add("Choco Mountain");
		white_background.add("Vanilla Lake");
		black_background.add("Bowsers Castle");			
	}
	
	public void loadOilSpills(){
		String ext = ".txt";
		String name = level + " oilspills" + ext;
		
		try{
			Scanner infile = new Scanner(new File(name));
			while (infile.hasNextLine()){
			
				String line = infile.nextLine();

				
				String[] data = line.split(" ");
				int xcoord = Integer.parseInt(data[0]); //xcoord
				int ycoord = Integer.parseInt(data[1]); //y coord
				Rectangle temp = new Rectangle(xcoord,ycoord,16,16);
				oil_spills.add(temp);
			}			
		}
		catch (Exception OnlyTwoDaysLeft){
			System.out.println(OnlyTwoDaysLeft);
		}
		
	}
	
	
	public void loadBoosts(){
		String ext = ".txt";
		String name = level + " boosts"+ext;

		try{
			Scanner infile = new Scanner(new File(name));
			while (infile.hasNextLine()){
			
				String line = infile.nextLine();

				
				String[] data = line.split(" ");
				int xcoord = Integer.parseInt(data[0]); //xcoord
				int ycoord = Integer.parseInt(data[1]); //y coord
				Rectangle temp = new Rectangle(xcoord,ycoord,16,16);
				boosts.add(temp);
			}				
			
		}
		catch (Exception OnlyTwoDaysLeft){
			System.out.println(OnlyTwoDaysLeft);
		}
		
	}
	
	public void loadMapImage(){
		String ext = ".png";
		String name = level + ext;
		level_map = new ImageIcon(name).getImage();
	}

	
	
	public void createFrameMap(){
		int j = 0;
		int[] numbers = {7,6,5,4,3,2,1,0,21,20,19,18,17,16,15,14,13,12,11,10,9,8,7};
		for (double i = 0 ; i <=180;i+=(double)90/7){
			System.out.println(round(i)+" "+numbers[j]);
			frameMap.put(round(i),numbers[j]);
			reverse_frameMap.put(numbers[j],round(i));
			j++;

		
		
	}
		for (double i = 180; i<=361;i+=(double)90/4){
			System.out.println(round(i)+" "+numbers[j]);
			frameMap.put(round(i),numbers[j]);
			reverse_frameMap.put(numbers[j],round(i));
			j++;			
		}
		
	
	}
	

	public void loadItemPics(){
		
		
		
		try{
			Scanner infile = new Scanner(new File("Item List.txt"));
			while (infile.hasNextLine()){
				String line = infile.nextLine();
				System.out.println(line);
				ImageIcon temp2 = new ImageIcon(line + "_item.PNG");
				System.out.println(temp2.getIconHeight());
				System.out.println(temp2.getIconWidth());
				Image temp = new ImageIcon(line + "_item.PNG").getImage();
				item_pics.put(line,temp);
			}
			
		}
		
		catch(Exception Ihavesomuchlefttodo){
			System.out.println(Ihavesomuchlefttodo);
		}
		
		
		
	}
		
		
			
//getAudioClip(getCodeBase(),"laser.wav");	
	public void loadMusic(){
		try{
			Scanner infile = new Scanner(new File("music.txt"));
			while (infile.hasNextLine()){
				String line = infile.nextLine();
				songs.put(line,line+".mid");
				System.out.println(line);
			}
			
		}
		
		catch(Exception e){
			System.out.println(e);
		}
		
	}
	
	
	
	public void playBGMidi(String file){
		
		file = songs.get(file);
		
        File midiFile = new File(file);
        // Play once
        try {
            Sequencer bg_music = MidiSystem.getSequencer();
            bg_music.setSequence(MidiSystem.getSequence(midiFile));
            bg_music.open();
        //    bg_music.setLoopCount(999); //loop the bg music 
            bg_music.start();

            
            
        } catch(MidiUnavailableException mue) {
            System.out.println("Midi device unavailable!");
        } catch(InvalidMidiDataException imde) {
            System.out.println("Invalid Midi data!");
        } catch(IOException ioe) {
            System.out.println("I/O Error!");
        } 

    }  

		
	
	
	
	public void generateStrips(){ //generate strip descriptions...
		
		strip_lengths = new double[view_height-horizon]; //as many strips as scanlines/pixels on the screen
		strip_heights = new double[view_height-horizon];
		line_scales = new double[view_height-horizon];
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
			//one term is 0
			
		//	System.out.println(screen_y);
		//	System.out.println(len);
		//	System.out.println(height);
			
			strip_lengths[screen_y - horizon] = len; //related by indices
			strip_heights[screen_y - horizon] = height; 
			line_scales[screen_y - horizon] = line_scale;
					
			if (first_flag==true){
				max_width = strip_lengths[0];
				max_height = strip_heights[0];
				view_range = new BufferedImage(round(max_width),round(max_height),BufferedImage.TYPE_INT_ARGB);
				first_flag=false; 
			}
			
			
	    	
	    }
	    
	    min_height = strip_heights[view_height - horizon - 1]; 
	    min_width = strip_lengths[view_height - horizon - 1];
	    if (min_height>0){
	    	System.out.println("Error: Player will not be visible");
	    }
	    player_scanline = view_height + min_height; 		
		
	}

	public void loadCheckPoints(){
		
		try{
			String ext = ".txt";
			String name = level + " checkpoint"+ext;
			Scanner infile = new Scanner(new File(name));
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
	
	public void bananaCollisions(Kart player){
		Rectangle r = new Rectangle(round(player.xpos),round(player.ypos),(int)player.getWidth(),(int)player.getHeight()); //get the rectangle made
		for (int i=0;i<bananas.size();i++){
			Banana b = bananas.get(i);
			if (b.collide(r)){
				bananas.remove(b);
				player.setStunned();
				
			}
		
	}		
	}
	
	public void shellCheck(Kart player){ //checks for shell collisions
		Rectangle r = new Rectangle(round(player.xpos),round(player.ypos),(int)player.getWidth(),(int)player.getHeight()); //get the rectangle made
		for (int i=0;i<green_shells.size();i++){
			GreenShell shell = green_shells.get(i);
			if (shell.collide(r)==true){
				System.out.println(player.name + " is stunned");
				player.setStunned();
				green_shells.remove(i);
			}
		}
		
	}
	

	public void checkItemCollisions(Kart player){
		Rectangle r = new Rectangle(round(player.xpos),round(player.ypos),(int)player.getWidth(),(int)player.getHeight()); //get the rectangle made
		//by the player 
		for (ItemBox item:items){ //for each item box check if it collides w/ the player 
			if (item.collide(r)==true && player.hasItem()==false && item.isUsed()==false && player.item_timer<=0){
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
			String ext = ".txt";
			String name = level + " Item Box Locations"+ext;
			System.out.println(name);
			Scanner infile = new Scanner(new File(name));
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
	
	
		BufferedImage buffer = new BufferedImage(v_width,bg_height,BufferedImage.TYPE_INT_ARGB); //buffered image used to scale the iamge
		Graphics2D g2d = buffer.createGraphics();
		

	

		
		double yscale =  bg_height/p_height;
		
		g2d.scale(1,yscale); //scale appropriately 

		if (bx<p_width&& bx>0){ //going left - using 2 images
			g2d.drawImage(picture,bx-p_width,by,null);
		}
		
		if (bx>p_width){ //reset - going left 
			bx=0;
		}
		
		if (bx*-1+view_width>p_width){
			int newX = p_width+bx;
			g2d.drawImage(picture,newX,by,null);
		}
		
		
		if (bx*-1>=p_width){ // reset - going right
			bx=0;
		}
		
		

		
		
		g2d.drawImage(picture,bx,by,null);

		dbg.drawImage(buffer,screenx_offset,0,null); 

	}	
	
	public void loadMapData(){
		
		
		try{
			String ext = ".txt";
			String name = level + " Map Data"+ext;
			
			Scanner infile = new Scanner(new File(name));
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
			//make startflag for this 
    }	
	
	
	public void createPlayers(){ //create players 
		
		players = new Kart[player_no];
		player_names.remove(player1_name);
		
		try{
			String ext = ".txt";
			String name = level + " Start Positions" + ext;
			Scanner infile = new Scanner(new File(name));
		
		for (int i = 0; i <player_no;i++){
			
			
			if (i==0){
				System.out.println("here");
				System.out.println(player1_name);
				String[] line = infile.nextLine().split(" ");
				int xcoord = Integer.parseInt(line[0]);
				int ycoord = Integer.parseInt(line[1]);
				System.out.println(xcoord);
				System.out.println(ycoord);
				players[i] = new Kart(player1_name,false,xcoord,ycoord,"hmn 1");
			}
			else{
				String[] line = infile.nextLine().split(" ");
				int xcoord = Integer.parseInt(line[0]);
				int ycoord = Integer.parseInt(line[1]);
			
				players[i] = new Kart(player_names.get(i),true,xcoord,ycoord,null);
			}
			
		}
		
		System.out.println("finished");
		
		}
		
		
		catch (Exception e){
			System.out.println(e);
		}		

	//	players[0] = new Kart("Mario",false,82,452,"hmn 1");
	//	players[1] = new Kart("KT",true,918,577,null);
	//	players[2] = new Kart("Luigi",false,951,553,"hmn 2");
				
		
	}
	
	public void run(){
		
		long lastUpdate, sleepLen;
		lastUpdate = System.currentTimeMillis ();
		start_time = System.currentTimeMillis ();


		rankings = new String[player_no+1];
//=============Stuff you have to load==================

		loadPlayerNames();
		getMapHorizonData();
//===================Non - Map Specific Stuff========================

		setMapBackgrounds();
		loadMusic(); //load all the music
		loadItemPics();		
		createFrameMap();		
		

//==========Loading Screens================

		char_select = new CharSelectScreen();

		map_select = new MapSelectScreen();
		
		while (true){
			if (char_select.get_char()!=null && map_select.get_map()!=null){
				player1_name = char_select.get_char();
				level = map_select.get_map();
				break;
			}
		//	System.out.println("waiting...");
		}
			

			
//===================Map Specific Stuff========================
		
		lap_no = 1; //number of laps in the race 		
		loadMapImage();
		loadMapData(); //load the data for that map
		loadItems(); //make item box stuff
		loadCheckPoints(); //load checkpoint information 
		loadBoosts();
		loadOilSpills();
		setUpHorizon(); //set up horizon pic
		playBGMidi(level);
		
		minimap_g.scale(mini_xscale,mini_yscale);
		
		createPlayers(); //create players and their starting positions and things 
		

		System.out.println(players[0].getName());
		
		generateStrips();

		
		
		while (true){
			
			moveKarts();
			moveObjects();
			render(players[0]); //draw stuff to offscreen buffer ==> karts[0] means everything is relative to the [0]th kart
			paintScreen(); //paint the screen
			
			
	    	sleepLen = lastUpdate + period - System.currentTimeMillis ();
	    	sleepLen = Math.max (5, sleepLen);
	    	delay (sleepLen);
	    	lastUpdate = System.currentTimeMillis ();
			
			if (raceOver()){
				break;
			}
			
			}
			
			
			int final_pos = 99; 
				
			for (int i = 0; i <rankings.length;i++){
				if (rankings[i]!=null){
				
				if (rankings[i].equals(player1_name)){
					final_pos = i;
				}
				}
			}
			
			System.out.println("You came in "+final_pos);
			
			
		}

	
	
	public void boostCheck(Kart player){
		Rectangle r = new Rectangle(round(player.xpos),round(player.ypos),(int)player.getWidth(),(int)player.getHeight()); //get the rectangle made
		for (Rectangle boost: boosts){
			if (boost.intersects(r)){
				player.setItemTimer(20,25);
			}
		}
	}	
	
	public void oilSpillCheck(Kart player){
		Rectangle r = new Rectangle(round(player.xpos),round(player.ypos),(int)player.getWidth(),(int)player.getHeight()); //get the rectangle made
		for (Rectangle oil: oil_spills){
			if (oil.intersects(r) && player.immuneCheck()==false){
				player.setStunned();
			}
		}
	}	

	public void moveKarts(){
		for (Kart player:players){
			if (player!=null && player.isAI()==false){
				player.move();
				player.useItemCheck();
				checkItemCollisions(player);
				bananaCollisions(player);
				player.lapCheck();
				shellCheck(player); //===> Check for getting hit by a shell 
				boostCheck(player); //check boost
				oilSpillCheck(player); //check oil				
				
			}
			if (player!=null && player.isAI()==true){
				player.AImove(); //move according to AI rules
				player.lapCheck(); //check laps for update
				bananaCollisions(player); //check banana
				shellCheck(player);  //check shell
			}
			
		}
	}
	
	public void moveObjects(){
		moveGreenShells();
		moveRedShells();
	}
	

	public void moveRedShells(){
		
		for (int i=0;i<red_shells.size();i++){
			RedShell shell = red_shells.get(i);
			if (shell.isAvailable()){
				shell.move();
			}
			if (shell.isAvailable()==false){ //remove if lifespan is over 
				red_shells.remove(i);
			}
			
		}
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
	
	
	public void drawObjects(Kart focalPlayer,Graphics g){ //draw shells, bananas etc...
		drawGreenShells(focalPlayer,g);
		drawBananas(focalPlayer,g);
		drawRedShells(focalPlayer,g);
		//drawPipes(focalPlayer,g);
	}

	
	public void drawRedShells(Kart focalPlayer,Graphics g){
		for (RedShell r:red_shells){
			drawObject(focalPlayer,null,null,null,r,r.getX(),r.getY(),8,8,"red shell",false,g);
		}
	}
	
	public void drawBananas(Kart focalPlayer,Graphics g){
		for (Banana b:bananas){
			b.draw(map_g);
			drawObject(focalPlayer,null,null,b,null,b.getX(),b.getY(),b.getWidth(),b.getHeight(),"banana",false,g);
			//draw 3d
			//width, height, object type (kart, etc...),isFOcalPlayer(boolean),Graphics g
		}
	}
	
	public void drawGreenShells(Kart focalPlayer, Graphics g){
		for (GreenShell shell:green_shells){
			shell.draw(map_g);
			drawObject(focalPlayer,null,shell,null,null,shell.getX(),shell.getY(),shell.getWidth(),shell.getHeight(),"shell",false,g);
			//draw 3d
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
	

	
	public void drawPlayers3D(Kart player, Graphics g){
		for (int i = 0; i <players.length;i++){
			
			if (players[i]!=null){
			
			
			if (players[i].equals(player)){
				Kart temp = players[i];
				drawObject(player,temp,null,null,null,temp.getX(),temp.getY(),temp.getWidth(),temp.getHeight(),"kart",true,g);
			}
			if (players[i].equals(player)==false){
				Kart temp = players[i];
				drawObject(player,temp,null,null,null,temp.getX(),temp.getY(),temp.getWidth(),temp.getHeight(),"kart",false,g);
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
	   
	    dbg.setColor(Color.black);
	    dbg.fillRect(0,0,s_width,s_height);
		
		
		
		
		
		
		//update position of background
		// drawBackground(dbg);
		if (player.left_turnflag==true || player.right_turnflag==true){
			bx+=15;
		}

		
		moveBackground(dbg);



	    
		//draw original map and stuff...
	    map_g.drawImage(level_map,0,0,null);
	    
	    drawItems(); //see if the item boxes should be drawn on the map...
	    
		

	 
		
		renderFloor32d(dbg,player);
		drawPlayers3D(player,dbg); //draw 3d player s
		drawObjects(player,dbg);
		
		//YOU SHOULD UPDATE THE BOTTOM OF THE SCREEN HERE....AFTER YOU DRAW OBJECTS ==> or just 		
		//not draw them at all if they're behind you
		
	    //render floor ==> draw red lines 
		renderFloor2D(dbg,player);
		drawPlayers(player); //this won't be used in 3d rendering  ==> only for debugging	    
		//draw player 
	    map_g.setColor(Color.black);
	    map_g.fillRect(round(player.xpos)-4,round(player.ypos)-4,8,8);	 

		 
   
	    
	    //draw minimap pl0x	
		minimap_g.drawImage(map,0,0,null); //draw in order to scale...(minimap) 		
	    dbg.drawImage(mini_map,0,0,null); //draw map to screen - USED ONLY FOR DEBUGGING 
	    
	    
	    		
		drawHUD(dbg, player);
		
		
		

	}
		public void drawObject(Kart Focalplayer,Kart tempPlayer,GreenShell tempShell, Banana tempBanana,RedShell tempRedShell, double obj_x,double obj_y,double obj_width, double obj_height,String object_type,boolean isFocalPlayer,Graphics g){

	
		
		double rotation_angle = Math.toRadians(90-Focalplayer.getAngle()); //angle of rotatoin for the camera

		//there delta_y must be > -30 (last height scanline) (or else the object is behind the camera) 
		
		double cx = Focalplayer.getX();
		double cy = Focalplayer.getY();
		
		
		double delta_x = cx - obj_x; //position relative to the camera ==> translation 
		double delta_y = cy - obj_y;
		
		
		//System.out.println(delta_x+" "+delta_y);
		
		
		//rotation transformation....
		double space_x = delta_x*Math.cos(rotation_angle) + delta_y*Math.sin(rotation_angle);
		double space_y = delta_x*Math.sin(rotation_angle)*-1 + delta_y*Math.cos(rotation_angle);
		
		//space_x and space_y is the new position relative to the camera 
		
	//	System.out.println(space_x+" "+space_y);
		
		double screen_y = (cam_height*scale_y)/(space_y+y_offset) - horizon;
		double line_length = ((cam_height*scale_y)/(screen_y+horizon))/scale_x*view_width;
		
		double temp_xscale =  v_width/line_length;  //xscale of the line 
		
		double screen_x = v_width -  (space_x + line_length/2)*temp_xscale;
		
		
	//	System.out.println("Screen coordinates are: ");
	//	System.out.println(screen_y+" "+screen_x);
		
		
		int newX = round(screen_x);
		int newY = round(screen_y);
		
		int screeny = newY; //for thing sto be simpleer
		
		//height represents the HEIGHT of something from the player position ==> not the CAMERA position
		//therefore px, py = 0 
		//values based off something relative to the player? 
		

//===================Getting the Width and Height of an Object=====================================
		
		//I guessed at these values =P
		

		
		double conste = 1;
		
		if (screeny>=450 && screeny<500)
		{
			conste = 1; 
		}
		
		if (screeny>=400 && screeny<450)
		{
			conste = 1;
		}
				

		if (screeny>=350 && screeny<400)
		{
			conste = 1.3;
		}

		if (screeny>=300 && screeny<350)
		{
			conste = 1.4;
		}


		if (screeny>=250 && screeny<300)
		{
			conste = 1.6;
		}

		if (screeny>=200 && screeny<250)
		{
			conste =  2;
		}

		if (screeny>=150 && screeny<200)
		{
			conste = 2.4;
		}


		if (screeny>=100 && screeny<150)
		{
			conste = 3.5;
		}


		if (screeny>=50 && screeny<100)
		{
			conste = 4.5;
		}
		
		if (screeny>=0 && screeny<50){
			conste = 5.2;
		}
		

		


		double height_y = space_y - obj_height*conste; //moving down relative to the player (axis system reversed)
		
		
		double end_y = (cam_height*scale_y)/(height_y+y_offset) - horizon;
		
		double screen_height = end_y - screen_y;
		
		
		
	//	System.out.println("Height is "+screen_height);
		
		double width_x = (v_width - (space_x + line_length/2 + obj_width)*temp_xscale);
		double screen_width = screen_x - width_x;
		
	//	System.out.println("Width is "+screen_width);
		
		int new_width = round(screen_width);
		int new_height = round(screen_height);

//===================================================================================================

//===================change obj_x and obj_y when drawing the actual object onto the screen=====
		
		int newX2 = round(newX - screen_width/2);
		int newY2 = round(newY - screen_height/2 );




//================Boundaries Check and Drawing====================
		
	//	System.out.println(newX2+" "+newY2);
	//	System.out.println(boundCheck(newX2,newY2)+"");
			

		if (boundCheck(newX2,newY2)){
			
			
			BufferedImage sprite_img; 
			ImageIcon temp = null;
			
			if (object_type.equals("kart") && isFocalPlayer==true){
				if (tempPlayer.getLeftTurnFlag()==true){
					if (tempPlayer.isDrifting()){
						temp = tempPlayer.getFrame(17);
					}
					else{
						temp = tempPlayer.getFrame(21);
					}
					
				}
				if (tempPlayer.getRightTurnFlag()==true){
					
					
					if (tempPlayer.isDrifting()){
						temp = tempPlayer.getFrame(4);
					}
					else{
						temp = tempPlayer.getFrame(1);
					}
				
				}
				
				if (tempPlayer.getNoTurnFlag()==true){						
					temp = tempPlayer.getFrame(0);							
				}
				
				if (tempPlayer.isStunned()){
					temp = tempPlayer.getStunFrame();
				}
				
				
				sprite_img = toBufferedImage(temp,temp.getIconWidth(),temp.getIconHeight(),new_width,new_height); //get real dimensiosn	
				g.drawImage(sprite_img,newX2+screenx_offset,newY2+screeny_offset,null);
				
				
			
			
			}
			
			if (object_type.equals("kart") && isFocalPlayer==false){
				double dev_angle = tempPlayer.getAngle() - Focalplayer.getAngle();
				int index = getFrameNo(dev_angle);
				temp  =  tempPlayer.getFrame(index);
				
				if (tempPlayer.isStunned()){ //same for AI...
					temp = tempPlayer.getStunFrame();
				}				
				
				sprite_img = toBufferedImage(temp,temp.getIconWidth(),temp.getIconHeight(),new_width,new_height); //get real dimensiosn
				g.drawImage(sprite_img,newX2+screenx_offset,newY2+screeny_offset,null);
				
				//get picture
				//scale picture appropriately
				//draw picture 
				
			}
			
			if (object_type.equals("banana")){
				//get scanline...depending on screen y
				temp= tempBanana.getPic(newY2);
				sprite_img = toBufferedImage(temp,temp.getIconWidth(),temp.getIconHeight(),new_width,new_height);
				//get real dimensions ==> then draw 
				g.drawImage(sprite_img,newX2+screenx_offset,newY2+screeny_offset,null);
			}
						
			
			if (object_type.equals("shell")){
				//get scanline...depending on screen y
				temp = tempShell.getPic(newY2);
				sprite_img = toBufferedImage(temp,temp.getIconWidth(),temp.getIconHeight(),new_width,new_height);
				//get real dimensions ==> then draw 
				g.drawImage(sprite_img,newX2+screenx_offset,newY2+screeny_offset,null);
			}
			
/*			if (object_type.equals("pipe")){
			//	System.out.println("Pipe to be drawn...");
				temp = tempPipe.getPic(newY2);
				
				sprite_img = toBufferedImage(temp,temp.getIconWidth(),temp.getIconHeight(),new_width,new_height);
				//get real dimensions ==> then draw 
				g.drawImage(sprite_img,newX2+screenx_offset,newY2+screeny_offset,null);				
			} */
			
			if (object_type.equals("red shell")){
				temp = tempRedShell.getPic(newY2);
				sprite_img = toBufferedImage(temp,temp.getIconWidth(),temp.getIconHeight(),new_width,new_height);
				//get real dimensions ==> then draw 
				g.drawImage(sprite_img,newX2+screenx_offset,newY2+screeny_offset,null);				
				
			}
				
		}

		

		
	}
	
	public int getFrameNo(double dev_angle){
		
	//	System.out.println(dev_angle);
		dev_angle = dev_angle+90;
	//	System.out.println(dev_angle);
		
		dev_angle = (dev_angle + 360)%360; //winding function...yay
		
		if (dev_angle == 0 || dev_angle==360){
			return 7;
		}
		
		if (dev_angle == 90){
			return 0;
		}
		
		if (dev_angle==180){
			return 15; 
		}
		
		if (dev_angle==270){
			return 11;
		}
		if (dev_angle > 0 && dev_angle < 180){
			
			double temp_const = (double)90/7;
			int t_ans = round(dev_angle/temp_const);
			int ans = round(t_ans*temp_const);
			
			
						
			return frameMap.get(ans); 
		}
		
		if (dev_angle > 180 && dev_angle < 360){
			double temp_const = (double)90/4;
			int t_ans = round(dev_angle/temp_const);
			int ans = round(t_ans*temp_const);
			return frameMap.get(ans);			
			
		}
		return 0;
		
		
	}
	
	public BufferedImage toBufferedImage(ImageIcon src, int src_width, int src_height, int img_width, int img_height){ //returns correct size buffered image that you want...
		BufferedImage temp = new BufferedImage(img_width,img_height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D temp_g = temp.createGraphics();
		double temp_xscale = (double)img_width/src_width;
		double temp_yscale = (double)img_height/src_height;
		temp_g.scale(temp_xscale,temp_yscale);


		
		
		Image temp_i = src.getImage(); //get image here 
	//	temp_g.translate(-img_width,-img_height);
		temp_g.drawImage(temp_i,0,0,null);
		return temp; 
			
	}
	
		
	public boolean boundCheck(int x, int y){
		
		int upper_xbound = v_width + screenx_offset;
		int lower_xbound = screenx_offset;
		int upper_ybound = (int)player_scanline - 80;
		int lower_ybound = screeny_offset;
		
		return (x+screenx_offset > lower_xbound && x + screenx_offset < upper_xbound && y+screeny_offset<upper_ybound && y+screeny_offset > lower_ybound);
	} //unsure about y+screeny_offset>screeny_offset....do you want to see objects in the horizon?
	
	
	public void drawHUD(Graphics g, Kart player ){
		g.setColor(Color.pink);
		drawLapCount(g,player);
		drawTime(g);
		drawItem(g,player);
		drawRankings(g);
	}
	
	public void drawRankings(Graphics g){
		int ystart = 550; 
		String temp; 
		for (int i = 1 ; i <rankings.length; i++){
			if (rankings[i]!=null){
				temp = (i)+".)"+rankings[i];
			}
			else{
				temp = (i)+".)"+"?????";	
			}
			
			g.drawString(temp,100,ystart);
			ystart+=50;			

		}
	}
	
	
	public void drawItem(Graphics g, Kart player){ //draws the item that the player has 
		String p_item = player.getItem();
	//	System.out.println(p_item);
		if (p_item.equals("")){
			p_item = "empty";
		}
	//	System.out.println(p_item);
		g.drawImage(item_pics.get(p_item),400,600,null);
	//	g.setColor(Color.blue);
	//	g.fillRect(400,600,200,200);	
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
	
	
	public void renderFloor2D(Graphics dbg,Kart player){

		double r_angle = Math.toRadians(90-player.angle);



		px = player.xpos; //shallow (like laura) copies 
		py = player.ypos; 
		

		cos_precalc = Math.cos(Math.toRadians(player.angle+180));
		sin_precalc = Math.sin(Math.toRadians(player.angle+180));

	    //why +180? O_o...cause axis system is reversed?
	    
	    
	    map_g.setColor(Color.red);
	    
	    for (int i = 0; i < strip_lengths.length;i++){
	    	
	    //	System.out.println(i);
	    	
	    	double length = strip_lengths[i];
	    	double height = strip_heights[i];
	    	
	    	double mid_x = px - cos_precalc*height; //reverse
	    	double mid_y = py + sin_precalc*height;
	    	
	    	double line_dx = sin_precalc;
	    	double line_dy  = cos_precalc;
	    	
	    	int x1 = round(mid_x - line_dx*length/2);
	    	int y1 = round(mid_y - line_dy*length/2);
	    	
	    	int x2 = round(mid_x + line_dx*length/2);
	    	int y2 = round(mid_y + line_dy*length/2);
	    	
	    	
	    //	System.out.println(x1+ " "+y1);
	    //	System.out.println(x2+ " "+y2);
	    		
	    	map_g.drawLine(x1,y1,x2,y2); //drawing lines onto the actual map
	    	
	    	
	    }	 
		
		
	}	
	
/*	public void renderFloor(Graphics dbg,Kart player){ //player is the perspective that you see stuff from
	
	

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
	    

		
	} */
	
	public void renderFloor32d(Graphics dbg, Kart player){

		double r_angle = Math.toRadians(90-player.angle);



		px = player.xpos; //shallow (like laura) copies 
		py = player.ypos; 
		

		cos_precalc = Math.cos(Math.toRadians(player.angle+180));
		sin_precalc = Math.sin(Math.toRadians(player.angle+180));

	    //why +180? O_o...cause axis system is reversed?
	    
	    
	    map_g.setColor(Color.red);
	    
	    int drawstart = horizon; 
	    
	    for (int i = 0; i < strip_lengths.length;i++){
	    	
	    //	System.out.println(i);
	    	
	    	double length = strip_lengths[i];
	    	double height = strip_heights[i];
	    	

	    	
	    	double mid_x = px - cos_precalc*height; //reverse ==> kind of like multiplying by -1
	    	double mid_y = py + sin_precalc*height;
	    	
	    	double line_dx = sin_precalc;
	    	double line_dy  = cos_precalc;
	    	
		//	System.out.println(line_dx);
	    	
	    	double x1 = mid_x - line_dx*length/2*-1; // *-1 is important!
	    	double y1 = mid_y - line_dy*length/2*-1;	    	
	    	
	    	double xinc = length/v_width*line_dx*-1; //you need to multiply by -1? is this cause of 180...
	    	double yinc = length/v_width*line_dy*-1; //0
	    	
	    
	    
	    for (int j = 0; j<v_width;j++){
	    	
	    		int pixelvalue = 0; //it isn't really zero...
	    		
	    		int newX = round(x1);
	    		int newY = round(y1);
	    	
	    		if (newX<0 || newY<0 || newX>=1024 || newY>=1024){
	    			
	    			if (black_background.contains(level)){
	    				pixelvalue = -16777216;
	    			}
	    			if (white_background.contains(level)){
	    				pixelvalue = -1;
	    			}	    			
	    			
	    			if (brown_background.contains(level)){
	    				pixelvalue = -8355776;	
	    			}	    			

	    			if (green_background.contains(level)){
	    				pixelvalue = -16744448;
	    			}	    			

	    				
	// black = -16777216
	//white = -1; 
	// green = -16744448
	//brown = -8355776	    				
	    				
	    		}
	    		
	    		else{
	    			pixelvalue = map.getRGB(newX,newY);
	    		}
	    		
	    		screen.setRGB(j,drawstart,pixelvalue);		    	
	    		
	    		x1+=xinc;
	    		y1+=yinc;
	    }
	    
	    drawstart++;
	    
	    }
	    
	    dbg.drawImage(screen,screenx_offset,screeny_offset,null);	 

	
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



	public void updateRankings(Kart player){
		String name = player.getName();

		for (int i = 1; i < rankings.length; i ++){

			if (rankings[i]==null){
				rankings[i] = name;
				if (player.getName().equals(player1_name)){
					System.out.println("race is over");
					race_endflag = true; //race is now over
				}
				//System.out.println("Player called "+ name + " is rank " + i );
				break; 
			}
		}
		
	}


class Kart implements KeyListener{
	
	private double map_start_x; //used incase of reset...
	private double map_start_y;
	
	double xpos;
	double ypos;
	double angle; //angle that this kart is facing

	
	//change this values if you want things to be bigger or not...O_o
	
	private double width; //used for collision checking and possibly for mode 7 after...?
	private double height; 
	
//=====================Constant Stat Values===============
	
	
	private double star1_accel = 0.02; //acceleration 
	private double star2_accel = 0.03;
	private double star3_accel = 0.04;
	private double star1_speed = 7; //max velocity
	private double star2_speed = 10 ;
	private double star3_speed = 13;
	private double star1_offroad = 3; //max offroad velocity 
	private double star2_offroad = 4;
	private double star3_offroad = 5;
	private double star1_offroad_accel = 0.01; //offroad acceleration
	private double star2_offroad_accel = 0.02;
	private double star3_offroad_accel = 0.03; 
	private double star1_handling = 5; //didn't use this yet  - handling variable 
	private double star2_handling = 7;
	private double star3_handling = 9; //O_o




//==================== Being Stunned and Stun variables ==========

	private boolean stun_flag; 
 	private int stun_counter; //count down 
 	private int immune_counter;

	
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
	double fpoint_dist = 40;
	
	String last_turn = "left"; //depends on level y'know, laura luo is...a meanie =(
	


//============Stuff for Human Players=======================

	private String control_config; 
	private boolean left_turnflag;
	private boolean right_turnflag;
	private boolean no_turnflag; 
	private boolean drift_flag;
	private double item_velocity;
	double handling; //how sharply you can turn your kart 
	
//==============for when you're on the road - STATS ===============	
	double acceleration;
	double velocity;
	
	double accel_increment;  //how quickly you speed up 
	double brake_increment; //how fast your brakes slow you down
	
	double max_velocity; //top speed
	double min_velocity; //min reverse speed...who cares about this though *yawns*
	//maybe I'll just make this the same for every character 

	String star_maxspeed;
	String star_offroadspeed;
	String star_acceleration;
	String star_handling;
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
		
//=====================================================Frames=============	
	
	
	String name; //name of character - used to determine stats later on...
	ImageIcon[] frames;
	int currentframe;
	
	
	public void loadFrames(String filename){ //load frames for that person...
		String ext = ".PNG";
		for (int i = 0 ; i<frames.length;i++){
			String temp_name = filename + i + ext;
		//	System.out.println(temp_name);
			ImageIcon temp = new ImageIcon(temp_name);
	//		System.out.println(temp.getIconHeight());
			frames[i] = new ImageIcon(temp_name);//.getImage();
		}
	}
	
	public double getStatValue(String stat, String stars){
		if (stars.equals("*")){
			if (stat.equals("accel")){
				return star1_accel; 
			}
			if (stat.equals("offroad accel")){
				return star1_offroad_accel; 
			}
			if (stat.equals("maxspeed")){
				return star1_speed; 
			}
			if (stat.equals("offroad maxspeed")){
				return star1_offroad; 
			}
			if (stat.equals("handling")){
				return star1_handling; 
			}			
		}
		if (stars.equals("**")){
			if (stat.equals("accel")){
				return star2_accel; 
			}
			if (stat.equals("offroad accel")){
				return star2_offroad_accel; 
			}
			if (stat.equals("maxspeed")){
				return star2_speed; 
			}
			if (stat.equals("offroad maxspeed")){
				return star2_offroad; 
			}
			if (stat.equals("handling")){
				return star2_handling; 
			}						
		}
		if (stars.equals("***")){
			if (stat.equals("accel")){
				return star3_accel; 
			}
			if (stat.equals("offroad accel")){
				return star3_offroad_accel; 
			}
			if (stat.equals("maxspeed")){
				return star3_speed; 
			}
			if (stat.equals("offroad maxspeed")){
				return star3_offroad; 
			}
			if (stat.equals("handling")){
				return star3_handling; 
			}						
		}
		
		return -1; //Java needs this apparently...*sighs*
	
	}
		
		

	
	
	public Kart(String name,boolean AIflag,double startx, double starty,String control_type){
		
		AI = AIflag;
		this.name = name;
		xpos = startx;//960;// 1441;
		ypos = starty;//683;//1173;
		
		map_start_x = startx;
		map_start_y = starty; 
		
		angle = 90;
		
		height = 16; 
		width = 16 ;
		
		if (AIflag==false){
			control_config = control_type; //what buttons are used
		}

//======================Loading Images======================


		String temp_name = name.toLowerCase();
		if (temp_name.equals("kt")){
			temp_name = "KT";
		}
		frames = new ImageIcon[22];
		loadFrames(temp_name);
		
		
		try{
			Scanner infile = new Scanner(new File("Character Stats.txt"));
			while (infile.hasNextLine()){
				String line = infile.nextLine();
				String[] temp; 
				if (line.equals(name)){
					System.out.println("okay name is " + name);
					star_maxspeed = infile.nextLine().split(" ")[1];
					star_offroadspeed = infile.nextLine().split(" ")[1];
					star_acceleration = infile.nextLine().split(" ")[1];
					star_handling = infile.nextLine().split(" ")[1];
					item_const = infile.nextLine();
					

				}
			}
		}
		
		catch (Exception lauraLuoisMean){
			System.out.println(lauraLuoisMean);
		}
		
		
//=========Character specific stats=================

		max_velocity = getStatValue("maxspeed",star_maxspeed);
		accel_increment = getStatValue("accel",star_acceleration);
		offroad_accel_increment = getStatValue("offroad accel",star_offroadspeed);
		offroad_max_velocity = getStatValue("offroad maxspeed",star_offroadspeed); //max velocity offroad 
		handling = getStatValue("handling",star_handling);
					System.out.println(handling);
					System.out.println(max_velocity);
					System.out.println(accel_increment);
					System.out.println(offroad_accel_increment);
					System.out.println(offroad_max_velocity);
		
		if (AI==true){
	/*		if (ai_dificultly==1){
				
			}
			if (ai_difficultly==2){
				
			}
			if (ai_difficutly==3){
				
			} */
		}
		
//==============constant stats (maybe I should change handling..===========		 	
		velocity = 0;
		acceleration = 0;			
		min_velocity = -5;
		brake_increment = 0.01;
		//handling = 5;
//=======================================================
		
		stun_flag = false; 
		stun_counter = 0; 
		
		requestFocus(); //for the key stuff ==> infact, I don't even know if I need this O_o
		addKeyListener(this);		
			
			
//		finLap = false;
		halfWay = false; 
			
		hasItem = false;
		item = "";
		item_timer = 0; 	
			

					
	}
	
	
	public int getLap(){ //return what lap you're on 
		return lapCount;
	}
	
	public void lapCheck(){
		
		Rectangle r = new Rectangle(round(xpos),round(ypos),(int)width,(int)height);
		
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
			velocity = item_velocity; //velocity of the item that currently increases your speed
			item_timer--;
		}		
	}
	
	
	public void move(){

		
		if (this.offCourseCheck()==true){ //if you're off the 
			xpos = map_start_x;
			ypos = map_start_y;
			this.angle = 90; //reset
			this.setStunned();
			halfWay=false; //you have to redo the lap
			item_velocity = 0;
			item_timer = 0;
			
		}
		
		if (stun_flag==true){
			this.stunAnimation();
		}
		
		else{
			updateAngle();
			updateSpeed();		
			mushroomCheck();
			moveAlong();	
		}
		
				
		
	}
	
	
	public boolean isDrifting(){
		return drift_flag==true;
	}
	
	public void updateAngle(){ //updates the speed and angle 
	
	
	
	if (control_config.equals("hmn 1")){
		
		int drift_const = 0; 
			
		if (keys[69]==true){
			drift_const = 10;
			drift_flag = true;
		}
		
		
		if (keys[69]==false){
			drift_flag = false; 
		}
		
	
		if (keys[37]==true){ //left turn 
			if (this.iceCheck()==true){ //slippery effect
				angle+=(handling+4+drift_const);
			//	System.out.println("on ice");
			}
			else{
				angle+=(handling+drift_const);
			}
			angle = (angle+360)%360;
			left_turnflag = true; 
			right_turnflag = false;
			no_turnflag = false;
			//currentframe = 2;
			
		}
		if (keys[39]==true){ //right turn
			if (this.iceCheck()==true){
				//System.out.println("on ice");
				angle-=(handling+4+drift_const);
			}
			else{
				angle-=(handling+drift_const);
			}
			
			angle = (angle+360)%360;
			left_turnflag = false; 
			right_turnflag = true;
			no_turnflag = false;
			//currentframe = 1;
	}
		if (keys[37]==false && keys[39]==false){ //if you aren't turning, your frame is the normal one
			left_turnflag = false; 
			right_turnflag = false;
			no_turnflag = true;
			//currentframe = 0;
		}
		
	}		
	
	}
	
	public void moveAlong(){
		boolean stopflag = false;
		for (int v=0;v<velocity;v++){ //loop as many "velocities" as he has...
			if (velocity>0){ //moving forward
				xpos+=1*Math.cos(Math.toRadians(angle));
				ypos+=1*Math.sin(Math.toRadians(angle))*-1;					
			}
			if (velocity<0){ //moving backward...why doesn't this work O_o
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
	
	
	public boolean iceCheck(){ //ice
		
		int newx = round(xpos);
		int newy = round(ypos);		

		int gridx = newx/8; //divide by grid ratio
		int gridy = newy/8;
		
		return terrain_grid[gridy][gridx]=='i';		
	}


	public boolean snowCheck(){ //snow
		
		int newx = round(xpos);
		int newy = round(ypos);		

		int gridx = newx/8; //divide by grid ratio
		int gridy = newy/8;
		
		return terrain_grid[gridy][gridx]=='s';		
	}


	public boolean offCourseCheck(){ //snow
		
		int newx = round(xpos);
		int newy = round(ypos);		

		int gridx = newx/8; //divide by grid ratio
		int gridy = newy/8;
		
		return (terrain_grid[gridy][gridx]=='x' ||  terrain_grid[gridy][gridx]=='w');		
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
		
		if (gridx<0 || gridy<0 || gridx>128 || gridy>128){
			return true;
		}
		
		return (terrain_grid[gridy][gridx]=='d' || terrain_grid[gridy][gridx]=='s') ;		
	}


	public boolean pointOffCourseCheck(double px,double py){
		int newx = (int)px;
		int newy = (int)py;
		
		int gridx = newx/8; //divide by grid ratio
		int gridy = newy/8;
		
		if (gridx<0 || gridy<0 || gridx>128 || gridy>128){
			return true;
		}
		
		return ( terrain_grid[gridy][gridx]=='x' || terrain_grid[gridy][gridx]=='w' ) ;		
	}	
	
	public void updateSpeed(){
		
		//System.out.println(offRoadCheck());
//============================Key Board Input=========================================================
		
		if (control_config.equals("hmn 1")){
			
	
		if (keys[38]==true && offRoadCheck()==false){ //up arrow 
			acceleration+=accel_increment; //0.03
		}

		if (keys[38]==true && offRoadCheck()==true){ //up arrow ==> on dirt 
			acceleration+=offroad_accel_increment; //0.03
		}
		if (keys[38]==true && snowCheck()==true){ //up arrow ==> on snow 
			acceleration+=offroad_accel_increment*.75; //0.03
		}

		
		if (keys[40]==true){ //down arrow ==> adjust for items?
			acceleration-=brake_increment; //0.01
		}
		
		if (keys[38]==false && keys[40]==false){ //not moving - no arrow 
			acceleration-=.2; //constant 
		}
		
		

		
		if (velocity<=0 && keys[38]==false && keys[40]==false){ //if you aren't moving at ALL
			acceleration = 0;
			velocity = 0;
		}
		
		}

//=============================End Keyboard Input ========================================		
		
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
		
		
		if (velocity>offroad_max_velocity && snowCheck()==true){ // on snow ==> .75 slower
			if (velocity+2>offroad_max_velocity*.75){
				velocity-=1;
			}
			else{
				velocity = offroad_max_velocity*.75;
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
	
	public String getItem(){
		return this.item;
	}
	
	public void setItem(){ //set to true
		this.hasItem = true;
		item = item_const;
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
		
		
		if (control_config.equals("hmn 1")){
		
			
		if (keys[70]==true && hasItem){

		    	
    	if (item.equals("Red Shell")){
    		
    	}
     	if (item.equals("greenshell")){
     		System.out.println("Green Shell Used");
     		item = ""; //don't need to do this
     		hasItem = false;
     		GreenShell temp; 
     		if (keys[40]==true){
     			temp = new GreenShell(xpos+height*2*Math.cos(Math.toRadians(angle+180)),ypos+height*2*Math.sin(Math.toRadians(angle+180))*-1,angle+180,terrain_grid); //make it 
     		}
     		else{
     			temp = new GreenShell(xpos+height*2*Math.cos(Math.toRadians(angle)),ypos+height*2*Math.sin(Math.toRadians(angle))*-1,angle,terrain_grid); //make it 
     		}     		
    		
    		green_shells.add(temp); //add it 
    	}
    	
     	if (item.equals("mushroom")){
     	//	System.out.println("Mushroom Used");
     		item = ""; //don't need to do this
     		hasItem = false;
     		setItemTimer(20,20); // time, velocity 
     	//	item_timer = 20; //some timer...indicates how long your velocity increases for     		
    	}
    	
    	if (item.equals("redshell")){
    		System.out.println("red shell used");
    		item = "";
    		hasItem  = false;
    		RedShell temp = new RedShell(xpos+height*Math.cos(Math.toRadians(angle)),ypos+height*Math.sin(Math.toRadians(angle))*-1,angle,terrain_grid,this); //make it 
    		red_shells.add(temp);
    		
    	}
    	
    	    	
     	if (item.equals("banana")){
     		System.out.println("Banana Used");
    		Banana temp = new Banana(xpos+(height+5)*Math.cos(Math.toRadians(angle)),ypos+(height+5)*Math.sin(Math.toRadians(angle)),20,20);
    		bananas.add(temp);
     		item = ""; //don't need to do this
     		hasItem = false;       		
    		//don't multiply by -1 for sin b/c you want it to be behind you 
    	}
    	
		}
		}    	
    	   	   	
    }
    
    
    public void setItemTimer(int no,int new_velocity){
    	item_timer+=no;
    	item_velocity = new_velocity; 
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
    	
    	if (isStunned()){
    		this.stunAnimation();
    		return;
    	}
 
    	
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
		
		boolean forward = pointDirtCheck(x_forwardpoint,y_forwardpoint);
		boolean left = pointDirtCheck(x_leftpoint,y_leftpoint);
		boolean right = pointDirtCheck(x_rightpoint,y_rightpoint);
		
		boolean off_forward =pointOffCourseCheck(x_forwardpoint,y_forwardpoint);
		boolean off_left = pointDirtCheck(x_leftpoint,y_leftpoint);
		boolean off_right = pointDirtCheck(x_rightpoint,y_rightpoint);
		
    	if (off_forward){
    		if (last_turn.equals("left")){
    			angle-=20;
    			velocity*=.9;
    		}
    		if (last_turn.equals("right")){
    			angle+=20;
    			velocity*=.9;
    		}
    		return; 
    	}

    	
    	if (off_left ){
    	//	System.out.println("left on dirt");
    		angle-=20;
    		velocity*=.9;
    		last_turn = "left";
    		return; 
    	}
     	if (off_right){
     	//	System.out.println("right on dirt");
    		angle+=20;
    		velocity*=.9;
    		last_turn = "right";
    		return;     		
    	}		
	
		
		

		
    	if (forward){
    		if (last_turn.equals("left")){
    			angle-=20;
    			velocity*=.9;
    		}
    		if (last_turn.equals("right")){
    			angle+=20;
    			velocity*=.9;
    		}
    		return; 
    	}

    	
    	if (left ){
    	//	System.out.println("left on dirt");
    		angle-=20;
    		velocity*=.9;
    		last_turn = "left";
    		return; 
    	}
     	if (right){
     	//	System.out.println("right on dirt");
    		angle+=20;
    		velocity*=.9;
    		last_turn = "right";
    		return;     		
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
    
    public boolean immuneCheck(){ //immune to obstacles and stuff
    	
    	if (immune_counter>0){
    		immune_counter--;
    		return true;
    	}
    	if (immune_counter<=0){
    		return false; 
    	}
    	return false; //doesn't get here
    	
    }

	public void setStunned(){
		velocity = 0; //set these to zero as well 
		acceleration = 0;
		stun_flag = true;
		stun_counter = 20; //set counter
		immune_counter = 40; 
	}
	
	public void unStunned(){
		stun_flag = false; 
	}
	
	public boolean isStunned(){
		return this.stun_flag == true;
	}
	
	public void stunAnimation(){
		if (stun_counter<=0){
			this.unStunned();
		}
		else{
			stun_counter--; //subtract time 
			currentframe++;
			if (currentframe>frames.length-1){
				currentframe = 0;
			}
			
			if (AI==false){
				this.angle = reverse_frameMap.get(currentframe); //get new angle...
			}
			
			System.out.println("Player is stunned...waiting...");
		}
	}
	
	public double getHeight(){
		return this.height;
	}
	
	public double getWidth(){
		return this.width; 
	}
	
	public ImageIcon getFrame(int index){
		return frames[index];
	}
	
	public boolean getLeftTurnFlag(){
		return left_turnflag; 
	}
	
	public boolean getRightTurnFlag(){
		return right_turnflag; 
	}
	
	public boolean getNoTurnFlag(){
		return no_turnflag; 
	}
	
	public ImageIcon getStunFrame(){
		return frames[currentframe]; 
	}
	


    	
	
	
	
}



}


