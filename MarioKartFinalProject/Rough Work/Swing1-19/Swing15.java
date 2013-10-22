import javax.swing.*;
import java.awt.*;

public class Swing15 extends JFrame{
	 
    public Swing15() {
	super("Icons can be quite big");
	ImageIcon icon = new ImageIcon("Phelddagrif.jpg","the hippo");
	JLabel phel = new JLabel(icon);

	setSize(800,600);
	//pack();
	getContentPane().add(phel);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setVisible(true);
    }    
    
    public static void main(String[] arguments) {
	Swing15 frame = new Swing15();
    }
}
