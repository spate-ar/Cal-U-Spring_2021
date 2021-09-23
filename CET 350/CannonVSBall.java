package CannonVSBall;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/* Program CannonVSBall
 * CET 350 Technical Computing using Java
 * Andrew Spate & Nicholas Spudich
 * Spa3195@calu.edu & spu8504@calu.edu
 * Group 2
 */

public class CannonVSBall implements ActionListener, WindowListener, ItemListener, ComponentListener, AdjustmentListener, MouseListener, MouseMotionListener, Runnable
{
	private final int EXPAND = 5;
	/* ====== Frame Components ===== */
	private final int WinLeft = 10;
	private final int WinTop = 10;
	private Frame EditorFrame; // frame for application
	private Point FrameSize = new Point(680, 420); // frame size
	private Point Screen = new Point(FrameSize.x - 1, FrameSize.y - 1); // create screen
	private Rectangle Perimeter = new Rectangle(0, 0, Screen.x, Screen.y); // create perimeter
	private Rectangle t = new Rectangle();
	private Insets I;
	private int n = 0;
	
	/* ===== Player/Ball Score Variables ===== */
	private static int PS = 0;
	private static int BS = 0;

	/* ====== Panels ====== */
	private Panel control = new Panel(); // create the bottom control panel
	private Panel sheet = new Panel(); // create the drawing sheet panel
	
	/* ===== Menu Bar ===== */
	private MenuBar MMB; // the menu bar
	private Menu FILE, ENVIROMENT, PARAMETERS; // main items on menu bar
	private Menu SIZE, SPEED; // sub-menu items under menu items
	private MenuItem QUIT, RUN, PAUSE, RESTART; // menu item
	
	/* ===== Checkbox Menu Items ===== */
	private CheckboxMenuItem ExtraSmall, Small, Medium, Large, ExtraLarge; // checkbox menu items for size
	private CheckboxMenuItem SLOWEST, SLOWER, REGULAR, FASTER, FASTEST; // checkbox menu items for font
	private CheckboxMenuItem Mercury, Venus, Earth, Moon, Mars, Jupiter, Saturn, Uranus, Neptune, Pluto;
	
	/* ====== Define Checkbox Menu Items ====== */
	private float MercuryG = (float) 3.7;
	private float VenusG = (float) 8.87;
	private float EarthG = (float) 9.798;
	private float MoonG = (float) 1.62;
	private float MarsG = (float) 3.71;
	private float JupiterG = (float) 24.92;
	private float SaturnG = (float) 10.44;
	private float NeptuneG = (float) 11.15;
	private float UranusG = (float) 8.87;
	private float PlutoG = (float) 0.58;
	private float gravity = EarthG;
	
	private final float SIZES = (float) 50;
	private float ExtraSmallS = (float)SIZES/4;
	private float SmallS = (float)SIZES/2;
	private float MediumS = (float)SIZES;
	private float LargeS = (float) SIZES * 2;
	private float ExtraLargeS = (float)SIZES * 4;
	private float size = MediumS;
	
	private float SPEEDS = (float) 1;
	private float SLOWESTS = (float) SPEEDS*4;
	private float SLOWERS = (float) SPEEDS*2;
	private float REGULARS = SPEEDS;
	private float FASTERS = (float) SPEEDS/2;
	private float FASTESTS = (float) SPEEDS/4;
	
	/* ====== Angle/Velocity ScrollBars ===== */
	private final int SBvisible = 10; // visible Scrollbar
	
	private final int AngleSBunit = 1; // angle scrollbar unit step size
	private final int AngleSBblock = 10; // angle scrollbar block step size
	private int AngleSBmin = 0; // angle Scrollbar minimum value
	private int AngleSBmax = 90 + SBvisible; // Angle Scrollbar maximum value with visible offset
	private int Angle = 45;
	
	private final int VelocitySBunit = 10; // velocity scrollbar unit step size
	private final int VelocitySBblock = 100; // velocity scrollbar block step size
	private int VelocitySBmin = 100;
	private int VelocitySBmax = 1200 + SBvisible;
	private int Velocity = 100;

	/* ===== Labels ===== */
	private Label VELOCITY = new Label("Velocity", Label.CENTER); // label for velocity scroll bar
	private Label ANGLE = new Label("Cannon Angle", Label.CENTER); // label for angle scrollbar
	private Label BALL = new Label("Ball Score: ", Label.CENTER); // label for ball score
	private Label PLAYER = new Label("Your Score: ", Label.CENTER); // label for player score
	private static Label Message= new Label ();
	
	static String GONE= "Thats not coming back";
	static String BallPt= "The ball scored a point!";
	static String PlayerPt= "You scored a point!";
			
	/* ===== TextFields ===== */
	private static TextField PlayerScore = new TextField(10);
    private static TextField BallScore = new TextField(10);
    private TextField CurAngle = new TextField(10);
    private TextField CurVelocity = new TextField(10);
    
	/* ===== Objects ===== */
	private Scrollbar VelocityScrollBar, AngleScrollBar; // scrollbars
	private Ballc Ball; // object to draw/buffer
	long Delay = (long) REGULARS; // handles ball speed
	private Polygon poly = new Polygon();
	private Thread thethread;
    
	/* ===== Booleans ===== */
    private boolean KeepLooping = true; // handles main loop
    private static boolean Started = false; // handles run/pause

