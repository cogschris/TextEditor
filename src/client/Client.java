package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

import server.Commands.Command;

final public class Client {
	
	private static Client mInstance;
	
	public static Client get() {
		return mInstance;
	}
	
	private static String CONFIG_FILE = "client.config";
	private static String host = null;
	private static int port = -1;
	private static Socket mSocket;
	private static BufferedReader mReader;
	private static BufferedWriter mWriter;
	private static String mUsername;
	private ClientThread client;
	
	static {
		mInstance = new Client();
	}
	
	{
		loadConfig();
		connect();
	}
	
	public void loadConfig(){
		Scanner s = null;
		try {
			s = new Scanner(new File(CONFIG_FILE));
			while(s.hasNext()) {
				String id = s.next();
				switch(id) {
				case "Port":
					port = Integer.valueOf(s.next());
					break;
				case "Host":
					host = s.next();
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("Error in configuration File!");
		} finally {
			if(s != null) s.close();
		}
	}
	
	public void connect() {
		if(mSocket != null) {
			try {
				mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			mSocket = new Socket(host, port);
			mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			mWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
		} catch (IOException e) {
			System.out.println("Unable to connect to:" + host + port);
		}
	}
	
	public void write(String str) throws IOException {
		mWriter.write(str);
	}
	
	public void writeln(String str) throws IOException {
		mWriter.write(str);
		mWriter.newLine();
	}
	
	public void writeFile(File f) throws IOException {
		FileInputStream fis = new FileInputStream(f);
		int x = 0;
		while((x=fis.read()) != -1) {
			mWriter.write(x);
		}
		mWriter.write(0);
		mWriter.flush();
		fis.close();
	}
	
	public void flush() throws IOException {
		mWriter.flush();
	}
	
	public String read() throws IOException {
		return mReader.readLine();
	}
	
	public void readFile(File f) throws IOException {
		FileOutputStream fos = new FileOutputStream(f);
		int x = 0;
		while((x=mReader.read()) != 0) fos.write(x);
		fos.close();
	}

	public boolean isOnline() {
		try {
			writeln(Command.Heartbeat.toString());
			flush();
		} catch (Exception e) { return false; }
		return true;
	}

	public void setUser(String inUsername) {
		mUsername = inUsername;
	}
	
	public boolean isAuthentic() {
		return mUsername != null;
	}
	
	public String getUser() {
		return mUsername;
	}
	
}
