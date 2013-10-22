//class for the checkpoints


//Checkpoints dictate when a player completes a lap or not


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


public class CheckPoint {
	
	private Rectangle location; //rectangle which tells us where the checkpoint is 

	

    public CheckPoint(int x, int y, int width, int height) { //constructor 
    	location = new Rectangle(x,y,width,height);
    }
    
    
    public boolean collide(Rectangle r){ //used for collision testing with player 
    	return location.intersects(r);
    }
    
    
	public Rectangle getRect(){ //returns where the checkpoint is 
		return location;
	}
    
    
    
}