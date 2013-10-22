import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
    
public class GraphicsMouse2 extends JFrame implements MouseListener
{
	String message;
	
	public GraphicsMouse2(){
		super("Mouse");
		message="";
		
		addMouseListener(this);
		setSize (500, 530);
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		setVisible (true);		
	}
	
 
    public void paint(Graphics g){
		g.setColor (Color.white);
		g.fillRect (0,0,500,530);
		g.setColor (Color.black);
		g.drawString(message,100,50);
	}
	
    public void mouseReleased(MouseEvent e) {
    	message = "Released: "+ e.getX() + "," + e.getY();
    	repaint();
    }
    
    public void mouseEntered(MouseEvent e) {
    	message = "Entered: "+ e.getX() + "," + e.getY();
    	repaint();
    }

    public void mouseExited(MouseEvent e) {
    	message = "Exited: "+ e.getX() + "," + e.getY();
    	repaint();
    }

    public void mouseClicked(MouseEvent e) {
    	message = "Clicked: "+ e.getX() + "," + e.getY();
    	repaint();
    }
        
    public void mousePressed(MouseEvent e) {
    	message = "Pressed: "+ e.getX() + "," + e.getY();
    	repaint();
    }
	
	public static void main(String []args){
		GraphicsMouse2 frame= new GraphicsMouse2();


	}
}
