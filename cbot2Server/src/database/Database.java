package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import errorMessage.ErrorMessage;

public class Database {
	private static final String DATABASE_URL = "jdbc:h2:~/cbotdb/cbotdb";
	private static final String DATABASE_USER = "Jharod";
	private static final String DATABASE_PASS = "halo1combat";

	public static void initialize(){
		try {
			Class.forName("org.h2.Driver");
		} catch (Exception e){
			e.printStackTrace(System.out);
			ErrorMessage.throwFatalError("Fatal error: Unable to connect to database.");
		}
	}
	
	public synchronized static ResultSet exec(String command, String type){
		if (type.equals("query")){
			return execQuery(command);
		} else if (type.equals("update")){
			execUpdate(command);
		}
		return null;
	}
	
	private synchronized static ResultSet execQuery(String query){
		ResultSet rs = null;
		
		try {
			Connection dbCon = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
			Statement dbStmt = dbCon.createStatement();
			rs = dbStmt.executeQuery(query);
		} catch (Exception e){
			e.printStackTrace(System.out);
			ErrorMessage.throwFatalError("Error: unable to access database with query " + query);
		}
		return rs;
	}
	
	private synchronized static void execUpdate(String update){
		try {
			Connection dbCon = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASS);
			Statement dbStmt = dbCon.createStatement();
			dbStmt.executeUpdate(update);
		} catch (Exception e){
			e.printStackTrace(System.out);
			ErrorMessage.throwFatalError("Error: unable to access database with update " + update);
		}
	}
}
