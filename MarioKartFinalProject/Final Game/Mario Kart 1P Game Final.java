
//=======================MOOSEY-O KART FINAL PROJECT====================================

/*

About the Game:

This game uses a rendering technique known as Mode 7. Mode 7 was first used on the Super Nintendo console in order to achieve
a perspective effect. It invovles getting each scanline in front of the player and scaling them in such a way that scanlines
further away look further away.

Some tutorials I used to help me with Mode 7 were...


http://pixwiki.bafsoft.com/mags/5/articles/circle/sincos.htm
http://www.coranac.com/tonc/text/mode7.htm
http://www.student.kuleuven.be/~m0216922/CG/raycasting2.html


*/


/*

	Introduction: Welcome to Moosey-O-Kart! There are 5 characters for you to choose from, each with different stats. There are
	7 maps to race on as well. Choose your favourite character and map and start racing! Also, there is a two player mode, if you're
	the kind of person who craves competition

	Controls:

	up - up arrow
	down - down arrow
	left - left arrow
	right - right arrow
	drift (sharp turn ) - e
	use item - f



*/



//Import require packages...(some are redundant)

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

//==========Game Setup - copied from Nettrek==================

class MainGame extends JFrame{

	private static int fps = 600;
	private int period;

	public MainGame(int period)throws IOException{
		super("Welcome to Moosey-O Kart!");
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




//==========================Dimensions of Screen  =======================

	int view = 800;
	private int s_width = 1000;//width and height of screen
	private int s_height = 800;
	private int period;
	private int map_width = 1024; //width and height of map used
	private int	map_height = 1024;

	private int minimap_width = 250; //width and height of minimap
	private int minimap_height = 250;

	private int view_height = 400; //width and height of 2.5d portion
	private int view_width = 400;

	private int v_height = 400; //width and height of 2.5d portion
	private int v_width = 400;



	int screenx_offset = 500;//x offset of the 2.5d portion
	int screeny_offset = 0;//y offset of the 2.5d portion

//======================More Graphics Stuff  ====================


	BufferedImage map = new BufferedImage(map_width,map_height,BufferedImage.TYPE_INT_ARGB); //hollds the map picture
	Graphics2D map_g = map.createGraphics(); //graphics context for the map picture

	BufferedImage screen = new BufferedImage(view_width,view_height,BufferedImage.TYPE_INT_ARGB); //holds a copy of the screen
	//we need this to be  a bufferedimage so we can setRGB


	BufferedImage mini_map = new BufferedImage(minimap_width,minimap_height,BufferedImage.TYPE_INT_ARGB); //minimap
	Graphics2D minimap_g = mini_map.createGraphics(); //graphics context of minimap

	double mini_xscale = (double)minimap_width/map_width; //xscale and yscale factors for real map to minimap
	double mini_yscale = (double)minimap_height/map_height;

	BufferedImage view_range; //everything infront of the player





//======================Mode 7 Specific Stuff====================================

	double[] strip_lengths; //length of each strip
	double[] strip_heights;  //height of each strip
	double[] line_scales; //factor by which each strip should be scaled ==> I don't think I ended up using this


	double y_offset = 130; //controls how close the camera is to the player
    double cam_height  = 60;
    int horizon = 20; //pixels line 0 is below the horizon

    double scale_x=800; //scaling factors
    double scale_y=800;

    double line_dist; //dist to scanline
    double line_scale; //horizontal scaling of this particular scanline

    double line_dx; //increments that identify how we move along a line
    double line_dy;

    double map_x; // x, y position on the bitmap
    double map_y;



	double px;
	double py;

	double cos_precalc; //precalculations for sine and cos of a given angle...
	double sin_precalc;

	double max_width; //max width of strip
	double max_height; //max height of strip

	double min_height; //min height and min width of strips
	double min_width;

	double player_scanline; //scanline that the player is ALWAYS centered on
	//therefore, no objects/sprites can be drawn before this point




//====================================Background stuff===============================

	private HashMap<String,String> map_horizons = new HashMap<String,String>(); //maps map name onto horizon picture

	private String bg_file; //
	private ImageIcon bg_pic; //
	private Image picture; //
	private Image picture2; //
	private int p_width; //
	private double p_height; //

	private int bx=0; //for bakcground
	private int by=0;
	private int bg_height = horizon+screeny_offset; //the height that you want the background to be


//==================In Game stuff==============================================

	private ItemBox[] items; //holds where the items are
	private Kart[] players; //represents all the players in the game
	char[][] terrain_grid = new char[128][128]; //grid for the terrain
	char[][] ai_terrain_grid = new char[128][128]; //grid for the ai

	private ArrayList<GreenShell> green_shells = new ArrayList<GreenShell>(); //holds all the green/red/yellow shells
	private ArrayList<RedShell> red_shells = new ArrayList<RedShell>(); //red shells - not used
	private ArrayList<Banana> bananas = new ArrayList<Banana>(); //bananas...
	private ArrayList<Pipe> pipes = new ArrayList<Pipe>(); //pipes - not used

	private CheckPoint half; //half lap checkpoint
	private CheckPoint finish_line; //finish line checkpoint

	private int lap_no; //how many laps there are in this race
	private String[] rankings; //holds final rank of the hmn characters

	private HashMap<Integer,Integer> frameMap = new HashMap<Integer,Integer>(); //a map which tells what frame
	//correponds to what angle the character is facing
	private HashMap<Integer,Integer> reverse_frameMap = new HashMap<Integer,Integer>();

	private ArrayList<Rectangle> boosts = new ArrayList<Rectangle>(); //holds oil spills
	private ArrayList<Rectangle> oil_spills = new ArrayList<Rectangle>(); //hold boosts

	private HashMap<String,String> level_first_turn = new HashMap<String,String>(); //the first turn of the level => used for AI
	private HashMap<String,Integer> ai_handling_map = new HashMap<String,Integer>(); //ai stats for a specific map
	//indicates if you finish a lap or not


//==========================HUD Stuff=============================

	private Font gameFont = new Font("Mario Kart Font",20,20);	 //font
	private HashMap<String,Image> item_pics = new HashMap<String,Image>(); //item pictures




//====================Map Pictures===================

	private HashMap<String,ImageIcon> maps = new HashMap<String,ImageIcon>(); //maps name of map, onto its image


	private Image level_map; //variable which holds the map of the level



//=========================Time=========================

		long start_time;
		long millisecond=0; //milli
		long second = 0; //seconds
		long min = 0; //mins
		boolean first_timeflag = true; //flag which indicates the stat time

//=================Music==========================

		Sequencer title_music;
		Sequencer bg_music;

		//get all music files

		String countdown_music = "countdown.wav";
		String racefanfare_music = "race fanfare.wav";
		String final_lap_music = "final lap warning.wav";
		String bad_ending = "bad ending.wav";
		String good_ending = "good ending.wav";
		String shell_shoot = "laser.wav";
		String get_item =  "Coin.wav";
		String get_hurt = "Power Down.wav";
		String use_mushroom = "Power Up.wav";
		String star_sound = "Invincible.wav";


		private HashMap<String,String> songs = new HashMap<String,String>();


//==================Level Things=========================

		String level; //name of level
		//there hold which level has what colored background
		ArrayList<String> green_background = new ArrayList<String>();
		ArrayList<String> white_background = new ArrayList<String>();
		ArrayList<String> black_background = new ArrayList<String>();

		ArrayList<String> brown_background = new ArrayList<String>();


//=================Options==========================

		int ai_difficultly = 1; //not used

		int player_no = 5;
		ArrayList<String> player_names = new ArrayList<String>(); //name of all the players
		String player1_name; //player name
		boolean race_endflag = false; //flag which indicates if the race is over or not


//============Pre Race Screen==============


		//latiku pictures for start of race

		Image lakitu1 = new ImageIcon("start0.PNG").getImage();
		Image lakitu2 = new ImageIcon("start1.PNG").getImage();
		Image lakitu3 = new ImageIcon("start2.PNG").getImage();

//==============End Screens=============

		//pictures at the end

		Image end1 = new ImageIcon("end screen1.PNG").getImage();
		Image end2 = new ImageIcon("end screen2.PNG").getImage();
		Image end3 = new ImageIcon("end screen6.PNG").getImage();
		Image end4 = new ImageIcon("end screen4.PNG").getImage();
		Image end5 = new ImageIcon("end screen5.PNG").getImage();

//==========Loading Screens=============

		CharSelectScreen char_select;
		MapSelectScreen map_select;




//===================Methods========================


	public void createAIHandlingMap(){ //creates map which contains the values for the ai handling
	//for each level
		ai_handling_map.put("Mario Circuit",20);
		ai_handling_map.put("Mario Circuit2",20);
		ai_handling_map.put("Mario Circuit3",25);
		ai_handling_map.put("Vanilla Lake",30);
		ai_handling_map.put("Choco Mountain",30);
		ai_handling_map.put("Rainbow Road",30);
		ai_handling_map.put("Ghost Valley",30);
	}

	public void createTurnMap(){ //create map which contains the first turn of each level
	//used for ai

		level_first_turn.put("Mario Circuit","left");
		level_first_turn.put("Mario Circuit2","right");
		level_first_turn.put("Mario Circuit3","	left");
		level_first_turn.put("Vanilla Lake","right");
		level_first_turn.put("Choco Mountain","left");
		level_first_turn.put("Rainbow Road","right");
		level_first_turn.put("Ghost Valley","right");

	}

	public boolean raceOver(){ //check if race is over
		return race_endflag==true;
	}

	public void getMapHorizonData(){ //maps what horizon picture goes with each levvel
		map_horizons.put("Choco Mountain","mountain");
		map_horizons.put("Mario Circuit","hilly");
		map_horizons.put("Mario Circuit2","cloud");
		map_horizons.put("Mario Circuit3","hilly");
		map_horizons.put("Ghost Valley","ghosts");
		map_horizons.put("Vanilla Lake","cloud hills");
		map_horizons.put("Rainbow Road","dark mountain");
	}

	public void setUpHorizon(){ //get the horizon picture for each level and initilize variables

		String name = map_horizons.get(level);
		String ext = ".PNG";
		bg_file = name + ext;
		bg_pic = new ImageIcon(bg_file);
		picture = new ImageIcon(bg_file).getImage();
		picture2  = new ImageIcon(bg_file).getImage();
		p_width = bg_pic.getIconWidth(); //dimensions of the picture itself
		p_height  = bg_pic.getIconHeight();



	}

	public void loadPlayerNames(){ //load player names
		player_names.add("mario");
		player_names.add("KT");
		player_names.add("Luigi");
		player_names.add("DK");
		player_names.add("Peach");

	}

	public void setMapBackgrounds(){ //tells what kind of background goes with each map

		green_background.add("Mario Circuit2");
		green_background.add("Mario Circuit");
		green_background.add("Mario Circuit3");
		brown_background.add("Choco Mountain");
		white_background.add("Vanilla Lake");
		black_background.add("Bowsers Castle");
	}

	public void loadOilSpills(){ //load where the oil spills are from dataflie
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
		//	System.out.println(OnlyTwoDaysLeft);
		}

	}


