package BouncingBall;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/* Program Bouncing Ball
 * CET 350 Technical Computing using Java
 * Andrew Spate & Nicholas Spudich
 * Spa3195@calu.edu & spu8504@calu.edu
 * Group 2
 */

public class BouncingBall extends Frame implements MouseMotionListener, MouseListener, WindowListener, ComponentListener, ActionListener, AdjustmentListener, Runnable
{
	private static final long serialVersionUID = 1L;
	
					/*====== Initializer values for Window ======*/
	private static final Rectangle ZERO = new Rectangle(0, 0, 0, 0); // zero rectangle
	private final int EXPAND = 5; // buffer on resize
	
					/* ===== Mouse Rect and Points ====== */
	private Point FrameSize = new Point(640, 400); // initial Frame size
	private Point Screen = new Point(FrameSize.x - 1, FrameSize.y - 1); // drawing screen size
	private Point m1 = new Point(0,0); // first mouse point
	private Point m2 = new Point(0,0); // second mouse point
	private Rectangle Perimeter = new Rectangle(0, 0, Screen.x, Screen.y); // bouncing perimeter
	private Rectangle db = new Rectangle(); // drag box rectangle
	private Vector<Rectangle> Walls = new Vector <Rectangle>();
	
					/* ====== Frame Sizing ====== */
	private int WinTop = 10; // top of frame
	private int WinLeft = 10; // left side of frame
	private int WinWidth = FrameSize.x; // initial frame width
	private int WinHeight = FrameSize.y; // initial frame height
	private int CENTER = (WinWidth/2); // initial screen center	
	
					/*====== Object Size/Speed Max/Min =====*/
	private final int MAXObj = 100; // maximum object size
	private final int MINObj = 10; // minimum object size
	private final int SOBJ = 21; // initial object width
	private int SBall = SOBJ; // initial object width
	private final int SPEED = 50;  // initial obj speed
	
					/*====== Scroll Bar variables ======*/
	private int BUTTONH = 20;
	private final int SBvisible = 10; // visible Scroll Bar
	private final int SBunit = 1; // Scroll Bar unit step size
	private final int SBblock = 10; // Scroll Bar block step size
	private int SpeedSBmin = 1; // speed Scroll Bar minimum value
	private int SpeedSBmax = 100 + SBvisible; // speed Scroll Bar maximum value with visible offset
	private int SpeedSBinit = SPEED; // initial speed Scroll Bar value
	int Speed = SPEED; // integer for Scrollbar Speed
	private final double DELAY = 50; // timer delay constant
	long Delay = (SpeedSBmax - SBvisible - SPEED); // int for current time delay
	
					/* ====== Booleans ====== */
	boolean PLOOP = true; // bool for program loop, run ; initialized as true so it does not quit
	boolean TimePause = true; // bool to identify run/pause ; initialized as true so the animation is paused when the program starts
	boolean Started = false; // bool to identify the animation started
	boolean ObjRight = false; // bool to handle right direction
	boolean ObjLeft = false; // bool to handle left direction
	boolean ObjDown = false; // bool to handle up and down directions
	boolean ok = true;	// bool to handle while loop
	
					/*===== Objects =====*/
	private Ballc Ball; // object to draw/buffer
	Button Start, Pause, Quit; //buttons
	Scrollbar SpeedScrollBar, ObjSizeScrollBar; // scroll bars
	private Thread thethread; // thread for timer delay
	
					/*===== Labels =====*/
	private Label SPEEDL = new Label("Speed", Label.CENTER); // label for speed scroll bar 
	private Label SIZEL = new Label("Size", Label.CENTER); // label for size scroll bar
	
					/* ===== Panels ===== */
	private Panel control = new Panel(); // control panel
	private Panel sheet = new Panel(); // sheet panel, drawing object, Ball
	
					/* ===== MAIN ===== */
	public static void main(String[] args)
	{
		BouncingBall b = new BouncingBall();  // create object
		Thread t = new Thread(); // create thread
		new Thread(t).start(); // starts the thread
	}
	
	BouncingBall() // BouncingBall constructor
	{
        MakeSheet();  // determine the sizes for the sheet
		try
		{
			initComponents(); // try to initialize the components
		}
		catch (Exception e){ e.printStackTrace();}
		start();	
	}
	
