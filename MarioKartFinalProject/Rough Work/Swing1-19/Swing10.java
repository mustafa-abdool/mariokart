import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class Swing10 extends JFrame{
    ImageIcon imgFire  = new ImageIcon("fire.jpg"); // Can only use jpg or gif.
    JButton btnFire = new JButton(imgFire);
    
    public Swing10() {
	super("Title Bar");
	getContentPane().add(btnFire);
	pack();
	setVisible(true);
    }

    public static void main(String[] arguments) {
	Swing10 frame = new Swing10();
    }
}
