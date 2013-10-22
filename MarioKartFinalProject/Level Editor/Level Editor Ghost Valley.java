
//FP game class



//=======================MOOSEY-O KART FINAL PROJECT====================================


//Level Editor


//Problems...


//only toggle through maps on the keyup
//b/c you don't want it to get pressed more than once




//==========================================================


/*

Pixel Value List


Grass (light)= -16754688
Grass (dark) = -16734208
Dirt (normal)= -10465240
Dirt (shadow) = -10422176
Dirt (??) = -6254496
Barrier - Red = -505784
Barrier - Green = -10422176
Barrier - Blue = -9934600
Barrier - Yellow = -460656





*/
 



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



//		try{
//		    ImageIO.write( copy, "PNG" /* format desired */ , new File( "save.png" ) /* target */ );
	//	}
	//	catch(Exception ex){} 
		

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
	 
	 private int imageIndex = 0; //index of selected image
	 private String selectedImage = "barrier blue";
	 private String size = "large"; //size of how to draw it 	
	 
	 private HashMap<String,Image> maps = new HashMap<String,Image>();	
	 	
	 
	public void loadMaps(){
		try{
			Scanner infile = new Scanner(new File("Map Names.txt"));
			while (infile.hasNextLine()){
				String line = infile.nextLine();
				String icon_name = line + ".png";
				Image picture = new ImageIcon(icon_name).getImage();
				maps.put(line,picture);
			}
		}
		
		catch (Exception CSishard){
			System.out.println(CSishard);
		}
	} 
	 	
	 	
	 	
	public void loadImages(){
		images.put("barrier blue",new ImageIcon("barrier blue.PNG").getImage());
		images.put("barrier green",new ImageIcon("barrier green.PNG").getImage());
		images.put("barrier red",new ImageIcon("barrier red.PNG").getImage());
		images.put("barrier yellow",new ImageIcon("barrier yellow.PNG").getImage());
		images.put("dirt1",new ImageIcon("dirt1.PNG").getImage());
		images.put("dirt2",new ImageIcon("dirt2.PNG").getImage());
		images.put("grass1",new ImageIcon("grass1.PNG").getImage());
		images.put("grass2",new ImageIcon("grass2.PNG").getImage());
		images.put("lightroad",new ImageIcon("lightroad.PNG").getImage());
		images.put("road1",new ImageIcon("road1.PNG").getImage());
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
			rx = e.getX();
			ry = e.getY();    		
    		
    	}
    	  	
    	mx = e.getX();
    	my = e.getY();
    }	
	
	
	public void setNewImage(int value){
		System.out.println("here");
		if (imageIndex+value>=0 && imageIndex+value<images.keySet().size()){ //if its valid
			imageIndex+=value;
			Object[] temp = images.keySet().toArray();
			System.out.println("change");
			selectedImage = temp[imageIndex].toString();
		}
		if (imageIndex+value<0){
			imageIndex=images.keySet().size()-1;
			Object[] temp = images.keySet().toArray();
			System.out.println("change");
			selectedImage = temp[imageIndex].toString();			
		}
		if (imageIndex+value>=images.keySet().size()){
			imageIndex=0;
			Object[] temp = images.keySet().toArray();
			System.out.println("change");
			selectedImage = temp[imageIndex].toString();			
		}		
		
		
		
	}
	
	public void checkInput(){
		
		if (keys[37]==true){ //left
			setNewImage(-1);
			
			
		}
		if (keys[39]==true){ //right 
			setNewImage(1);
			
		}
		
		if (keys[83]==true){
			size = "small";
		}

		if (keys[76]==true){
			size = "large";
		}				
				
		if (keys[77]==true){
			size = "medium";
		}
		
		if (keys[10]==true){ //save
			save("outfile0");
		}
		
		if (keys[30]==true){
			
		}		
		
	}
	
	
	public void load(String filename){ //load in the 2d array from a file
		
		
		try{
			
		
		Scanner infile = new Scanner(new File(filename+".txt"));
		
		for (int i=0;i<128;i++){
			String line = infile.nextLine();
			for (int j=0;j<128;j++){
				char whatever = line.charAt(j);
				
				if (whatever=='a'){
					terrain_grid[i][j] = "barrier red";
				}
				if (whatever=='b'){
					terrain_grid[i][j]= "barrier green" ;
				}
				if (whatever == 'c'){
					terrain_grid[i][j] = "barrier yellow";
				}
				if (whatever == 'd'){
					terrain_grid[i][j] = "barrier blue";
				}
				if (whatever == 'e'){
					terrain_grid[i][j] = "grass1";
				}				
				
				if (whatever == 'f'){
					terrain_grid[i][j] = "grass2";
				}
				if (whatever == 'g'){
					terrain_grid[i][j] = null; //nothing?
				}				
				if (whatever == 'h'){
					terrain_grid[i][j] = "dirt1";
				}
				if (whatever == 'i'){
					terrain_grid[i][j] = "dirt2";
				}

				if (whatever == 'r'){
					terrain_grid[i][j] = "road1";
				}					
								
								
			}
		}

		}
		
		
		catch(Exception Ex){ 
			System.out.println("file "+filename+" not found");
		}
		
}
		
		
	
	
	public void save(String source){ //writes the data to a file 
		
		
		try{
			PrintWriter outfile=new PrintWriter(new BufferedWriter(new FileWriter(source+".txt")));
			for (int i=0;i<128;i++){
				String line  = "";
				for (int j=0;j<128;j++){
					String letter = terrain_grid[i][j].substring(0,1);
					line+=letter;
				}
				outfile.println(line);
			}
			outfile.close();
			
		}
		
		
		catch(Exception e){
			System.out.println("can't write to file");
		}
		
		
		BufferedImage copy = new BufferedImage(1024,1024,BufferedImage.TYPE_INT_ARGB);
		Graphics temp_g = copy.getGraphics();
		drawScene(temp_g);
		
		
		try{
		    ImageIO.write( copy, "PNG" /* format desired */ , new File( "save.png" ) /* target */ );
		}
		catch(Exception ex){} 
			
			
	}
	
    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    	System.out.println(e.getKeyCode());
        keys[e.getKeyCode()] = true;

    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }	
	
	public void run(){
		
		long lastUpdate, sleepLen;
		lastUpdate = System.currentTimeMillis ();
		loadMaps();

		for (int i=0;i<128;i++){ //fill the grid with emptiness
			for (int j=0;j<128;j++){
				terrain_grid[i][j]="n";
			}
		}		
		
		//load FILE
		
		load("outfile0");
		pictureTerrainTest("Ghost_Valley");

		
		
		while (true){

			checkInput();
		
			render(); //draw stuff to offscreen buffer
		//	dbg.setColor(Color.pink);
		//	dbg.drawImage(maps.get("Ghost_Valley"),0,0,null);
		
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
	    
	   // pictureTest(dbg);
	    
	    
	    dbg.setColor(Color.gray);
	    drawGrid(dbg); //draws grid lines 
	    drawScene(dbg); //draws the stuff you put on there
	    floodFillCheck();
	    dbg.setColor(Color.blue);
	    drawImage(dbg);
	  //  drawRectangle(mx,my,rx,ry,dbg);


	    

	}		
		
		
	public void floodFillCheck(){
		if (mb2Down==true){
			int closeX = rx/gridratio;
			int closeY = ry/gridratio;
			String changeFrom = terrain_grid[closeY][closeX]; //all the things to change to this
			String changeTo = selectedImage; //what to change the tiles to
			System.out.println(rx+" "+ry);
			System.out.println(closeX+" "+closeY);
			System.out.println(changeFrom);
			System.out.println(changeTo);
			
			boolean[][] temp = new boolean[128][128];
			
			
			
		
			floodFill(closeX,closeY,changeFrom,changeTo,temp);
		}
	}
	
	
	
	
	
	public void floodFill(int currentX,int currentY,String changeFrom,String changeTo,boolean[][] traversed){
	

		
		if (currentX<0 || currentY<0 || currentY>127|| currentX>127){ //out of bounds
			return; 
		}
		
		if (traversed[currentX][currentY]==true){ //if you've already been to that location you can't go back?
			return;
		}
		
		traversed[currentX][currentY]=true; 


		if (terrain_grid[currentY][currentX].equals(changeFrom)){ //if the spot in teh grid is the kind of tile you want to change
			
			terrain_grid[currentY][currentX] = changeTo; //change it
			
			//recurse
			
			floodFill(currentX+1,currentY,changeFrom,changeTo,traversed);
			floodFill(currentX-1,currentY,changeFrom,changeTo,traversed);
			floodFill(currentX,currentY+1,changeFrom,changeTo,traversed);
			floodFill(currentX,currentY-1,changeFrom,changeTo,traversed);

		}
		


	}	
	
	public void drawScene(Graphics g){
		for (int i=0;i<128;i++){
			for (int j=0;j<128;j++){
				if (terrain_grid[i][j].equals("nothing")==false){
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
		
		

		
		
		try{ //try/catch when writing to file 
			

			
			PrintWriter outfile=new PrintWriter(new BufferedWriter(new FileWriter("outfile"+count+".txt")));
		
		
	//	System.out.println(buffer.getRGB(16,16));
		
		
		for (int i=0;i<1024;i+=gridratio){
			String line = "";
			for (int j=0;j<1024;j+=gridratio){
				int pixel = buffer.getRGB(j,i);
				
			//	System.out.println(i+" "+j);
				
			//	System.out.println(pixel);
				
				if (pixel==-505784){ //red barrier 
					line+="a";
				//	System.out.println("here");
				}
				else if (pixel==-10422176){ //green barrier 
					line+="b";
				}				
				else if (pixel==-460656){  //yellow barrier 
					line+="c";
				}
				else if (pixel==-9934600	){ //blue barrier 
					line+="d";
				}
				else if (pixel==-16754688	){ //dark grass 
					line+="e";
				}
				else if (pixel==-16734208	){ // light grass 
					line+="f";
				}								
					
				else if (pixel==-10422176	){  //nothing?
					line+="g";
				}	

				else if (pixel==-10465240	){ //dark dirt (shadow)
					line+="h";
				}				

				else if (pixel==-6254496){ // light dirt (ground) 
					line+="i";
				}				

				else{ //road
					line+="r";
				}

				

				
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

	public void pictureTerrainTest( String filename){ //takes in a given map and writes the 2d array to a datafile 
	
		BufferedImage buffer = new BufferedImage(1024,1024,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = buffer.createGraphics();
		g2d.drawImage(maps.get(filename),0,0,null);
		
		int pvalue = buffer.getRGB(40,24);
		System.out.println(pvalue);
		pvalue = buffer.getRGB(0,0);
		System.out.println(pvalue);
		
		
		try{ //try/catch when writing to file 
			

			
		PrintWriter outfile=new PrintWriter(new BufferedWriter(new FileWriter("outfile"+count+".txt")));
		
		
		
		for (int i=0;i<1024;i+=gridratio){
			String line = "";
			for (int j=0;j<1024;j+=gridratio){
				int pixel = buffer.getRGB(j,i);
				
			//	System.out.println(i+" "+j);
				
				if (pixel==-505784 || pixel==-10422176 || pixel ==-460656 || pixel==-9934600 || pixel==-65536 || pixel==-5201808){ //barrier ==> including ice one in vanilla lake
					line+="b"; //and border in GV
				}
				else if (pixel==-16754688 || pixel==-16734208){ // grass 
					line+="g";
				}

				
				else if (pixel==-10465240 || pixel==-10422176 || pixel==-6254496){ //dirt
					line+="d";
				}
				
				else if (pixel == -1509128){ //snow
					line+="s";
				}
				
				else if (pixel==-12558088){
					line+="w";
				}
				
				else if (pixel==-16777216){
					line+="x"; //darkness ==> DEATH!
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
			System.out.println("mario kart is harddd");
		}	 
		

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

