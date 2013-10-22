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

//Red Shell Class

public class RedShell {
	
	
	private double x_leftpoint;
	private double x_rightpoint;
	private double x_forwardpoint;
	private double y_leftpoint;
	private double y_rightpoint;
	private double y_forwardpoint;



	double angle_dev = 20; 
	double point_dist = 55;
	double fpoint_dist = 55;
	private String last_turn = "right";
	
	
	private double velocity = 15;
	private ImageIcon[] pics; 
	private double angle;
	private double xpos;
	private double ypos;
	private int width = 8;
	private int height = 8;
	private FP.Kart target; //target of the red shell 
	private int lifeSpan;
	char[][] terrain_grid;
	char[][] ai_terrain_grid;
	private double ai_handling;
	
	public double dist(double x1, double y1, double x2, double y2){
		
		double temp = (x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2);
		return Math.pow(temp,.5);
	}


	public boolean isClose2(){
		double distance = dist(xpos,ypos,target.getX(),target.getY());

		return (distance<50);
	}	
	
	public boolean isClose(){
		double distance = dist(xpos,ypos,target.getX(),target.getY());
		if (distance<50){
			System.out.println("close to target");
			double delta_y = ypos - target.getY();
			double delta_x = xpos - target.getX();
			double ang = Math.atan2(delta_y,delta_x);
			angle = ang;
		//	checkCollision();
		//	System.out.println(target.getName());
		}
		else{
			System.out.println("not close to target");
		}
		
		
		return (distance<50);
	}
	
	
	public void checkCollision(){
		Rectangle player = new Rectangle(round(target.getX()),round(target.getY()),(int)target.getWidth(),(int)target.getHeight());
		Rectangle shell = new Rectangle(round(xpos),round(ypos),width,height);
		if (shell.intersects(player)){
			System.out.println("Intersected");
			target.setStunned();
			lifeSpan = 0; //finished 
		}
	}
		
    public RedShell(double x, double y, double angle, char[][] terrain, char[][] ai_terrain, FP.Kart target,double ai_stat ) {
    	
    	xpos = x;
    	ypos = y;
		
		
		System.out.println(target.getName());
    	this.angle = angle; //don't do this 
    	ai_handling = ai_stat;  //handling on turns 
    	lifeSpan = 200; 
    	terrain_grid = terrain;
		ai_terrain_grid = ai_terrain;
    	pics = new ImageIcon[1];    	
    	pics[0] = new ImageIcon("redshell.PNG");
    	this.target = target;

    	
    }
    
 	public boolean allBorderCheck(){
		return (pointBorderCheck(xpos+4,ypos+4) || pointBorderCheck(xpos+4,ypos-4) || pointBorderCheck(xpos-4,ypos-4) || pointBorderCheck(xpos-4,ypos+4)); 
		
	}  
		
	public boolean pointBorderCheck(double px,double py){ //checks to see if this specific point lies on a border
		
		int newx = round(px);
		int newy = round(py);
		
		int gridx = newx/8; //divide by grid ratio
		int gridy = newy/8;
		
		return terrain_grid[gridy][gridx]=='b';
		

	}   


    public void move(){
		
		isClose(); //update angle if close 

    	x_leftpoint = xpos + point_dist*Math.cos(Math.toRadians(angle + angle_dev));
    	y_leftpoint = ypos + point_dist*Math.sin(Math.toRadians(angle + angle_dev))*-1;
    	
     	x_rightpoint = xpos + point_dist*Math.cos(Math.toRadians(angle - angle_dev));
    	y_rightpoint = ypos + point_dist*Math.sin(Math.toRadians(angle - angle_dev))*-1;   	
    	
 
     	x_forwardpoint = xpos + fpoint_dist*Math.cos(Math.toRadians(angle));
    	y_forwardpoint = ypos + fpoint_dist*Math.sin(Math.toRadians(angle))*-1;   

		boolean forward = AIpointDirtCheck(x_forwardpoint,y_forwardpoint);
		boolean left = AIpointDirtCheck(x_leftpoint,y_leftpoint);
		boolean right = AIpointDirtCheck(x_rightpoint,y_rightpoint);


    	if (forward){
    		System.out.println("Forward on Dirt");
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

    	
    	if (left ){
    		System.out.println("left on dirt");
    		angle-=ai_handling;
    		//velocity*=.9;
    		last_turn = "left";
    		return; 
    	}
     	if (right){
     		System.out.println("right on dirt");
    		angle+=ai_handling;
    		//velocity*=.9;
    		last_turn = "right";
    		return;     		
    	}
    	
		
		moveAlong();
    	lifeSpan--; //decrease lifeSpan
    	
    }

    public boolean isAvailable(){ //true if you can use it...false if otherwise
    	return lifeSpan>0;
    }


    
 	public void moveAlong(){ //check for each point 
		boolean stopflag = false;
		for (int v=0;v<velocity;v++){ //loop as many "velocities" as he has...
			if (velocity>0){ //moving forward
				xpos+=1*Math.cos(Math.toRadians(angle));
				ypos+=1*Math.sin(Math.toRadians(angle))*-1;					
			}
			if (isClose2()){ //check in loop
				checkCollision();
				if (lifeSpan==0){
					break;
				}
			}
			
			if (allBorderCheck()==true || offCourseCheck()==true || AIoffRoadCheck()==true){
				lifeSpan = 0; //this the shells hit a border or is on grass ors omething bad
				break;
			}			

		}		
	}  


	public boolean AIoffRoadCheck(){

		int newx = round(xpos);
		int newy = round(ypos);		

		int gridx = newx/8; //divide by grid ratio
		int gridy = newy/8;
		
		return ai_terrain_grid[gridy][gridx]=='d';
		
	}

	public boolean offCourseCheck(){ //snow
		
		int newx = round(xpos);
		int newy = round(ypos);		

		int gridx = newx/8; //divide by grid ratio
		int gridy = newy/8;
		
		return (terrain_grid[gridy][gridx]=='x' ||  terrain_grid[gridy][gridx]=='w');		
	}	
	

	public boolean AIpointDirtCheck(double px,double py){
		int newx = (int)px;
		int newy = (int)py;
		
		int gridx = newx/8; //divide by grid ratio
		int gridy = newy/8;
		
		
		if (gridx <= 0  || gridy<= 0 || gridx > 128 || gridy>128){
			return true;
		}
		
		return (ai_terrain_grid[gridy][gridx]=='d' || ai_terrain_grid[gridy][gridx]=='s' || ai_terrain_grid[gridy][gridx]=='x' ) ;		
	}
	
	
    public double getWidth(){
    	return this.width;
    }
    public double getHeight(){
    	return this.height;
    } 
    	
    public ImageIcon getPic(int currentY){


    	return pics[0];
    	
    }
    
 	public int round(double d){
		double n = d+.5;
		return (int)n;
		
	}	      
 
    public double getX(){
    	return this.xpos;
    }
    
    public double getY(){
    	return this.ypos;
    } 
           			   
  
}