    /* ===== Main ===== */
	public static void main(String[] args)
	{
		new CannonVSBall();
		Thread t = new Thread();
		new Thread(t).start();
	}
	/* ===== CannonVSBall Constructor ===== */
	public CannonVSBall()
	{
		EditorFrame = new Frame("Cannon Versus Ball"); // frame title/creation
		EditorFrame.setLayout(new BorderLayout()); // sets borderlayout for frame
		EditorFrame.setBounds(WinLeft, WinTop, FrameSize.x, FrameSize.y); // sets the Frame size
		
		/* ===== MenuBar Creation ===== */
		MMB = new MenuBar(); // create the menu bar
		FILE = new Menu("FILE"); // create first menu entry for menu bar
		ENVIROMENT = new Menu("ENVIROMENT"); // create second menu entry for menu bar
		PARAMETERS= new Menu("PARAMETERS");
		SIZE = new Menu("Size"); // create first menu entry for Text Menu
		SPEED = new Menu("Speed"); // create second menu entry for Text menu
		
		/* == Add Sub-menus to Parameters Menu ===== */
		PARAMETERS.add(SIZE); // add Size to Parameters
		PARAMETERS.add(SPEED); // add Speed to Parameters
		
		/* ===== Add Menu's to MenuBar ===== */
		MMB.add(FILE); // add file to menu bar
		MMB.add(PARAMETERS); //add Text to menu bar
		MMB.add(ENVIROMENT);
		
		/* ===== File MenuBar Settings ===== */
		RUN = FILE.add(new MenuItem("Run", new MenuShortcut(KeyEvent.VK_R)));
		FILE.addSeparator();
		PAUSE = FILE.add(new MenuItem("Pause", new MenuShortcut(KeyEvent.VK_P)));
		FILE.addSeparator();
		RESTART = FILE.add(new MenuItem("Restart"));
		FILE.addSeparator(); // add separator
		QUIT = FILE.add(new MenuItem("Quit", new MenuShortcut(KeyEvent.VK_Q)));
		
		/* ===== Environment MenuBar Settings ===== */
		ENVIROMENT.add(Mercury= new CheckboxMenuItem("Mercury"));
		ENVIROMENT.add(Venus= new CheckboxMenuItem("Venus"));
		ENVIROMENT.add(Earth= new CheckboxMenuItem("Earth"));
		ENVIROMENT.add(Moon= new CheckboxMenuItem("Moon"));
		ENVIROMENT.add(Mars= new CheckboxMenuItem("Mars"));
		ENVIROMENT.add(Jupiter= new CheckboxMenuItem("Jupiter"));
		ENVIROMENT.add(Saturn= new CheckboxMenuItem("Saturn"));
		ENVIROMENT.add(Uranus= new CheckboxMenuItem("Uranus"));
		ENVIROMENT.add(Neptune= new CheckboxMenuItem("Neptune"));
		ENVIROMENT.add(Pluto= new CheckboxMenuItem("Pluto"));
		
		/* ====== Size MenuBar Settings ===== */
		SIZE.add(ExtraSmall = new CheckboxMenuItem("Extra Small")); // CheckboxMenuItem size 10
		SIZE.add(Small = new CheckboxMenuItem("Small")); // CheckboxMenuItem size 14
		SIZE.add(Medium = new CheckboxMenuItem("Medium")); // CheckboxMenuItem size 18
		SIZE.add(Large = new CheckboxMenuItem("Large"));
		SIZE.add(ExtraLarge = new CheckboxMenuItem("Extra Large"));
		
		/* ===== Speed MenuBar Settings ===== */
		SPEED.add(SLOWEST = new CheckboxMenuItem("Slowest"));
		SPEED.add(SLOWER = new CheckboxMenuItem("Slower"));
		SPEED.add(REGULAR = new CheckboxMenuItem("Normal"));
		SPEED.add(FASTER = new CheckboxMenuItem("Faster"));
		SPEED.add(FASTEST = new CheckboxMenuItem("Fastest"));
		
		/* ===== Initializers for Start-up ===== */
		Earth.setState(true);
		Medium.setState(true);
		REGULAR.setState(true); 
		
		/* ===== Add ActionListeners to File Menu Items ===== */
		QUIT.addActionListener(this); // add listener for File Menu
		RUN.addActionListener(this);
		PAUSE.addActionListener(this);
		RESTART.addActionListener(this);
		
		/* ===== Add ItemListeners to CheckBox Menu Items ===== */
		ExtraSmall.addItemListener(this); // add listener for font sizes
		Small.addItemListener(this);
		Medium.addItemListener(this);
		Large.addItemListener(this);
		ExtraLarge.addItemListener(this);
		
		SLOWEST.addItemListener(this);
		SLOWER.addItemListener(this);
		REGULAR.addItemListener(this);
		FASTER.addItemListener(this);
		FASTEST.addItemListener(this);
		
		Mercury.addItemListener(this);
		Venus.addItemListener(this);
		Earth.addItemListener(this);
		Moon.addItemListener(this);
		Mars.addItemListener(this);
		Jupiter.addItemListener(this);
		Saturn.addItemListener(this);
		Uranus.addItemListener(this);
		Neptune.addItemListener(this);
		Pluto.addItemListener(this);
		
		/* ===== Create the control panel/sheet for Frame ===== */
		initComponents(); // sets up the control panel/sheet/ball
		
		/*===== Create the Cannon Polygon ===== */
		BuildnDrawPoly(); // gets the points and creates the polygon for the cannon
		MakeSheet();
		
		/* ===== Set Initial values to the ballc class ===== */
		Ball.setAngle(Angle);
		Ball.setVelocity(Velocity);
		Ball.setGravity(gravity);
		
		/* ===== Add Components to the Frame ===== */
		EditorFrame.setMenuBar(MMB); // add menu bar to frame
		EditorFrame.add("Center", sheet);
		EditorFrame.add("South", control); // add the textarea
		
		/* ===== Frame Validation/Add WindowListener ===== */
		EditorFrame.addWindowListener(this); // add window listener
		EditorFrame.setResizable(true);
		EditorFrame.setVisible(true);
		EditorFrame.validate();
	} // end CannonVSBall constructor
	
