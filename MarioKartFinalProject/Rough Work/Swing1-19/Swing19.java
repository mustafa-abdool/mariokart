import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class Swing19 extends JFrame
{
    JButton b1, b2, b3,b4;

    public Swing19 ()
    {        
		super ("Ummm ... Ugly stuff ... I Love Freedom");
		Container pane = getContentPane();
		
		pane.setLayout(null);        
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		setSize (getToolkit ().getScreenSize ());
		b1 = new JButton ("With");
		b1.setSize (150, 45);
		b1.setLocation (150, 55);
		b2 = new JButton ("Freedom");
		b2.setSize (110, 115);
		b2.setLocation (250, 455);
		b3 = new JButton ("Comes");
		b3.setSize (170, 45);
		b3.setLocation (350, 255);
		b4 = new JButton ("Responsibility");
		b4.setSize (170, 25);
		b4.setLocation (450, 355);
		pane.add (b1);
		pane.add (b2);
		pane.add (b3);
		pane.add (b4);
		setVisible (true);
    }


    public static void main (String [] arguments)
    {
	Swing19 frame = new Swing19 ();
    }
}
