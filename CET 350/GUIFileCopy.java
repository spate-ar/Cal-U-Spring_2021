//package pkg; created using Eclipse IDE
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/*
 Program3 - File Copy GUI Program
 CET - 350 Technical Computing using Java
 Group 2
 Andrew Spate & Nicholas Spudich
 spa3195@calu.edu & spu8504@calu.edu 
*/


public class Main extends Frame implements WindowListener, ActionListener
{
	private static final long serialVersionUID = 1L;
	Label Heading1 = new Label("Source:");
	Label Heading2 = new Label("File Name:");
	Label TargetLabel = new Label("Select Target Directory:");
	Label MessageLabel = new Label("");
	Label SourceLabel = new Label("");
	Button TargetButton = new Button("Target"); // constructs button with label "Target"
	Button OkButton = new Button("OK"); // construct button with label "OK"
	List list = new List(13); // create a 13 row List
	TextField FileName = new TextField(80);	// creates an empty 80 column TextField called FileName
	
	boolean fSource;	// flags for source, target outfile
	boolean fTarget; 
	boolean fOutfile;
	File curDir; // current directory
	String filename; // file name string

	Main(File dir)
	{
		curDir = dir; // set current directory to parameter
		fSource = false; // set to false for sequential machine
		fTarget = false;
		fOutfile = false;
		TargetButton.setEnabled(false); // set Target initialization
		
		GridBagConstraints c = new GridBagConstraints(); //setup for GridBagLayout
		GridBagLayout displ = new GridBagLayout();
		double colWeight[] = {1, 20, 100};			
		double rowWeight[] = {20, 1, 1, 1};
		int colWidth[] = {1, 20, 100};
		int rowHeight[] = {20, 1, 1, 1, 1};
		
		displ.rowHeights = rowHeight;
		displ.columnWidths = colWidth;
		displ.columnWeights = colWeight;
		displ.rowWeights = rowWeight;
		
		this.setBounds(20, 20, 800, 400); //Start 20 right and 20 down, 800 by 400
		this.setLayout(displ);
		c.anchor = GridBagConstraints.CENTER; // places the component in the center
		c.weightx = 1; // spacing for columns
		c.weighty = 1; // spacing for rows
		c.gridwidth = 1;	// specify the number of columns in the display
		c.gridheight = 1;	// specify the number of rows in the display
		c.fill = GridBagConstraints.BOTH; // fills the display
		
		/*======== Headings =======*/
		c.gridx = 0;	
		c.gridy = 1; // moves to (0,1)
		displ.setConstraints(Heading1, c); // source:
		this.add(Heading1);
		c.gridx = 0; 
		c.gridy = 4; // moves to (0,4)
		displ.setConstraints(Heading2, c); // file name:
		this.add(Heading2);
		c.gridx = 1; 
		c.gridy = 3; // moves to (1,3)
		displ.setConstraints(TargetLabel, c); // select target directory:
		this.add(TargetLabel);
		
		c.gridx = 1;
		c.gridy = 1; // moves to (1, 1)
		displ.setConstraints(SourceLabel, c); // directory location
		this.add(SourceLabel);
		
		c.gridx = 1;
		c.gridy = 5; // moves to (1, 5)
		displ.setConstraints(MessageLabel, c); // error message label
		this.add(MessageLabel);
		
		/*====== Buttons ======*/
		c.gridx = 0; 
		c.gridy = 3; // moves to (0,3)
		displ.setConstraints(TargetButton, c); 
		this.add(TargetButton); // component TargetButton added
		c.gridx = 2; 
		c.gridy = 4; // moves to (2,4)
		displ.setConstraints(OkButton, c); 
		this.add(OkButton); // component OkButton added

		/* ========== TextField ============*/
		c.gridwidth = 1; // use six cells
		c.gridx = 1; // position in leftmost column
		c.gridy = 4; // position on the 16th row
		displ.setConstraints(FileName, c); // apply the TextField to the Layout
		this.add(FileName); // add the TextField to the Frame
		FileName.setText(filename); // display name in the TextField

		/* ========== List ============*/
		list.setSize(300, 800); // set an initial size of 300 pixel by 800 pixel
		c.gridwidth = 3; // set the constraints for 8 columns
		c.gridheight = 1; // set the constraints for 13 rows
		c.fill = GridBagConstraints.BOTH; // stretch to fill both directions
		c.gridx = 0; // position the List starting in cell 0, 0 (upper left)
		c.gridy = 0;
		displ.setConstraints(list, c); // apply the constraints
		list.add("..."); // add parent reference to list
		displ.setConstraints(list, c);
		this.add(list); // add the List to the frame
		
		/*========= ActionListeners ========*/
		FileName.addActionListener(this); // add the ActionListener to the TextField
		list.addActionListener(this); // add the ActionListener to the List
		TargetButton.addActionListener(this); // user action prompt
		OkButton.addActionListener(this); // user action prompt
		
		this.setTitle(curDir.getAbsolutePath()); // set title for gui window based on current path
		this.pack(); // pack gui with objects
		this.setVisible(true); // makes it visible
		this.addWindowListener(this); // looks for user input
		display(null); // initialize display
	} // end Main constructor


	public static void main(String[] args) throws IOException
	{
		if(args.length != 0)
			{
				File dir = new File(args[0]); // creates an instance file of parameter to test
				if(dir.isDirectory()) // checks to see if it is a directory
					new Main(new File(dir.getAbsolutePath())); // passes the absolute path if it is a directory
				else
					System.out.println("Parameter is not a directory!"); // error when parameter is not a directory
			}
		else
			new Main(new File(new File(System.getProperty("user.dir")).getAbsolutePath())); // passes when there are no command line parameters, uses user directory
		
	} // end main	

