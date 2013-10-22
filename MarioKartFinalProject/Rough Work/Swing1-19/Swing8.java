import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class Swing8 extends JFrame implements ActionListener {
	    JButton calc = new JButton("Calculate");
	    JTextField inches = new JTextField(5);
	    JLabel cm = new JLabel("            ");
	    
	    public Swing8() {
		super("Title Bar");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		calc.addActionListener(this);
		FlowLayout flow = new FlowLayout();
		getContentPane().setLayout(flow);
	
		getContentPane().add(new JLabel("Inches:"));
		getContentPane().add(inches);
		getContentPane().add(new JLabel("Cenimeters:"));
		getContentPane().add(cm);
		getContentPane().add(calc);
		pack();
		setVisible(true);
    }


    public void actionPerformed(ActionEvent evt) {
	double in= Double.valueOf(inches.getText()).doubleValue();
	cm.setText("" + in *2.54);
	repaint();
    }

    public static void main(String[] arguments) {
	Swing8 frame = new Swing8();
    }
}