	/* ===== CannonVSBall Methods ===== */
	public void initComponents() 
	{
		/* ===== Create the Sheet ===== */
		sheet.setLayout(new BorderLayout(0,0));
		sheet.setSize(Screen.x, Screen.y);
		Ball = new Ballc(size, Perimeter);
		Ball.setPerimeter(Perimeter);
		Ball.setBackground(Color.white);
		sheet.add("Center", Ball);
		sheet.setVisible(true);	
		
		/* ====== Add MouseListener ====== */
		Ball.addMouseMotionListener(this);
		Ball.addMouseListener(this);
		EditorFrame.addComponentListener(this);
		
		/* ===== Create the ScrollBars ===== */
		AngleScrollBar = new Scrollbar(Scrollbar.HORIZONTAL); // create the angle scroll bar
		AngleScrollBar.setMaximum(AngleSBmax); // set the max angle
		AngleScrollBar.setMinimum(AngleSBmin); //set the min angle
		AngleScrollBar.setUnitIncrement(AngleSBunit); // set the unit increment
		AngleScrollBar.setBlockIncrement(AngleSBblock); // set the block increment
		AngleScrollBar.setValue(Angle); // set the intial value
		AngleScrollBar.setVisibleAmount(SBvisible); // set the visible size
		AngleScrollBar.setBackground(Color.gray); // set the bkg color
				
		VelocityScrollBar = new Scrollbar(Scrollbar.HORIZONTAL); // create the velocity scroll bar
		VelocityScrollBar.setMaximum(VelocitySBmax); // set the max angle
		VelocityScrollBar.setMinimum(VelocitySBmin); //set the min angle
		VelocityScrollBar.setUnitIncrement(VelocitySBunit); // set the unit increment
		VelocityScrollBar.setBlockIncrement(VelocitySBblock); // set the block increment
		VelocityScrollBar.setValue((Velocity - SBvisible)); // set the intial value
		VelocityScrollBar.setVisibleAmount(SBvisible); // set the visible size
		VelocityScrollBar.setBackground(Color.gray); // set the bkg color
		
		/* ===== Create the Control Panel ===== */
		GridBagConstraints c = new GridBagConstraints();
		GridBagLayout gbl = new GridBagLayout();

		double colWeight[]= {1,1,1,1,1,1,1,1,1,1,1};
		double rowWeight[]= {1,1,1,1,1};
		int colWidth[]= {1,1,1,1,1,1,1,1,1,1};
		int rowHeight[]= {1,1,1,1,1};
		gbl.rowHeights = rowHeight;
		gbl.columnWidths = colWidth;
		gbl.columnWeights = colWeight;
		gbl.rowWeights = rowWeight;
		c.anchor = GridBagConstraints.CENTER;
				
		c.weightx = 1; // spacing for columns
		c.weighty = 1; // spacing for rows
		c.gridheight = 1; 
		c.gridwidth = 3;
		c.fill = GridBagConstraints.BOTH; // fills display
				
		// scrollbars
		c.gridx = 8;
		c.gridy = 1;
		c.gridwidth = 2;
		gbl.setConstraints(AngleScrollBar, c);
		control.add(AngleScrollBar); // add the angle scrollbar to the layout
				
		c.gridy=2;
		c.gridx = 8;
		c.gridwidth = 2;
		gbl.setConstraints(VelocityScrollBar, c);
		control.add(VelocityScrollBar); // add the velocity scrollbar to the layout
				
		// labels
		c.gridy = 1;
		c.gridx = 6;
		c.gridwidth = 1;
		gbl.setConstraints(ANGLE, c);
		control.add(ANGLE); // add the angle label to control layout
				
		c.gridy=2; // position in 3rd row down
		c.gridx = 6; // position in 7th column from left
		gbl.setConstraints(VELOCITY, c); //add velocity label to the control panel
		control.add(VELOCITY); // add the velocity label to control layout	

		c.gridx = 0;	// position in left most column
		c.gridy = 4;	// position in 5th row down
		c.gridwidth = 10;	// use 10 cells accross as width
		gbl.setConstraints(Message, c);	// set constraints
		control.add(Message); // add to control panel
		Message.setVisible(true);
		
		c.gridwidth=1;	// use 1 block gridwith
		c.gridx=2;	// position in 3rd column from left
		c.gridy=1;	// position in 2nd row down
		gbl.setConstraints(PLAYER, c); // set constraints
		control.add(PLAYER); // add to control panel
				
		c.gridwidth=1;  // use 1 block grid width
		c.gridx=1;	// position in 2nd column from left
		c.gridy=1;	// position in the 2nd row down
		gbl.setConstraints(BALL, c);	// set constraints to the grid
		control.add(BALL);	// add grid to the control panel
		
		//Textfields
		c.gridwidth = 1; // use six cells
		c.gridx = 2; // position in 3rd leftmost column
		c.gridy = 2; // position on the 3rd row
		gbl.setConstraints(PlayerScore, c); // apply the TextField to the Layout
		control.add(PlayerScore); // add the TextField to the Frame
		PlayerScore.setText(String.valueOf(PS)); // display name in the TextField
		PlayerScore.setEnabled(false);
				        
		c.gridwidth = 1; // use six cells
		c.gridx = 1; // position in leftmost column
		c.gridy = 2; // position on the 16th row
		gbl.setConstraints(BallScore, c); // apply the TextField to the Layout
		control.add(BallScore); // add the TextField to the Frame
		BallScore.setText(String.valueOf(BS)); // display name in the TextField
		BallScore.setEnabled(false);
		        
		c.gridwidth = 1; // use six cells
		c.gridx = 7; // position in leftmost column
		c.gridy = 1; // position on the 16th row
		gbl.setConstraints(CurAngle, c); // apply the TextField to the Layout
		control.add(CurAngle); // add the TextField to the Frame
		CurAngle.setText(String.valueOf(Angle) + "°"); // display name in the TextField
		CurAngle.setEnabled(false);
			    
		c.gridwidth = 1; // use six cells
		c.gridx = 7; // position in leftmost column
		c.gridy = 2; // position on the 16th row
		gbl.setConstraints(CurVelocity, c); // apply the TextField to the Layout
		control.add(CurVelocity); // add the TextField to the Frame
		CurVelocity.setText(String.valueOf(Velocity) + " m/sec"); // display name in the TextField
		CurVelocity.setEnabled(false);
				        
		control.setLayout(gbl);
		control.setVisible(true);
		
		// add the listeners to the textfields/scrollbars
		CurAngle.addActionListener(this);
		CurVelocity.addActionListener(this);
		VelocityScrollBar.addAdjustmentListener(this);
		AngleScrollBar.addAdjustmentListener(this);
	} // end initComponents()
	public void MakeSheet()
	{
		Rectangle q = new Rectangle();
		int mr = 0, mb = 0;
		I = sheet.getInsets();
		int i = 0;
		if(Ball.getWallSize() > 0)
		{
			t .setBounds(Ball.getOne(i));
			mr = t.x + t.width; // initialize max right
			mb = t.y + t.height; // initialize max bottom
			i++;
			while(i < Ball.getWallSize())
			{
				t = Ball.getOne(i);
				mr = Math.max((t.x + t.width), mr); //keep max right
				mb = Math.max((t.y + t.height), mb); // keep max bottom
				i++;
			}
		}
		q.setBounds(Ball.getBall());
		mr = Math.max((q.x + q.width + EXPAND), mr);
		mb = Math.max((q.y + q.height + EXPAND), mb);
		if(mr > sheet.getWidth() || mb > sheet.getHeight())
		{
			EditorFrame.setSize((Math.max((mr + EXPAND), Screen.x)), (Math.max((mb + EXPAND), Screen.y)));
			//EditorFrame.setExtendedState(EditorFrame.ICONIFIED);
			//EditorFrame.setExtendedState(EditorFrame.NORMAL);
		} 
		Screen.x = sheet.getWidth() - I.left - I.right;
		Screen.y = sheet.getHeight() - I.top - I.bottom;
		Perimeter.setBounds(I.left, I.top, Screen.x, Screen.y);	// set perimeter to screen dimensions
		Perimeter.grow(-1, -1);	// shrink the perimeter 
		Ball.setPerimeter(Perimeter); // pass the rectangle to the ball class
		System.out.println("t: " + t);
		System.out.println("Ball Perimeter: " + Ball.getPerimeter());
		
		BuildnDrawPoly();
		Ball.repaint();
	} // end MakeSheet()
	public void BuildnDrawPoly()
	{
		int x1, x2, y1, y2, w = 20, l = 100; 
		/* ===== Create the Cannon Arm Points ===== */
		Point a = new Point(Screen.x - 50, Screen.y - 50); 
		Point a1 = new Point(); // bottom left point
		Point a2 = new Point(); // bottom right point
		Point c1 = new Point(); // top left point
		Point c2 = new Point(); // top right point
		Point c = new Point();
		Point f = new Point();
		
		poly.reset(); // reset the old points
		
		double r = (Angle * Math.PI)/180; // convert angle to radians
		
		c.x = (int) (a.x-(l*Math.cos(r)));
		c.y = (int) (a.y-(l*Math.sin(r)));
		
		x1 = x2 = (int) ((w/2) * Math.cos(r));
		y1 = y2 = (int) ((w/2) * Math.sin(r));
		
		/* ===== Calculate the Cannon Arm Points ===== */
		a1.x = a.x - y1;
		a1.y = a.y + x2;

		a2.x = a.x + y2;
		a2.y = a.y - x2;

		c1.x = (int) (int) (c.x - y1);
		c1.y = (int) (int) (c.y + x1);

		c2.x = (int) (int) (c.x +y2);
		c2.y = (int) (int) (c.y - x2);
		
		/* ===== Add the points to the Polygon ===== */
		poly.addPoint(a1.x, a1.y);
		poly.addPoint(c1.x, c1.y);
		poly.addPoint(c2.x, c2.y);
		poly.addPoint(a2.x, a2.y);
		
		
		f.x = (int) (a.x-(l*Math.cos(r) + 50));
		f.y = (int) (a.y-(l*Math.sin(r) + 50));
		
		
		/* ===== Send the Polygon to the Ballc Class ===== */
		Ball.setPoly(poly); // set the polgon in the drawing class
		loadProjectile(a, c, w);
	} // end BuildnDrawPoly()
	public void loadProjectile(Point a, Point f, int w)
	{
		if(!Ball.getShotFired())
		{
			Ball.setProjectilePoint(f, a, w); // for testing	
		}
	}
	public void PlayerPoint()
	{
		PS = PS + 1;		// update player score
		PlayerScore.setText(String.valueOf(PS));	// update the textfield
		Message.setText(PlayerPt);
	}
	public void BallPoint()
	{
		BS = BS + 1;		// update the ball score
		BallScore.setText(String.valueOf(BS));	// update textfield
		Message.setText(BallPt);
	}
	public void setTheGravity()
	{
		// sets the desired value as the gravity variable for use in determining projectile motion
		if(Mercury.getState()==true)
		{
			gravity = MercuryG;
		}
		if(Venus.getState()==true)
		{
			gravity = VenusG;
		}
		if(Earth.getState()==true)
		{
			gravity = EarthG;
		}
		if(Moon.getState()==true)
		{
			gravity = MoonG;
		}
		if(Mars.getState()==true)
		{
			gravity = MarsG;
		}
		if(Jupiter.getState()==true)
		{
			gravity = JupiterG;
		}
		if(Saturn.getState()==true)
		{
			gravity = SaturnG;
		}
		if(Neptune.getState()==true)
		{
			gravity = NeptuneG;
		}
		if(Uranus.getState()==true)
		{
			gravity = UranusG;
		}
		if(Pluto.getState()==true)
		{
			gravity = PlutoG;
		}
		Ball.setGravity(gravity);
	} // end setTheGravity()
	public void setTheSpeed()
	{
		// sets the desired value as the speed/delay variable for use in the Ball object
		if(SLOWEST.getState()==true)
		{
			Delay=(long) SLOWESTS;
		}
		if(SLOWER.getState()==true)
		{
			Delay=(long) SLOWERS;
		}
		if(REGULAR.getState()==true)
		{
			Delay=(long) REGULARS;
		}
		if(FASTER.getState()==true)
		{
			Delay=(long) FASTERS;
		}
		if(FASTEST.getState()==true)
		{
			Delay=(long) FASTESTS;
		}
	} // end setTheSpeed()
	public void setTheSize()
	{
		// sets the desired value as the size variable for use in the Ball object
		if(ExtraSmall.getState()==true)
		{
			size = ExtraSmallS;
		}
		if(Small.getState()==true)
		{
			size = SmallS;
		}
		if(Medium.getState()==true)
		{
			size = MediumS;
		}
		if(Large.getState()==true)
		{
			size = LargeS;
		}
		if(ExtraLarge.getState()==true)
		{
			size = ExtraLargeS;
		}
		Ball.update(size);
	} // end setTheSize()
	public void start()
	{
		if(thethread == null) // create a thread if it does not exist
		{
			thethread = new Thread(this); // create a new thread
			thethread.start(); // start the thread
		}
		Ball.repaint(); // repaint the object
	} // end start()
	public void stop()
	{	
		//removes all the Listeners that were used for the program
		EditorFrame.removeComponentListener(this);
		Ball.removeMouseMotionListener(this);
		Ball.removeMouseListener(this);
		VelocityScrollBar.removeAdjustmentListener(this);
		AngleScrollBar.removeAdjustmentListener(this);
		CurAngle.removeActionListener(this);
		CurVelocity.removeActionListener(this);
		QUIT.removeActionListener(this);
		ExtraSmall.removeItemListener(this); 
		Small.removeItemListener(this);
		Medium.removeItemListener(this);
		Large.removeItemListener(this);
		ExtraLarge.removeItemListener(this);
		SLOWEST.removeItemListener(this);
		SLOWER.removeItemListener(this);
		REGULAR.removeItemListener(this);
		FASTER.removeItemListener(this);
		FASTEST.removeItemListener(this);
		Mercury.removeItemListener(this);
		Venus.removeItemListener(this);
		Earth.removeItemListener(this);
		Moon.removeItemListener(this);
		Mars.removeItemListener(this);
		Jupiter.removeItemListener(this);
		Saturn.removeItemListener(this);
		Uranus.removeItemListener(this);
		Neptune.removeItemListener(this);	
		Pluto.removeItemListener(this);
		EditorFrame.removeWindowListener(this);
		EditorFrame.dispose();
		System.exit(0);
	} // end stop()
	/* ===== Thread/Runnable ===== */
	public void run()
	{
		while(KeepLooping == true)
		{
			if(Started == true)
			{
				// counter every n (set by scrollbar), move the ball
				if(n == 3)
				{
					Ball.move();
					n = 0;
				}
				n++;
				Ball.Projectile(); // handle projectile move
				Ball.repaint(); // repaint the object
				try
				{
						Thread.sleep(Delay);
				} catch(InterruptedException e) {e.printStackTrace();}
				
				// handle message label
				if(Ball.getPointCase() == 0)
				{
					// do nothing
				}
				else if(Ball.getPointCase() == 1)
				{
					PlayerPoint();
					Ball.DestroyBall();
					try 
					{
						Thread.sleep(3000);
					} catch (InterruptedException e) {e.printStackTrace();}
					Ball.CreateBall();
					Ball.PointCase = 0;	
				}
				else if(Ball.getPointCase() == 2)
				{
					BallPoint();
					Ball.DestroyCannon();
					try 
					{
						Thread.sleep(3000);
					} catch (InterruptedException e) {e.printStackTrace();}
					BuildnDrawPoly();
					Ball.PointCase = 0;
				}
				else if(Ball.getPointCase() == 3)
				{
					Message.setText(GONE);
					Ball.PointCase = 0;
				}
				else if(Ball.getPointCase() == 4)
				{
					Message.setText(""); // clear the text
				}
			}
			else
			{
				try
				{
					Thread.sleep(2);
				}
				catch (InterruptedException e) {}
			}
		}
	} // end run()
	
