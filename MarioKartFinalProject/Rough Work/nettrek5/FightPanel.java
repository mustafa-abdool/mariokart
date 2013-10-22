/*
   The System timer is used to drive the animation loop.
*/
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.text.DecimalFormat;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

public class FightPanel extends JPanel implements Runnable, MouseMotionListener, MouseListener
{
    private static final int PWIDTH = 600;   // size of panel
    private static final int PHEIGHT = 500;
    private Thread animator;           // the thread that performs the animation
    private volatile boolean running = false;   // used to stop the animation thread
    private volatile boolean isPaused = false;

    private int period;                // period between drawing in _ms_

    private JetTrekMain jtTop;
    private int clickX, clickY;

    private Ship userShip;
    // used at game termination
    private boolean gameOver = false;

    // off screen rendering
    private Graphics dbg;
    volatile private Image dbImage = null;
    private boolean mbDown;
    private Image back;
    private int backX,backY;
    private int upCnt,drawCnt;
    private List photons;
    
    public FightPanel (JetTrekMain jt, int period)
    {
	back = new ImageIcon("space_004.jpg").getImage();
	jtTop = jt;
	this.period = period;
	userShip = new Ship (200, 200,this);
	mbDown = false;
	backX=-400;
	backY=-300;
	clickX = 100;
	clickY = 100;
	photons = Collections.synchronizedList(new ArrayList());
	
	setBackground (Color.black);
	setPreferredSize (new Dimension (PWIDTH, PHEIGHT));

	setFocusable (true);
	requestFocus ();            // the JPanel now has focus, so receives key events

	this.addMouseListener (this);
	this.addMouseMotionListener(this);
    } // end of FightPanel()

    public int getHeight()
    {
	return PHEIGHT;
    }

    public int getWidth()
    {
	return PWIDTH;
    }

