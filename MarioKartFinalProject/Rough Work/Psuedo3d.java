//Testing of Perspective and Psuedo 3d graphics

//Orthrographic projection
// Bx = (Sx)(Ax) + (Cx)
// By = (Sz)(Az) + (Cz)

//ALWAYS CONVERT TO RADIANS


import java.applet.*; //Import all the stuff we need...
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;             
import java.awt.event.*;
import java.awt.font.*;
import java.applet.AudioClip;


public class Psuedo3d extends Applet implements Runnable{
	Thread gamethread; //thread
	private Image dbImage; //double buffering image
	private Graphics dbg; //db graphics context
	private boolean[] keys=new boolean[2000]; //boolean array for keys
	private int[][] map; //2d map...each grid square is 64x64 pixels...
	private int gridratio=64; //means that each square on the grid is 64 pixels/units
	private int s_height=200; //height and width of screen
	private int s_width=320;
	//0 = no walls
	//1 = walls
	private int wall_height=64;
	private Player player;
	
	
	public int toGrid(double coord){
		return (int)(coord/gridratio);
	}
	
	public boolean firstquad(double angle){ //returns if the angle is in teh first quad 
		return angle>=0 && angle<90;
		
	}
	
	public boolean secondquad(double angle){ //second quad 
		return angle>=90 && angle<180;
	}
	
	public boolean thirdquad(double angle){ //third quad
		return angle>=180 && angle<270;
	}
	
	public boolean fourthquad(double angle){ //fourth quad
		return angle>=270;
	}
	
	public double distance(double x1,double y1,double x2,double y2){
		double deltax = (x1-x2);
		double deltay = (y1-y2);
		double dist = Math.sqrt(deltax*deltax+deltay*deltay);
		return dist;
	}
	
