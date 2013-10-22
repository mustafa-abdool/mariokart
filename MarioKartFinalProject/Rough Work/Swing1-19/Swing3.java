import javax.swing.*;

public class Swing3 extends JFrame
{
    public Swing3 ()
    {
	super ("Swing example 2");
	setSize (400, 100);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setVisible (true);
	while (true)
	    ;
    }
    
    public static void main(String[] args){
	Swing3 swng = new Swing3();
    }
}
