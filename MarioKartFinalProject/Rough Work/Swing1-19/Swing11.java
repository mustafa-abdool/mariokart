import javax.swing.*;

public class Swing11 extends JFrame{
    JRadioButton mon = new JRadioButton("Monday", true);
    JRadioButton tue = new JRadioButton("Tuesday", false);
    JRadioButton wed = new JRadioButton("Wednesday", false);
    JRadioButton thr = new JRadioButton("Thursday", false);
    JRadioButton fri = new JRadioButton("Friday", false);
	 
    public Swing11() {
	super("This is a good day to dye.");
		JPanel pane = new JPanel();

	pane.add(mon);
	pane.add(tue);
	pane.add(wed);
	pane.add(thr);
	pane.add(fri);
	getContentPane().add(pane);
	pack();
	setVisible(true);
    }

    public static void main(String[] arguments) {
	Swing11 frame = new Swing11();
    }
}
