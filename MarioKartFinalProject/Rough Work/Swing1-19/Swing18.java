import javax.swing.*;
import java.util.*;
public class Swing18 extends JFrame
{
    public Swing18 ()
    {
	super ("The Date");
	Calendar now = Calendar.getInstance();
	
	setTitle(now.getTime().toString());
	setSize (400, 100);
	setVisible (true);
    }
    
    public static void main(String[] args){
	Swing18 eg2 = new Swing18();
    }
}