	public void windowClosing(WindowEvent e)
	{
		this.removeWindowListener(this);
		TargetButton.removeActionListener(this);
		OkButton.removeActionListener(this);
		list.removeActionListener(this);
		FileName.removeActionListener(this);
		this.dispose();
	}
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e){}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void display(String name) 
	{
		String [] filenames; // string array to store contents of filenames
		String [] children; // string array to store contents of children 
		boolean fchild = false; // bool flag for loop
		File f = null; // file to test if it is a directory
		int i = 0; // loop incrementer for filename loop
		int j = 0; // loop incrementer for child loop
		
		if(name != null) // check to see if filename or directory is null
		{	
			if(name == "...") // test if it is a parent
			{
				curDir = new File(curDir.getParent()); // current directory is updated to the parent directory
			}
			else
			{
				f = new File(curDir, name); // creates file to test if a directory
				if(f.isDirectory())
					curDir = new File(curDir, name); // current directory become new directory
				else
				{
					if(!fSource || !fTarget) // test source or target flag for false
					{
						SourceLabel.setText(curDir.getAbsolutePath()+ "\\" + name); // if not in either, set to source mode
						fSource = true; // set flag true
						TargetButton.setEnabled(true); // enable target button
					}
					else if(fSource && fTarget) // when source and target flags are true
					{
						FileName.setText(name); // update target file name
						fOutfile = true; // sets outfile flag to true
					}
				}
			}
		}
		if(curDir.getAbsolutePath().endsWith("...")) // trim the ... off of the directory name
			{
				curDir = new File(curDir.getAbsolutePath().substring(0, curDir.getAbsolutePath().length() - 3));
			}
		//=== Update the List ===//
		filenames = curDir.list(); // store contents of current directory in filenames array
		this.setTitle(curDir.getAbsolutePath()); // set the title
		list.removeAll(); // clear the list
		list.add("...");
		for(i = 0; i < filenames.length; i++)
		{
			filenames = curDir.list(); // store contents of current directory in filenames array
			f = new File(curDir, filenames[i]); // create file to test if directory
			if(f.isDirectory()) // test if directory
				{
					children = f.list(); // create another String array of this file
					while(j < children.length && fchild == false)
					{
						fchild = new File(f, children[j]).isDirectory();
						if(fchild == true)
							{
								filenames[i] += " +"; // adds + to filenames strings
							}
						j++; // incrementer
					}
					fchild = false; // reset child directory flag
					j = 0;
				}
			if(curDir.getParent() != null)
			{
				list.add(filenames[i]);
			}	
		}
	}
	public void actionPerformed(ActionEvent e)
		 {
		 	Object source = e.getSource(); // get the current event source
		 	if(source == FileName) // output file name
		 	{
		 		MessageLabel.setText("");
		 		String item = list.getSelectedItem(); // input file 
		 		filename = FileName.getText(); // gets the filename and saves it in string filename
		 		if(filename.length() != 0)
		 			{
		 				fOutfile = true;
						try
						{
							BufferedReader infile = new BufferedReader(new FileReader(item)); // input file handle
							File outp = new File (TargetLabel.getText() + "//" + filename);						
							CopyFile(infile, outp);
						}	
						catch (FileNotFoundException FNFE)
						{
							MessageLabel.setText("File not found");
						}
		 			} 
		 		else
		 			MessageLabel.setText(MessageLabel.getText() + "Target file not specified.");
		 	}
		 	if (source == list)
		 	{
		 		String item = list.getSelectedItem(); // get the item that was selected
	 			if(item.endsWith(" +")) // trim the + off of the directory name
	 			{
	 				item = item.substring(0, item.length() - 2);
	 			}
		 		display(item);
		 	}
		 	if(source == TargetButton) // Target button
			{
		 		TargetLabel.setText("");
		 		fTarget = true;
		 		MessageLabel.setText("");
		 		String item = list.getSelectedItem(); // get the item that was selected
		 		if(item != null)
		 		{
		 			if(item.endsWith(" +")) // trim the + off of the directory name
		 			{
		 				item = item.substring(0, item.length() - 2);
		 			}
		 			TargetLabel.setText(curDir.getAbsolutePath() + "\\" + item);
		 			display(item); // display the new directory
		 		}
			}
			if(source == OkButton) // ok button
			{ 
				MessageLabel.setText("");
				String item = SourceLabel.getText(); // input file 
				File inp = new File(item);
				filename = FileName.getText(); // output file
				if(filename.length() != 0) // checks output file for size
				{
					fOutfile = true; // set output flag to true
					try
					{
						BufferedReader infile = new BufferedReader(new FileReader(inp)); // input file handle
						File outp = new File (TargetLabel.getText() + "//" + filename);						
						CopyFile(infile, outp);
					}	
					catch (FileNotFoundException FNFE)
					{
						MessageLabel.setText("File not found");
					}
				}
				else
				{
					MessageLabel.setText(MessageLabel.getText() + "Target file not specified.");
				}
			}	
		 }
public void CopyFile(BufferedReader infile, File outp) 
{
	int c;
	PrintWriter o;
	try 
	{
		o = new PrintWriter(new FileWriter(outp));
		if(outp.exists())
			MessageLabel.setText(filename + " exists and will be overwritten!");
		else
			MessageLabel.setText(filename + " created!");
		while((c = infile.read()) != -1)
		{
			o.write(c);
		}
		o.close(); // closes i/o files
		infile.close();
		SourceLabel.setText("");
		TargetLabel.setText("");
		TargetButton.setEnabled(false);
		fTarget = false;
		fSource = false;
		fOutfile = false;
		
	} 
	catch (IOException e) 
	{
		
		e.printStackTrace();
	} 

}
	
} // end window class
