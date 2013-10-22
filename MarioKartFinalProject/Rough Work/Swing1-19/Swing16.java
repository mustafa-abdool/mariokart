import java.awt.event.*;
import javax.swing.*;

public class Swing16 extends JFrame implements ActionListener
{
    JButton hippo = new JButton ("The Hippo");
    JButton combo = new JButton ("Combo");
    JButton rad = new JButton ("Radio Buttons");

    public Swing16 ()
    {
		super ("Multiple Forms");
		setSize (120, 170);
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		JPanel pane = new JPanel ();
		hippo.addActionListener (this);
		combo.addActionListener (this);
		rad.addActionListener (this);
		pane.add (hippo);
		pane.add (combo);
		pane.add (rad);
		getContentPane ().add (pane);
		setVisible (true);
    }


    public void actionPerformed (ActionEvent evt)
    {
	Object source = evt.getSource ();
	if (source == hippo)
	{
	    Swing15 hip = new Swing15 ();
	}
	else if (source == combo)
	{
	    Swing13 com = new Swing13 ();
	}
	else if (source == rad)
	{
	    Swing12 lis = new Swing12 ();
	}
    }


    public static void main (String [] args)
    {
	Swing16 sButtons = new Swing16 ();
    }
}