	class Ballc extends Canvas	// drawing class, Ballc
	{
		private static final long serialVersionUID = 2L;
		private int SBall;
		private int x, y;
		private Rectangle ball;
		Rectangle r = new Rectangle(ZERO);
		
		Graphics g;
		Image buffer; 
	
		public Ballc(int SB) // drawing constructor, Ballc
		{
			SBall = SB;
			y = Screen.y/2;
			x = (Screen.x)/2;
			this.ball = new Rectangle(x, y, SBall, SBall);
			ball.grow(1, 1);
		}
		public void update(int NS)
		{
			//SBall = NS; // sets new size for object
			setballHeight(NS);
			setballWidth(NS);
		}
		public void move() // controls movement of the object
		{
			int i = 0;
			// set the flag and tests edges
			Perimeter.setBounds(0, 0, Screen.x - 1, Screen.y - 1);
			if(ball.getMaxY() == Perimeter.getMaxY())
			{
				ObjDown = !ObjDown; // complement down flag
			}
			if(ball.getMinY() == Perimeter.getMinY())
			{
				ObjDown = !ObjDown; // complement down flag
				
			}
			if(ball.getMaxX() == Perimeter.getMaxX())
			{
				ObjRight = !ObjRight; // complement right flag
			}
			if(ball.getMinX()  == Perimeter.getMinX())
			{
				ObjRight = !ObjRight; // complement right flag
			}
			
			while((i < Walls.size()))
	            {
	                r = Walls.elementAt(i);
	                if (ball.intersects(r))
	                {	
	                	if(ball.getMinY() <= r.getMinY() || ball.getMaxY() >= r.getMaxY())	
	                	{
	                		ObjDown = !ObjDown;
	                	}	
	                	if(ball.getMinX() <= r.getMinX() || ball.getMaxX() >= r.getMaxX())
	                	{
	                		ObjRight = !ObjRight;
	                	}
	                }		    
	                i++;
	            }
			// controls movement of Object
			if(ObjDown)
			{
				ball.y -= 1; // moves obj down
			}
			else
			{
				ball.y += 1; // moves obj up
			}
			if(ObjRight)
			{
				ball.x -= 1; // moves obj left
			}
			else
			{
				ball.x += 1; // moves obj right
			}
		} // close move method
		public void reSize(Point r)
		{ 
			WinWidth = (int) r.getX(); // gets new width, saves in Screen's x
			WinHeight = (int) r.getY(); // gets new height, saves in Screen's y
		}
		public void paint(Graphics cg)
		{
			buffer = createImage(Screen.x, Screen.y);
			if(g != null) // check if g exists
			{
				g.dispose(); // remove g
			}
			g = buffer.getGraphics(); // get graphics
			g.setColor(Color.blue); // paint border
			g.drawRect(0,0,Screen.x - 1, Screen.y - 1);
			g.setColor(Color.red); // paint ball
			g.fillOval(ball.x, ball.y, ball.width, ball.height);
			g.setColor(Color.black);
			g.drawOval(ball.x, ball.y, ball.width, ball.height);
			g.drawRect(db.x, db.y, db.width, db.height);
			for(int i = 0; i < Walls.size(); i++)
			{
				Rectangle temp = Walls.elementAt(i);
				g.fillRect(temp.x, temp.y, temp.width, temp.height);
			}
			cg.drawImage(buffer,  0,  0,  null); //switch the graphics
		}
		public void update(Graphics g)
		{
			paint(g); // override update to call paint; stops flickering from occuring because we used repaint 
		}
		public int getX() // gets x value
		{
				return x; // get current x
		}
		public int getY() // accessor for y
		{
				return y; // get current y
		}
		public Object getSBall() // accessor for object size
		{
				return SBall; // get current obj size
		}
		public void setballHeight(int NH)
		{
			ball.height = NH;
		}
		public void setballWidth(int NW)
		{
			ball.width = NW;
		}
		public Rectangle BallRec()
		{
			return ball;
		}
		public void setX(int newX) // sets the x value
		{
			x = newX;
		}
		public void setY(int newY) // sets y value
		{
			y = newY;
		}
		/* =============================================================================================================*/
						/* ====== Rectangle Mutators ===== */
		public void AddOne(Rectangle r)
		{
			Walls.addElement(new Rectangle(r));
		}
		public void removeElementAt(int i)
		{
			Walls.removeElementAt(i);
		}
		
