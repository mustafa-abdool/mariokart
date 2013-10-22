
//FP game class

//Things to fix....


//find ratio of scale change yourself - DON'T GUESS

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
	
	private static int fps = 26;
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
	static int[][] map;
	static int rows;
	static int cols;
	static int gridratio;
	private Graphics dbg;
	private Image dbImage;
	private int s_width = 800;
	private int s_height = 600;
	private Thread th;
	private int period;
	private int count; //used for testing
	private BufferedImage bitmap; //map thingy
	private Image real_map = new ImageIcon("new map.png").getImage();
	MainGame top;
	private int real_width = 2000;  //width and height of the acutal bitmap that you're using
	private int real_height = 2000;
	private Player player;
	
	private int view_width = 100; //width and height of the portion of the image that you see
	private int view_height = 100;
	
	private int actual_height = (int)(s_height*.75); //height of actual screen in which you see stuff
	
	private double scale_x = s_width/view_width;
	private double scale_y = actual_height/view_height;
	
	 
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
			player.angle-=5;
			
		}
		if (keys[39]==true){ //right 
			player.angle+=5;
			
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
	   // 	count++;
			
			
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
	    
	    dbg.setColor(Color.blue); //reset screen
	    dbg.fillRect(0,0,s_width,s_height); //reset screen
	

	    
	    	
	    BufferedImage h_strip; //strip of horizontal stuff 

	    bitmap = new BufferedImage(2100,2100,BufferedImage.TYPE_INT_ARGB); //make buffered image of background 
	    Graphics2D g2d = bitmap.createGraphics(); //get graphics 
	    double rangle = Math.toRadians(player.angle);
	    double rotation_angle = Math.PI/2 - rangle;
	    g2d.rotate(rotation_angle);
	    System.out.println("Angle of rotation is " +Math.toDegrees(rotation_angle));

	    
	    g2d.drawImage(real_map,0,0,null); //draw map to it
	    double newX = (player.xpos)*Math.cos(rotation_angle)+(player.ypos)*Math.sin(rotation_angle)*-1;
	    double newY = (player.xpos)*Math.sin(rotation_angle)+(player.ypos)*Math.cos(rotation_angle);	    
	    
	    int tempx = (int)(newX);
	    int tempy = (int)newY;
	    
		
		System.out.println("The Y coordinate is " +newY);
		System.out.println("The X Coordinate is  "+newX);
	    
	    int screen_y=s_height;
	    
	    int yscalefactor=5; //how much to scale the image in the y by	    
	    int strip_height = 4; //length of each "strip"
	    double xincrement = 75;
	    int xincrement_i = (int)(xincrement);
	    double xscale = s_width/xincrement; //CHANGE THIS MOFO!
	    
	    
	    int count = 1;

	//    int strip_no = 600/(strip_height);
	  //  int interval = strip_no/3;
		int dx = 5;
	    int scalechange = 10;

	    while (screen_y>=0){ //loop through horizontal strips 
	    
	    	h_strip = bitmap.getSubimage(tempx - xincrement_i/2,tempy,xincrement_i,strip_height); //get line 
	    	  	
	    	screen_y = (int)(screen_y - strip_height*yscalefactor);

	    	
	    	if (screen_y<=0){ //can't go offscreen
	    		break;
	    	}
	    	
	    	BufferedImage temp = new BufferedImage(s_width,(int)strip_height*yscalefactor,BufferedImage.TYPE_INT_ARGB);
	    	Graphics2D tempg = temp.createGraphics();
	    	xscale = s_width/xincrement; //CHANGE THIS MOFO!
	    	tempg.scale(xscale,yscalefactor);
	    	
	    	tempg.drawImage(h_strip,0,0,null);
	    	
	    	dbg.drawImage(temp,0,screen_y,null);
	    	
	    	tempy-=strip_height;	    	
	    	if (count%scalechange==0){
	    		//System.out.println("okay");
	    		yscalefactor--;
	    	}
	    	count ++;
	    	xincrement+=dx;
	    	xincrement_i=(int)(xincrement);

	    } 
	    	
	   // System.out.println(count);
	    


	    
	  //  dbg.drawImage(screen_copy,0,0,null);
	    

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
	public Player(){
		xpos = 1441;
		ypos = 1173;
		angle = 90;
		
	}
}

