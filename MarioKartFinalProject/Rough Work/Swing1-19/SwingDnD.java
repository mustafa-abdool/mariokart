import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;

public class SwingDnD extends JFrame
{
    JTextField textField;
    JList list;
    
    public SwingDnD ()
    {        
		super ("Drag and Drop");       
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		setSize (getToolkit ().getScreenSize ());
		setLayout(new FlowLayout());

        textField = new JTextField(40);
        textField.setDragEnabled(true);
        
        String []names = {"An", "Bob", "Cindy", "Doug"};
        list = new JList(names);
        
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setDragEnabled(true);
                
		add(textField);
		add(list);
		DropTarget dt = new DropTarget(textField, 
					DnDConstants.ACTION_COPY_OR_MOVE, new MyDropListener());
		setVisible (true);
    }

    public static void main (String [] arguments)
    {
		SwingDnD frame = new SwingDnD ();
    }
    
    private class MyDropListener implements DropTargetListener{
		public void dragEnter(DropTargetDragEvent dtde) {
		}
 		public void dragExit(DropTargetEvent dte) {}
 		public void dragOver(DropTargetDragEvent dtde){} 
 		
  		public void drop(DropTargetDropEvent dtde) {
  			System.out.println("DROP");
  		}
  		
  		public void dropActionChanged(DropTargetDragEvent dtde) {}
    	
    }
}