	/* ===== ActionListener ===== */
	public void actionPerformed(ActionEvent e) 
	{
		Object source = e.getSource();
		if(source == RUN)	// if run menu item is selected
		{
			Started = true;	// flip the started flag
			start();		// begin start method
		}
		if(source == QUIT)	// if quit menu item selected, begin stop method
		{
			stop();
		}
		if(source == PAUSE)	// if pause menu item is selected, interupt the thread and flip the flag to signal a stop
		{
			thethread.interrupt();
			Started = false;
		}
		if(source == RESTART)
		{
			BS = 0;
			PS = 0;
			PlayerScore.setText(String.valueOf(PS));
			BallScore.setText(String.valueOf(BS));
			Started=false;
			Ball.ShotFired = false;
			int i=0;
			while (i < Ball.getWallSize())
			{
				Ball.removeElementAt(i);
			}
			Ball.db = new Rectangle();
			Ball.resetBallLocation();
			Ball.resetProjectileLocation();
			gravity = EarthG;
			Angle = 45;
			AngleScrollBar.setValue(Angle);
			Velocity = 100;
			VelocityScrollBar.setValue(Velocity);
			Ball.setAngle(Angle);
			Ball.setVelocity(Velocity);
			Ball.setGravity(gravity);
			BuildnDrawPoly();
			CurVelocity.setText(String.valueOf(Velocity) + " m/sec");	// update textfield
			CurAngle.setText(String.valueOf(Angle) + "°");	// update the textfield
			Message.setText("");
			Started = true;
		}	
	} // end actionPerformed()
	
