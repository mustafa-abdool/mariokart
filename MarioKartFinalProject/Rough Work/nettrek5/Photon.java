/*
    Photon
	Need to add at least a draw Method.
*/
import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.image.*;

class Photon{
    double x,y,vx,vy;
    static int frame;
    private static Image []shotPic;
    private FightPanel panel;
    private int timer;
    
    Photon(double xx, double yy, double vxx, double vyy,FightPanel panel){
	if(shotPic==null)
	{   
	    shotPic = new Image[3];
	    for(int i=0;i<shotPic.length;i++)
		shotPic[i] = new ImageIcon("photon" + (i+1) + ".png").getImage();
	}
	this.panel = panel;
	x=xx;
	y=yy;
	vx=vxx;
	vy=vyy;
	frame=0;
	timer = 80;
    }

    public void draw (Graphics g)
    {
	Graphics2D g2D = (Graphics2D)g;
//        System.out.println("Drawing: " + frame); 
	g2D.drawImage(shotPic[(timer/3)%3],(int)x,(int)y,panel);
    } // end of draw()
    
    public boolean dead()
    {
/*        if(x<-10 && vx < 0)
	    return true;
	if(y<-10 && vy < 0)
	    return true;
	if(y>1200 && vy > 0)
	    return true;
	if(x>1600 && vx > 0)
	    return true;
*/        return (timer<=0);
	
    }
    public void move ()
    {
	timer--;
	x+=vx;
	y+=vy;
    }

    public void shift (int x,int y)
    {
	this.x+=x;
	this.y+=y;
    }
    
    public double getX(){return x;} 
    public double getY(){return y;} 

}
