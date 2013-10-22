
//FP game class



//=======================MOOSEY-O KART FINAL PROJECT====================================


//Level Editor
//trying to get 2d array for pre made maps
//using pixel values
//now I'm trying to work on creating my own map 

//different sizes for what you can add ontot he map


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
	
	private static int fps = 300;
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


class FP extends JPanel implements KeyListener, MouseListener, MouseMotionListener, Runnable{
	


	private Graphics dbg; //set up stuff
	private Image dbImage;
	private Thread th;
	private MainGame top;
	private int period;	
		
	private Image picture = new ImageIcon("mushroom map2.png").getImage();
	
	private int s_width = 1024; //height and width of screen
	private int s_height = 1024;
	private int gridratio = 8; //how many pixels each "tile" is 
	
	private String[][] terrain_grid = new String[s_width/gridratio][s_height/gridratio];
	

	
	private int mx=0; //double mx and my position
	private int my=0;
	
	private int rx=0;
	private int ry=0;
	
	private int tx = 0;
	private int ty= 0;
	
	private boolean mbDown=false; //tells if the mouse button is up or down 
	private boolean mb2Down=false; //for the right click
	
	 
	 boolean []keys = new boolean[256]; 
	 	
	 private int count = 0; //for the datafile
	 
	 
	 private HashMap<String,Image> images = new HashMap<String,Image>(); //maps name onto images
	 
	 private String selectedImage = "barrier blue";
	 private String size = "large"; //size of how to draw it 	
	 	
	public void loadImages(){
		images.put("barrier blue",new ImageIcon("barrier blue.PNG").getImage());
		images.put("barrier green",new ImageIcon("barrier green.PNG").getImage());
		images.put("barrier red",new ImageIcon("barrier red.PNG").getImage());
		images.put("barrier yellow",new ImageIcon("barrier yellow.PNG").getImage());
		images.put("dirt1",new ImageIcon("dirt1.PNG").getImage());
		images.put("dirt2",new ImageIcon("dirt2.PNG").getImage());
		images.put("grass1",new ImageIcon("grass1.PNG").getImage());
		images.put("grass2",new ImageIcon("grass2.PNG").getImage());
	} 	
	 	
	

	public FP(MainGame game, int period)throws IOException{
		super();
		
		requestFocus(); //for the key stuff
		setFocusable(true);
		addKeyListener(this);
		addMouseListener(this);		
		addMouseMotionListener(this);
		loadImages();	
		top = game;
		this.period = period;
		setPreferredSize (new Dimension (s_width,s_height));
		setBackground(Color.black);


		
	}


    public void mouseDragged(MouseEvent e) {
    	
     tx = e.getX();
    ty = e.getY();	  	
	if(mb2Down){
		
		rx = e.getX();
		ry = e.getY();

    }
    }
    
    public void mouseMoved(MouseEvent e) {
    	
    tx = e.getX();
    ty = e.getY();	        
	if(mb2Down){

		rx = e.getX();
		ry = e.getY();
    }
    
    
    }


