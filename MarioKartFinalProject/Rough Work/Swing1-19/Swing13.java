import javax.swing.*;

public class Swing13 extends JFrame{
    String [] units = {"Zealot","Dragoon","High Templar","Dark Templar","Probe","Shuttle",
		       "Reaver","Observer","Scout","Corsair","Carrier","Archon","Dark Archon"};
    JComboBox cboUnits = new JComboBox();
	 
    public Swing13() {
	super("Zealot rush?");

	JPanel pane = new JPanel();
	pane.add(new JLabel("Pick your unit:"));
	//pane.add(cboUnits);               // You can add the items after you add the combo-box
					    // even at run-time
	for(int i=0; i<units.length; i++)
	    cboUnits.addItem(units[i]);
	    
	pane.add(cboUnits);                 // again use this one OR the above add, not both
	getContentPane().add(pane);
       
	setSize(300,150);
	//pack();
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setVisible(true);
    }

    public static void main(String[] arguments) {
	Swing13 frame = new Swing13();
    }
}
