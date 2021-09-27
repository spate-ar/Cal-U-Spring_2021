package Chat;
import java.awt.*;
import java.awt.event.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.io.*;

/*
* Program Chat
* CET 350 Technical Computing using Java
* Andrew Spate & Nicholas Spudich
* Spa3195@calu.edu & spu8504@calu.edu
* Group 2
*/
public class Chat implements WindowListener, ActionListener, Runnable
{
	BufferedReader br; // input from
	PrintWriter pw; // output to the network socket
	
	protected final static boolean auto_flush = true; // PrintWrite constructor
	
	Point FrameSize = new Point(640, 480);
	Point Screen = new Point(FrameSize.x - 1, FrameSize.y - 1);
	String line;
	
	// Buttons
	Button ChangePortButton = new Button(" Change Port");
	Button SendButton = new Button("   Send   ");
	Button ServerButton = new Button("Start Server");
	Button ClientButton = new Button(" Connect ");
	Button DisconnectButton = new Button("Disconnect ");
	Button ChangeHostButton = new Button("Change Host");
	
	// Labels
	Label PortLabel = new Label("Port: ");
	Label HostLabel = new Label("Host: ");
	
	// Textfields
	TextField ChatText = new TextField(70);
	TextField PortText = new TextField(10);
	TextField HostText = new TextField(10);
	
	// Frame & Thread
	Frame DispFrame;
	Thread TheThread;
	
	// TextAreas
	TextArea DialogScreen = new TextArea("", 10, 80); // 10 rows & 80 columns
	TextArea MessageScreen = new TextArea("", 3, 80); // 3 rows & 80 columns
	
	// Sockets
	Socket client;
	Socket server;
	
	// ServerSocket
	ServerSocket listen_socket;
	
	// panels
	Panel ControlPanel;
	
	// Variables
	String host = ""; // string for the internet host name
	int DEFAULT_PORT = 44004;
	int port = DEFAULT_PORT; // network port
	
	int service = 0; // states (3) - initial (0), server (1), client (2)

	static int timeout = 2000; // specifies the wait time for a connection
	
	boolean more = false; // controls the process loop for the program when in server or client
	
