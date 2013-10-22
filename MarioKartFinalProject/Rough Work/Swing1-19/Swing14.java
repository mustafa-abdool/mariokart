import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;             // for the rectangle

public class Swing14 extends JFrame{
	 
    public Swing14() {
	super("By making Pane it's own class we encapsulate drawing seperately");
	Pane p = new Pane();
	setSize(400,200);

	getContentPane().add(p);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setVisible(true);
    }
    
    
    public static void main(String[] arguments) {
	Swing14 frame = new Swing14();
    }
}

class Pane extends JPanel{

    public void paintComponent(Graphics comp) {
	Graphics2D comp2D = (Graphics2D)comp;
	comp2D.setColor(Color.blue);
	Rectangle2D.Float background = new Rectangle2D.Float(
		0F, 0F, (float)getSize().width, (float)getSize().height);
	comp2D.fill(background);
	comp2D.setColor(Color.yellow);
	comp2D.drawString("Hmmm, Graphics object without an Applet",20,20);
	comp2D.drawString("You can also use this.getGraphics() in any JFrame",20,60);
    }

}
