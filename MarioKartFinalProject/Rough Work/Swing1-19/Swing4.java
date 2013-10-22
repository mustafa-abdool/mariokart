import javax.swing.*;

public class Swing4 extends JFrame
{
    JButton load = new JButton("Load");    
    JButton save = new JButton("Save");    
    JButton exit = new JButton("Exit");    

    public Swing4 ()
    {
	super ("Fun with buttons");
	setSize (80,170);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	JPanel pane = new JPanel();
	pane.add(load);
	pane.add(save);
	pane.add(exit);
	getContentPane().add(pane);
	setVisible (true);
    }
    
    public static void main(String[] args){
	Swing4 sButtons = new Swing4();
    }
}