	public static void main(String [] args)
	{
		boolean good = true; String number; int num = 0;
		if(args.length != 0)
		{
			number = args[0];
			if(number == null) {good = false;}
			try	{ num = Integer.parseInt(number);}
			catch(NumberFormatException NFE) {good = false; System.out.println(NFE);}
			if(good) {timeout = num;}
		}
		new Chat(timeout);
	}
	public Chat(int SocketWait)
	{
		DispFrame = new Frame("Chat Program"); // frame title & creation
		DispFrame.setLayout(new BorderLayout(0,0)); // set the border layout to the frame
		initComponents();
		DispFrame.add("Center", DialogScreen);
		DispFrame.add("South", ControlPanel);
		
		MessageScreen.setEditable(false);
		DialogScreen.setEditable(false);
		
		more = true;
		service = 0;
		DispFrame.setSize(Screen.x, Screen.y); // set its size
		DispFrame.addWindowListener(this);
		DispFrame.setResizable(true);
		DispFrame.setVisible(true);
		DispFrame.validate();
	}
	public void initComponents()
	{
		// Control Panel
		ControlPanel = new Panel();
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		double ColWeight[] = {1,1,1,1,1,1,1,1}; 
		double RowWeight[] = {1,1,1,1,1,1};
		int ColWidth[] = {1,1,1,1,1,1,1,1};
		int RowHeight[] = {1,1,1,1,1,1};
		
		gbl.columnWeights = ColWeight;
		gbl.columnWidths = ColWidth;
		gbl.rowWeights = RowWeight;
		gbl.rowHeights = RowHeight;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.HORIZONTAL;
		ControlPanel.setLayout(gbl);

		// add the text fields
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 6;
		gbl.setConstraints(ChatText, c);
		ControlPanel.add(ChatText);
		
		// add the labels
		c.fill = GridBagConstraints.NONE;
		c.gridx = 4; // spacing for columns
		c.gridy = 1; // spacing for rows
		c.gridwidth = 1;
		gbl.setConstraints(HostLabel, c);
		ControlPanel.add(HostLabel);
		
		c.gridy = 2; // spacing for rows
		gbl.setConstraints(PortLabel, c);
		ControlPanel.add(PortLabel);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 5; // spacing for columns
		c.gridy = 1; // spacing for rows
		gbl.setConstraints(HostText, c);
		ControlPanel.add(HostText);
		
		c.gridy = 2; // spacing for rows
		gbl.setConstraints(PortText, c);
		ControlPanel.add(PortText);
		
		// add the Change Host Button
		c.gridx = 6; // spacing for columns
		c.gridy = 1; // spacing for rows
		gbl.setConstraints(ChangeHostButton, c);
		ControlPanel.add(ChangeHostButton);
		
		// add the Change Port Button
		c.gridy = 2; // spacing for rows
		gbl.setConstraints(ChangePortButton, c);
		ControlPanel.add(ChangePortButton);
	
		// add the send button
		c.gridy = 0; // spacing for rows
		gbl.setConstraints(SendButton, c);
		ControlPanel.add(SendButton);
		
		// add the server button
		c.gridx = 7; // spacing for columns
		c.gridy = 0; // spacing for rows
		gbl.setConstraints(ServerButton, c);
		ControlPanel.add(ServerButton);
		
		// add the Client/connect button
		c.gridy = 1; // spacing for rows
		gbl.setConstraints(ClientButton, c);
		ControlPanel.add(ClientButton);
		
		// add the disconnect button
		c.gridy = 2; // spacing for rows
		gbl.setConstraints(DisconnectButton, c);
		ControlPanel.add(DisconnectButton);
		
		c.gridx = 0;
		c.gridy = 3;
		c.gridheight = 3;
		c.gridwidth = 8;
		gbl.setConstraints(MessageScreen, c);
		ControlPanel.add(MessageScreen);
		ControlPanel.setVisible(true);
		
		// Add the ActionListener to the Buttons
		ChangePortButton.addActionListener(this);
		SendButton.addActionListener(this);
		ServerButton.addActionListener(this);
		ClientButton.addActionListener(this);
		DisconnectButton.addActionListener(this);
		ChangeHostButton.addActionListener(this);
		ChatText.addActionListener(this);
	}
	public void start()
	{
		if(TheThread == null)
		{
			TheThread = new Thread(this); // creates the Thread and starts it
			TheThread.start();
		}
	}
	public void stop() // closes the socket, reader, and writer, and reset program to initial state (close)
	{
		try { if(client != null) {client.close();} if(server != null) {server.close();}
			  if(br != null) {br.close();} if(pw != null) {pw.close();} service = 0;}
		catch (IOException e) {e.printStackTrace();}
		if(TheThread != null) {TheThread.setPriority(Thread.MIN_PRIORITY);}
		ChangePortButton.removeActionListener(this);
		ChatText.removeActionListener(this);
		SendButton.removeActionListener(this);
		ServerButton.removeActionListener(this);
		ClientButton.removeActionListener(this);
		DisconnectButton.removeActionListener(this);
		ChangeHostButton.removeActionListener(this);
		DispFrame.removeWindowListener(this);
		DispFrame.dispose();
		System.exit(0);
	}
	public void close() // checks the existence of the sockets, reader, and writer and closes and null them if they exist
	{
		try
		{
			if(server != null) // does the server socket exist?
			{
				if(pw != null) {pw.print("");} // does the printwriter exist, send null to other device
				server.close(); // close the socket
				server = null; // null the socket
			}
		} catch(IOException e) {}
		// reset the buttons
		ChangePortButton.setEnabled(true);
		SendButton.setEnabled(true);
		ServerButton.setEnabled(true);
		ClientButton.setEnabled(true);
		DisconnectButton.setEnabled(true);
		ChangeHostButton.setEnabled(true);
		HostText = new TextField(10); // reset the host text field
		service = 0; // reset the state
		TheThread = null; // null the Thread
		
	}
	public void Display(String n)
	{
		if(service == 1) // server
		{
			MessageScreen.append("Server: " + n + "\n");
		}
		if(service == 2) // client
		{
			MessageScreen.append("Client: " + n + "\n");
		}
		ChatText.requestFocus(); // sets the focus back to the Chat TextField
	}
	public void run() 
	{
		TheThread.setPriority(Thread.MAX_PRIORITY); // set the Thread Priority to Max
		// Display a status message
			while(more)
			{	try 
				{	
					line = br.readLine();
					if (line == null) {more = !more;}
					DialogScreen.append("in: "+line+"\n");
				} catch(IOException e) {}
			}
		// Display a status message
		try { if(client != null) {client.close();} if(server != null) {server.close();}
		  if(br != null) {br.close();} if(pw != null) {pw.close();} service = 0;}
		catch (IOException e) {e.printStackTrace();}
		}
	public void actionPerformed(ActionEvent e) 
	{
		Object source = e.getSource();
		String data = "";
		if((source == ChatText || source == SendButton) && service > 0)
		{
			data = ChatText.getText();
			DialogScreen.append("out: " +data+"\n"); // append the message to the DialogScreen TextArea
			pw.println(data); // send the message out through the Socket's PrintWriter
			ChatText.setText(""); // clear the chat textfield
		}
		if(source == ServerButton)
		{
			// send a message to the status message TextArea
			MessageScreen.append("Starting Server\n");
			try
			{
				ServerButton.setEnabled(false); // disable the server & client buttons
				ClientButton.setEnabled(false);
				if(listen_socket != null) {listen_socket.close(); listen_socket = null;}
				MessageScreen.append("Server: listening on port " + port + "\n");
				listen_socket = new ServerSocket(port);
				MessageScreen.append("Server: timeout time set to " + timeout + "mS." + "\n");
				listen_socket.setSoTimeout(10 * timeout);
				if(client != null) {client.close(); client = null;}
				try
				{
					// send an updated status message to the status message TextArea
					MessageScreen.append("Server: waiting for a request on " + port + "\n");
					client = listen_socket.accept(); // listen for a socket request
					DispFrame.setTitle("Server"); // set the title of the Frame to indicate it is a server
					MessageScreen.append("Server: connection from " + client.getInetAddress() + "\n");
					try
					{
						br = new BufferedReader (new InputStreamReader(client.getInputStream()));
						pw = new PrintWriter (client.getOutputStream(), auto_flush);
						service = 1; // change the state to server mode
						ChatText.setEnabled(true); // enable the chat text field
						more = true; // set the run while loop flag to true
						start(); // start the Thread
						MessageScreen.append("Server: Chat is running.\n");
					} catch(IOException er) {close(); MessageScreen.append(er.toString());}
				} catch(SocketTimeoutException s) {close(); MessageScreen.append(s.toString());}
			} catch(IOException er) {close(); MessageScreen.append(er.toString());}
		}
		if(source == ClientButton)
		{
			MessageScreen.append("Starting Client\n");// send a message to the status message TextArea
			try
			{
				ServerButton.setEnabled(false); // disable the server & client buttons
				ClientButton.setEnabled(false);
				if(server != null) {server.close(); server = null;} // check if server exists and close and clear if it does
				server = new Socket(); // create a server socket
				// send a message to the status message TextArea
				MessageScreen.append("Client: timeout time set to " + timeout + "mS\n");
				server.setSoTimeout(timeout); // set the timeout for the client to wait for a connection
				try
				{
					// send a message to the status message TextArea
					MessageScreen.append("Connecting to " + HostText.getText() + "\n");
					server.connect(new InetSocketAddress(host, port)); // send a connection request to the server
					DispFrame.setTitle("Client"); // set the frame title to client
					// send an updated status message to the status message TextArea that a connection has been made to...
					MessageScreen.append("Client: connected to" + server.getInetAddress() + " at port " + port + "\n");
					try
					{
						br = new BufferedReader(new InputStreamReader(server.getInputStream())); // create the BufferedReader
						pw = new PrintWriter(server.getOutputStream(), auto_flush); // create the PrintWriter
						service = 2; // change the state to client
						ChatText.setEnabled(true); // enable the chat text field
						more = true; // set the run while loop flag to true
						start(); // start the thread with start
						MessageScreen.append("Client: Chat is running.\n");
					} catch(IOException er) {close(); MessageScreen.append(er.toString());}
				} catch(SocketTimeoutException s) {close(); MessageScreen.append(s.toString());}
			} catch(IOException er) {close(); MessageScreen.append(er.toString());}
		}
		if(source == DisconnectButton)
		{
			// send a status message to the status display TextArea
			MessageScreen.append("Disconnected.\n");
			TheThread.interrupt(); // interrupt the thread
			try { if(client != null) {client.close(); client = null;} if(server != null) {server.close(); server = null;}
			  if(br != null) {br.close();} if(pw != null) {pw.close();} service = 0; close();}
			catch (IOException er) {}
		}
		if(source == HostText || source == ChangeHostButton) 
		{
			if((data = HostText.getText()) != null) {ClientButton.setEnabled(true);}
		}
		if(source == PortText || source == ChangePortButton)
		{
			String p = PortText.getText();
			try
			{
				int number = Integer.parseInt(p); // convert string to an int
				port = number; // set the port variable to this new int port value
				if(data != null) {ClientButton.setEnabled(true);} // set Connect Button enabled based on having a Host String
			} catch(NumberFormatException NFE) {}
		}
		ChatText.requestFocus(); // set the focus to the chat text field for the user to enter a message to send
	}
	public void windowOpened(WindowEvent e) {ChatText.requestFocus();} 
	public void windowClosing(WindowEvent e) {stop();}
	public void windowClosed(WindowEvent e) {ChatText.requestFocus();}
	public void windowIconified(WindowEvent e) {ChatText.requestFocus();}
	public void windowDeiconified(WindowEvent e) {ChatText.requestFocus();}
	public void windowActivated(WindowEvent e) {ChatText.requestFocus();}
	public void windowDeactivated(WindowEvent e) {ChatText.requestFocus();}
}
