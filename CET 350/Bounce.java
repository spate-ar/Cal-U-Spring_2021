package Bounce;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
/* Program Bounce
 * CET 350 Technical Computing using Java
 * Andrew Spate & Nicholas Spudich
 * Spa3195@calu.edu & spu8504@calu.edu
 * Group 2
 */
public class Bounce extends Frame implements WindowListener, ComponentListener, ActionListener, AdjustmentListener, Runnable
{
	private static final long serialVersionUID = 10L;
	
	/*====== Initializer values for Window and Buttons ======*/
	private final int WIDTH = 640; // initial frame width
	private final int HEIGHT = 400; // initial frame height
	private final int BUTTONH = 20; // button height
	private final int BUTTONHS = 5; // button height spacing
	
	/*===== Initialize Window and Buttons to variables for resizing ======*/
	private int WinWidth = WIDTH; // initial frame width
	private int WinHeight = HEIGHT; // initial frame height
	private int ScreenWidth; // drawing screen width
	private int ScreenHeight; // drawing screen height
	private int WinTop = 10; // top of frame
	private int WinLeft = 10; // left side of frame
	private int BUTTONW = 50; // initial button width
	private int CENTER = (WIDTH/2); // initial screen center
	private int BUTTONS = BUTTONW/4; // initial button spacing
	
		/*====== Object limitations =====*/
	private final int MAXObj = 100; // maximum object size
	private final int MINObj = 10; // minimum object size
	private final int SPEED = 50;  // initial speed
	private final int SOBJ = 21; // initial object width
	private int SObj = SOBJ; // initial object width
	
		/*====== Scroll Bar variables ======*/
	private final int SBvisible = 10; // visible Scroll Bar
	private final int SBunit = 1; // Scroll Bar unit step size
	private final int SBblock = 10; // Scroll Bar block step size
	private final int SCROLLBARH = BUTTONH; // Scroll Bar height
	private int SpeedSBmin = 1; // speed Scroll Bar minimum value
	private int SpeedSBmax = 100 + SBvisible; // speed Scroll Bar maximum value with visible offset
	private int SpeedSBinit = SPEED; // initial speed Scroll Bar value
	private int ScrollBarW; // Scroll Bar width
	
		/*====== Object/Thread flags and delay =====*/
	private final double DELAY = 100; // timer delay constant
	boolean PLOOP = true; // bool for program loop, run ; initialized as true so it does not quit
	boolean TimePause = true; // bool to identify run/pause ; initialized as true so the animation is paused when the program starts
	boolean Started = false; // bool to identify the animation started
	int Speed = SPEED; // integer for Scrollbar Speed
	long Delay = (SpeedSBmax - SBvisible - SPEED); // int for current time delay
	int ymin; // add variables to the drawing object: ymin, ymax, xmin, xmax, xold, yold
	int ymax; // add variable to the drawing object
	int xmin; // add variable to the drawing object 
	int xmax; // add variable to the drawing object
	int xold; // add old x position
	int yold; // add old y position
	boolean ObjRight = false; // add boolean variables for the direction of the object like: right and down
	boolean ObjLeft = false; // sets boolean flags
	boolean ObjDown = false; // sets boolean flags
	boolean TailFlag = true; // sets boolean flags
	
			/*===== Objects =====*/
	private Objc Obj; // object to draw
	private Insets I; 	// borders 
	Button Start, Shape, Clear, Tail, Quit; //buttons
	Scrollbar SpeedScrollBar, ObjSizeScrollBar; // scroll bars
	private Thread thethread; // thread for timer delay
	
			/*===== Labels =====*/
	private Label SPEEDL = new Label("Speed", Label.CENTER); // label for speed scroll bar 
	private Label SIZEL = new Label("Size", Label.CENTER); // label for size scroll bar
	
			/* ===== MAIN ===== */
	public static void main(String[] args)
	{
		Bounce b = new Bounce();  // create object
		Thread t = new Thread(); // create thread
		new Thread(t).start();  // instantiates a new thread object
	}
	
