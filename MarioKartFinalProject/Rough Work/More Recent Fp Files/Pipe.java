

//Banana Class

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
	
	
	public void loadImages(String filename){
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

    public ImageIcon getPic(int currentY){
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
 
 
     public double getX(){
    	return this.xpos;
    }
    
    public double getY(){
    	return this.ypos;
    }
    
}