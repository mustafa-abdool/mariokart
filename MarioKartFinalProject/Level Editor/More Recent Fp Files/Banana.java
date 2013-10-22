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

public class Banana {
	
	private double xpos;
	private double ypos;
	ImageIcon[] pics; 
	private int width = 8;
	private int height = 8; 
	private Rectangle location;	
	

	public void loadImages(String filename){
		String ext = ".png";
		for (int i = 0; i <pics.length;i++){
			String temp_name = filename + i + ext;
			ImageIcon temp = new ImageIcon(temp_name);
			pics[i] = new ImageIcon(temp_name);//.getImage();			
		}
		
	}
	

    public Banana(double x,double y, int width, int height ) {
    	

    	xpos = x;
    	ypos = y;
    	location = new Rectangle((int)x,(int)y,width,height);
    	pics = new ImageIcon[7];
    	String temp = "banana";
    	loadImages(temp);
    	
    	
    }
    
    public boolean collide(Rectangle r){
    	return location.intersects(r);
    }
    
    
     public void draw(Graphics2D g){
    	//g.setColor(Color.green);
    	//g.fillRect((int)xpos-4,(int)ypos-4,width,height);
    	g.drawImage(pics[0].getImage(),(int)xpos-4,(int)ypos-4,null);
    }
    
    public double getX(){
    	return this.xpos;
    }
    public double getY(){
    	return this.ypos;
    }
    public ImageIcon getPic(int currentY){
    	double total_screenheight = 500;
    	int total_frames = pics.length-1;
    	double ans = currentY/(total_screenheight/total_frames);
    	int frame_ans = round(ans);
    	System.out.println(frame_ans);
    	return pics[frame_ans];
    		
    }
    
    
    
    public double getWidth(){
    	return this.width;
    }
    public double getHeight(){
    	return this.height;
    }
 
 	public int round(double d){
		double n = d+.5;
		return (int)n;
		
	}	      
    
}