						/* ====== Rectangle Accessors ====== */
		public Rectangle getOne(int i)
		{
			return Walls.elementAt(i); // gets i'th element of walls
		}
		public int getWallSize()
		{
			return Walls.size(); // gets the size of vector walls
		}
						/* ======= Rectangle Check ======= */
		public void RectangleToucher()
		{
			Rectangle b = new Rectangle(ball);
			b.grow(1,  1);
			int i = 0;
			while((i < Walls.size() && ok))
			{
				r = Walls.elementAt(i);
				if(r.intersects(b))
				{
					ok = false;
				}
				else
				{
					i++;
				}
			}
		}
		public void setDragBox(Rectangle db) 
		{
			if(db.y < Perimeter.getMinY())
			{
				db.y = (int) Perimeter.getMinY();
			}
			if(db.y > Perimeter.getMaxY() - SBall)
			{
				db.y = (int) Perimeter.getMaxY();
			}
			if(db.x < Perimeter.getMinX())
			{
				db.x = (int) Perimeter.getMinX();
			}
			if(db.x > Perimeter.getMaxX() - SBall)
			{
				db.x = (int) Perimeter.getMaxX();
			}
		}
	} // end Ballc class
	private void MakeSheet()
	{
		//Screen.x = WinWidth; // make screen size the width of the frame less the left and right insets
		//Screen.y = WinHeight; // make screen height the height of the frame less the top and bottom insets and space for two rows of buttons and two button spaces
		//setSize(WinWidth, WinHeight);  // set the frame size
		CENTER = (WinWidth/2);  // determine the center of the screen
		setBackground(Color.lightGray); //set the background Color
	}
	public void initComponents() throws Exception, IOException
	{
		/* ===== Button Creation ===== */
		Start = new Button("Run");  // create the start button
		Pause = new Button("Pause"); // create the shape button
		Quit = new Button("Quit");  // create the quit button
		
		/* ====== SpeedScrollBar Creation ===== */
		SpeedScrollBar = new Scrollbar(Scrollbar.HORIZONTAL); // create the speed scroll bar
		SpeedScrollBar.setMaximum(SpeedSBmax); // set the max speed
		SpeedScrollBar.setMinimum(SpeedSBmin); // set the min speed
		SpeedScrollBar.setUnitIncrement(SBunit); // set the unit increment
		SpeedScrollBar.setBlockIncrement(SBblock); // set the block increment
		SpeedScrollBar.setValue(SpeedSBinit); // set the initial value
		SpeedScrollBar.setVisibleAmount(SBvisible); // set the visible size
		SpeedScrollBar.setBackground(Color.gray); // set the background color
		
		/* ===== ObjScrollBar Creation ===== */
		ObjSizeScrollBar = new Scrollbar(Scrollbar.HORIZONTAL); // create the size scroll bar
		ObjSizeScrollBar.setMaximum(MAXObj); // set the max speed
		ObjSizeScrollBar.setMinimum(MINObj); // set the min speed
		ObjSizeScrollBar.setUnitIncrement(SBblock); // set the unit increment
		ObjSizeScrollBar.setBlockIncrement(SBblock);
		ObjSizeScrollBar.setValue(SOBJ); // set the initial value
		ObjSizeScrollBar.setVisibleAmount(SBvisible); // set the visible size
		ObjSizeScrollBar.setBackground(Color.gray); // set the background color
		
		/* ====== Mouse Locations ===== */
		m1.setLocation(0, 0);
		m2.setLocation(0, 0);
		
		/* ===== Obj Creation ===== */
		Ball = new Ballc(SBall); // create the object
		Ball.setBackground(Color.white); // set the background color
		Perimeter.setBounds(0, 0, Screen.x, Screen.y);
		Perimeter.grow(-1, -1);
		
		/* ====== Layout Management ====== */
		setLayout(new BorderLayout()); // set border layout for the Frame
		sheet.setLayout(new BorderLayout(0, 0)); // create border layout for the Sheet
		
/* =============================================================================================================*/	
		/* ===== GridBagLayout ===== */
		GridBagConstraints c = new GridBagConstraints();
		GridBagLayout gbl = new GridBagLayout();
		control.setSize(FrameSize.x, 2 * BUTTONH);
		
		
	    double colWeight[]={1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
	    double rowWeight[]={1,2,2,1};
	    int colWidth[]={1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
	    int rowHeight[]={1,2,2,1};
	    
	    gbl.rowHeights = rowHeight;
	    gbl.columnWidths = colWidth;
	    gbl.columnWeights = colWeight;
	    gbl.rowWeights = rowWeight;
	    
	    c.anchor = GridBagConstraints.CENTER;
	    c.weightx = 1; // spacing for columns
	    c.weighty = 1; // spacing for rows
	    c.gridwidth = 3; // specifies the number for columns in the display
	    c.gridheight = 1; // specifies the number of rows in the display
	    c.fill = GridBagConstraints.BOTH; // fills display
	    
	    /* ===== ScrollBars ===== */
	    c.gridx = 1; 
	    c.gridy = 1; 
	    gbl.setConstraints(SpeedScrollBar, c);
	    control.add(SpeedScrollBar);
	    
	    c.gridx = 12;
	    gbl.setConstraints(ObjSizeScrollBar, c);
	    control.add(ObjSizeScrollBar);
	    
	    /* ===== Buttons ===== */
	    c.gridx = 5;
	    c.gridwidth = 2;
	    gbl.setConstraints(Start, c);
	    control.add(Start);
	    
	    c.gridx = 7;
	    gbl.setConstraints(Pause, c);
	    control.add(Pause);
	    
	    c.gridx = 9;
	    gbl.setConstraints(Quit, c);
	    control.add(Quit);

	    /* ===== Labels ====== */
	    c.gridy = 2;
	    c.gridx = 2;
	    c.gridwidth = 1;
	    gbl.setConstraints(SPEEDL,c);
	    control.add(SPEEDL);
	    
	    c.gridx= 13;
	    gbl.setConstraints(SIZEL,c);
	    control.add(SIZEL);
	    
	    control.setLayout(gbl);
	    control.setVisible(true);
/* =============================================================================================================*/	    
		/* ===== Adding eveything to the Frame ===== */
	    sheet.add("Center", Ball);
	    sheet.setVisible(true);
		add("Center", sheet); // add the sheet panel to the center of frame
		add("South", control); // add the control panel to the south of the frame

		/* ===== Add to the AdjustmentListener ===== */
		SpeedScrollBar.addAdjustmentListener(this); // add the speed scroll bar listener
		ObjSizeScrollBar.addAdjustmentListener(this); // add the size scroll bar listener
		
		/* ====== Add MouseListener ====== */
		Ball.addMouseMotionListener(this);
		Ball.addMouseListener(this);
		
		/* ===== Add to the ActionListener ===== */
		Start.addActionListener(this); // add the start button listener
		Pause.addActionListener(this); // add the shape button listener
		Quit.addActionListener(this);  // add the quit button listener
		
		/* ===== Add to ComponentListener ===== */
		this.addComponentListener(this); //add the component listener
		
		/* ===== Add to WindowListener ===== */
		this.addWindowListener(this); // add the window listener
		
		/* ===== Screen Manipulation ===== */
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setMinimumSize(getPreferredSize());
		setBounds(WinLeft, WinTop, WinWidth, WinHeight);  // size and position frame
		Pause.setEnabled(false);
		setVisible(true);
		validate();  // validate the layout
	}
	public Rectangle getDragBox(MouseEvent e)
	{
		int height = 0, width = 0;
		Rectangle DragBox = new Rectangle();
		m2.setLocation(e.getPoint());
		int w = Math.min(m1.x, m2.x);
		int w1 = Math.max(m1.x, m2.x);
		int h = Math.min(m1.y, m2.y);
		int h1 = Math.max(m1.y, m2.y);
		width = w1 - w;
		height = h1 - h;
		DragBox.setBounds(w, h, width, height);
		return DragBox;
	}
	public void start()
	{
		if(thethread == null) // create a thread if it does not exist
		{
			thethread = new Thread(this); // create a new thread
			thethread.start(); // start the thread
		}
		Ball.repaint(); // repaint the object
	}
	public void stop()
	{
		PLOOP = false; // sets the run flag to false so the Thread loop can terminate
		thethread.interrupt(); // interrupts the thread
		Start.removeActionListener(this);
		Pause.removeActionListener(this);
		Quit.removeActionListener(this);
		SpeedScrollBar.removeAdjustmentListener(this);
		ObjSizeScrollBar.removeAdjustmentListener(this);
		Ball.removeMouseListener(this);
		Ball.removeMouseMotionListener(this);
		this.removeComponentListener(this);
		this.removeWindowListener(this);
		dispose();
		System.exit(0);
	}
	/* ====== Thread Handler ===== */
	public void run() {
		while(PLOOP == true)
		{
			if(TimePause == true)
			{
				try
				{
					Thread.sleep(2);
				}
				catch (InterruptedException e) {}
			}
			else
			{
				Started = true; // set started flag to true
				try
				{
					Thread.sleep(Delay);
					
				}
				catch (InterruptedException e) {}
				Ball.repaint(); // repaint the object
				Ball.move();
			}
			try
			{
				Thread.sleep(2); // small delay outside of loop to interrupt loop in pause mode
			}
			catch(InterruptedException e){}
		} // end animation loop	
	}
	/* ===== AdjustmentListener Handler ===== */
	public void adjustmentValueChanged(AdjustmentEvent e) {
		int TS, half, i;
		ok = true;
		Rectangle b = new Rectangle();
		Scrollbar sb = (Scrollbar)e.getSource(); // get the scroll bar that triggered the event
		if(sb == SpeedScrollBar) 
		{
			Speed = e.getValue();
			Delay = (long)(DELAY * (SpeedSBmax-SBvisible - Speed + 1)/100);
			thethread.interrupt();
		} 
		if(sb == ObjSizeScrollBar)
		{
			TS = e.getValue(); // get the value
			TS = (TS/2) * 2 + 1; // Make odd to account for center position i.e. +
			half = (TS - 1)/2;
			b.setBounds(Ball.x - half - 1, Ball.y - half - 1, TS + 2, TS + 2);
			
			int x1 = (int) (Ball.ball.getMaxX() + (TS - 1)/2); // track right
			int x2 = (int) (Ball.ball.getMinX() - (TS - 1)/2); // track left
			int y1 = (int) (Ball.ball.getMaxY() + (TS - 1)/2); // track bottom
			int y2 = (int) (Ball.ball.getMinY() - (TS - 1)/2); // track top
			if(x1 > Perimeter.getMaxX() || x2 < Perimeter.getMinX() || y1 > Perimeter.getMaxY() || y2 < Perimeter.getMinY())
			{
				 ok = false;
				 System.out.println("PerimeterCheck");
			}
			i = 0; // start with the first rectangle
			while(i < Ball.getWallSize() && ok)
			{
				Rectangle t = Ball.getOne(i); // get the ith rectangle
				if(t.intersects(b)) // check for intersection
				{
					ok = false;
				}
				else
				{
					i++;
				}
			}
			if(ok)
			{
				Ball.update(TS); // update to new ball size
			}
			//thethread.interrupt();
		}
		Ball.repaint(); // force a repaint
	}
	/* ===== ActionListener Handler ===== */
	public void actionPerformed(ActionEvent e) {
		Object source=e.getSource();
		if(source==Start)
		{
			if(Start.getLabel()=="Run")
			{
				thethread.interrupt();
				Pause.setEnabled(true);
				Start.setEnabled(false);
				TimePause = false; // When paused
			}
		}
		if(source== Pause)
		{
			if(Pause.getLabel()=="Pause")
			{
				thethread.interrupt();
				Start.setEnabled(true);
				Pause.setEnabled(false);
				TimePause = true; // when running
				start();
			}
		}
		if (source==Quit)
		{
			stop();
		}	
	}
	/* ===== Component Handlers ===== */
	public void componentResized(ComponentEvent e) 
	{
		Rectangle t = new Rectangle();
		int i = 0;
		if(Ball.getWallSize() > 0) // checks if there is anything in the vector
		{
			t.setBounds(Ball.getOne(i)); // get the Oth rectangle
			int mr = t.x + t.width; // initialize max right
			int mb = t.y + t.height; // initialize max bottom
			i++; // increment i
			while(i < Ball.getWallSize())
			{
				t.setBounds(Ball.getOne(i)); // get the i'th rectangle
				mr = Math.max((t.x + t.width), mr); //keep max right
				mb = Math.max((t.y + t.height), mb); // keep max bottom
				i++;
			}
			t.setBounds(Ball.ball); // process the ball
			mr = Math.max((t.x + t.width), mr);
			mb = Math.max((t.y + t.height), mb);
			if(mr > WinWidth || mb > WinHeight)
			{
				int w = Math.max((mr + EXPAND), WinWidth);
				int l = Math.max((mb + EXPAND), WinHeight) + 2*BUTTONH;
				setSize(w, l);
				setExtendedState(NORMAL);
				//setExtendedState(ICONIFIED);
			}
		}
		Screen.setLocation(sheet.getWidth() - 1, sheet.getHeight() - 1); // update the Screen Point
		Perimeter.setBounds(getX(), getY(), Screen.x, Screen.y); // update the perimeter Rectangle
		Perimeter.grow(-1, -1); // shrink for border
		Ball.reSize(Screen); // resize the ball screen
		Ball.repaint(); // repaint
	}
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentHidden(ComponentEvent e) {}

	/*===== Window Handlers ===== */
	public void windowOpened(WindowEvent e) {}
	public void windowClosing(WindowEvent e) 
	{
		stop();
	}
	public void windowClosed(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	
	/* ===== Mouse Handlers ===== */
	public void mouseClicked(MouseEvent e) 
	{
		Point p = new Point(e.getX(), e.getY()); // get the mouse clicked position and create a point
		Rectangle b = new Rectangle();
		int i = 0;
		while(i < Walls.size()) // cycle through the vector
		{
			b = Ball.getOne(i);
			if(b.contains(p))
			{
				Ball.removeElementAt(i);
				db = new Rectangle();
			}
			else
			{
				i++;
			}
		}
	}
	public void mousePressed(MouseEvent e) 
	{
		m1.setLocation(e.getPoint());
	}
	public void mouseReleased(MouseEvent e) 
	{
		Rectangle b = new Rectangle(Ball.ball); // ball rectangle
		b.grow(1, 1);
		if(Ball.r.intersects(b)) // check if ball intersects with the rectangle
		{
			// don't store the rectangle
		}
		else if(db.contains(Ball.ball))
		{
			// don't create rect
		}
		else if(Perimeter.intersects(db)) // check if the rect exceeds the perimeter
		{
			if(db.getMaxX() > Perimeter.getMaxX())
			{
				db.width = (int) (Perimeter.getMaxX() - m1.x - EXPAND); 
			}
			if(db.getMaxY() > Perimeter.getMaxY())
			{
				db.height = (int) (Perimeter.getMaxY() - m1.y - EXPAND);
			}
			if(db.getMinX() < Perimeter.getMinX())
			{
				db.x = EXPAND;
				db.width = m1.x - (int) (Perimeter.getMinX() + EXPAND); 
			}
			if(db.getMinY() < Perimeter.getMinY())
			{
				db.y = EXPAND;
				db.height = m1.y - (int) (Perimeter.getMinY() + EXPAND);
			}
			Ball.AddOne(db);
		}
		else if(b.intersection(db).equals(db)) // check if the new rect is covered in the vector
		{
			int i = 0;
			Rectangle temp = new Rectangle();
			while(i < Walls.size())
			{
				temp = Walls.elementAt(i);
				if(temp.contains(db))
				{
					Walls.removeElementAt(i);// delete the Rectangle in the Vector
				}
				else
				{
					i++;
				}
				// not store the rectangle
			}
		}
		else if(b.intersection(db).equals(b)) // check new rect in vector
		{	int i = 0;
			Rectangle temp = new Rectangle();
			while(i < Walls.size())
			{
				temp = Walls.elementAt(i);
				if(db.contains(temp))
				{
					Walls.removeElementAt(i);// delete the Rectangle in the Vector
				}
				else
				{
					i++;
				}
			}
			Ball.AddOne(db); // move to next element
		}
		else 
		{
			Ball.AddOne(db); // add it to the Vector
		}
	}
	public void mouseEntered(MouseEvent e) 
	{
		Ball.repaint();
	}
	public void mouseExited(MouseEvent e) {}
	public void mouseDragged(MouseEvent e) 
	{
		db.setBounds(getDragBox(e));
		if(Perimeter.contains(db))
		{
			Ball.setDragBox(db);
			Ball.repaint();
		}
	}
	public void mouseMoved(MouseEvent e) {}	
} // end BouncingBall class