	/* ===== AdjustmentListener ===== */
	public void adjustmentValueChanged(AdjustmentEvent e) 
	{
		Scrollbar sb = (Scrollbar) e.getSource();
		if(sb == AngleScrollBar)		// if angle scroll bar is moved
		{
			if(!Ball.getShotFired())
			{
				Ball.setAngle(Angle);		// sets angle in Draw object
			}
			Angle = e.getValue();		// recieve value from the scrollbar
			CurAngle.setText(String.valueOf(Angle) + "°");	// update the textfield
			BuildnDrawPoly();			//build the new polygon
		}
		if(sb == VelocityScrollBar)		// if velocity scroll bar is moved
		{
			if(!Ball.getShotFired())
			{
				Velocity = e.getValue();	// recieve info from scrollbar
				CurVelocity.setText(String.valueOf(Velocity) + " m/sec");	// update textfield
			}
			Ball.setVelocity(Velocity); // set the velocity in Drawing class
		}	
	} // end adjustmentValueChanged()
	
	/* ===== ComponentListeners ===== */
	public void componentResized(ComponentEvent e) 
	{
		MakeSheet();
	} // end componentResized()
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
	public void componentHidden(ComponentEvent e) {}
																		
	/* ===== WindowListeners ===== */
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
	
	/* ===== ItemListener ===== */
	public void itemStateChanged(ItemEvent e) 
	{
		CheckboxMenuItem checkbox = (CheckboxMenuItem) e.getSource();
		CheckboxMenuItem checkbox2 = (CheckboxMenuItem) e.getSource();
		CheckboxMenuItem checkbox3 = (CheckboxMenuItem) e.getSource();
		if(checkbox == Mercury || checkbox == Venus || checkbox == Earth || checkbox == Moon || checkbox == Mars || checkbox == Jupiter || checkbox == Saturn || checkbox == Uranus || checkbox == Neptune || checkbox == Pluto)
		{
			//sets all buttons to false and sets choice from menu to true
			Mercury.setState(false);
			Venus.setState(false);
			Earth.setState(false);
			Moon.setState(false);
			Mars.setState(false);
			Jupiter.setState(false);
			Saturn.setState(false);
			Neptune.setState(false);
			Uranus.setState(false);
			Pluto.setState(false);
			checkbox.setState(true);
		}
		if (checkbox2== ExtraSmall || checkbox2 == Small || checkbox2 == Medium || checkbox2 == Large || checkbox2==ExtraLarge) 
		{
			//sets all buttons to false and sets choice from menu to true
			ExtraSmall.setState(false);
			Small.setState(false);
			Medium.setState(false);
			Large.setState(false);
			ExtraLarge.setState(false);
			checkbox2.setState(true);
		}
		if(checkbox3 == SLOWEST || checkbox3 == SLOWER || checkbox3 == REGULAR || checkbox3 == FASTER|| checkbox3 == FASTEST)
		{
			//sets all buttons to false and sets choice from menu to true
			SLOWEST.setState(false);
			SLOWER.setState(false);
			REGULAR.setState(false);
			FASTER.setState(false);
			FASTEST.setState(false);
			checkbox3.setState(true);
		}
		//updates all attributes based on menu selections
		setTheSize();		
		setTheSpeed();
		setTheGravity();
	} // end itemStateChanged()
	
