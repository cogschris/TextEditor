package server;

public class UpdateThread extends Thread{
	Server s;
	int time;
	
	public UpdateThread(Server s) {
		this.s = s;
		this.start();
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(5000);
				String blahCode = new String("////blah////");
				s.sendAll(blahCode);
				//System.out.println("Sent the blah");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
