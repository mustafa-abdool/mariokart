// Ship.java

import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.image.*;

public class Ship
{
    private double posX,posY;
    private double velX,velY;
    private int targetX,targetY;
    private double direction;
    private double speed;
    private FightPanel panel;
    private static Image shipPic;
    private boolean onTarget; 
    private boolean picLoaded;
    private int shotSpeed;
    private int numShot;
    
    public Ship (double x, double y,FightPanel pan)
    {
		if(shipPic==null)
		    shipPic = new ImageIcon("ship.png").getImage();
		picLoaded=true;
		panel = pan;  
		posX =x;
		posY = y;
		velX = 0;
		velY = 0;
		onTarget=false;
		direction = 0;
		speed =3;
		shotSpeed = 5;
		numShot=0;
    } // end of Ship()

    private int greaterAngle(double a1,double a2)
    {
		int ang1 = ((int)Math.toDegrees(a1)+360)%360;
		int ang2 = ((int)Math.toDegrees(a2)+360)%360;
	     
		if(ang2 == ang1)  
		    return 0;   
		if(ang2 > ang1 && ang2 - ang1 <= 180) 
		    return 1;
		if(ang2 < ang1 && (ang2+360) - ang1 <= 180)         
		    return 1;
		return -1;
    }

    public void move (int x,int y)
    {
		Vect2D diff = new Vect2D(x-(posX+25),y-(posY+25));
	//        System.out.println("[" + posX +","+posY+"] to ["+x+","+y+"]  is Ang "+Math.toDegrees(diff.ang));
		double dx,dy;
		
		if(x!=targetX || y!=targetY)
		{
		    onTarget=false;
		    targetX=x;
		    targetY=y;
		}
	
		if(!onTarget)
		{
		    if(greaterAngle(direction,diff.ang) > 0)
			direction+=.05;
		    else if(greaterAngle(direction,diff.ang) < 0)
			direction-=.05;            
		    else
			onTarget=true;
		}
		
		if(direction>2*Math.PI)
		    direction-=2*Math.PI;
		if(direction<-2*Math.PI)
		    direction+=2*Math.PI;
		dx = (Math.cos(direction))*speed;
		dy = (Math.sin(direction))*speed;
		posX+=dx;
		posY+=dy;
    } // end of move()
    
    // used when the background moves
    public void shift (int x,int y)
    {
		posX+=x;
		posY+=y;
    }
   
    // TO DO - add the correct starting velocity to the shot
    public Photon fire(int x, int y){
		if(numShot <6)
		{
		    double shotX = x - (posX+25);
		    double shotY = y - (posY+25);
		    double shotHyp = Math.sqrt(shotX*shotX + shotY*shotY);
		    if(shotHyp > 0)
		    {
			numShot++;
			return new Photon(posX+25,posY+25,shotX/shotHyp*shotSpeed,shotY/shotHyp*shotSpeed,panel);
		    }
		    else
			return null;
		}
		else
		    return null;
    } 

    public void reload()
    {
		numShot--;
    }
    
    public boolean ready ()
    {
		return picLoaded;
    }

    public void draw (Graphics g)
    {
		Graphics2D g2D = (Graphics2D)g;
	
		AffineTransform saveXform = g2D.getTransform();
		AffineTransform at = new AffineTransform();
		at.rotate(direction,posX+25,posY+25);
		g2D.transform(at);
		g2D.drawImage(shipPic,(int)posX,(int)posY,panel);
		g2D.setTransform(saveXform);
	    } // end of draw()
} // end of Ship class

