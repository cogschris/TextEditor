package server;

import java.net.ServerSocket;
import java.net.Socket;

public class ManageThread extends Thread{

	private Server s;
	private ServerSocket sock;
	public ManageThread(Server s, ServerSocket sock) {
		this.s = s;
		this.sock = sock;
	}
	
	@Override 
	public void run() {
		/*try {
			while (true) {
				Socket b = sock.accept();
			}
		} catch(IOException) {
			
		}*/
	}
}
