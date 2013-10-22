//Green Shell Class


//can be spinning shells or...bigger/smaller shells?

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
	
	private double velocity = 20;
	private ImageIcon[] pics;
	private double angle;
	private double xpos;
	private double ypos;
	private int width = 8; //width and height of shell 
	private int height = 8;
	private double xscale;
	private double yscale;
	private BufferedImage newpic; 
	private int currentframe; 
	String type; //type of shell - red, yellow, green ===> this dictates the velocity
	
	private int lifeSpan; //number of frames the shell is avaiable for...
	char[][] terrain_grid; 
	private Rectangle location;
	
	
	public int round(double d){
		double n = d+.5;
		return (int)n;
		
	}	
	
	public void loadImages(String filename){
		String ext = ".png";
		for (int i = 0; i <pics.length;i++){
			String temp_name = filename + i + ext;
			ImageIcon temp = new ImageIcon(temp_name);
			pics[i] = new ImageIcon(temp_name);//.getImage();			
		}
		
	}

	
    public GreenShell(double x,double y, double angle,char[][] terrain,String type) {
    	
    	xpos = x;
    	ypos = y;
    	this.angle = angle; //don't do this 
    	
    	String temp = null;
    	
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
   
	public void moveAlong(){ //check for each point 
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
    
    public void move(){
    	
    	moveAlong();
    	lifeSpan--; //decrease lifeSpan
    	
    }
 
 	public boolean allBorderCheck(){
		return (pointBorderCheck(xpos+4,ypos+4) || pointBorderCheck(xpos+4,ypos-4) || pointBorderCheck(xpos-4,ypos-4) || pointBorderCheck(xpos-4,ypos+4)); 
		
	}   
    
    public void checkBorderCollision(){
    	if (pointBorderCheck(xpos-4,ypos)){ //left 
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
    	if (pointBorderCheck(xpos+4,ypos)){ //right 
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
    	if (pointBorderCheck(xpos,ypos-4)){
    		angle = 360 - angle;
    	}
    	if (pointBorderCheck(xpos,ypos+4)){
    		angle = 360 -angle; 
    	}
    	
    	
    	
    }
    
    public boolean isAvailable(){ //true if you can use it...false if otherwise
    	return lifeSpan>0;
    }
    
    public void draw(Graphics2D g){
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
    
     public boolean collide(Rectangle r){
     	location = new Rectangle(round(xpos),round(ypos),width,height);
    	return location.intersects(r);
    }
    
    public double getX(){
    	return this.xpos;
    }
    
    public double getY(){
    	return this.ypos;
    }
    public ImageIcon getPic(int currentY){
 /*   	double total_screenheight = 500;
    	int total_frames = pics.length-1;
    	double ans = currentY/(total_screenheight/total_frames);
    	int frame_ans = round(ans);
    	return pics[frame_ans]; */
    	currentframe++;
    	if (currentframe == pics.length-1){
    		currentframe = 0;
    	}
    	return pics[currentframe];
    	
    }       

    public double getWidth(){
    	return this.width;
    }
    public double getHeight(){
    	return this.height;
    } 
    
}