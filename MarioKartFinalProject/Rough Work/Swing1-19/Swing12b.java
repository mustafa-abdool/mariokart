import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Swing12b extends JFrame implements ItemListener{
    JCheckBox mon = new JCheckBox("Monday", true);
    JCheckBox tue = new JCheckBox("Tuesday", false);
    JCheckBox wed = new JCheckBox("Wednesday", false);
    JCheckBox thr = new JCheckBox("Thursday", false);
    JCheckBox fri = new JCheckBox("Friday", false);
	 
    public Swing12b() {
	super("This is a good day to dye.");
	JPanel pane = new JPanel();
	mon.addItemListener(this);
	tue.addItemListener(this);
	
	pane.add(mon);
	pane.add(tue);
	pane.add(wed);
	pane.add(thr);
	pane.add(fri);
	getContentPane().add(pane);
	pack();
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setVisible(true);
    }
    
    public void itemStateChanged(ItemEvent e) {
	Object source = e.getItemSelectable();
	System.out.println(((JCheckBox)e.getItem()).getText());
	}


    public static void main(String[] arguments) {
	Swing12b frame = new Swing12b();
    }
}
