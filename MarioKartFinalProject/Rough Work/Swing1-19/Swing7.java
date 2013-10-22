import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class Swing7 extends JFrame implements ActionListener {
    JButton bStay = new JButton("Stay");
    JButton bGo = new JButton("Go");

    public Swing7() {
	super("Title Bar");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	bStay.addActionListener(this);
	bGo.addActionListener(this);
	FlowLayout flow = new FlowLayout();
	getContentPane().setLayout(flow);
	getContentPane().add(bStay);
	getContentPane().add(bGo);
	pack();
	setVisible(true);
    }


    public void actionPerformed(ActionEvent evt) {
	Object source = evt.getSource();
	if (source == bStay) {
	    setTitle("Stay");
	} else if (source == bGo) {
	    setTitle("Go");
	}
	repaint();
    }

    public static void main(String[] arguments) {
	Swing7 frame = new Swing7();
    }
}