    public void mouseReleased(MouseEvent e) {
    	if (e.getButton()==1){
    		
		mbDown = false;

    	}
    	
    	if (e.getButton()==3){
    		mb2Down = false;
/*    		System.out.println("relased time to raw yo"); //draw things here ==> make a function
    		
			if (mx<rx && my<ry){
				g.drawRect(mx,my,width,height);
			}
			if (mx>rx && my<ry){
				g.drawRect(rx,ry-height,width,height);
			}
			if (mx>rx && my>ry){
				g.drawRect(rx,ry,width,height);
			}
			if (mx<rx && my>ry){
				g.drawRect(mx,my-height,width,height);
			} */
			
			
    		rx=-1;
			ry= -1;	
    	}
    	
    	
    }
    
    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e){} 


    public void mouseClicked(MouseEvent e) {}
        
    public void mousePressed(MouseEvent e) {
    	if (e.getButton()==1){
    		mbDown = true;
    	}
    	
      	if (e.getButton()==3){
    		mb2Down = true;
    	}
    	  	
    	mx = e.getX();
    	my = e.getY();
    }	
	
	public void move(){
		if (keys[37]==true){ //left

		}
		if (keys[39]==true){ //right 

			
		}
		if (keys[38]==true){ //up

			
		}
		if (keys[40]==true){ //down
	
			
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
	
	public void run(){
		
		long lastUpdate, sleepLen;
		lastUpdate = System.currentTimeMillis ();

		

		while (true){
			//move(); //move the kart 
			//System.out.println(mb2Down);
		//	System.out.println(mx + " "+my);
			render(); //draw stuff to offscreen buffer
			paintScreen(); //paint the screen
	    	sleepLen = lastUpdate + period - System.currentTimeMillis ();
	    	sleepLen = Math.max (5, sleepLen);
	    	delay (sleepLen);
	    	lastUpdate = System.currentTimeMillis ();
	    //	System.out.println(lastUpdate);

			
			
		}
	}
	

	public void drawRectangle(int mx,int my, int rx, int ry,Graphics g){
		int width = Math.abs((mx - rx));
		int height = Math.abs((my - ry));
		
		if (mb2Down==true){
			if (mx<rx && my<ry){
				g.drawRect(mx,my,width,height);
			}
			if (mx>rx && my<ry){
				g.drawRect(rx,ry-height,width,height);
			}
			if (mx>rx && my>ry){
				g.drawRect(rx,ry,width,height);
			}
			if (mx<rx && my>ry){
				g.drawRect(mx,my-height,width,height);
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
	    
	    dbg.setColor(Color.gray);
	    drawGrid(dbg); //draws grid lines 
	    drawScene(dbg); //draws the stuff you put on there
	    
	    dbg.setColor(Color.blue);
	    drawImage(dbg);
	    drawRectangle(mx,my,rx,ry,dbg);


	    

	}		
	
	public void drawScene(Graphics g){
		for (int i=0;i<128;i++){
			for (int j=0;j<128;j++){
				if (terrain_grid[i][j]!=null){
					String type = terrain_grid[i][j];
					g.drawImage(images.get(type),j*gridratio,i*gridratio,null);
				}
			}
		}
	}
	
	
	public void drawImage(Graphics g){
		
		if (mbDown==false){
			g.drawImage(images.get(selectedImage),mx,my,null);
		}
		if (mbDown==true){ //draw to that location
		//	System.out.println(tx+" "+ty);
			int closeX=tx/gridratio;
			int closeY = ty/gridratio;
		//	System.out.println(closeX+" "+closeY);
			int decre;
			int incre;
			
			
			
			
			if (size.equals("medium")){
				
				decre = -2;
				incre = 2;
				
				for (int i=closeX+decre;i<closeX+incre;i++){
					for (int j=closeY+decre;j<closeY+incre;j++){
						if (j>=0 && i>=0 && j<128 && i<128)
						terrain_grid[j][i]=selectedImage;
					}
				} 				
			}
			
			else if (size.equals("small")){
				terrain_grid[closeY][closeX]=selectedImage;
			}
			
			else if (size.equals("large")){
				decre = -4;
				incre = 4;
				
				for (int i=closeX+decre;i<closeX+incre;i++){
					for (int j=closeY+decre;j<closeY+incre;j++){
						if (j>=0 && i>=0 && j<128 && i<128)
						terrain_grid[j][i]=selectedImage;
					}
				} 					
			}
			
			
		}
		
	}
		
		
	public void pictureTest(Graphics g){ //takes in a given map and writes the 2d array to a datafile 
		BufferedImage buffer = new BufferedImage(1024,1024,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = buffer.createGraphics();
		g2d.drawImage(picture,0,0,null);
		
		
	//	System.out.println(buffer.getRGB(96,0));
		
	/*	ArrayList<Integer> pixels = new ArrayList<Integer>();

		for (int i = 0;i<1024;i+=8){
			
			int value = buffer.getRGB(i,16);
			if (pixels.contains(value)==false){
				pixels.add(value);
				System.out.println(value);
			}
		}	
		System.out.println(pixels); */
		
		
		try{ //try/catch when writing to file 
			

			
			PrintWriter outfile=new PrintWriter(new BufferedWriter(new FileWriter("outfile"+count+".txt")));
		
		
		
		for (int i=0;i<1024;i+=gridratio){
			String line = "";
			for (int j=0;j<1024;j+=gridratio){
				int pixel = buffer.getRGB(j,i);
				
			//	System.out.println(i+" "+j);
				
				if (pixel==-505784 || pixel==-10422176 || pixel ==-460656 || pixel==-9934600){ //barrier 
					line+="b";
				}
				else if (pixel==-16754688 || pixel==-16734208){ // grass 
					line+="g";
				}
				
				else if (pixel==-10465240 || pixel==-10422176){ //dirt
					line+="d";
				}
				
				else if (pixel==-6254496){ //dirt
					line+="d";
				}
				
				else{ //road
					line+="r";
				}
			//	System.out.println(line);
				

				
			}

			outfile.println(line); //new line
			

		}

		outfile.close();
		count++;
		
		}
		
		
		catch(Exception ex){
			System.out.println("ur gay");
		}	 
		
		g.drawImage(buffer,0,0,null);
	}	
		
	public void drawGrid(Graphics g){
		for (int i=0;i<=s_width;i+=gridratio){
			g.drawLine(i,0,i,s_height);
			g.drawLine(0,i,s_width,i);
		}
		
		
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


}

