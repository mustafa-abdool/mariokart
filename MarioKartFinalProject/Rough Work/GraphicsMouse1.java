import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
    
public class GraphicsMouse1 extends JFrame implements MouseListener
{
	String message;
	
	public GraphicsMouse1(){
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
		System.out.println(e);
    }
    
    public void mouseEntered(MouseEvent e) {
		System.out.println(e);
    }

    public void mouseExited(MouseEvent e) {
		System.out.println(e);
    }

    public void mouseClicked(MouseEvent e) {
		System.out.println(e);
    }
        
    public void mousePressed(MouseEvent e) {
		System.out.println(e);
    }
	
	public static void main(String []args){
		GraphicsMouse1 frame= new GraphicsMouse1();


	}
}
