import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class Swing17 extends JFrame implements KeyListener{
    
    public Swing17() {
	super("Title Bar");
	addKeyListener(this);
	setSize (400, 100);
	setVisible(true);
    }

    public void keyPressed(KeyEvent e){
	int key = e.getKeyCode();
	String txt = KeyEvent.getKeyText(key);
	setTitle("" + key+ "   " +txt);
	repaint();
    }
    
    public void keyReleased(KeyEvent e){
	setTitle(e.toString());
	repaint();
    
    }
    
    public void keyTyped(KeyEvent e){
 
    }
    
    
    public static void main(String[] arguments) {
	Swing17 frame = new Swing17();
    }
}