	public void move(){
		if (keys[1006]==true){ //left
			player.rotateLeft();
		}
		if (keys[1007]==true){ //right
			player.rotateRight();
		}
		if (keys[1004]==true){
			player.moveForward();
		}
		if (keys[1005]==true){
			player.moveBackward();
		}
			
			
	}
	
	
	public void rayCast(Graphics g){ //cast out them rays ;)
	
		//CONSIDER THE SIGN OF THE TAN VALUE GIVEN
		//REMEMBER THAT THE AXIS IS REVERSED IN THE Y DIRECTION

		double player_distance = Math.abs(((s_width)/2)/Math.tan(Math.toRadians(player.FOV/2)));  //distance from player to plane
		double angle_increment = player.FOV/s_width; //the increment of the angles
		double start_angle = (player.angle-(player.FOV/2)-angle_increment+360)%360; //the starting angle within 0-360
		
		double angle;
		
	//	System.out.println("Staring angle is "+start_angle);
	//	System.out.println("Player distance is "+player_distance);

		
		for (int i=0;i<s_width;i++){ //loop for each column
		
			
		//	System.out.println("Column is "+i);

		
			start_angle+=angle_increment; //increase angle
			start_angle=(start_angle+360)%360; //make sure its an angle within 0-360
			angle=start_angle; //same thing just bad naming
		//	System.out.println("Angle is "+angle);
			
			double min_distance=-1;
			double startx=player.xpos;
			double starty=player.ypos;
			
			double xincrement = Math.abs(gridratio/Math.tan(Math.toRadians(start_angle))); //x increment
			double yincrement = Math.abs(gridratio*Math.tan(Math.toRadians(start_angle))); //y increment
			
						
			int gridy=99999; 
			int gridx=99999;

			
			double tempx=9999;
			double tempy=9999;
			
//===================================================HORIZONTAL COLLISIONS======================================
			
			
			if (firstquad(angle) || secondquad(angle)){ //for horizontal checks...facing up...(ycoordinate)
				tempy=(int)(starty/gridratio)*gridratio-1;
			}
			
			if (thirdquad(angle) || fourthquad(angle)){
				tempy=(int)(starty/gridratio)*gridratio+gridratio; //for horizontal checks....facing down...(ycoordnate)
			}
			
			if (firstquad(angle) || fourthquad(angle)){ //for finding value of first coordinate (X)
				tempx=startx+Math.abs((starty-tempy)/(Math.tan(Math.toRadians(start_angle))));
				}
			
			if (secondquad(angle) || thirdquad(angle)){ //for finding value of first coordinate (X)
				tempx=startx-Math.abs((starty-tempy)/(Math.tan(Math.toRadians(start_angle))));
			}
			
		//	System.out.println("temp x is "+tempx+" grid x coord is: "+gridx);
		//	System.out.println("temp y is "+tempy+" grid y coord is: "+gridy);			
			
			gridx=toGrid(tempx);
			gridy=toGrid(tempy);	
						
			
			
			while (true){              //check for horizontal collisions
				

				gridx=toGrid(tempx);
				gridy=toGrid(tempy);

				if (gridx<0 || gridx>3 || gridy<0 || gridy>2){ //out of range
					break;
				}				
				
			//	System.out.println("temp x is "+tempx+" grid x coord is: "+gridx);
			//	System.out.println("temp y is "+tempy+" grid y coord is: "+gridy);				
				
				if (map[gridy][gridx]==1){ //if there is a wall at that grid point
					double fake_distance=distance(tempx,tempy,startx,starty);
					double real_dist=fake_distance*Math.abs(Math.cos(Math.toRadians(start_angle-player.angle)));;
					double projected_height = gridratio/real_dist*player_distance;					
					min_distance=projected_height;
					break;
				}
				
				if (firstquad(angle)){
					tempx+=xincrement;
					tempy-=gridratio;

					
				}
				if (secondquad(angle)){
					tempx-=xincrement;
					tempy-=gridratio;

					
				}
				if (thirdquad(angle)){
					tempx-=xincrement;
					tempy+=gridratio;

				}
				if (fourthquad(angle)){
					tempx+=xincrement;
					tempy+=gridratio;
				
				}
				
				
				
			}

//===================================================VERTICAL COLLISIONS======================================
		//	System.out.println("Starting veritical collisions....");


			if (firstquad(angle) || fourthquad(angle)){ //...//SHOULD BE INTEGER...make abs value?
				tempx=(int)(startx/gridratio)*gridratio+gridratio;

			}
			if (secondquad(angle) || thirdquad(angle)){
				tempx=(int)(startx/64)*64-1;
			}
			
			if (firstquad(angle) || secondquad(angle)){
				tempy=starty-Math.abs((tempx-startx)*Math.tan(Math.toRadians(start_angle)));
			}
			
			if (thirdquad(angle) || fourthquad(angle)){
				tempy=starty+Math.abs((tempx-startx)*Math.tan(Math.toRadians(start_angle)));
			}
			
			
			gridx=toGrid(tempx);
			gridy=toGrid(tempy);	
						
		//	System.out.println("temp x is "+tempx+" grid x coord is: "+gridx);
		//	System.out.println("temp y is "+tempy+" grid y coord is: "+gridy);			
			
			
			while (true){				//check for veritical collisions

			//	System.out.println("temp x is "+tempx+" grid x coord is: "+gridx);
			//	System.out.println("temp y is "+tempy+" grid y coord is: "+gridy);
				
				gridx=toGrid(tempx);
				gridy=toGrid(tempy);
				
				if (gridx<0 || gridx>3 || gridy<0 || gridy>2){ //out of range
					break;
				}
								
				//System.out.println("Looping2");
				if (map[gridy][gridx]==1){ //if there is a wall at that grid point
					double fake_distance=distance(tempx,tempy,startx,starty);
					double real_dist=fake_distance*Math.abs(Math.cos(Math.toRadians(start_angle-player.angle)));
					double projected_height = gridratio/real_dist*player_distance;
					if (projected_height<min_distance){ //see if its better
						min_distance=projected_height;
					}
					break;
				}
				if (firstquad(angle)){
					tempx+=gridratio;
					tempy-=yincrement;
				}
				if (secondquad(angle)){
					tempx-=gridratio;
					tempy-=yincrement;
				}
				if (thirdquad(angle)){
					tempx-=gridratio;
					tempy+=yincrement;	
				}
				if (fourthquad(angle)){
					tempx+=gridratio;
					tempy+=yincrement;	
				}


			}
			
			int startcoord=(int)(s_height*.5-(int)(.5*min_distance));
			int min = (int)(min_distance);
			g.drawLine(i,startcoord,i,startcoord+min); //draw that col
			
						
		}
		
	}
	
	
	public void run(){ //main game stuff
		

		
		
		
		//done map setup
		
		
		
		while (true){
			//move kart
			
			move();
			repaint();
			delay(20);
		}
		
		
	}
	
