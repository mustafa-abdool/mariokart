import javax.swing.*;

public class Swing5 extends JWindow
{
    public Swing5 ()
    {
	super ();
	setBounds (400, 300,10,10);
	setVisible (true);
    }
    
    public static void main(String[] args){
	Swing5 sWnd = new Swing5();
	for(int i=10;i<400; i++){
	    sWnd.setBounds(400 - (i/2), 300-(i/2), i,i);
	}
	    
    }
}
