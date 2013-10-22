//This is a class for the GreenShell

//Though it says greenshell, it is really used for red and yellow shells as well


//Import.....

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

public class GreenShell{
	
	private double velocity = 20; //velocity
	private ImageIcon[] pics; //frame pics
	private double angle; //angle
	private double xpos;
	private double ypos;
	private int width = 8; //width and height of shell 
	private int height = 8;
	private int currentframe; //current frame
	private String type; //type of shell - red, yellow, green ===> this dictates the velocity
	
	private int lifeSpan; //number of frames the shell is avaiable for...
	private char[][] terrain_grid; //used to detect collisions if the shell hits is a wall
	private Rectangle location; //used to check intersection
	
	
	public int round(double d){ //method I made to round a double
		double n = d+.5;
		return (int)n;
		
	}	
	
	public void loadImages(String filename){ //Load frame pics 
		String ext = ".png";
		for (int i = 0; i <pics.length;i++){
			String temp_name = filename + i + ext;
			ImageIcon temp = new ImageIcon(temp_name);
			pics[i] = new ImageIcon(temp_name);//.getImage();			
		}
		
	}

	
    public GreenShell(double x,double y, double angle,char[][] terrain,String type) { //Constructor
    	
    	xpos = x;
    	ypos = y;
    	this.angle = angle; //don't do this 
    	
    	String temp = null;
    	
    	//The velocity and pictures depends on the type of shell used....
    	
    	
    	if (type.equals("yellow")){ 
    		temp = "yellowshell";
    		pics = new ImageIcon[6];
    		velocity = 13;
    	}
    	
    	if (type.equals("red")){
    		temp = "redshell";
    		pics = new ImageIcon[6];
    		velocity = 30;
    	}
    	
    	if (type.equals("green")){
    		temp = "shell";
    		pics = new ImageIcon[12];
    		velocity = 20;
    	}
    	
    	loadImages(temp);
    //	velocity = 15; 
    	lifeSpan = 100; 
    	terrain_grid = terrain;
    	currentframe = 0; 	
   
    }
   
	public void moveAlong(){ //Check Collisions against a wall...explained more in FP class
		boolean stopflag = false;
		for (int v=0;v<velocity;v++){ //loop as many "velocities" as he has...
			if (velocity>0){ //moving forward
				xpos+=1*Math.cos(Math.toRadians(angle));
				ypos+=1*Math.sin(Math.toRadians(angle))*-1;					
			}			
			
		
		 	stopflag = allBorderCheck();
		 	if (stopflag){ //if you know it hit for sure...
		 		checkBorderCollision();
		 	}

		}		
	}   
    
    public void move(){ //move the shell
    	
    	moveAlong();
    	lifeSpan--; //decrease lifeSpan
    	
    }
 
 	public boolean allBorderCheck(){
		return (pointBorderCheck(xpos+4,ypos+4) || pointBorderCheck(xpos+4,ypos-4) || pointBorderCheck(xpos-4,ypos-4) || pointBorderCheck(xpos-4,ypos+4)); 
		
	}   
    
    public void checkBorderCollision(){ //check if shell hits a border, if it does change the angle accordiningly
    
    	if (pointBorderCheck(xpos-4,ypos)){ //left collide
    		if (angle>180){
    			angle = angle + 90;
    		}
    		if (angle==180){
    			angle = 0;
    		}
    		if (angle<180){
    			angle=angle - 90;
    		}
    	}
    	if (pointBorderCheck(xpos+4,ypos)){ //right collide
     		if (angle>180){
    			angle = angle-90;
    		}
    		if (angle==0){
    			angle = 180;
    		}    		
    		
    		if (angle<180){
    			angle=angle + 90;
    		} 		
    	}
    	if (pointBorderCheck(xpos,ypos-4)){ //up collide
    		angle = 360 - angle;
    	}
    	if (pointBorderCheck(xpos,ypos+4)){ //down collide
    		angle = 360 -angle; 
    	}
    	
    	
    	
    }
    
    public boolean isAvailable(){ //Tells if the shell is still active or not 
    	return lifeSpan>0;
    }
    
    public void draw(Graphics2D g){ //used to draw the shell on the minimap
    	g.setColor(Color.green);
    	g.drawImage(pics[0].getImage(),(int)xpos-4,(int)ypos-4,null);
    	
    }
    

	public boolean pointBorderCheck(double px,double py){ //checks to see if this specific point lies on a border
		
		int newx = round(px);
		int newy = round(py);
		
		int gridx = newx/8; //divide by grid ratio
		int gridy = newy/8;
		
		if (gridx < 0 || gridy < 0 || gridx >=128 || gridy>=128){
			lifeSpan = 0;
			return true;
		}
		
		return terrain_grid[gridy][gridx]=='b';
		

	}   
    
     public boolean collide(Rectangle r){ //used to check for player collisions
     	location = new Rectangle(round(xpos),round(ypos),width,height);
    	return location.intersects(r);
    }
    

    public ImageIcon getPic(int currentY){ //get the frame/picture for the shell in order to draw it 

    	currentframe++;
    	if (currentframe == pics.length-1){
    		currentframe = 0;
    	}
    	return pics[currentframe];
    	
    }
    
    //Getter methods...       

    public double getWidth(){
    	return this.width;
    }
    public double getHeight(){
    	return this.height;
    } 

    public double getX(){
    	return this.xpos;
    }
    
    public double getY(){
    	return this.ypos;
    }
    
}