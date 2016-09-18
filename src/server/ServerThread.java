package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//import ccognett_CSCI201_Assignment2.Server;

public class ServerThread extends Thread{
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Server s;
	//private int name;
	private boolean sendthings;
	private String name;
	
	public ServerThread(Socket s, Server server, String name) {
		sendthings = true;
		this.name = name;
		try {
			this.s = server;
			//this.name = name;
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			this.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getfile() {
		return name;
	}
	
	public void send(String update) {
		
		try {
			oos.writeObject(update);
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(true) {
			try {
				String r = (String)ois.readObject();
				s.send(r, name);
				
			}  catch(IOException e) {
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
}
