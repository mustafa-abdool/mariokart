
//Import the required packages..
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;             
import java.awt.event.*;
import java.awt.font.*;




class CharSelectScreen extends JFrame implements ActionListener,Runnable { //GUI for the starting screen, where you select pkmn

	private ArrayList<JButton> buttons; //buttons that represent each kart
	private ArrayList<String> names = new ArrayList<String>();
	private JLayeredPane contentpane; //content pane
	private int xoffset; //used to draw pokemon
	private int yoffset; //used to draw pokemon
	JLabel title;
	private String selected_char = null;
	private boolean finish_flag = false;
	private ImageIcon bg = new ImageIcon("mk load background.PNG");
	private JLabel background_pic = new JLabel(bg);
	int frame_pos = 0;
	int modulus = 0;
	
	
	public String get_char(){
		return selected_char; 
	}
	
	public void loadCharacters(){
		names.add("Mario");
		names.add("Luigi");
		names.add("KT");
		names.add("DK");
		names.add("Peach");
				
	}
	
	public void buttonSetUp(){
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
	
	public CharSelectScreen(){ //set up the template thing
	
	//set size and location of anyone...
		super ("Select Your Character! =P "); 
		setSize(1000,1000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		buttons=new ArrayList();
//		contentpane=this.getContentPane();
		contentpane = new JLayeredPane();
		contentpane.setLayout(null);
		
		System.out.println(bg.getIconWidth());
		background_pic.setSize(1000,1000);
		background_pic.setLocation(0,0);
		contentpane.add(background_pic,new Integer(10));
		
		this.xoffset=100;
		this.yoffset=100;
		Font moosefont=new Font("Moose",2,18);
		title = new JLabel();
   		title.setFont(moosefont);
   		title.setSize(300,100);
   		title.setLocation(350,0);
   		title.setText("Choose Your Character!");
   		contentpane.add(title);
		
		loadCharacters();
		
		buttons = new ArrayList<JButton>();
		for (int i = 0; i <names.size(); i++ ){
			String name = names.get(i)+ "0" + ".PNG";

			ImageIcon pic =new ImageIcon(name);
			
			JButton temp = new JButton(pic);

			temp.setIcon(pic);
			temp.setRolloverEnabled(true);
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
		
		//Thread th = new Thread(this);
	//th.start();


		this.add(contentpane);
   		this.setVisible(true);

	
	}
	
	
	
	public void updateFramePos(){
		for (JButton temp: buttons){
			String info=temp.getIcon().toString(); //get pokemon 
			int pos = info.indexOf(".");
			String name = info.substring(0,pos-1);
			String pic_name = name +  frame_pos + ".PNG";
			if (frame_pos==21){
				frame_pos = 0;
			}
			frame_pos++;
			ImageIcon temp_i = new ImageIcon(pic_name);
			
			temp.setRolloverIcon(temp_i);
			
		}
	}
		
		
		
	public void run(){
		modulus = 0;
		while (true){
	//		System.out.println("This function has been called");
			
			System.out.println(modulus);
			if (modulus%500==0){
				//System.out.println("called");
				updateFramePos();
			}
			
			modulus++;
			
			if (selected_char!=null){
				
				System.out.println("done");
				break;
			}
		}
	}
	
	public void actionPerformed(ActionEvent evt){
	

	Object source = evt.getSource();

	for (JButton whatever:this.buttons){ //when they click the mouse, we loop throught to see what they clicked
		if (source==whatever){
			String info=whatever.getIcon().toString(); //get pokemon 
			int pos = info.indexOf(".");
			String name = info.substring(0,pos-1);
			System.out.println(name);
			selected_char = name;
			setVisible(false);
			dispose();

			

			break;	
		}
	}



		
		
	}
	
	public static void main(String[] args){
		CharSelectScreen temp = new CharSelectScreen();
	}


	
	public void update(){ //just updates the screen
		this.setVisible(true);
	}
	

	

		

		

}	