package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import server.Commands.Command;

public class ClientConnection implements Runnable{

	private Socket mSocket;
	private BufferedReader mReader;
    private BufferedWriter mWriter;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Server s;
    private Log mLog;
    private ServerSocket tempsocket;
    private ManageThread mt;
    
    private final static String DIRECTORY = "serverfiles/";
    private String user;
    private File userDirectory;
	
	public ClientConnection(Socket inSocket, Log inLog, Server s, ServerSocket s2) throws IOException {
		System.out.println("this is formed");
		mSocket = inSocket;
		tempsocket = s2;
		mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
		mWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
		//ois = new ObjectInputStream(mSocket.getInputStream());
		//oos = new ObjectOutputStream(mSocket.getOutputStream());
		mLog = inLog;
		this.s = s;
		mt = new ManageThread(s, s2);
	}
	
	private void newLineFlush() throws IOException {
		mWriter.newLine();
		mWriter.flush();
	}
	
	@Override
	public void run() {
		boolean running = true;
		while(running) {
			try {
				
				String line = mReader.readLine();
				System.out.println("read");
				String[] split = line.split(" ");
				mLog.log(Commands.logCommand(split));
				switch(Command.valueOf(split[0])) {
				case Signup:
					if(Database.get().signup(split[1], split[2])) {
						mLog.log("Signup success User:" + split[1]);
						mWriter.write(Command.Success.toString());
						user = split[1];
						userDirectory = new File(DIRECTORY+user);
						userDirectory.mkdir();
					}
					else {
						mLog.log("Signup failure User:" + split[1]);
						mWriter.write(Command.Failure.toString());
					}
					newLineFlush();
					break;
				case Login:
					System.out.println("sent okay");
					if(Database.get().login(split[1], split[2])) {
						mLog.log("Login success User:" + split[1]);
						mWriter.write(Command.Success.toString());
						user = split[1];
						userDirectory = new File(DIRECTORY+user);
					}
					else {
						mLog.log("Login failure User:" + split[1]);
						mWriter.write(Command.Failure.toString());
					}
					newLineFlush();
					break;
				case GetUserFileNames:
					StringBuilder sb = new StringBuilder();
					for(File f : userDirectory.listFiles()) if(f.isFile()) sb.append(f.getName()+" ");
					mWriter.write(sb.toString());
					newLineFlush();
					break;
				case OpenUserFile: {
					FileInputStream fis = new FileInputStream(DIRECTORY+user+"/"+split[1]);
					int x = 0;
					
					while((x=fis.read()) != -1) {
						mWriter.write(x);
					}
					mWriter.write(0);
					mWriter.flush();
					accept(DIRECTORY+user+"/"+split[1]);
					fis.close();
					mLog.log("File opened User:" + user + " File:" + split[1]);
				} break;
				case OpenShareFile: {
					FileInputStream fis = new FileInputStream(DIRECTORY+split[2]+"/"+split[1]);
					int x = 0;
					
					while((x=fis.read()) != -1) {
						mWriter.write(x);
					}
					mWriter.write(0);
					mWriter.flush();
					System.out.println("i am about to accept");
					accept(DIRECTORY+split[2]+"/"+split[1]);
					System.out.println("i finished to accept");
					fis.close();
					//mLog.log("File opened User:" + user + " File:" + split[1]);
				} break;
				case SaveUserFile: {
					FileOutputStream fos = new FileOutputStream(DIRECTORY+user+"/"+split[1]);
					int x = 0;
					while((x=mReader.read()) != 0) fos.write(x);
					fos.close();
					mLog.log("File saved User:" + user +" File:"+split[1]);
					Database.get().newDoc(user, split[1]);
				} break;
				case GetSharedUsers: {
					StringBuilder st = new StringBuilder();
					Vector<String> v = Database.get().getUsers(split[1], user);
					for (int i = 0; i < v.size(); i++) {
						st.append(v.get(i) + " ");
						System.out.println(v.get(i));
					}
					mWriter.write(st.toString());
					newLineFlush();
					System.out.println("sent back to client");
					mWriter.flush();
					
				} break;
				case AddUser: {
					if (Database.get().check(split[1])) {
						System.out.println("received");
						if (!Database.get().newShare(split[1], split[2], user)) {
							System.out.println("failed");
						}
					}
				} break;
				case RemoveUser: {
					System.out.println("Did i even make it here yet?");
					if (!Database.get().removeShare(split[1], split[2], user)) {
						System.out.println("failed");
					}
				} break;
				case GetSharedtome: {
					StringBuilder st = new StringBuilder();
					Vector<String> v = Database.get().getHosts(user);
					for (int i = 0; i < v.size(); i++) {
						st.append(v.get(i) + " ");
						System.out.println(v.get(i));
					}
					mWriter.write(st.toString());
					newLineFlush();
					System.out.println("sent back to client");
					mWriter.flush();
				} break;
				case GetSharedFiles: {
					StringBuilder st = new StringBuilder();
					//st.append(split[1] + " ");
					Vector<String> v = Database.get().getuserfiles(split[1], user);
					for (int i = 0; i < v.size(); i++) {
						st.append(v.get(i) + " ");
						System.out.println(v.get(i));
					}
					mWriter.write(st.toString());
					newLineFlush();
					System.out.println("sent2 back to client");
					mWriter.flush();
				} break;
				case Heartbeat: break;
				default:
					mLog.log("Command not processed: " + split[0]);
					break;
				}
			} catch (Exception e) {
				System.out.println("Shutting down");
				e.printStackTrace();
				try {mSocket.close();} catch (IOException ioe) {}
				running = false;
			}
		}
	}
	
	public void accept(String blah) throws IOException {
		//System.out.println("start");
		Socket sock = tempsocket.accept();
		//System.out.println("middle");
		s.add(new ServerThread(sock, s, blah));
		System.out.println("end");
	}

}
