import javax.swing.*;
import java.awt.Dimension;

public class Swing6 {
    public static void main(String[] args) {
	JFrame frame = new JFrame("Frame as a simple object");
       
	frame.getContentPane().add(new JLabel("Look Ma, no Inheritance"));
	frame.setBounds(100,100,250,300);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.show();
    }
} 