	public boolean keyDown(Event e,int x){	//true means we have fully handled the event 
		keys[x]=true;
		return true; 
		
	}
	
	public boolean  keyUp(Event e,int x){ //method overriding from applet
		keys[x]=false;
		return true; 
	}

	public void update(Graphics g){
		
		if (dbImage==null){ //if the image is not made yet
			dbImage=createImage(getWidth(),getHeight());
			dbg=dbImage.getGraphics();	//get graphics context		
		}
		paint(dbg); //paint the screen to an offscreen image
		g.drawImage(dbImage,0,0,this); //"blit" that image onto the real screen
		
		
	}
	
	
	public void paint(Graphics g){
		g.setColor(Color.black);
		g.fillRect(0,0,320,200);
		g.setColor(Color.blue);
		//g.fillRect(300,400,100,100);
		rayCast(g);
		
		
	}	


	public void start(){

		 player=new Player();
/*		 Scanner datafile = new Scanner(new File("datafile.txt"));
		 
		 int rows = datafile.nextInt();
		 int cols = datafile.nextInt();
		 
		 map = new int[rows][cols];
		 
		 for (int i=0;i<rows:i++){
		 	String line = datafile.nextLine();
		 	char[] whatever = line.tocharArray();
		 	for (int j=0;j<cols;j++){
		 		map[i][j]=(int)(whatever[j]);
		 	}
		 } */
		//map set up
		
		
		map=new int[3][4];
		
		for (int j=0;j<3;j++){
			for (int i=0;i<4;i++){
				map[j][i]=0;
			}
		}
		
		//make walls
		for (int i=0;i<4;i++){
			map[0][i]=1;
		}
		
		map[1][3]=1;
		map[2][3]=1;		

		gamethread=new Thread(this);
		gamethread.start();

	}
	
    public static void delay (long len) //method to delay a thread
    {
		try
		{
		    Thread.sleep (len);
		}
		catch (InterruptedException ex)
		{
		    System.out.println("I hate when my sleep is iterrupted");
		}
    }		
			
		
}


class Player{
	public double xpos;
	public double ypos;
	public int velocity;
	public double angle;
	public int height;
	public double FOV; //field of view, in degrees
	
	public Player(){
		xpos=96;
		ypos=160;
		velocity=0;
		height=32;
		angle=45;
		FOV=60;
//		frames=new 
		
	}
	
	public void rotateLeft(){
		angle+=5;
		
	}
	
	public void rotateRight(){
		angle-=5;
	}
	
	public void moveForward(){
		double xdirection=5*Math.cos(Math.toRadians(angle));
		double ydirection=5*Math.sin(Math.toRadians(angle));
		xpos+=xdirection;
		ypos+=ydirection;
	}
	
	public void moveBackward(){
		double xdirection=5*Math.cos(Math.toRadians(angle));
		double ydirection=5*Math.sin(Math.toRadians(angle));
		xpos-=xdirection;
		ypos-=ydirection;
		
		
	}
}

