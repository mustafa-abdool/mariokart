
//FP game class



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
	
	private int view_width = 200; //width and height of the portion of the image that you see
	private int view_height = 200;
	
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
			player.angle+=5;
			
		}
		if (keys[39]==true){ //right 
			player.angle-=5;
			
		}
		if (keys[38]==true){ //up
			player.xpos+=50*Math.cos(Math.toRadians(player.angle));
			player.ypos+=50*Math.sin(Math.toRadians(player.angle))*-1;
			
		}
		if (keys[40]==true){ //down
			player.xpos-=50*Math.cos(Math.toRadians(player.angle));
			player.ypos-=50*Math.sin(Math.toRadians(player.angle))*-1;		
			
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
	    
	    dbg.setColor(Color.blue); //reset screen
	    dbg.fillRect(0,0,s_width,s_height); //reset screen
	
	    double temp_x = player.xpos;
	    double temp_y = player.ypos;
	    
	    
	    bitmap = new BufferedImage(2000,2000,BufferedImage.TYPE_INT_ARGB); //make buffered image
	    Graphics2D g2d = bitmap.createGraphics(); //get graphics 
	    double rotation_angle = Math.toRadians(player.angle) - Math.PI/2;//angle which to rotate
	   // rotation_angle = Math.PI/4;
	    g2d.rotate(rotation_angle); //rotate relative to player angle
	    g2d.drawImage(real_map,0,0,null); //draw map to it
	    
	    

	    
	    //This Image holds the unscaled version of what you can see
	    BufferedImage sub = new BufferedImage(view_width,view_height,BufferedImage.TYPE_INT_ARGB); 
	    	
	    temp_x = player.xpos*Math.cos(rotation_angle)+player.ypos*Math.sin(rotation_angle)*-1;
	    temp_y = player.ypos*Math.cos(rotation_angle)+player.xpos*Math.sin(rotation_angle);

		double degree = (Math.toDegrees(rotation_angle));
		System.out.println(degree);

		

		sub = bitmap.getSubimage((int)temp_x-view_width/2,(int)temp_y-view_height/2,view_width,view_height);	




	   
	    
	    
	    //This scales the images to the proper ratio required 
		BufferedImage temp = new BufferedImage(s_width,actual_height,BufferedImage.TYPE_INT_ARGB); //scale image 	    
	    Graphics2D temp_g = temp.createGraphics();
	    temp_g.scale(scale_x,scale_y);
	    temp_g.drawImage(sub,0,0,null); 
	    
	    
	    
	    
	    
	    
	    
	    dbg.drawImage(temp,0,0,null);
	    

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
		ypos = 1073;
		angle = 90;
		
	}
}