	public void loadBoosts(){ //load where the boost are from a datafile
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

		}

	}

	public void loadMapImage(){ //load the image for the selected map
		String ext = ".png";
		String name = level + ext;
		level_map = new ImageIcon(name).getImage();
	}



	public void createFrameMap(){ //create a map of what frame corresponds to what angle the player is facing
		int j = 0;
		int[] numbers = {7,6,5,4,3,2,1,0,21,20,19,18,17,16,15,14,13,12,11,10,9,8,7};
		for (double i = 0 ; i <=180;i+=(double)90/7){
			System.out.println(round(i)+" "+numbers[j]);
			frameMap.put(round(i),numbers[j]);
			reverse_frameMap.put(numbers[j],round(i)); //we want a reverse map as well so we know what frame correponds
			//to a specific angle
			j++;



	}
		for (double i = 180; i<=361;i+=(double)90/4){
			System.out.println(round(i)+" "+numbers[j]);
			frameMap.put(round(i),numbers[j]);
			reverse_frameMap.put(numbers[j],round(i));
			j++;
		}


	}

	public static int randInt(int low, int high){ //taken from class
		int range = high-low+1;
		return (int)(Math.random()*range)+low;

	}

	public void loadItemPics(){ //Load pictures for each item



		try{
			Scanner infile = new Scanner(new File("Item List.txt"));
			while (infile.hasNextLine()){
				String line = infile.nextLine();
				ImageIcon temp2 = new ImageIcon(line + "_item.PNG");
				Image temp = new ImageIcon(line + "_item.PNG").getImage();
				item_pics.put(line,temp);
			}

		}

		catch(Exception Ihavesomuchlefttodo){

		}



	}




	public void loadMusic(){ //Load midi files
		try{
			Scanner infile = new Scanner(new File("music.txt"));
			while (infile.hasNextLine()){
				String line = infile.nextLine();
				songs.put(line,line+".mid");
				System.out.println(line);
			}

		}

		catch(Exception e){

		}

	}


	public void playTitleMidi(String file){ //Plays the midi for teh title screen song


		file = songs.get(file);

		System.out.println("here");

        File midiFile = new File(file);
        // Play once
        try {
            title_music = MidiSystem.getSequencer();
            title_music.setSequence(MidiSystem.getSequence(midiFile));
            title_music.open();
            title_music.start();



        } catch(MidiUnavailableException mue) {
            System.out.println("Midi device unavailable!");
        } catch(InvalidMidiDataException imde) {
            System.out.println("Invalid Midi data!");
        } catch(IOException ioe) {
            System.out.println("I/O Error!");
        }

    }



	public void playBGMidi(String file){ //Plays the midi for the background music (specific to map)

		file = songs.get(file);

        File midiFile = new File(file);
        // Play once
        try {
            bg_music = MidiSystem.getSequencer();

            bg_music.setSequence(MidiSystem.getSequence(midiFile));
            bg_music.open();
           bg_music.setLoopCount(999); //loop the bg music



            bg_music.start();



        } catch(MidiUnavailableException mue) {
            System.out.println("Midi device unavailable!");
        } catch(InvalidMidiDataException imde) {
            System.out.println("Invalid Midi data!");
        } catch(IOException ioe) {
            System.out.println("I/O Error!");
        }

    }





	public void generateStrips(){ //generate width and height of each strip (relative to player)

		strip_lengths = new double[view_height-horizon]; //as many strips as scanlines/pixels on the screen
		strip_heights = new double[view_height-horizon];
		line_scales = new double[view_height-horizon];
	    sin_precalc = 1; //assume 90
    	cos_precalc = 0;
    	boolean first_flag = true;


	    for (int screen_y = horizon; screen_y<view_height;screen_y++){
	    	line_dist = (cam_height*scale_y)/(screen_y+horizon); //dist to the line

	    	line_scale = line_dist/scale_x; //horizontal scale of the line

	    	line_dx = -sin_precalc*line_scale; //slope of line basically
	    	line_dy = cos_precalc*line_scale;


			double len = line_dx*view_width*-1; //get length by multiplying the dx by the width of screen
			double height = ((line_dist*sin_precalc-(double)view_width/2*line_dy)*-1+y_offset)*-1;
			//one term is 0


			strip_lengths[screen_y - horizon] = len; //related by indices
			strip_heights[screen_y - horizon] = height;
			line_scales[screen_y - horizon] = line_scale;

			if (first_flag==true){ //if its he first strip, its also the max
				max_width = strip_lengths[0];
				max_height = strip_heights[0];
				view_range = new BufferedImage(round(max_width),round(max_height),BufferedImage.TYPE_INT_ARGB);
				first_flag=false;
			}



	    }

	    //get max and min height
	    min_height = strip_heights[view_height - horizon - 1];
	    min_width = strip_lengths[view_height - horizon - 1];
	    if (min_height>0){
	    	System.out.println("Error: Player will not be visible");
	    }
	    player_scanline = view_height + min_height;

	}

	public void loadCheckPoints(){ //Load checkpoints from datafile

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

	public void bananaCollisions(Kart player){ //Check if a player collides with a banana
		Rectangle r = new Rectangle(round(player.xpos),round(player.ypos),(int)player.getWidth(),(int)player.getHeight()); //get the rectangle made
		for (int i=0;i<bananas.size();i++){
			Banana b = bananas.get(i);
			if (b.collide(r)){
				bananas.remove(b); //if they do collide, remove the banana, and the player is now stunned
				player.setStunned();

			}

	}
	}

	public void shellCheck(Kart player){ //checks for shell collisions w/ player
		Rectangle r = new Rectangle(round(player.xpos),round(player.ypos),(int)player.getWidth(),(int)player.getHeight()); //get the rectangle made
		for (int i=0;i<green_shells.size();i++){
			GreenShell shell = green_shells.get(i);
			if (shell.collide(r)==true){
				player.setStunned(); //reomve shell and set the player to stunned
				green_shells.remove(i);
			}
		}

	}


	public void checkItemCollisions(Kart player){
		Rectangle r = new Rectangle(round(player.xpos),round(player.ypos),(int)player.getWidth(),(int)player.getHeight()); //get the rectangle made
		//by the player
		for (ItemBox item:items){ //for each item box check if it collides w/ the player
			if (item.collide(r)==true && player.hasItem()==false && item.isUsed()==false && player.item_timer<=0){ //mmake sure the player doens't already have an item and that item box is avaiaable
				new AePlayWave(get_item).start(); //play item box sound
				item.setUsed();
				player.setItem();

			}
		}
	}

	public void drawItems(){ //draw items on minimap
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

		catch(Exception e){

			}
	}


	public void moveBackground(Graphics dbg){ //function that handles the background moving


		BufferedImage buffer = new BufferedImage(v_width,bg_height,BufferedImage.TYPE_INT_ARGB); //buffered image used to scale the iamge
		Graphics2D g2d = buffer.createGraphics();





		double yscale =  bg_height/p_height; //factor by which you scale background in the y

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





		g2d.drawImage(picture,bx,by,null); //draw to buffer

		dbg.drawImage(buffer,screenx_offset,0,null);  //draw to screen

	}


	public void loadAIMapData(){ //Load data for the AI Map

	//The AI has a different map than the player so its easier for them to navigate


		try{
			String ext = ".txt";
			String name = level + " AI Map Data"+ext;

			Scanner infile = new Scanner(new File(name));
			int j=0;
			while (infile.hasNextLine()){
				String line = infile.nextLine();
				for (int i=0;i<128;i++){
					char letter = line.charAt(i);
					ai_terrain_grid[j][i] = letter;
				}
				j++;
			}

		}

		catch(Exception e){

		}
	}

	public void loadMapData(){ //load the 2D terrain grid for the map


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
		}
	}


	public FP(MainGame game, int period)throws IOException{ //Set up the main game stuff - copied from Nettrek
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


	//Override Methods

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;


    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }


    public String getTime(){ //get the time

    	if (first_timeflag==true){ //start time
    		start_time = System.currentTimeMillis ();
    		first_timeflag = false;
    	}

		long diff = System.currentTimeMillis ()-start_time;
		long milli = diff%1000; //mod by 1000 for milli
		second = (diff/1000)%60; //60 for seconds
		min = diff/60000; // 60000 for min
		return "Time: "+ min+" "+second+" "+milli+" s";

    }


	public void createPlayers(){ //create players

		players = new Kart[player_no];
		player_names.remove(player1_name);


		try{
			String ext = ".txt";
			String name = level + " Start Positions" + ext;
			Scanner infile = new Scanner(new File(name));


				String[] line = infile.nextLine().split(" ");
				int xcoord = Integer.parseInt(line[0]);
				int ycoord = Integer.parseInt(line[1]);
				players[0] = new Kart(player1_name,false,xcoord,ycoord,"hmn 1"); //human player

		for (int i = 1; i <player_no;i++){ //AI Players




				line = infile.nextLine().split(" ");
				 xcoord = Integer.parseInt(line[0]);
				 ycoord = Integer.parseInt(line[1]);

				String temp_name = player_names.get(i);



				players[i] = new Kart(player_names.get(i),true,xcoord,ycoord,null);


		}



		}


		catch (Exception e){

		}



	}

	public void run(){



		long lastUpdate, sleepLen;
		lastUpdate = System.currentTimeMillis ();



		rankings = new String[player_no+1];
//=============Stuff you have to load==================

		loadPlayerNames();
		getMapHorizonData();
//===================Non - Map Specific Stuff========================

		setMapBackgrounds();
		loadMusic(); //load all the music
		createTurnMap();
		loadItemPics();
		createFrameMap();
		createAIHandlingMap();

//==========Loading Screens================
		playTitleMidi("Title Screen");

		char_select = new CharSelectScreen(); //char seleect

		map_select = new MapSelectScreen(); //map select



		while (true){ //wait until a character/map have been picked
			if (char_select.get_char()!=null && map_select.get_map()!=null){
				player1_name = char_select.get_char();
				level = map_select.get_map();
				break;
			}
			System.out.println("waiting...");
		}

		 title_music.stop();


//===================Map Specific Stuff========================

		lap_no = 3; //number of laps in the race
		loadMapImage();
		loadMapData(); //load the data for that map
		loadAIMapData(); //load AI map data
		loadItems(); //make item box stuff
		loadCheckPoints(); //load checkpoint information
		loadBoosts();
		loadOilSpills();
		setUpHorizon(); //set up horizon pic


		minimap_g.scale(mini_xscale,mini_yscale); //scale minimap

		createPlayers(); //create players and their starting positions and things



		generateStrips();

		render(players[0]); //render for the first time
		paintScreen();

//==========pre race load=============

		new AePlayWave(racefanfare_music).start(); //play music
		delay(2500);

		new AePlayWave(countdown_music).start(); //blit pictures while delaying...
		dbg.drawImage(lakitu1,700,150,null);
		paintScreen();
		delay(1000);
		dbg.drawImage(lakitu2,700,150,null);
		paintScreen();
		delay(1000);
		dbg.drawImage(lakitu3,700,150,null);
		paintScreen();
		delay(1000);
//==================now the race starts==============


		playBGMidi(level);
		//Main Game Loop
		while (true){

			moveKarts(); //move karts - check for collisiosn etc
			moveObjects(); //move objects
			render(players[0]); //draw stuff to offscreen buffer ==> karts[0] means everything is relative to the kart at position 0
			paintScreen(); //paint the screen


	    	sleepLen = lastUpdate + period - System.currentTimeMillis ();
	    	sleepLen = Math.max (5, sleepLen);
	    	delay (sleepLen);
	    	lastUpdate = System.currentTimeMillis ();
			//delay

			if (raceOver()){ //stop if the race is over
				break;
			}

			}


			int final_pos = 99;


			//get ranking
			for (int i = 0; i <rankings.length;i++){
				if (rankings[i]!=null){

				if (rankings[i].equals(player1_name)){
					final_pos = i;
				}
				}
			}


			dbg.setColor(Color.black);
			dbg.fillRect(0,0,s_width,s_height);
			paintScreen();
			bg_music.stop();


			dbg.setFont(gameFont);
			dbg.setColor(Color.red);

			//Draw screen according to how they ranked
			if (final_pos==1){
				new AePlayWave(good_ending).start();
				dbg.drawString("Congratulations! You came in 1st!",100,100);
				dbg.drawImage(end1,200,200,null);
				paintScreen();

			}
			else{
				new AePlayWave(bad_ending).start();
				if (final_pos==2){
					dbg.drawString("Good Job! You came in 2nd!",100,100);
					dbg.drawImage(end2,200,200,null);
					paintScreen();
				}
				if (final_pos==3){
					dbg.drawString("Nice try...You came in 3rd",100,100);
					dbg.drawImage(end3,200,200,null);
					paintScreen();
				}
				if (final_pos==4){
					dbg.drawString("That was pretty awful....You came in 4th",100,100);
					dbg.drawImage(end4,200,200,null);
					paintScreen();
				}
				if (final_pos==5){
					dbg.drawString("Zomggg...How could you come in 5th?!",100,100);
					dbg.drawImage(end5,200,200,null);
					paintScreen();
				}

			}


		}



	public void boostCheck(Kart player){ //check if player hit a boost
		Rectangle r = new Rectangle(round(player.xpos),round(player.ypos),(int)player.getWidth(),(int)player.getHeight()); //get the rectangle made
		for (Rectangle boost: boosts){
			if (boost.intersects(r)){
				player.setItemTimer(20,25); //make them go faster if they did
			}
		}
	}

	public void oilSpillCheck(Kart player){ //check if player hit an oil spill
		Rectangle r = new Rectangle(round(player.xpos),round(player.ypos),(int)player.getWidth(),(int)player.getHeight()); //get the rectangle made
		for (Rectangle oil: oil_spills){
			if (oil.intersects(r) && player.immuneCheck()==false){
				player.setStunned(); //set them stunned if they did
			}
		}
	}

	public void moveKarts(){
		for (Kart player:players){
			if (player!=null && player.isAI()==false){ //human plays
				player.move(); //move
				player.useItemCheck(); //check if they used an item
				checkItemCollisions(player); //check if they hit an item
				bananaCollisions(player); //check if they hit a banana
				player.lapCheck(); //check if they finish lap
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

	public void moveObjects(){ //Move shells
		moveGreenShells();
		moveRedShells();
	}


	public void moveRedShells(){ //move redshells

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


	public void moveGreenShells(){ //move greenshells

		for (int i=0;i<green_shells.size();i++){
			GreenShell shell = green_shells.get(i);
			if (shell.isAvailable()){ //if the shell is still active, then move it
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


	public void drawRedShells(Kart focalPlayer,Graphics g){ //draw redshelll on 2.5d porition
		for (RedShell r:red_shells){
			drawObject(focalPlayer,null,null,null,r,r.getX(),r.getY(),8,8,"red shell",false,g);
		}
	}

	public void drawBananas(Kart focalPlayer,Graphics g){//draw banana on 2.5d porition
		for (Banana b:bananas){
			b.draw(map_g);
			drawObject(focalPlayer,null,null,b,null,b.getX(),b.getY(),b.getWidth(),b.getHeight(),"banana",false,g);
			//draw 3d
			//width, height, object type (kart, etc...),isFOcalPlayer(boolean),Graphics g
		}
	}

	public void drawGreenShells(Kart focalPlayer, Graphics g){//draw greenshell on 2.5d porition
		for (GreenShell shell:green_shells){
			shell.draw(map_g);
			drawObject(focalPlayer,null,shell,null,null,shell.getX(),shell.getY(),shell.getWidth(),shell.getHeight(),"shell",false,g);
			//draw 3d
		}
	}



	public void drawPlayers(Kart player){ //draw players on map
		for (Kart character:players){
			if (character!=null){
				if (character.equals(player)==false){
					character.draw(map_g);
				//	character.drawAIPoints(map_g);
				}

			}
		}
	}



	public void drawPlayers3D(Kart player, Graphics g){  //draw each player in 2.5d
		for (int i = 0; i <players.length;i++){

			if (players[i]!=null){


			if (players[i].equals(player)){ //this means the player you're trying to draw is the "focal" player
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




		//draw background map
	    map_g.drawImage(level_map,0,0,null);

	    drawItems(); //see if the item boxes should be drawn on the map...




		//draw in 2.5d
		renderFloor32d(dbg,player);
		drawPlayers3D(player,dbg); //draw 3d player s
		drawObjects(player,dbg);



		drawPlayers(player); //draw plays on minimap

	    map_g.setColor(Color.black); //draw focal player on minimap
	    map_g.fillRect(round(player.xpos)-4,round(player.ypos)-4,8,8);




	    //draw minimap
		minimap_g.drawImage(map,0,0,null); //draw in order to scale...(minimap)
	    dbg.drawImage(mini_map,0,0,null); //draw map to screen - USED ONLY FOR DEBUGGING



		drawHUD(dbg, player); //draw HUD




	}


		//Function used for drawing sprites/objects in 2.5d
		public void drawObject(Kart Focalplayer,Kart tempPlayer,GreenShell tempShell, Banana tempBanana,RedShell tempRedShell, double obj_x,double obj_y,double obj_width, double obj_height,String object_type,boolean isFocalPlayer,Graphics g){



		double rotation_angle = Math.toRadians(90-Focalplayer.getAngle()); //angle of rotatoin for the camera

		//there delta_y must be > -30 (last height scanline) (or else the object is behind the camera)

		double cx = Focalplayer.getX();
		double cy = Focalplayer.getY();


		double delta_x = cx - obj_x; //position relative to the focalplayer
		double delta_y = cy - obj_y;




		//rotation transformation....
		double space_x = delta_x*Math.cos(rotation_angle) + delta_y*Math.sin(rotation_angle);
		double space_y = delta_x*Math.sin(rotation_angle)*-1 + delta_y*Math.cos(rotation_angle);

		//space_x and space_y is the new position relative to the player


		double screen_y = (cam_height*scale_y)/(space_y+y_offset) - horizon;
		double line_length = ((cam_height*scale_y)/(screen_y+horizon))/scale_x*view_width;

		double temp_xscale =  v_width/line_length;  //xscale of the line

		double screen_x = v_width -  (space_x + line_length/2)*temp_xscale;

		//Get screen x,y coordinates


		int newX = round(screen_x);
		int newY = round(screen_y);

		int screeny = newY; //for thing sto be simpleer




//===================Getting the Width and Height of an Object=====================================

		//We don't want objects/sprites to be scaled in the same way as the floor
		//if we did they'd look too small - inside we introduce these scalnig factors to
		//offset that...and make sprites/objects look bigger



		double conste = 1;

		if (screeny>=450 && screeny<500)
		{
			conste = 1.4;
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
			conste = 4.7;
		}

		if (screeny>=0 && screeny<50){
			conste = 5.5;
		}





		double height_y = space_y - obj_height*conste; //moving down relative to the player (axis system reversed)


		double end_y = (cam_height*scale_y)/(height_y+y_offset) - horizon; //get end screen coordinate

		double screen_height = end_y - screen_y; //get total height

		if (screen_height>125){ //don't draw it if the object rendered it TOO big
			return;
		}


		//get xpos when you move along according to the width

		double width_x = (v_width - (space_x + line_length/2 + obj_width)*temp_xscale);


		//calculate total width by subtracting
		double screen_width = screen_x - width_x;



		int new_width = round(screen_width);
		int new_height = round(screen_height);



		int newX2 = round(newX - screen_width/2); //top left corner
		int newY2 = round(newY - screen_height/2 );




//================Boundaries Check and Drawing====================




		if (boundCheck(newX2,newY2)){ //check if object is in bounds


			BufferedImage sprite_img;
			ImageIcon temp = null;

			if (object_type.equals("kart") && isFocalPlayer==true){

				//if its the focal player we only want specific frames for turning/moving forward etc

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
				//toBufferedimage scales the image accordinignly
				g.drawImage(sprite_img,newX2+screenx_offset,newY2+screeny_offset,null);




			}


			//if its a kart and not the focalplayer
			if (object_type.equals("kart") && isFocalPlayer==false){
				double dev_angle = tempPlayer.getAngle() - Focalplayer.getAngle();
				int index = getFrameNo(dev_angle);
				temp  =  tempPlayer.getFrame(index); //find the relative angle, and then get the frame for it

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

	public int getFrameNo(double dev_angle){ //get the frame no for a player


		dev_angle = dev_angle+90;


		dev_angle = (dev_angle + 360)%360; //make it from 0 - 360


		//4 specific points
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

		//otherwise find the closest point
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


	//Scales a source image to some width/height and returns it as a bufferedimage
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


	public boolean boundCheck(int x, int y){ //make sure the object being drawn is not offscreen

		int upper_xbound = v_width + screenx_offset;
		int lower_xbound = screenx_offset;
		int upper_ybound = (int)player_scanline - 50;
		int lower_ybound = screeny_offset;

		return (x+screenx_offset > lower_xbound && x + screenx_offset < upper_xbound && y+screeny_offset<upper_ybound && y+screeny_offset > lower_ybound);
	} //unsure about y+screeny_offset>screeny_offset....do you want to see objects in the horizon?


	public void drawHUD(Graphics g, Kart player ){ //draw HUD
		g.setColor(Color.pink);
		drawLapCount(g,player);
		drawTime(g);
		drawItem(g,player);
		drawRankings(g);
	}

	public void drawRankings(Graphics g){ //draw rankings for each position
		int ystart = 550;
		String temp;
		g.setColor(Color.blue);
		for (int i = 1 ; i <rankings.length; i++){
			if (rankings[i]!=null){
				temp = (i)+".)"+rankings[i];
			}
			else{
				temp = (i)+".)"+"?????";
			}

			g.drawString(temp,100,ystart);
			ystart+=50;			//move down

		}
	}


	public void drawItem(Graphics g, Kart player){ //draws the item that the player has
		String p_item = player.getItem();

		if (p_item.equals("")){
			p_item = "empty";
		}
	//	System.out.println(p_item);
		g.setFont(gameFont);
		g.setColor(Color.pink);
		g.drawString("Item: ",400,560);
		g.drawImage(item_pics.get(p_item),400,600,null);
	//	g.setColor(Color.blue);
	//	g.fillRect(400,600,200,200);
	}


	public void drawTime(Graphics g){ //draw time
		g.setColor(Color.red);
		g.drawString(this.getTime(),400,700);

	}


	public void drawLapCount(Graphics g, Kart player ){ //draw laop
		g.setColor(Color.green);
		g.drawString("Lap: "+player.getLap() + " of "+ lap_no,400,750);
	}

	public int round(double d){ //rounds double to int
		double n = d+.5;
		return (int)n;

	}





/*	public void renderFloor3D(Graphics dbg,Kart player){
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



	} */


	//used for debugging...draws scanliens onto the minimap
	public void renderFloor2D(Graphics dbg,Kart player){

		double r_angle = Math.toRadians(90-player.angle);



		px = player.xpos; //shallow (like laura) copies
		py = player.ypos;


		cos_precalc = Math.cos(Math.toRadians(player.angle+180));
		sin_precalc = Math.sin(Math.toRadians(player.angle+180));




	    map_g.setColor(Color.red);

	    for (int i = 0; i < strip_lengths.length;i++){

	    //	System.out.println(i);

	    	double length = strip_lengths[i];
	    	double height = strip_heights[i];

	    	double mid_x = px - cos_precalc*height; //reverse
	    	double mid_y = py + sin_precalc*height;

	    	double line_dx = sin_precalc;
	    	double line_dy  = cos_precalc;

	    	int x1 = round(mid_x - line_dx*length/2); //start of line
	    	int y1 = round(mid_y - line_dy*length/2);

	    	int x2 = round(mid_x + line_dx*length/2); //end of line
	    	int y2 = round(mid_y + line_dy*length/2);



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


	public void renderFloor32d(Graphics dbg, Kart player){//Function used to render the map

		double r_angle = Math.toRadians(90-player.angle); //relative angle of player



		px = player.xpos;
		py = player.ypos;


		cos_precalc = Math.cos(Math.toRadians(player.angle+180));
		sin_precalc = Math.sin(Math.toRadians(player.angle+180));




	    map_g.setColor(Color.red);

	    int drawstart = horizon;

	    //Basically for each strip we get the screenx, screeny
	    //Then we move along the line according to dx, dy
	    //we get the pixel at that position and set the corresponding screen pixel to that color

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

	    			//if its out of bounds on the map we use the background pixel values, depending, on what the level is
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

	    dbg.drawImage(screen,screenx_offset,screeny_offset,null); //draw to screen


	}


	public void paintScreen(){ //update dbg
		Graphics g;
		g = this.getGraphics();
		if ((g != null) && (dbImage != null)){
			g.drawImage (dbImage, 0, 0, null);
		}

	}

	private void startGame(){ //start new thread
		th = new Thread(this);
		th.start();
	}


    public void addNotify ()
	// wait for the JPanel to be added to the JFrame before starting
    {

	super.addNotify ();    // creates the peer
	startGame ();   // start the thread

    }




    public void delay (long len) //delay
    {
	try
	{
	    Thread.sleep (len);
	}
	catch (InterruptedException ex)
	{
	}

    }



	public void updateRankings(Kart player){ //update the rankings for the race
		String name = player.getName();

		for (int i = 1; i < rankings.length; i ++){

			if (rankings[i]==null){ //if that spot has not been used
				rankings[i] = name;
				if (player.getName().equals(player1_name)){ //if the ranking is  hmn player...end the race
					race_endflag = true; //race is now over
				}
				//System.out.println("Player called "+ name + " is rank " + i );
				break;
			}
		}

	}


class Kart implements KeyListener{ //Kart class

	private double map_start_x; //used when going offcourse
	private double map_start_y;

	double xpos;
	double ypos;
	double angle; //angle that this kart is facing



	private double width;
	private double height;

//=====================Constant Stat Values===============


	private double star1_accel = 0.02; //acceleration
	private double star2_accel = 0.03;
	private double star3_accel = 0.04;
	private double star1_speed = 10; //max velocity
	private double star2_speed = 13;
	private double star3_speed = 15;
	private double star1_offroad = 5; //max offroad velocity
	private double star2_offroad = 7;
	private double star3_offroad = 8;
	private double star1_offroad_accel = 0.01; //offroad acceleration
	private double star2_offroad_accel = 0.02;
	private double star3_offroad_accel = 0.03;
	private double star1_handling = 3; //didn't use this yet  - handling variable
	private double star2_handling = 5;
	private double star3_handling = 8; //O_o




//==================== Being Stunned and Stun variables ==========

	private boolean stun_flag; //if you're stunned or not
 	private int stun_counter; //count down
 	private int immune_counter; //if you're immuned to being stunned


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

	String last_turn = level_first_turn.get(level); //depends on level
	double ai_handling = ai_handling_map.get(level);


//============Stuff for Human Players=======================

	private String control_config; //this is used on 2P more...to set key values
	private boolean left_turnflag; //are they turning left, right or not turning at all
	private boolean right_turnflag;
	private boolean no_turnflag = true;
	private boolean drift_flag;
	private double item_velocity;
	double handling; //how sharply you can turn your kart

//==============for when you're on the road - STATS ===============
	double acceleration;
	double velocity;

	double accel_increment;  //how quickly you speed up
	double brake_increment; //how fast your brakes slow you down

	double max_velocity; //top speed
	double min_velocity; //min reverse speed...
	//maybe I'll just make this the same for every character

	//values if you're using a star
	String star_maxspeed;
	String star_offroadspeed;
	String star_acceleration;
	String star_handling;
	String item_const; //what item a palyer has

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
	private boolean halfWay; //flag indicating if you've passed the halfway point
	private int lapCount;  //flag indicating if you're finished the lap

//=====================================================Frames=============


	String name; //name of character - used to determine stats later on...
	ImageIcon[] frames; //all frames
	int currentframe; //currentframe


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

	public double getStatValue(String stat, String stars){ //get the stat value depending on how many stars in a catagory
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





	public Kart(String name,boolean AIflag,double startx, double starty,String control_type){ //Constructor

		AI = AIflag;
		this.name = name;
		xpos = startx;
		ypos = starty;

		map_start_x = startx;
		map_start_y = starty;

		angle = 90;

		height = 16;
		width = 16 ;

		if (AIflag==false){
			control_config = control_type; //what buttons are used for each player
		}

//======================Loading Images======================


		String temp_name = name.toLowerCase();
		if (temp_name.equals("kt")){
			temp_name = "KT";
		}

		if (temp_name.equals("dk")){
			temp_name = "DK";
		}


		frames = new ImageIcon[22];
		loadFrames(temp_name);


		try{ //get char stats from datafile
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


		//rna

		max_velocity+=randInt(-5,5);
		offroad_max_velocity+=randInt(-5,5);



//==============constant stats for each char ===========
		velocity = 0;
		acceleration = 0;
		min_velocity = -5;
		brake_increment = 0.01;
		//handling = 5;
//=======================================================

		stun_flag = false; //stun flag
		stun_counter = 0;

		requestFocus(); //for the key stuff ==> infact, I don't even know if I need this O_o
		addKeyListener(this);



		halfWay = false; //tells if the player has passed the halfway point or not

		hasItem = false;
		item = "";
		item_timer = 0;



	}


	public int getLap(){ //return what lap you're on
		return lapCount;
	}

	public void lapCheck(){ //checks if the player has completed a lap

		Rectangle r = new Rectangle(round(xpos),round(ypos),(int)width,(int)height);

		if (half.collide(r)==true && halfWay == false){ //halfWay check
			halfWay = true;

		}

		if (finish_line.collide(r)==true && halfWay==true){ //Lap Complete check - must have already passed the halfway mark though
			lapCount++; //increase lap
			if (lapCount==lap_no-1 && control_config!=null){
				new AePlayWave(final_lap_music).start(); //play final lap music

			}

			halfWay = false;
			if (lapCount==lap_no){
				updateRankings(this);
			}

		}


	}


	public String getName(){ //getter
		return this.name;
	}

	public boolean isAI(){ //returns if the player is AI or not
		return AI==true;
	}


	public void mushroomCheck(){ //if you have a mushroom/star...change velocity
		if (item_timer>0){
			velocity = item_velocity; //velocity of the item that currently increases your speed
			item_timer--;
		}
	}


	public void move(){ //move the player


		if (this.offCourseCheck()==true){ //if you're off the course
			xpos = map_start_x;
			ypos = map_start_y;
			this.angle = 90; //reset
			this.setStunned(); //stunned
			halfWay=false; //you have to redo the lap
			item_velocity = 0; //cant be used an item
			item_timer = 0;

		}

		if (stun_flag==true){
			this.stunAnimation();
		}

		else{
			updateAngle(); //update angle
			updateSpeed(); //update speed
			mushroomCheck(); //check if they're using a mushroom
			moveAlong();	//move the player along
		}



	}


	public boolean isDrifting(){ //returns is the palyer is drfitng or not
		return drift_flag==true;
	}

	public void updateAngle(){ //updates the angle



	if (control_config.equals("hmn 1")){

		int drift_const = 0;

		if (keys[69]==true){  //see if the player is drifting
			drift_const = 10;
			drift_flag = true;
		}


		if (keys[69]==false){
			drift_flag = false;
		}


		if (keys[37]==true){ //left turn
			if (this.iceCheck()==true){ //slippery effect if on ice
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


	public void AImoveAlong(){ //same as player moveAlong but no border check
		for (int v=0;v<velocity;v++){ //loop as many "velocities" as he has...
			if (velocity>0){ //moving forward
				xpos+=1*Math.cos(Math.toRadians(angle));
				ypos+=1*Math.sin(Math.toRadians(angle))*-1;
			}
			if (velocity<0){ //moving backward...why doesn't this work O_o
				xpos+=-1*Math.cos(Math.toRadians(angle));
				ypos+=-1*Math.sin(Math.toRadians(angle))*-1;
			}



		}

	}


	public void moveAlong(){ //move by 1 each time...in order to make checks more accurate
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

	public boolean allBorderCheck(){ //checks if player hit a border
		return (pointBorderCheck(xpos+4,ypos+4) || pointBorderCheck(xpos+4,ypos-4) || pointBorderCheck(xpos-4,ypos-4) || pointBorderCheck(xpos-4,ypos+4));

	}


	public boolean AIoffRoadCheck(){ //check if the ai character went offroad

		int newx = round(xpos);
		int newy = round(ypos);

		int gridx = newx/8; //divide by grid ratio
		int gridy = newy/8;

		return ai_terrain_grid[gridy][gridx]=='d';

	}

	public boolean offRoadCheck(){ //check if human player went offroad

		int newx = round(xpos);
		int newy = round(ypos);

		int gridx = newx/8; //divide by grid ratio
		int gridy = newy/8;

		return terrain_grid[gridy][gridx]=='d';

	}


	public boolean iceCheck(){ //check if human player is on ice

		int newx = round(xpos);
		int newy = round(ypos);

		int gridx = newx/8; //divide by grid ratio
		int gridy = newy/8;

		return terrain_grid[gridy][gridx]=='i';
	}


	public boolean snowCheck(){ //check if human player is on snow

		int newx = round(xpos);
		int newy = round(ypos);

		int gridx = newx/8; //divide by grid ratio
		int gridy = newy/8;

		return terrain_grid[gridy][gridx]=='s';
	}


	public boolean offCourseCheck(){ //check if human player went offcouse

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


	public boolean AIpointDirtCheck(double px,double py){ //checsk to see if one of the AI points lie on the dirt
		int newx = (int)px;
		int newy = (int)py;

		int gridx = newx/8; //divide by grid ratio
		int gridy = newy/8;

		if (gridx<0 || gridy<0 || gridx>128 || gridy>128){
			return true;
		}

		if (gridx < 0  || gridy< 0 || gridx >= 128 || gridy>=128){
			return true;
		}

		return (ai_terrain_grid[gridy][gridx]=='d' || ai_terrain_grid[gridy][gridx]=='s' || ai_terrain_grid[gridy][gridx]=='x' ) ;
	}


	public boolean pointDirtCheck(double px,double py){ //checks if a human point lies on the dirt/snow
		int newx = (int)px;
		int newy = (int)py;

		int gridx = newx/8; //divide by grid ratio
		int gridy = newy/8;

		if (gridx<0 || gridy<0 || gridx>128 || gridy>128){
			return true;
		}

		return (terrain_grid[gridy][gridx]=='d' || terrain_grid[gridy][gridx]=='s') ;
	}


	public boolean pointOffCourseCheck(double px,double py){ //checks if a specific point is offcourse
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


		if (velocity>max_velocity && offRoadCheck()==false){ //can't go over max velocity
			velocity = max_velocity;
			acceleration = 0;
		}







		if (velocity<min_velocity){ //can't go below min velocity
			velocity = min_velocity;
			acceleration = 0;
		}

	}

	public String getItem(){ //getter method
		return this.item;
	}

	public void setItem(){ //set that the player has an item to true
		this.hasItem = true;
		item = item_const;
	}

	public boolean hasItem(){
		return this.hasItem==true;
	}


//=========keyboard input==========
    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        //System.out.println(e.getKeyCode());

    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    public void useItemCheck(){ //Checks if the player has an item or not


		if (control_config.equals("hmn 1")){


		if (keys[70]==true && hasItem){ //if they have an item and pressed the button


    	if (item.equals("Red Shell")){

    	}



     	if (item.equals("greenshell")){
     		//System.out.println("Green Shell Used");

     		new AePlayWave(shell_shoot).start();
     		//play music...

     		item = ""; //don't need to do this
     		hasItem = false;
     		GreenShell temp;
     		if (keys[40]==true){ //if you're holding downt he shell goes backgrounds ==> therefore angle + 180
     			temp = new GreenShell(xpos+height*2*Math.cos(Math.toRadians(angle+180)),ypos+height*2*Math.sin(Math.toRadians(angle+180))*-1,angle+180,terrain_grid,"green"); //make it
     		}
     		else{
     			temp = new GreenShell(xpos+height*2*Math.cos(Math.toRadians(angle)),ypos+height*2*Math.sin(Math.toRadians(angle))*-1,angle,terrain_grid,"green"); //make it
     		}

    		green_shells.add(temp); //add it
    	}

     	if (item.equals("mushroom")){
     	//	System.out.println("Mushroom Used");
     		new AePlayWave(use_mushroom).start();
     		item = ""; //don't need to do this
     		hasItem = false;
     		setItemTimer(20,20); // indicates how long you have this speed boost/item for

    	}

     	if (item.equals("star")){
     	//	System.out.println("Mushroom Used");
     		new AePlayWave(star_sound).start();
     		item = ""; //don't need to do this
     		hasItem = false;
     		setItemTimer(200,15); // indicates how long you have this speed boost/item for

    	}




    	if (item.equals("redshell")){


     		new AePlayWave(shell_shoot).start(); //play sound
     		//play music...

     		item = "";
     		hasItem = false;
     		GreenShell temp;
     		if (keys[40]==true){ //if you're holding downt he shell goes backgrounds ==> therefore angle + 180
     			temp = new GreenShell(xpos+height*2*Math.cos(Math.toRadians(angle+180)),ypos+height*2*Math.sin(Math.toRadians(angle+180))*-1,angle+180,terrain_grid,"red"); //make it
     		}
     		else{
     			temp = new GreenShell(xpos+height*2*Math.cos(Math.toRadians(angle)),ypos+height*2*Math.sin(Math.toRadians(angle))*-1,angle,terrain_grid,"red"); //make it
     		}

    		green_shells.add(temp); //add it

    	}

    	if (item.equals("yellowshell")){

    		item = "";
    		hasItem  = false;
     		new AePlayWave(shell_shoot).start();
     		//play music...

     		GreenShell temp;
     		if (keys[40]==true){//if you're holding downt he shell goes backgrounds ==> therefore angle + 180
     			temp = new GreenShell(xpos+height*2*Math.cos(Math.toRadians(angle+180)),ypos+height*2*Math.sin(Math.toRadians(angle+180))*-1,angle+180,terrain_grid,"yellow"); //make it
     		}
     		else{
     			temp = new GreenShell(xpos+height*2*Math.cos(Math.toRadians(angle)),ypos+height*2*Math.sin(Math.toRadians(angle))*-1,angle,terrain_grid,"yellow"); //make it
     		}

    		green_shells.add(temp); //add it

    	}



     	if (item.equals("banana")){

    		Banana temp = new Banana(xpos+height*2*Math.cos(Math.toRadians(angle+180)),ypos+height*2*Math.sin(Math.toRadians(angle+180))*-1,8,8);
    		bananas.add(temp);
     		item = ""; //don't need to do this
     		hasItem = false;

    	}

		}
		}

    }


    public void setItemTimer(int no,int new_velocity){ //sets the item timer to a specfiic value
    	item_timer+=no;
    	item_velocity = new_velocity;
    }

    //getter method

    public double getAngle(){
    	return this.angle;
    }

    public double getX(){
    	return this.xpos;
    }

    public double getY(){
    	return this.ypos;
    }

    public void draw(Graphics2D g){ //draws player on minimap


		int x = round(xpos);
		int y = round(ypos);
		g.setColor(Color.blue);
		g.fillRect(x-4,y-4,8,8);

    }

    public void AIUpdateAngle(){ //update the AI angle

    	//Basically the AI has 3 points infront of it
    	//if hte left point hits the dirt you turn right
    	//if the right point hits the turn you turn left
    	//if hte forward point hits the dirt you turn in the direction you turned last
    	//this is how the ai moves

  		boolean forward = AIpointDirtCheck(x_forwardpoint,y_forwardpoint);
		boolean left = AIpointDirtCheck(x_leftpoint,y_leftpoint);
		boolean right = AIpointDirtCheck(x_rightpoint,y_rightpoint);





    	if (forward){
    	//	System.out.println("Forward on Dirt");
    		//turn in direction you turned last
    		if (last_turn.equals("left")){
    			angle-=ai_handling;
    		//	velocity*=.9;
    		}
    		if (last_turn.equals("right")){
    			angle+=ai_handling;
    		//	velocity*=.9;
    		}
    		return;
    	}


    	if (left ){ //left point hit - turn right
    	//	System.out.println("left on dirt");
    		angle-=ai_handling;
    		//velocity*=.9;
    		last_turn = "left";
    		return;
    	}
     	if (right){ //right point hit - turn left
     	//	System.out.println("right on dirt");
    		angle+=ai_handling;
    		//velocity*=.9;
    		last_turn = "right";
    		return;
    	}

    }

    public void AImove(){ //move the AI

    	if (isStunned()){
    		this.stunAnimation();
    		return;
    	}


    	x_leftpoint = xpos + point_dist*Math.cos(Math.toRadians(angle + angle_dev)); //Create points....
    	y_leftpoint = ypos + point_dist*Math.sin(Math.toRadians(angle + angle_dev))*-1;

     	x_rightpoint = xpos + point_dist*Math.cos(Math.toRadians(angle - angle_dev));
    	y_rightpoint = ypos + point_dist*Math.sin(Math.toRadians(angle - angle_dev))*-1;


     	x_forwardpoint = xpos + fpoint_dist*Math.cos(Math.toRadians(angle));
    	y_forwardpoint = ypos + fpoint_dist*Math.sin(Math.toRadians(angle))*-1;

  //  	System.out.println(x_leftpoint+" "+x_rightpoint);


    	//same as player movement....

    	acceleration+=accel_increment; //0.03

		velocity+=acceleration;


		if (velocity>offroad_max_velocity && AIoffRoadCheck()==true){ // on dirt
			if (velocity+2>offroad_max_velocity){
				velocity-=1;
			}
			else{
				velocity = offroad_max_velocity;
			}
			acceleration = 0;
		}

		if (velocity>max_velocity && AIoffRoadCheck()==false){
			velocity = max_velocity;
			acceleration = 0;
		}

		///basic stuff

		//==================AI Stuff==================





   // 	velocity = 2;
    	AIUpdateAngle(); //update angle

    	if (level.equals("Ghost Valley")){
    		moveAlong();
    	}
    	else{
    		AImoveAlong(); //last thing you doo...
    	}


    }

    public void drawAIPoints(Graphics2D g){ //.function that draws AI points onto minimap - used for debugging

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

    public boolean immuneCheck(){ //check if player is immune to obstacles

    	if (immune_counter>0){
    		immune_counter--; //if they are immune decrease counter
    		return true;
    	}
    	if (immune_counter<=0){
    		return false;
    	}
    	return false; //doesn't get here

    }

	public void setStunned(){ //make the player stunned/spin around
		new AePlayWave(get_hurt).start(); //play sound
		item_timer = 0;
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
			if (currentframe>frames.length-1){ //loop through frames to make it look like they're spinning
				currentframe = 0;
			}

			if (AI==false){
				this.angle = reverse_frameMap.get(currentframe); //get new angle...
			}


		}
	}


	//Getter Methods...
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


//YAY I'M DONE!