	/* ====== MouseListener / MotionListener ===== */
	public void mouseDragged(MouseEvent e) 
	{
		Ball.db.setBounds(Ball.getDragBox(e));
		if(Perimeter.contains(Ball.db))
		{
			Ball.setDragBox(Ball.db);
			Ball.repaint();
		}
	}
	public void mouseMoved(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) 
	{
		Point p = new Point(e.getX(), e.getY()); // get the mouse clicked position and create a point
		Rectangle b = new Rectangle();		
		
		if (e.getClickCount()==2)
		{
			for(int i = 0; i < Ball.getWallSize();) // cycle through the vector
			{
				b = Ball.getOne(i);
				if(b.contains(p))		// test if point is contained in a wall wihin vector
				{
					Ball.removeElementAt(i);	// if so, removes it
					Ball.db.setBounds(0,0,0,0);	// resets the dragbox
				}
				else
				{
					i++;
				}
			}
		}
		if ((poly.contains(p)|| Ball.getBase().contains(p)) && Ball.ShotFired == false)
		{
			Ball.ShotFired = true;
		}
	} // end mouseClicked()
	public void mousePressed(MouseEvent e) 
	{
		Ball.m1.setLocation(e.getPoint());
	} // end mousePressed()
	public void mouseReleased(MouseEvent e) 
	{
		Rectangle b = new Rectangle(Ball.getBall()); // ball rectangle
		b.grow(1, 1);
		Rectangle db = Ball.getDragBox(e);

		if(db.intersects(b)||poly.intersects(db))
		{
			// don't create rect
		}
		else if(Perimeter.intersects(db)) // check if the rect exceeds the perimeter
		{
			if(db.getMaxX() > Perimeter.getMaxX())
			{
				db.width = (int) (Perimeter.getMaxX() - Ball.m1.x - EXPAND); 
			}
			if(db.getMaxY() > Perimeter.getMaxY())
			{
				db.height = (int) (Perimeter.getMaxY() - Ball.m1.y - EXPAND);
			}
			if(db.getMinX() < Perimeter.getMinX())
			{
				db.x = EXPAND;
				db.width = Ball.m1.x - (int) (Perimeter.getMinX() + EXPAND); 
			}
			if(db.getMinY() < Perimeter.getMinY())
			{
				db.y = EXPAND;
				db.height = Ball.m1.y - (int) (Perimeter.getMinY() + EXPAND);
			}
			Ball.AddOne(db);
		}
		else if(b.intersection(db).equals(db)) // check if the new rect is covered in the vector
		{
			
			int i = 0;
			Rectangle temp = new Rectangle();
			while(i < Ball.getWallSize())
			{
				temp = Ball.getOne(i);
				if(temp.contains(Ball.db))
				{
					Ball.removeElementAt(i);// delete the Rectangle in the Vector
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
			while(i < Ball.getWallSize())
			{
				temp = Ball.getOne(i);
				if(db.contains(temp))
				{
					Ball.removeElementAt(i);// delete the Rectangle in the Vector
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
		Ball.setDragBox(db);
	} // end mouseReleased()
	public void mouseEntered(MouseEvent e) 
	{
		Ball.repaint();
	} // end mouseEntered()
	public void mouseExited(MouseEvent e) {}
} // end CannonVSBall class

/* ====== Ballc Class ===== */
class Ballc extends Canvas
{
	private static final long serialVersionUID = 2L;
	public Rectangle db = new Rectangle();
	private static final Rectangle ZERO = new Rectangle(0, 0, 0, 0);
	private Rectangle r = new Rectangle(ZERO);
	private Vector<Rectangle> Walls = new Vector <Rectangle>();
	public static final Color LightBrown = new Color(102,51,0);
	public Point m1 = new Point(0, 0);
	public Point m2 = new Point(0, 0);
	
	//init variables for the Ballc class
	private float SBall;
	private int x, y;
	private int Velocity;
	private int Angle;
	private float Gravity;
	private double time = 0.05;
	private Rectangle ball = new Rectangle();
	private Rectangle projectile = new Rectangle();
	private Rectangle Perimeter = new Rectangle();
	private Rectangle base= new Rectangle();
    boolean ObjDown = true; // bool to handle up and down directions
    boolean ObjRight = true; // bool to handle up and down directions
	Graphics g;
	Image buffer;
	Polygon poly;
	Point o;
	Point MidPt;
	boolean ShotFired = false;
	
	//init projectile variables
	int width;
	double dx;
	double dy;
	double dvy;
	int X;
	double vy;
	int InitialX;
	int InitialY;
	int InitialVelocity;
	int InitialVelocityX;
	int InitialVelocityY;
	int VelocityX;
	int VelocityY;
	int PointCase;
	
	/* ===== Ballc Constructor ===== */
	public Ballc(float NS, Rectangle p)
	{
		x = 10;
		y = 10;
		SBall= NS;
		ball.setBounds(x, y, (int) SBall, (int) SBall);
		Perimeter.setBounds(p);
	} // end ballc constructor
	public void setPerimeter(Rectangle p)
	{
		Perimeter.setBounds(p);
	}
	public void update(float NS)
	{
		// creating newly sized ball using the derived midpoint of the old ball
		Point Center = new Point(ball.x + ball.width/2, ball.y + ball.height/2);
		if(SBall > NS)
		{
			SBall = NS;
			ball.setBounds((Center.x - (int) NS/2), (Center.y - (int) NS/2), (int) SBall, (int) SBall);
		}
		else if(SBall < NS)
		{
			SBall = NS;
			ball.setBounds((Center.x - (int) NS/2), (Center.y -  (int) NS/2), (int) SBall, (int) SBall);
		}
	} // end update()
	public void setPoly(Polygon p)
	{
		poly = p;
	}
	
	public void setBase(Rectangle b)
	{
		base=b;
	}
	
	public void setProjectilePoint(Point a, Point b, int w)
	{
		//offset the projectile slightly out of cannon barrel so that test for self destruct are not a false positive on intersects
		int x = a.x - (w/2) - 12;
		int y = a.y - (w/2) - 12;
		MidPt = new Point(x, y);
		projectile.setBounds(MidPt.x, MidPt.y, (w-1), (w-1));
		o = b;
	}
	public void setVelocity(int v)
	{
		Velocity = v;
		vy = Velocity/50;   // scaled down velocity for appearance sake
	}
	public void setAngle(int a)
	{
		Angle = a;
	}
	public void setGravity(float g)
	{
		Gravity = g;
	}
	public void Projectile()
	{
		if(ShotFired == true) // if fired flag:
		{
			if(projectile.getMaxY() >= Perimeter.getMaxY()) // if Crossing out of bottom of perimeter
			{
				ShotFired = false; 	//reset fired flag
				projectile.setLocation(MidPt); 	// reset projectile location
				PointCase = 4;	// set label to read for info
				vy = Velocity;	// reset our velocity in y axis
			}
			else
			{
				double r = (Angle * Math.PI)/180; // convert angle to radians
				InitialVelocityX = (int) (Velocity * Math.cos(r));
				InitialVelocityY = (int) (Velocity * Math.sin(r));
				
				// calculate deltax 
				dx = (0.5 * Velocity * Math.cos(r) * time); // change in x direction
				// calculate deltay
				dy = (0.5 * vy * Math.sin(r) * time - Gravity * Math.pow(time, 2)); // change in y direction
				dvy = (-Gravity * time);	//increment of Velocity as a result of gravity
				vy = vy + dvy;	// compound velocity on y axis as a result of gravity
				
				//update projectile position
				projectile.x -= dx;
				projectile.y -= dy;
				
				//determine total position move for the projectle
				X = (int) ((dx + 0.5 * Velocity * Math.cos(r) * 2 * Velocity * Math.cos(r))/Gravity);
				
				// projectile collisions
				Rectangle Wall = new Rectangle();
				for (int i=0; i<Walls.size(); i++)	// if projectile hits a wall
				{
					Wall = getOne(i);
					if (projectile.intersects(Wall))
					{
						Walls.removeElementAt(i);	// removes the wall
						ShotFired = false;			// flips the flag
						db = new Rectangle();
						resetProjectileLocation();	// reset location of projectile
						vy = Velocity;				// resets velocity in y axis
					}	
				}
				if (projectile.intersects(ball))
				{
					ShotFired = false;				// flip the fired flag
					resetBallLocation();			// resets ball's location for next round
					resetProjectileLocation();		// reset projectile location
					PointCase = 1; 					// player point
					vy = Velocity;					// reset velocity in Y axis
				}	
				if(Perimeter.getMinX() > projectile.x)	// if ball is flying uncontrollably off screen in x axis
				{
					PointCase = 3; 					// ball's not coming back message
				}
				if(projectile.intersects(base) || poly.intersects(projectile))	// if the projectile hits cannon barrel or base
				{
					ShotFired = false;				// Flip fire flag
					PointCase = 2; 					// ball's point
					resetBallLocation();			// reset Ball Location for next round
					resetProjectileLocation();		// reset projectile for next roung
					vy = Velocity;					// reset velocity in y axis
				}
			}
		}
	}
	public void move()
	{
		// ball checks
		//System.out.println(ball.x);				// for development purposes
		if(ball.getMaxY() <= Perimeter.getMaxY())	// if ball hits bottom side of sheet
		{
			ObjDown = !ObjDown; 					// complement down flag
		}
		if(ball.getMinY() >= Perimeter.getMinY())	// if ball hits top of the sheet
		{
			ObjDown = !ObjDown; 					// complement down flag
			
		}
		if(ball.getMaxX() <= Perimeter.getMaxX())	// if ball hits right side of sheet
		{
			ObjRight = !ObjRight; 					// complement right flag
		}
		if(ball.getMinX()  >= Perimeter.getMinX())	// if ball hit left side of sheet
		{
			ObjRight = !ObjRight; // complement right flag
		}
		for(int i=0; i<Walls.size(); i++)			// cycle through walls to check for bounce
		{
            r = Walls.elementAt(i);				// grab current wall in vector
            if (ball.intersects(r))				// test if it intersects with ball
            {	
            	if(ball.getMinY() <= r.getMinY() || ball.getMaxY() >= r.getMaxY())		// intersects on Top or bottom
            	{
            		ObjDown = !ObjDown;			// complements flag
            	}	
            	if(ball.getMinX() <= r.getMinX() || ball.getMaxX() >= r.getMaxX())		// intersects on left/right
            	{
            		ObjRight = !ObjRight;		// complements flag
            	}
            }		    
        }
		if (poly.intersects(ball) || ball.intersects(base))	// if ball hits the cannon
		{
			ShotFired = false;			//flips fired flag
			PointCase = 2;				// point for ball and label displayed
			resetBallLocation();		// reset ball for next game
			resetProjectileLocation();	// reset projectile 
		}
		if(ObjDown == true)				// position logic for moving ball
		{
			ball.y -= 1; // moves obj down
		}
		else
		{
			ball.y += 1; // moves obj up
		}
		if(ObjRight == true)
		{
			ball.x -= 1; // moves obj left
		}
		else
		{
			ball.x += 1; // moves obj right
		}
	
	} // end move()
	public void reSize(Point r)
	{
		
	}
	public int getPointCase()
	{
		return PointCase;
	}
	public void paint(Graphics cg)
	{
		buffer = createImage(Perimeter.width, Perimeter.height);
		if(g != null) // check if g exists
		{
			g.dispose(); // remove g
		}
		g = buffer.getGraphics(); // get graphics
		g.setColor(Color.blue); // set perimeter color
		g.drawRect(0, 0, Perimeter.width - 1, Perimeter.height - 1);	// draws the border perimeter
		
		//create projectile
		if(ShotFired && Perimeter.contains(projectile)) // if fired flag is active
		{
			g.setColor(Color.black); // paint ball
			g.fillOval(projectile.x, projectile.y, projectile.width, projectile.height);
		}
		// create cannon
		g.setColor(Color.lightGray);
		g.drawPolygon(poly);
		g.fillPolygon(poly);
		g.setColor(LightBrown);
		
		// create base
		g.fillOval(o.x - 25, o.y - 25, 50, 50);
		base.setBounds(o.x - 25, o.y - 25, 50, 50);
		
		// create ball
		g.setColor(Color.red); // paint ball
		g.fillOval(ball.x, ball.y, ball.width, ball.height);
		g.setColor(Color.black);
		g.drawOval(ball.x, ball.y, ball.width, ball.height);
		
		// draw the drag box
		g.drawRect(db.x, db.y, db.width, db.height);
		
		// draw the walls
		for(int i = 0; i < getWallSize(); i++)
		{
			Rectangle temp = getOne(i);
			g.fillRect(temp.x, temp.y, temp.width, temp.height); // create solid rectangle for box
		}
		cg.drawImage(buffer,  0,  0,  null); //switch the graphics
	} // end paint()
	public void update(Graphics g)
	{
		paint(g);
	}
	public void AddOne(Rectangle r)
	{
		Walls.addElement(new Rectangle(r));
	}
	public void removeElementAt(int i)
	{
		Walls.removeElementAt(i);
	}
	public Rectangle getOne(int i)
	{
		return Walls.elementAt(i); // gets i'th element of walls
	}
	public int getWallSize()
	{
		return Walls.size(); // gets the size of vector walls
	}
	public Rectangle getDragBox(MouseEvent e)
	{
		int height = 0, width = 0;
		Rectangle DragBox = new Rectangle();
		m2.setLocation(e.getPoint());
		// finds min and max points to orient the rectangle draw
		int w = Math.min(m1.x, m2.x);
		int w1 = Math.max(m1.x, m2.x);
		int h = Math.min(m1.y, m2.y);
		int h1 = Math.max(m1.y, m2.y);
		// finishes calculations for height/ width
		width = w1 - w;
		height = h1 - h;
		// sets the drag box for drawing
		DragBox.setBounds(w, h, width, height);
		return DragBox;
	}
	public void setDragBox(Rectangle db) 
	{
		// makes sure dragbox is on the drawing sheet, if not, sets to the max allowed value
		if(db.y < Perimeter.getMinY())
		{
			db.y = (int) Perimeter.getMinY();
		}
		if(db.y > Perimeter.getMaxY())
		{
			db.y = (int) Perimeter.getMaxY();
		}
		if(db.x < Perimeter.getMinX())
		{
			db.x = (int) Perimeter.getMinX();
		}
		if(db.x > Perimeter.getMaxX())
		{
			db.x = (int) Perimeter.getMaxX();
		}
	}
	public int getVelocity()
	{
		return Velocity;
	}
	public boolean getShotFired()
	{
		return ShotFired;
	}
	public void resetProjectileLocation()
	{
		projectile.setLocation(MidPt);
	}
	public void resetBallLocation()
	{
		ball.setLocation(10, 10);
	}
	public Rectangle getBall()
	{
		return ball;
	}
	public void DestroyBall()	// destroys ball and creates new
	{
		width = ball.width;
		ball = new Rectangle();
	}
	public void CreateBall()	// orients and loads initial ball
	{
		ball.setBounds(10, 10, width, width);
	}
	public void DestroyCannon()
	{
		poly = new Polygon();
	}
	public Rectangle getPerimeter()
	{
		return Perimeter;
	}
	public Rectangle getBase()
	{
		return base;
	}
} // end Ballc Class
