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
	public Player viewer;
	
	
	public int toGrid(int coord){
		return coord/gridratio;
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
	
	public boolean fouthquad(double angle){ //fourth quad
		return angle>=270;
	}
	
	public double distance(int x1,int y1,int x2,int y2){
		double deltax = (x1-x2);
		double deltay = (y1-y2);
		double dist = Math.sqrt(deltax*deltax+deltay*deltay);
		return dist;
	}
	
	
	public void rayCast(Graphics g){ //cast out them rays ;)
	
		//CONSIDER THE SIGN OF THE TAN VALUE GIVEN
	
		double player_distance =  ((s_width)/2)/Math.tan(Math.toRadians(player.FOV));  //distance from player to plane
		double angle_increment = player.FOV/s_width; //the increment of the angles
		double start_angle = (player.angle-(player.FOV/2)-angle_increment+360)%360; //the starting angle within 0-360
		
		for (int i=0;i<s_width;i++){ //loop for each column
			start_angle+=angle_increment; //increase angle
			start_angle=(start_angle+360)%360; //make sure its an angle within 0-360
			
			int min_distance;
			double startx=player.xpos;
			double starty=player.ypos;
			
			double xincrement = gridratio/Math.tan(Math.toRadians(start_angle)); //x increment
			double yincrement = gridratio*Math.tan(Math.toRadians(start_angle)); //y increment
			
						
			int gridy; 
			int gridx;
			int count = 1; //number of times to loop
			
			double tempx;
			double tempy;
			
			
			
			if (firstquad(angle) || secondquad(angle)){ //for horizontal checks...facing up.....SHOULD BE INTEGER
				temp_y=(int)(starty/gridratio)*gridratio-1;
			}
			if (thirdquad(angle) || fourthquad(angle)){
				temp_y=(int)(starty/gridratio)*gridratio+gridratio; //for horizontal checks....facing down...SHOULD BE INTEGER
			}
			
			if (firstquad(angle)){ //for finding the first Xa value
				tempx=startx+(starty-tempy)/Math.tan(Math.toRadians(start_angle));}
			
			if (secondquad(angle)){
				tempx=startx-(starty-tempy)/Math.tan(Math.toRadians(start_angle));
			}
			
			if (thirdquad(angle)){
				tempx=startx-(tempy-starty)/Math.tan(Math.toRadians(start_angle));
			}
			
			if (fourthquad(angle)){
				tempx=startx+(tempy-starty)/Math.tan(Math.toRadians(start_angle));
			}
			
			


			
			
			while (true){              //check for horizontal collisions
				
				gridx=toGrid(tempx);
				gridy=toGrid(tempy);
				
				if (map[gridy][gridx]==1){ //if there is a wall at that grid point
					double distance=distance(tempx,tempy,startx,starty);
					double distance=distance(tempx,tempy,startx,starty);
					double real_dist=distnance*Math.cos(Math.toRadians(startangle-player.angle));
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
				if (fouthquad(angle)){
					tempx+=xincrement;
					tempy+=gridratio;
				
				}
				
				
				
			}
			

			
			if (firstquad(angle) || fourthquad(angle)){ //...//SHOULD BE INTEGER...make abs value?
				tempx=(int)(startx/64)*64+64;

			}
			if (secondquad(angle) || thirdquad(angle)){
				tempx=(int)(startx/64)*64-1;
			}
			
			if (firstquad(angle)){
				tempy=starty+(tempx-startx)*Math.tan(Math.toRadians(start_angle));
			}
			
			if (secondquad(angle)){
				tempy=starty+(startx-tempx)*Math.tan(Math.toRadians(start_angle));
			}
			
			if (thridquad(angle)){
				tempy=starty+(startx-tempx)*Math.tan(Math.toRadians(start_angle));
			}
			
			if (fourthquad(angle)){
				tempy=starty+(tempx-startx)*Math.tan(Math.toRadians(start_angle));;
			}
			
			
			
			
			while (true){				//check for veritical collisions
			
				if (map[gridy][gridx]==1){ //if there is a wall at that grid point
					double distance=distance(tempx,tempy,startx,starty);
					double real_dist=distance*Math.cos(Math.toRadians(startangle-player.angle));
					double projected_height = gridratio/real_dist*player_distance;
					if (projected_height<min_distance){ //see if its better
						min_distance=projected_hieght;
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
			
			int startcoord=s_height*.5-(int)(.5*min_distance);
			g.drawLine(i,startcoord,i,startcoord+min_distance); //draw that col
			
						
		}
		
	}
	
	
	public void run(){ //main game stuff
		
		Player player=new Player();
		
		//map set up
		map=new int[9][7];
		for (int y=0;y<9;y++){
			for (int x=0;x<7;x++){
				map[y][x]=0;
			}
		}
		
		for (int x=0;x<7;x++){
			map[0][x]=1;
			map[8][x]=1;
			map[4][x]=1;
		}
		
		for (int y=0;y<9;y++){
			map[y][0]=1;
			map[y][3]=1;
			
		}
		
		//done map setup
		
		
		
		while (true){
			//move kart
			
			
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
		xpos=64*5+32;
		ypos=64+64*.5;
		velocity=0;
		height=32;
		angle=45;
		FOV=60;
//		frames=new 
		
	}
}


class Camera{
	private int angle;
	public Camera(){
		//your code here =P
	}
	
}