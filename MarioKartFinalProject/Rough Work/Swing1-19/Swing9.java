import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class Swing9 extends JFrame implements ActionListener {
    JButton calc = new JButton("Calculate");
    JTextField inches = new JTextField(5);
    JLabel cm = new JLabel("            ");
    
    public Swing9() {
	super("Title Bar");

	JPanel inPane = new JPanel();
	JPanel cmPane = new JPanel();
    
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	calc.addActionListener(this);
	BoxLayout box = new BoxLayout(getContentPane(),BoxLayout.Y_AXIS);
	getContentPane().setLayout(box);

	inPane.add(new JLabel("Inches:"));
	inPane.add(inches);
	getContentPane().add(inPane);
	
	cmPane.add(new JLabel("Cenimeters:"));
	cmPane.add(cm);
	getContentPane().add(cmPane);

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
	Swing9 frame = new Swing9();
    }
}
