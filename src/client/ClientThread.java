package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JOptionPane;

import client.diff_match_patch.Patch;
import textdocument.TextDocumentPanel;




public class ClientThread extends Thread{
	
	private Socket s;
	private diff_match_patch dmp;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Vector<ClientThread> otherguys;
	private Vector<String> potential;
	TextDocumentPanel tdp;
	private String orig;
	String filename;
	public ClientThread(TextDocumentPanel tdp, String name, String text) {
		this.tdp = tdp;
		orig = text;
		dmp = new diff_match_patch();
		filename = name;
		potential = new Vector<String>();
		try{
			s = new Socket("localhost", 8798);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
		} catch (IOException ioe) {
			System.out.println("Error establishing");
		}
	}
	public void run() {
		while(true) {
			try {
				//System.out.println("Trying to read object");
				String update = (String)ois.readObject();
				//System.out.println("Already read object");
				if (update.equals("////blah////")) {
					merge();
					send();
				}
				else {
					//potential.addElement(update);
					potential.add(update);
				}
				
			}  catch(IOException e) {
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
	}
	
	public void merge() {
		String one = tdp.getText();
		LinkedList<Patch> patch1 = dmp.patch_make(orig, one);
		String merge1 = (String)(dmp.patch_apply(patch1, orig)[0]);
		
		for (int i = 0; i < potential.size(); i++) {
			LinkedList<Patch> patch2 = dmp.patch_make(orig, potential.get(i));
			merge1 = (String)(dmp.patch_apply(patch2, merge1)[0]);
		}
		potential = new Vector<String>();
		tdp.setText(merge1);
		orig = merge1;
		System.out.println(merge1);
	}
	public void send() {
		try {
			oos.writeObject(tdp.getText());
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