	public Bounce() // bounce constructor
	{
		setLayout(null);  // use null layout of Frame
		setVisible(true); // make it visible
		MakeSheet();  // determine the sizes for the sheet
		try
		{
			initComponents(); // try to initialize the components
		}
		catch (Exception e){ e.printStackTrace();}
		SizeScreen();  // size the items on the screen
		start();
	}
	public void windowClosing(WindowEvent e)
	{
		stop();  // calls the stop method for exiting program
	}
	public void windowClosed(WindowEvent e){}
	public void windowOpened(WindowEvent e){}
	public void windowActivated(WindowEvent e){}
	public void windowDeactivated(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	
	private void MakeSheet()
	{
		I = getInsets();
		ScreenWidth = WinWidth-I.left-I.right; // make screen size the width of the frame less the left and right insets
		ScreenHeight = WinHeight - I.top - 2 * (BUTTONH + BUTTONHS) - I.bottom; // make screen height the height of the frame less the top and bottom insets and space for two rows of buttons and two button spaces
		setSize(WinWidth, WinHeight);  // set the frame size
		CENTER = (ScreenWidth/2);  // determine the center of the screen
		BUTTONW = ScreenWidth/11; // determine the width of the buttons( 11units)
		BUTTONS = BUTTONW/4; // determine the button spacing
		setBackground(Color.lightGray); //set the background Color
		ScrollBarW = 2 * BUTTONW; // determine the scroll bar width
	}
	public void initComponents() throws Exception, IOException
	{
		Start = new Button("Run");  // create the start button
		Shape = new Button("Circle"); // create the shape button
		Clear = new Button("Clear"); // create the clear button
		Tail = new Button("No Tail");  // create the tail button
		Quit = new Button("Quit");  // create the quit button
		SpeedScrollBar = new Scrollbar(Scrollbar.HORIZONTAL); // create the speed scroll bar
		SpeedScrollBar.setMaximum(SpeedSBmax); // set the max speed
		SpeedScrollBar.setMinimum(SpeedSBmin); // set the min speed
		SpeedScrollBar.setUnitIncrement(SBunit); // set the unit increment
		SpeedScrollBar.setBlockIncrement(SBblock); // set the block increment
		SpeedScrollBar.setValue(SpeedSBinit); // set the initial value
		SpeedScrollBar.setVisibleAmount(SBvisible); // set the visible size
		SpeedScrollBar.setBackground(Color.gray); // set the background color
		
		ObjSizeScrollBar = new Scrollbar(Scrollbar.HORIZONTAL); // create the size scroll bar
		ObjSizeScrollBar.setMaximum(MAXObj); // set the max speed
		ObjSizeScrollBar.setMinimum(MINObj); // set the min speed
		ObjSizeScrollBar.setUnitIncrement(SBblock); // set the unit increment
		ObjSizeScrollBar.setBlockIncrement(SBblock);
		ObjSizeScrollBar.setValue(SOBJ); // set the initial value
		ObjSizeScrollBar.setVisibleAmount(SBvisible); // set the visible size
		ObjSizeScrollBar.setBackground(Color.gray); // set the background color
		
		Obj = new Objc(SObj, ScreenWidth, ScreenHeight); // create the object
		Obj.setBackground(Color.white); // set the background color
		
		add(SpeedScrollBar); // add the speed scroll bar to the frame
		add(ObjSizeScrollBar); // add the size scroll bar to the frame
		add(SPEEDL); // add the speed label to the frame
		add(SIZEL); // add the size label to the frame
		add(Obj); // add the object to the frame
		SpeedScrollBar.addAdjustmentListener(this); // add the speed scroll bar listener
		ObjSizeScrollBar.addAdjustmentListener(this); // add the size scroll bar listener
		
		add("Center", Start);  // add the start button to the frame
		add("Center", Shape); // add the shape button to the frame
		add("Center", Tail); // add the tail button to the frame
		add("Center", Clear); // add the clear button to the Frame
		add("Center", Quit); // add the clear button to the Frame
		Start.addActionListener(this); // add the start button listener
		Shape.addActionListener(this); // add the shape button listener
		Tail.addActionListener(this); // add the tail button listener
		Clear.addActionListener(this); // add the clear button listener
		Quit.addActionListener(this);  // add the quit button listener
		this.addComponentListener(this); //add the component listener
		this.addWindowListener(this); // add the window listener
		setPreferredSize(new Dimension(WIDTH, HEIGHT));  // designates a preferred size from dimension object
		setMinimumSize(getPreferredSize()); // designates a minimum size from the preferred size
		setBounds(WinLeft, WinTop, WIDTH, HEIGHT);  // size and position frame
		validate();  // validate the layout
	}
	private void SizeScreen()
	{
				/*===== Positions the Buttons =====*/
		Start.setLocation(CENTER-2*(BUTTONW+ BUTTONS)-BUTTONW/2, ScreenHeight+BUTTONHS+I.top); // sets location for start button based upon default button and location of other controls
		Shape.setLocation(CENTER-BUTTONW-BUTTONS-BUTTONW/2, ScreenHeight+ BUTTONHS+I.top); // sets location for shape button based upon default button and location of other controls
		Tail.setLocation(CENTER-BUTTONW/2, ScreenHeight+BUTTONHS+I.top); // sets location for tail button based upon default button and location of other controls
		Clear.setLocation(CENTER+BUTTONS+BUTTONW/2,ScreenHeight+BUTTONHS+I.top); // sets location for clear button based upon default button and location of other controls
		Quit.setLocation(CENTER+BUTTONW+2*BUTTONS+BUTTONW/2, ScreenHeight+BUTTONHS+I.top); // sets location for quit button based upon default button and location of other controls
		
				/*===== Set Button Sizes =====*/
		Start.setSize(BUTTONW, BUTTONH); // assigns the default button sizes to the Start button
		Shape.setSize(BUTTONW, BUTTONH); // assigns the default button sizes to the Shape button
		Tail.setSize(BUTTONW, BUTTONH); // assigns the default button sizes to the Tail button
		Clear.setSize(BUTTONW, BUTTONH); // assigns the default button sizes to the Tail button
		Quit.setSize(BUTTONW, BUTTONH); // assigns the default button sizes to the quit button
		
			/*===== Scrollbar sizes/locations & Object bounds =====*/ 
		SpeedScrollBar.setLocation(I.left + BUTTONS, ScreenHeight + BUTTONHS + I.top); // sets location of the speed scrollbar based on button sizes and locations
		ObjSizeScrollBar.setLocation(WinWidth - ScrollBarW - I.right - BUTTONS, ScreenHeight + BUTTONHS + I.top); // sets location of the size scrollbar based on button sizes and locations
		SPEEDL.setLocation(I.left + BUTTONS, ScreenHeight + BUTTONHS + BUTTONH + I.top); // sets location of the speed label based on button and scrollbar sizes and locations
		SIZEL.setLocation(WinWidth - ScrollBarW - I.right, ScreenHeight + BUTTONHS + BUTTONH + I.top); // sets location of the size label based on button and scrollbar sizes and locations
		SpeedScrollBar.setSize(ScrollBarW, SCROLLBARH); // sets size of the speed scroll bar based on defaults previously established
		ObjSizeScrollBar.setSize(ScrollBarW, SCROLLBARH); // sets size of the size scroll bar based on defaults previously established
		SPEEDL.setSize(ScrollBarW, BUTTONH); // sets the speed label based on the scrollbar and button height
		SIZEL.setSize(ScrollBarW, SCROLLBARH); // sets the size label based on the scrollbar and button height
		Obj.setBounds(I.left, I.top, ScreenWidth, ScreenHeight); // sets the bounds of the play area
	}
	public void start()
	{
		if(thethread == null) // create a thread if it does not exist
		{
			thethread = new Thread(this); // create a new thread
			thethread.start(); // start the thread
		}
		Obj.repaint(); // repaint the object
	}
	public void stop()
	{
		PLOOP = false; // sets the run flag to false so the Thread loop can terminate
		thethread.interrupt(); // interrupts the thread
		Start.removeActionListener(this); //removes actionListener from start button
		Shape.removeActionListener(this); //removes actionListener from shape button
		Clear.removeActionListener(this); //removes actionListener from clear button
		Tail.removeActionListener(this); //removes actionListener from Tail button
		Quit.removeActionListener(this); //removes actionListener from quit button
		SpeedScrollBar.removeAdjustmentListener(this); //removes adjustmentListener from speed scroll bar
		ObjSizeScrollBar.removeAdjustmentListener(this); //removes adjustmentListener from size scroll bar
		this.removeComponentListener(this); //removes componentListener from the frame
		this.removeWindowListener(this); // removes windowlistener from the frame
		dispose(); // disposes and safely ends the program
		System.exit(0); // terminates any lingering program
	}
	public void componentResized(ComponentEvent e)
	{ 
		int TWid = WinWidth;  // creates temp width variable for object repositioning
		int THei = WinHeight; // creates temp height variable for object repositioning
		WinWidth = getWidth(); // sets new window width
		WinHeight = getHeight(); // sets new window height
		MakeSheet(); // adjusts the sheet
		SizeScreen(); // resizes the screen
		Obj.reSize(ScreenWidth, ScreenHeight); // computes new bounds based on updated window
		Obj.setX((Obj.getX() * WinWidth)/TWid); // proportionately adjusts the x position of the object on resize
		Obj.setY((Obj.getY() * WinHeight)/THei); // proportionately adjusts the y position of the object on resize
	}
	public void componentHidden(ComponentEvent e){}
	public void componentShown(ComponentEvent e){}
	public void componentMoved(ComponentEvent e){}
	public void actionPerformed(ActionEvent e)
	{
		Object source=e.getSource();
		if(source==Start) // if start button is pressed
		{
			if(Start.getLabel()=="Pause") // if current label is pause
			{
				Start.setLabel("Run"); // swaps the label to run
				TimePause = true; // When paused
				
			}
			else
			{
				Start.setLabel("Pause"); // resets label to pause
				TimePause = false; // when running
				start(); // calls the start method
			}
		}
		if(source==Shape) // if the Shape button is pressed
		{
			if(Shape.getLabel()=="Circle") // if the label is Circle
			{
				Shape.setLabel("Square"); // sets the label to square
				Obj.rectangle(false); // deactivates the rectangle flag
			}
			else
			{
				Shape.setLabel("Circle"); // set the label to Circle
				Obj.rectangle(true); // raises the rectangle flag
			}
			Obj.repaint(); // repaints new shape to the sheet
		}
		if(source==Tail) // if the Tail button is pressed
		{ 
			if(Tail.getLabel() =="Tail") // if tail label is Tail
			{
				Tail.setLabel("No Tail"); // sets the tail label to No Tail
				TailFlag = true; // tail button toggled on
			}
			else
			{
				Tail.setLabel("Tail"); // sets the tail label to Tail
				TailFlag = false; // tail button toggled off
			}
		}
		if(source==Clear) // if the Clear button is pressed
		{
			Obj.Clear(); // calls the clear method to clear previous tails
			Obj.repaint();
		}
		if (source==Quit) // if the Quit button is pressed
		{
			stop(); // calls and performs the stop method
		}
	} 
	public void adjustmentValueChanged(AdjustmentEvent e)
	{ 
		int TS; // creates scaler value for adjusting size
		Scrollbar sb = (Scrollbar)e.getSource(); // get the scroll bar that triggered the event
		if(sb == SpeedScrollBar) // if speed scrollbar is moved
		{
			Speed = e.getValue(); // sets the value of the scroll bar to speed variable
			Delay = (long)(DELAY * (SpeedSBmax-SBvisible - Speed + 1)/100);
			thethread.interrupt(); // interupts the thread to adjust the values
		} 
		if(sb == ObjSizeScrollBar) // if the size scrollbar is moved
		{
			TS = e.getValue(); // get the value
			TS = (TS/2) * 2 + 1; // Make odd to account for center position i.e. +
			if(Obj.CheckSize(TS) == true)
			{
				Obj.update(TS); // change the size in the drawing object
			}
			else
			{
				ObjSizeScrollBar.setEnabled(false); // blocks the movement of the scrollbar if it will send object off screen
			}
		}
		Obj.repaint(); // force a repaint
		ObjSizeScrollBar.setEnabled(true); // enables the size scroll bar
	}
	
	class Objc extends Canvas
	{
		private static final long serialVersionUID = 11L;
		private int ScreenWidth; // creates private integer value for screen width
		private int ScreenHeight; // creates private integer value for screen height
		private int SObj; // creates private integer value for object size
		private int x, y; // creates values for object positioning
		private boolean rect = true; // sets boolean flag for rectangle status
		private boolean clear = false; // sets boolean flag for clear functions
		
		public Objc(int SB, int w, int h)
		{
			ScreenWidth = w; //takes w value and assigns to screenwidth
			ScreenHeight = h; // assigns value h to screenheight
			SObj = SB; // takes object size and asigns to local value
			rect = true; // sets rect constructor flag
			clear = false; // sets clear constructor flag
			y = ScreenHeight/2; // sets initial position for object
			x = ScreenWidth/2; // sets initial positon for object
		}
		public void rectangle(boolean r)
		{
			rect = r; // returns the boolean of rectangle element
		}
		public void update(int NS)
		{
			SObj = NS; // reset the object size 
		}
		public void move() // controls movement of the object
		{
			// set the flag and tests edges
			if(y == ymax)
				ObjDown = !ObjDown; // complement down flag
			if(y == ymin)
				ObjDown = !ObjDown; // complement down flag
			if(x == xmax)
				ObjRight = !ObjRight; // complement right flag
			if(x == xmin)
				ObjRight = !ObjRight; // complement right flag

			// control movement of Object
			if(ObjDown)
				y -= 1; 
			else
				y += 1;
			if(ObjRight)
				x -= 1;
			else
				x += 1;
	
		} // close move method
		public void reSize(int w, int h)
		{ 
			ScreenWidth = w; // value for resize the sheet
			ScreenHeight = h; // value for resize the sheet
		}
		public void Clear()
		{
			clear = true; // sets the clear flag
		}
		public void paint(Graphics g)
		{
			g.setColor(Color.red); // sets color to red
			g.drawRect(0,0,ScreenWidth - 1, ScreenHeight - 1); // draws a rectangle around the play surface
			update(g); // updates the graphics
		}
		public void update(Graphics g)
		{
			if(!TailFlag)  // sets graphics to erase the tail if the tail flag is off 
			{
				g.setColor(getBackground());
				if(rect)
					g.fillRect(xold - (SObj - 1)/2, yold - (SObj - 1)/2, SObj, SObj); // fills the spot of the old rectangle with the background color
				else
					g.fillOval(xold - (SObj - 1)/2 - 1, yold - (SObj - 1)/2 - 1, SObj + 2, SObj + 2); // fills spot of the old oval with the background color
			} // at the end of update, update the old x and y values with the current x and y values
			if(clear)
			{
				super.paint(g);
				clear = false;
				g.setColor(Color.red); // set color to red
				g.drawRect(0, 0, ScreenWidth - 1, ScreenHeight - 1); // draw rectangle around play surface
			}
			if(rect)
			{
				g.setColor(Color.lightGray); // set color to light gray
				g.fillRect(x - (SObj - 1)/2, y - (SObj - 1)/2, SObj, SObj); // draws a grey rectangle
				g.setColor(Color.black); // sets color to black
				g.drawRect(x - (SObj - 1)/2, y - (SObj - 1)/2, SObj - 1, SObj - 1); // draws the shape outline
			}
			else
			{
				g.setColor(Color.lightGray); // set color to light gray
				g.fillOval(x - (SObj - 1)/2, y - (SObj - 1)/2, SObj, SObj); // draws grey oval
				g.setColor(Color.black); // set color to black 
				g.drawOval(x - (SObj - 1)/2, y - (SObj - 1)/2, SObj - 1, SObj - 1); // draws shape outline
			}
			yold = getY();
			xold = getX();
		}
		public int getX() // gets x value
		{
				return x; // get current x
		}
		public int getY() // accessor for y
		{
				return y;
		}
		public Object getSObj() // accessor for object size
		{
				return SObj;
		}
		public boolean CheckSize(int NS)
		{
			boolean SizeCheck = true;
			int x1 = getX() + (NS - 1)/2; // track right
			int x2 = getX() - (NS - 1)/2; // track left
			int y1 = getY() + (NS - 1)/2; // track bottom
			int y2 = getY() - (NS - 1)/2; // track top
			
			if(x1 > xmax || x2 < xmin || y1 > ymax || y2 < ymin) // if the leading edge of the object hits a wall
			{
				 SizeCheck = false; // throws flag to false
			}
			return SizeCheck; // returns the flag
		}
		public void CalcMinMax()
		{
				xmin = 0 + (SObj - 1)/2 + 1; // calc the min x value
				xmax = (ScreenWidth - 2) - (SObj - 1)/2; // calc the max x value
				ymin = 0 + (SObj - 1)/2 + 1; // calc the min y value
				ymax = (ScreenHeight - 2) - (SObj - 1)/2; // calc the max y value
		}
		public void setTailFlag() // complements the tail flag
		{
			if(TailFlag == true) // if tail flag is true
			{
				TailFlag = false; // set to false
			}
			else
			{
				TailFlag = true; // if it is false, set to true
			}
		}
		public boolean getTailFlag() // gets the current state of the tail flag
		{
			return TailFlag;
		}
		public void setX(int newX) // sets the x value
		{
			x = newX; // sets the x position of the object
		}
		public void setY(int newY) // sets y value
		{
			y = newY; // sets the y position of the object
		}
	} // end Objc class
	public void run() // method run for animation looping
	{
		
		while(PLOOP == true)
		{
			if(TimePause == true) // if it is paused
			{
				try
				{
					Thread.sleep(1); // sleep for 1 mili second without moving object
				}
				catch (InterruptedException e) {}
			}
			else
			{
				Started = true; // set started flag to true
				try
				{
					Thread.sleep(Delay); // sleep for the delay value previously established
					
				}
				catch (InterruptedException e) {}
				Obj.CalcMinMax(); // calculate the minimum and maximum values for position
				Obj.repaint(); // repaint the object
				Obj.move(); // move the object
			}
			try
			{
				Thread.sleep(1); // small delay outside of loop to interrupt loop in pause mode
			}
			catch(InterruptedException e){}
		} // end animation loop	
	} // end run method
} // end bounce class
