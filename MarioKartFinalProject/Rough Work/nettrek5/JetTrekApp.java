// Ron McKenzie
// Feb 2006
// Nettrek (JetTrekApp?) - my version the old nettrek game that  
// was all the rage at Universities circa 1990


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class JetTrekApp extends JFrame
{
    private static int FPS = 80;
    
    public JetTrekApp(int period)
    { 
	super("Jet Trek");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	makeGUI(period);

	pack();
	setResizable(false);
	setVisible(true);
    }

    /* ------------------------------------------------------------
    makeGUI
	- I want the main game to be independent of it's context. In
	most games you will have buttons to pause or whatever and
	points and number of lives.  In this version none of that is 
	there, but still we want to have the hooks in place.  Also by
	having the main game independant we can run it as a Frame or
	an Applet. 
    --------------------------------------------------------------- */    
    private void makeGUI(int period)
    {
	Container content = getContentPane();    // default BorderLayout used
	FightPanel jp = new FightPanel(this, period);
	content.add(jp, "Center");

    }  // end of makeGUI()


    public static void main(String args[])
    { 
	int period = (int) 1000.0/FPS;
	new JetTrekApp(period);
    }

}