    public void mouseReleased(MouseEvent e) {
	    mbDown = false;
    }
    
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}    

    public void mouseDragged(MouseEvent e) {
	if(mbDown)
	    testPress (e.getX (), e.getY ());
    }

    public void mouseMoved(MouseEvent e) {        
	if(mbDown)
	    testPress (e.getX (), e.getY ());
    }
    
    public void mousePressed(MouseEvent e) {
	if(e.getButton()==1)
	{
	    testPress (e.getX (), e.getY ());
	    mbDown = true;
	}
	else
	    firePhoton(e.getX (), e.getY ());
    }

    // Saves the screen to a PNG file (could use jpeg just as easily)
    private void save()
    {
	    if(userShip.ready())
	    {
		BufferedImage copy = new BufferedImage(PWIDTH, PHEIGHT,BufferedImage.TYPE_INT_ARGB);
		// create a graphics context
		Graphics2D g2d = copy.createGraphics();
		// g2d.setComposite(AlphaComposite.Src);

		// copy image
		g2d.drawImage(dbImage,0,0,null);
		
		try{
		    ImageIO.write( copy, "PNG" /* format desired */ , new File( "save.png" ) /* target */ );
		}
		catch(Exception ex){}

		g2d.dispose();
		System.out.println("SAVE IMAGE");
		
		
	    }    
    } 

    public void firePhoton(int x, int y){
	Photon newShot =userShip.fire(x,y);
	if(newShot!=null)   
	    photons.add(newShot);
    }
    
    public void addNotify ()
	// wait for the JPanel to be added to the JFrame before starting
    {
	super.addNotify ();    // creates the peer
	startGame ();   // start the thread
    }


    private void startGame ()
	// initialise and start the thread
    {
	if (animator == null || !running)
	{
	    animator = new Thread (this);
	    animator.start ();
	}
    } // end of startGame()


    private void testPress (int x, int y)
    {
	clickX = x;
	clickY = y;
    } // end of testPress()


    public void delay (long len)
    {
	try
	{
	    Thread.sleep (len);
	}
	catch (InterruptedException ex)
	{
	}
    }


    public void run ()
	/* The frames of the animation are drawn inside the while loop. */
    {
	long lastUpdate, sleepLen;
	Graphics g;

	lastUpdate = System.currentTimeMillis ();
	running = true;

	while (running)
	{
	    gameUpdate ();
	    gameRender ();  // render the game to a buffer
	    paintScreen (); // draw the buffer on-screen
	    // sleepLen is a only used to avoid a very ugly expression. Basically we want each
	    // update to be spaced by the value in period (thus the name.)
	    sleepLen = lastUpdate + period - System.currentTimeMillis ();
	    sleepLen = Math.max (5, sleepLen);
	    delay (sleepLen);
	    lastUpdate = System.currentTimeMillis ();
	}
	System.exit (0);  // so window disappears
    } // end of run()


    // this is where the action for the game will go.
    private void gameUpdate ()
    {
	int picX = back.getWidth(this);
	int picY = back.getHeight(this);
	int maxX = (picX - PWIDTH); 
	int maxY = (picY - PHEIGHT);
	
	if(clickX<10 && backX<0)
	{
	    backX+=2;            
	    userShip.shift(2,0);
	    for(Iterator i=photons.iterator();i.hasNext();)          
		((Photon)i.next()).shift(2,0);
	}
	if(clickY<10 && backY<0)
	{
	    backY+=2;
	    userShip.shift(0,2);
	    for(Iterator i=photons.iterator();i.hasNext();)          
		((Photon)i.next()).shift(0,2);
	}
	if(clickY>490 && backY> -maxY)
	{
	    backY-=2;
	    userShip.shift(0,-2);
	    for(Iterator i=photons.iterator();i.hasNext();)          
		((Photon)i.next()).shift(0,-2);
	}
	if(clickX>590 && backX> -maxX)
	{
	    backX-=2;
	    userShip.shift(-2,0);
	    for(Iterator i=photons.iterator();i.hasNext();)          
		((Photon)i.next()).shift(-2,0);            
	}
	upCnt++;
	userShip.move (clickX, clickY);
	for(Iterator i=photons.iterator();i.hasNext();)
	{   
	    Photon shot = ((Photon)i.next());
	    shot.move();
	    if(shot.dead())
	    {
		userShip.reload();
		i.remove();
	    }
	}
    } // end of gameUpdate()


    private void gameRender ()
    {
	if (dbImage == null)
	{
	    dbImage = createImage (PWIDTH, PHEIGHT);
	    if (dbImage == null)
	    {
		System.out.println ("dbImage is null");
		return;
	    }
	    else
		dbg = dbImage.getGraphics ();
	}
	// clear the background
	//dbg.setColor (Color.black);
	//dbg.fillRect (0, 0, PWIDTH, PHEIGHT);
	dbg.drawImage(back,backX,backY,this);

	dbg.setColor (Color.blue);
	drawCnt++;
	dbg.drawString ("X,Y : " + clickX + ", " + clickY + "  Missed:" +(upCnt-drawCnt), 20, 25); // was (10,55)

	// draw game stuff
	userShip.draw (dbg);
	List tmp = new ArrayList();
	tmp.addAll(photons);                // this is to avoid concurrent access ERROR
	for(Iterator i=tmp.iterator();i.hasNext();)
	{
	    ((Photon)i.next()).draw(dbg);
	}

    } // end of gameRender()


    private void paintScreen ()
	// use active rendering to put the buffered image on-screen
    {
	Graphics g;
	try
	{
	    g = this.getGraphics ();
	    if ((g != null) && (dbImage != null))
		g.drawImage (dbImage, 0, 0, null);
	    Toolkit.getDefaultToolkit ().sync (); // sync the display on some systems
	    g.dispose ();
	}
	catch (Exception e)
	{
	    System.out.println ("Graphics error: " + e);
	}
    } // end of paintScreen()
} // end of FightPanel class

