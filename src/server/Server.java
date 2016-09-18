package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JButton;

public class Server {
	private final ServerLogFrame mServerLog;
	private static final String CONFIG_FILE = "server.config";
	private int port;

	private ServerSocket ss = null;
	private ServerSocket ss2 = null;
	private Thread mThread;
	private ArrayList<Socket> mConnections;
	private Vector<ServerThread> st;
	
	{
		JButton toggleButton = new JButton() {
			private static final long serialVersionUID = 6990460083243951898L;
			private final static String START_TEXT = "Start";
			private final static String STOP_TEXT = "Stop";
			private boolean started = false;
			{
				setText(START_TEXT);
				addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(!started) {
						setText(STOP_TEXT);
						mThread = new Thread(()->{run();});
						mThread.start();
					} else {
						setText(START_TEXT);
						mThread.interrupt();
						try { ss.close(); } catch (IOException e) { }
					}
					started = !started;
				}
			});
			}
		};
		mServerLog = new ServerLogFrame(toggleButton);
		
		Scanner s = null;
		try {
			s = new Scanner(new File(CONFIG_FILE));
			while(s.hasNext()) {
				String id = s.next();
				switch(id) {
				case "Port":
					port = Integer.valueOf(s.next());
					break;
				}
			}
		} catch (FileNotFoundException e) {
			mServerLog.log("Error in configuration File!");
		} finally {
			if(s != null) s.close();
		}
		mConnections = new ArrayList<Socket>();
		st = new Vector<ServerThread>();
		new UpdateThread(this);
		mServerLog.setVisible(true);
	}
	
	public static void main(String[] args) {
		new Server();
	}

	public void run() {
		try {
			ss = new ServerSocket(port);
			ss2 = new ServerSocket(8798);
			mServerLog.log("Server Started On Port:" + port);
			while(true) {
				Socket socket = ss.accept();
				
				new Thread(new ClientConnection(socket, mServerLog, this, ss2)).start();
				//ServerThread thread = new ServerThread(socket, this);
				//new Thread(thread).start();
				mConnections.add(socket);
				//st.add(thread);
				//st.add(new ServerThread(socket, this));
			}
		} catch (Exception e) {
			for(Socket s : mConnections)
				try { s.close(); } catch (IOException e1) { }
			mServerLog.log("Server stopped.");
		} finally {
			try {
				if(ss != null) ss.close();
			} catch (IOException e) {
			}
		}
	}
	
	public void add(ServerThread s) {
		st.addElement(s);
	}
	
	public void send(String update, String name) {
		for (int i = 0; i < st.size(); i++) {
			System.out.println(st.get(i).getfile() + "      sending to: " + name);
			if (st.get(i).getfile().equals(name)) {
				System.out.println(update + "           something");
				st.get(i).send(update);
			}
		}
	}
	
	public void sendAll(String message) {
		//System.out.println("Size of sending" + st.size());
		for (int i = 0; i < st.size(); i++) {
			
				st.get(i).send(message);
			
		}
	}
}
