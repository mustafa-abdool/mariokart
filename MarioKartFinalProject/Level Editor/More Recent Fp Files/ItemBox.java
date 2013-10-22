//Class for the Item Box Stuff 

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



public class ItemBox {
	
	public Image empty_pic = new ImageIcon("emptybox.png").getImage();
	
	private Rectangle location;
	private boolean isUsed;
	

    public ItemBox(int x, int y) {

    	
    	location = new Rectangle(x,y,16,16);
    	isUsed = false;
    }
    
    public void draw(Graphics2D g){
    	if (isUsed==true){ //if that item box is used....
    		g.fillRect(location.x,location.y,16,16); //draw the picture to that square
    		g.drawImage(empty_pic,location.x,location.y,null);

    	}
    }
    
    public boolean collide(Rectangle r){
    	return location.intersects(r);
    }
    
    public void setUsed(){ //make that item box used 
    	isUsed = true;
    }
    
    public boolean isUsed(){
    	return isUsed==true;
    }
    
    
}