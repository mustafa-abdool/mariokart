
//FP game class


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
	
	private static int fps = 2;
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
	private int view = (int)(view_height*3); //this represents the TOTAL screenshot that is gotten, and the position is taken from that
	
	private int actual_height = (int)(s_height*.75); //height of actual screen in which you see stuff
	
	private double scale_x = s_width/view_width;
	private double scale_y = actual_height/view_height;
	private Image random_pic = new ImageIcon("test thing.png").getImage();
	private Image norm = new ImageIcon("new map.png").getImage();
	
	 
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
		if (keys[37]==true){ //left
			player.angle+=5;
			
		}
		if (keys[39]==true){ //right 
			player.angle-=5;
			
		}
		if (keys[38]==true){ //up
			player.xpos+=30*Math.cos(Math.toRadians(player.angle));
			player.ypos+=30*Math.sin(Math.toRadians(player.angle))*-1;
			
		}
		if (keys[40]==true){ //down
			player.xpos-=30*Math.cos(Math.toRadians(player.angle));
			player.ypos-=30*Math.sin(Math.toRadians(player.angle))*-1;		
			
		}		
				
				
		
		
	}
	
    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        move();
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }	
	
	public void run(){
		
		long lastUpdate, sleepLen;
		lastUpdate = System.currentTimeMillis ();
		player = new Player();
		

		while (true){
		//	move();
			render();
			paintScreen();
	    	sleepLen = lastUpdate + period - System.currentTimeMillis ();
	    	sleepLen = Math.max (5, sleepLen);
	    	delay (sleepLen);
	    	lastUpdate = System.currentTimeMillis ();
	    	count++;
			
			
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
		int kartY = real_height/2; //no OFFSET ON THIS ONE 
		
		int newxpos = kartX;
		int newypos = kartY;
		
		viewrange = map2.getSubimage(newxpos - view_width/2,newypos - view_height/2,view_width,view_height); //get the image of what mario actually SEES


//drawing on a scanline by scanline basis

		int strip_no = (int)(s_height*.75); //number of strips to have 
		int dx = 200; //view hieght
		
		for (int i=0;i<strip_no;i++){
			
			h_strip = map2.getSubimage(kartX - dx/2,kartY,dx,1); //get subimage of what you can see
			BufferedImage scaled_ver = new BufferedImage(s_width,1,BufferedImage.TYPE_INT_ARGB); //BufferedImage used to scale the strip
			Graphics2D scalever_g = scaled_ver.createGraphics(); //graphics context for scaled buffered image
			//has to be width of screen
			int xscale = s_width/dx; //get xscale
			scalever_g.scale(xscale,1); //scale image
			scalever_g.drawImage(h_strip,0,0,null); //draw to scaled image
			dbg.drawImage(scaled_ver,0,s_height - i,null);  //draw to real screen
	//		System.out.println("Looping");
			kartY--;
			
			
			
			
		}
		System.out.println("Done");

	    dbg.drawImage(viewrange,0,0,null); //finally, draw everything to the real screen
	    

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


void displayMatrix(AffineTransform theTransform){
  double[] theMatrix = new double[6];
  theTransform.getMatrix(theMatrix);
 //Display first row of values by displaying every
    // other element in the array starting with element
    // zero.
    for(int cnt = 0; cnt < 6; cnt+=2){
      System.out.print(theMatrix[cnt] + " ");
    }//end for loop
    
    //Display second row of values displaying every
    // other element in the array starting with element
    // number one.
    System.out.println();//new line
    for(int cnt = 1; cnt < 6; cnt+=2){
      System.out.print(theMatrix[cnt] + " ");
    }//end for loop
    System.out.println();//end of line
    System.out.println();//blank line
    
  }//end displayMatrix

		
}


class Player{
	double FOV;
	double xpos;
	double ypos;
	double angle;
	public Player(){
		xpos = 1441;
		ypos = 1173;
		angle = 90;
		
	}
}

