
//Import the required packages..
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;             
import java.awt.event.*;
import java.awt.font.*;




class MapSelectScreen extends JFrame implements ActionListener { //GUI for the starting screen, where you select pkmn

	private ArrayList<JButton> buttons; //buttons that represent each kart
	private ArrayList<String> names = new ArrayList<String>();
	private Container contentpane; //content pane
	private int xoffset; //used to draw pokemon
	private int yoffset; //used to draw pokemon
	JLabel title;
	private String selected_map;
	private boolean finish_flag = false;
	
	
	
	
	public String get_map(){
		return selected_map; 
	}
	
	public void loadMaps(){
		names.add("Vanilla Lake");
		names.add("Ghost Valley");
		names.add("Rainbow Road");
		names.add("Mario Circuit");
		names.add("Mario Circuit2");
		names.add("Mario Circuit3");
		names.add("Choco Mountain");		
	}

	public MapSelectScreen(){ //set up the template thing
	
	//set size and location of anyone...
		super ("Select Your Map =D"); 
		setSize(1204,1024);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		buttons=new ArrayList();
		contentpane=this.getContentPane();
		contentpane.setLayout(null);
		this.xoffset= 0;
		this.yoffset= 100;
		Font moosefont=new Font("Moose",2,18);
		title = new JLabel();
   		title.setFont(moosefont);
   		title.setSize(300,100);
   		title.setLocation(550,0);
   		title.setText("Choose Your Map!!");
   		contentpane.add(title);
		
		loadMaps();
		
		buttons = new ArrayList<JButton>();
		for (int i = 0; i <names.size(); i++ ){
			String name = names.get(i)+ " small" + ".png";

			ImageIcon pic =new ImageIcon(name);
			System.out.println(pic.getIconHeight());
			JButton temp = new JButton(pic);
	//		temp.setText("Ghost Valley");

			temp.setIcon(pic);
			temp.setSize(308,308); //set location
			temp.setLocation(this.xoffset,this.yoffset);
			temp.addActionListener(this); //add actionlistner 
			contentpane.add(temp);
			buttons.add(temp);
			if (this.xoffset==700){
				this.xoffset=0; //increase grid y coord
				this.yoffset+=300;
			}
			else{ //increase grid x coord
				this.xoffset+=350;
			}			
		}


	
   		this.setVisible(true);

	
	}
	

	
	public void actionPerformed(ActionEvent evt){
	

	Object source = evt.getSource();
	for (JButton whatever:this.buttons){ //when they click the mouse, we loop throught to see what they clicked
		if (source==whatever){
			String info=whatever.getIcon().toString(); 
			int pos = info.indexOf(".");
			String name = info.substring(0,pos-6);
			System.out.println(name);
			selected_map = name;
			setVisible(false);
			dispose();

			

			break;	
		}
	}



		
		
	}
	
	public static void main(String[] args){
		MapSelectScreen temp = new MapSelectScreen();
	}


	
	public void update(){ //just updates the screen
		this.setVisible(true);
	}
	

	

		

		

}	