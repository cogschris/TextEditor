package server;

import com.mysql.jdbc.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class Database {
	
	private static Database sDatabase;
	public static Database get() {
		return sDatabase;
	}
	
	static {
		sDatabase = new Database();
	}
	
	private final static String newAccount = "INSERT INTO USERLIST(USERNAME,PASSWORD) VALUES(?,?)";
	private final static String selectUser = "SELECT PASSWORD FROM USERLIST WHERE USERNAME=?";
	private final static String newDocument = "INSERT INTO OWNERS(FILENAME, USEROWNER) VALUES(?,?)";
	private final static String selectDoc = "SELECT FILENAME FROM OWNERS WHERE USEROWNER=?";
	private final static String newShared = "INSERT INTO SHARED(FILEID,USEROWNER,USERSHARED) VALUES(?,?,?)"; ////////
	private final static String getSharedOwners = "SELECT USEROWNER FROM SHARED WHERE USERSHARED=?";
	private final static String getSharedGuys = "SELECT * FROM SHARED WHERE USEROWNER=? AND FILEID=?";
	private final static String getFiles = "SELECT * FROM OWNERS WHERE FILENAME=?";
	private final static String removeShare = "DELETE FROM SHARED WHERE FILEID=? AND USEROWNER=? AND USERSHARED=?";
	private final static String getSharedtome = "SELECT * FROM SHARED WHERE USERSHARED=?";
	private final static String getSharedfilez = "SELECT * FROM SHARED WHERE USEROWNER=? AND USERSHARED=?";
	private final static String getFilename = "SELECT * FROM OWNERS WHERE FILEID=?";
	
	private Connection con;
	
	{
		try{
			new Driver();
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ccognett_users?user=root&password=root");
		} catch(SQLException sqle){
			System.out.println("SQL:"+sqle.getMessage());
		}
	}
	
	public void stop() {
		try {con.close();} catch (SQLException e) {e.printStackTrace();}
	}
	
	public boolean check(String username) {
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(selectUser);
			ps.setString(1,username);
			ResultSet result = ps.executeQuery();
			if(result.next()) {//if we have any results, don't let the user sign up.
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean signup(String username, String password) {
		try {
			{ //check if the user exists
				PreparedStatement ps = con.prepareStatement(selectUser);
				ps.setString(1,username);
				ResultSet result = ps.executeQuery();
				if(result.next()) {//if we have any results, don't let the user sign up.
					return false;
				}
			}
			{ //sign up
				PreparedStatement ps = con.prepareStatement(newAccount);
				ps.setString(1, username);
				ps.setString(2, password);
				ps.executeUpdate();
			}
		} catch (SQLException e) {return false;}
		return true;
	}
	
	public boolean login(String username, String password) {
		try {
			PreparedStatement ps = con.prepareStatement(selectUser);
			ps.setString(1,username);
			ResultSet result = ps.executeQuery();
			while(result.next()) {
				if(password.equals(result.getString("password"))) return true;
			}
		} catch (SQLException e) {return false;}
		return false;
	}
	
	public boolean newDoc(String username, String filename) {
		try {
			PreparedStatement ps = con.prepareStatement(newDocument);
			ps.setString(1,filename);
			ps.setString(2, username);
			ps.executeUpdate();
			return true;
			//ResultSet result = ps.executeQuery();
			//while(result.next()) {
				//if(password.equals(result.getString("password"))) return true;
			//}
		} catch (SQLException e) {return false;}
		//return false;
	}
	
	public boolean newShare(String username, String filename, String userOwner) {
		try {
			System.out.println("trying to insert");
			PreparedStatement ps = con.prepareStatement(getFiles);
			ps.setString(1,filename);
			ResultSet result = ps.executeQuery();
			int temp = -1;
			while(result.next()) {
				//System.out.println("fun  " + result.getInt("fileID"));
				int id = result.getInt("fileID");
				String owner = result.getString("userOwner");
				System.out.println("fun23  " + owner + userOwner);
				System.out.println("fuck  " + id);
				System.out.println(owner.equals(userOwner));
				if(owner.equals(userOwner)) {
					
					temp = id;
					
					System.out.println("id is:  " + id);
				}
			}
			System.out.println("temp is: " + temp);
			if (temp != -1) {
				PreparedStatement ps2 = con.prepareStatement(newShared);
				ps2.setInt(1,temp); ps2.setString(2, userOwner); ps2.setString(3, username);
				ps2.executeUpdate();
				return true;
			}
			
			
			
			return false;
			//ResultSet result = ps.executeQuery();
			//while(result.next()) {
				//if(password.equals(result.getString("password"))) return true;
			//}
		} catch (SQLException e) {
			System.out.println("sql fail");return false;}
		//return false;
	}
	
	public boolean removeShare(String username, String filename, String userOwner) {
		System.out.println("please start");
		try {
			System.out.println("starting...");
			PreparedStatement ps = con.prepareStatement(getFiles);
			ps.setString(1,filename);
			ResultSet result = ps.executeQuery();
			
			int temp = -1;
			while(result.next()) {
				if(userOwner.equals(result.getString("userOwner"))) {
					temp = result.getInt("fileID");
				}
			}
			System.out.println(temp + " ryan");
			if (temp == -1) {
				return false;
			}
			System.out.println("midway " + temp);
			PreparedStatement ps2 = con.prepareStatement(removeShare);
			System.out.println(temp + "   " + userOwner + "   " + username);
			ps2.setInt(1,temp); ps2.setString(2, userOwner); ps2.setString(3, username);
			ps.executeUpdate();
			
			return true;
			//ResultSet result = ps.executeQuery();
			//while(result.next()) {
				//if(password.equals(result.getString("password"))) return true;
			//}
		} catch (SQLException e) {System.out.println("sql eeor");return false;}
		//return false;
	}
	
	public Vector<String> getUsers(String filename, String fileOwner) throws SQLException {
		Vector<String> vec = new Vector<String>();
		PreparedStatement ps = con.prepareStatement(getFiles);
		ps.setString(1,filename);
		ResultSet result = ps.executeQuery();
		int temp = -1;
		while(result.next()) {
			//System.out.println("fun  " + result.getInt("fileID"));
			int id = result.getInt("fileID");
			String owner = result.getString("userOwner");
			
			if(owner.equals(fileOwner)) {
				
				temp = id;
				
			}
		}
		if (temp == -1) {
			return null;
		}
		
		
		PreparedStatement ps2 = con.prepareStatement(getSharedGuys);
		ps2.setInt(2, temp);
		ps2.setString(1, fileOwner);
		ResultSet r = ps2.executeQuery();
		while (r.next()) {
			vec.add(r.getString("userShared"));
		}
		
		return vec;
		
		
	}
	
	public Vector<String> getHosts(String username) {
		Vector<String> vec = new Vector<String>();
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(getSharedtome);
			ps.setString(1,username);
			ResultSet result = ps.executeQuery();
			while(result.next()) {
				System.out.println(result.getString("userOwner"));
				if (!vec.contains(result.getString("userOwner"))) {
					vec.addElement(result.getString("userOwner"));
				}
			}
			return vec;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	public Vector<String> getuserfiles(String owner, String username) {
		Vector<String> vec = new Vector<String>();
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(getSharedfilez);
			ps.setString(1,owner); ps.setString(2, username);
			ResultSet result = ps.executeQuery();
			while(result.next()) {
				//System.out.println(result.getString("userOwner"));
				PreparedStatement ps2 = con.prepareStatement(getFilename);
				int i = result.getInt("fileID");
				System.out.println("file id is : " + i);
				ps2.setInt(1, i);
				ResultSet resultz= ps2.executeQuery();
				while (resultz.next()) {
					if (!vec.contains(resultz.getString("fileName"))) {
						vec.addElement(resultz.getString("fileName"));
					}
				}
			}
			return vec;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	public static java.sql.Timestamp getCurrentTimeStamp() {
		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());
	}
}