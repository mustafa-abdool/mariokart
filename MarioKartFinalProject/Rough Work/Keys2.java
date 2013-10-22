import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Keys2 extends JFrame implements KeyListener, Runnable
{
    // Place instance variables here
    boolean []keys = new boolean[256];
    Thread th;
    
    public Keys2 ()
    {
    	super ("keys and stuff");
    	setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		addKeyListener(this);
		setSize(600,400);
		setVisible(true);
    } 

    public void start ()
    {
		th = new Thread(this);
    } 

	public void run(){
	}
	
    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        System.out.println(e.getKeyCode());
        repaint();
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
        repaint();
    }
    
    
    public void paint (Graphics g)
    {
    	String down="";
    	for(int i=0; i< 256; i++)
    		if(keys[i])
    			down += i +" ";
    			
    	g.clearRect(0,0,getWidth(), getHeight());
		g.drawString(down,20,100);

    }
  
  	public static void main(String []args){
  		Keys2 mn = new Keys2();
  	}  
} 
