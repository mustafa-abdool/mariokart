//===========================Mario Kart Character Select Screen================


//This class is used to let the player select a character






//Import the required packages..
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;             
import java.awt.event.*;
import java.awt.font.*;




class CharSelectScreen extends JFrame implements ActionListener{ 

	private ArrayList<JButton> buttons; //buttons that represent each kart
	private ArrayList<String> names = new ArrayList<String>(); //contains the name of each player
	private JLayeredPane contentpane; //content pane
	private int xoffset; //used to draw buttons
	private int yoffset; //used to draw buttons
	JLabel title; //title of screen
	private String selected_char = null; //the character that was selected

	private ImageIcon bg = new ImageIcon("mk load background.PNG"); //background
	private JLabel background_pic = new JLabel(bg); //jlabel for entire background

	
	
	public String get_char(){ //returns the character that you selected
		return selected_char; 
	}
	
	public void loadCharacters(){ //add the names of each character
		names.add("Mario");
		names.add("Luigi");
		names.add("KT");
		names.add("DK");
		names.add("Peach");
				
	}
	
	public void buttonSetUp(){ //draws the button in hte correct place, with the correct image 
		buttons = new ArrayList<JButton>();
		for (int i = 0; i <names.size(); i++ ){
			String name = names.get(i);
			ImageIcon pic =new ImageIcon(names + "11" + ".PNG");
			JButton temp = new JButton(pic);
			temp.setSize(100,100); //set location
			temp.setLocation(this.xoffset,this.yoffset);
			temp.addActionListener(this); //add actionlistner 
			this.add(temp);
			if (this.xoffset==700){
				this.xoffset=100; //increase grid y coord
				this.yoffset+=100;
			}
			else{ //increase grid x coord
				this.xoffset+=100;
			}			
		}
	}
	
	public CharSelectScreen(){ //set up the template 
	

		super ("Select Your Character! =P "); 
		setSize(1000,1000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		buttons=new ArrayList();

		contentpane = new JLayeredPane(); //we used a layered pane b/c you want to be able to see the background
		contentpane.setLayout(null);
		

		background_pic.setSize(1000,1000);
		background_pic.setLocation(0,0);
		contentpane.add(background_pic,new Integer(10)); //we make the background pciture have a lower depth than the buttons
		//this way you are able to see the buttons as if they were ontop of the background img. 
		
		this.xoffset=100;
		this.yoffset=100;
		Font moosefont=new Font("Moose",2,18); //front
		title = new JLabel();
   		title.setFont(moosefont);
   		title.setSize(300,100);
   		title.setLocation(350,0);
   		title.setText("Choose Your Character!");
   		contentpane.add(title);
		
		loadCharacters(); //load cahracters 
		
		buttons = new ArrayList<JButton>(); //Create Buttons and Images
		for (int i = 0; i <names.size(); i++ ){
			String name = names.get(i)+ "0" + ".PNG";

			ImageIcon pic =new ImageIcon(name);
			
			JButton temp = new JButton(pic);

			temp.setIcon(pic);
			temp.setRolloverEnabled(true); //Rollover Icons...to change the image when the mouse is on it
			//for ashetic appeal =P
			temp.setRolloverIcon(new ImageIcon(names.get(i) + "11" + ".PNG"));
			
			temp.setSize(100,100); //set location
			temp.setLocation(this.xoffset,this.yoffset);
			temp.addActionListener(this); //add actionlistner 
			contentpane.add(temp,new Integer(20));
			buttons.add(temp);
			if (this.xoffset==700){
				this.xoffset=100; //increase grid y coord
				this.yoffset+=300;
			}
			else{ //increase grid x coord
				this.xoffset+=300;
			}			
		}
		



		this.add(contentpane); //add pane
   		this.setVisible(true); //make visible

	
	}
	
	
	

		
		
		

	
	public void actionPerformed(ActionEvent evt){
	

	Object source = evt.getSource();

	for (JButton whatever:this.buttons){ //when they click the mouse, we loop throught to see what they clicked
		if (source==whatever){
			String info=whatever.getIcon().toString(); //get the name of the image 
			int pos = info.indexOf(".");
			String name = info.substring(0,pos-1); //get the name of the actual character by some String manipulation =P
			System.out.println(name);
			selected_char = name;
			setVisible(false);
			dispose();

			

			break;	
		}
	}



		
		
	}
	
	public static void main(String[] args){ //Only used for Testing
		CharSelectScreen temp = new CharSelectScreen();
	}


	
	public void update(){ //just updates the screen
		this.setVisible(true);
	}
	

	

		

		

}	