//Pipe class

//Pipes were not used in the final version due to the fact that it would be hard for the AI to navigate around them
//I did not make a function for colliding with pipes though ===> though I could just make them a "b" square, then update the
//2d terraing rid
//however this class is complete...and pipes do work in the game
//if you'd like to add any =P
//I would have added pipes if I had more time....



//Import....
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

public class Pipe {
	
	double xpos;
	double ypos; 
	int width;
	int height;
	ImageIcon[] pics; 
	
	
	public void loadImages(String filename){ //load image 
		String ext = ".png";
		for (int i = 0; i <pics.length;i++){
			String temp_name = filename + i + ext;
			ImageIcon temp = new ImageIcon(temp_name);
			pics[i] = new ImageIcon(temp_name);//.getImage();			
		}
		
	}	
	
    public Pipe(double x, double y, int width, int height) {
    	
    	xpos = x;
    	ypos = y;
    	this.width = 16;
    	this.height = 16;
    	String temp = "pipe";
    	pics = new ImageIcon[9];
    	loadImages(temp);
    	
    }

    public ImageIcon getPic(int currentY){ //the picture you return depends on how far away the pipe is on the screen
    //if the pipe is far away from the player you get the picture in which the pipe looks smaller and with less detail
    
    	double total_screenheight = 440;
    	int total_frames = pics.length-1;
    	double ans = currentY/(total_screenheight/total_frames);
    	int frame_ans = round(ans);
    	System.out.println(frame_ans);
    	return pics[frame_ans];
    		
    }
    
 	public int round(double d){
		double n = d+.5;
		return (int)n;
		
	}	          
//Getter Methods  
 
     public double getX(){
    	return this.xpos;
    }
    
    public double getY(){
    	return this.ypos;
    }
    
}