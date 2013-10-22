import javax.swing.*;
import java.awt.*;

public class SwingPro extends JFrame{
	ImageIcon icon = new ImageIcon("Phelddagrif.jpg","the hippo");
	JLabel phel = new JLabel(icon);

    private static void pause(int t)
    {
	    try{
		    Thread.sleep(t);
	    }
	    catch (InterruptedException e){
		// do nothing
	    }
    }
    
    public SwingPro() {
	super("Icons can be quite big");

	setSize(800,600);
	//pack();
	getContentPane().add(phel);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setVisible(true);
    }    
    
    public void update()
    {
	Point p = phel.getLocation();
	phel.setLocation(p.x-1,p.y);
    }
    
    public static void main(String[] arguments) {
	SwingPro frame = new SwingPro();
	boolean running=true;
	while(running)
	{
	    frame.update();
	    pause(50);            
	}
    }
}
