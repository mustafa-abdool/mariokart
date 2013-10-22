import javax.swing.*;

public class Swing12 extends JFrame{
    JRadioButton mon = new JRadioButton("Monday", true);
    JRadioButton tue = new JRadioButton("Tuesday", false);
    JRadioButton wed = new JRadioButton("Wednesday", false);
    JRadioButton thr = new JRadioButton("Thursday", false);
    JRadioButton fri = new JRadioButton("Friday", false);
	 
    public Swing12() {
	super("This is a good day to dye.");
	JPanel pane = new JPanel();
	ButtonGroup daze = new ButtonGroup();
	daze.add(mon);
	daze.add(tue);
	daze.add(wed);
	daze.add(thr);
	daze.add(fri);
	
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

    public static void main(String[] arguments) {
	Swing12 frame = new Swing12();
    }
}
