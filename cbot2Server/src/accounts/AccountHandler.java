package accounts;

import gui.AccountPanel;
import gui.AccsRemainingLabel;
import gui.TextPanel;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import client.ClientType;
import accounts.Account;
import config.Config;
import database.Database;
import errorMessage.ErrorMessage;

public class AccountHandler {
	private static int numAccsSent = 40;

	private static final int CHANGE_INTERVAL = 2;

	private static ArrayList<Account> accounts = new ArrayList<Account>();

	private static ArrayList<Account> guiRemAccounts = new ArrayList<Account>();
	private static ArrayList<Account> guiUsedAccounts = new ArrayList<Account>();

	private static TextPanel output;

	private static AccountPanel remAccsOutput;
	private static AccountPanel usedAccsOutput;

	private static AccsRemainingLabel arLabel;
	private static Calendar cal;
	private static int totalAccs;

	
	public static synchronized int getNumAccsSent(){
		return numAccsSent;
	}
		
	public static synchronized void resetNumAccsSent(){
		numAccsSent = 0;
	}

	public synchronized static void initialize(TextPanel output){
		AccountHandler.output = output;
		
		//setup calendar
		cal = Calendar.getInstance(); // creates calendar
		cal.setTime(new Date()); // sets calendar time/date
		
		//change this line back after login event is over
		cal.add(Calendar.HOUR_OF_DAY, 10); // add 2 hours because the server is 2 hrs ahead	
		//cal.add(Calendar.HOUR_OF_DAY, 2); // add 2 hours because the server is 2 hrs ahead	
	}

	public synchronized static void setRemAccsOutput(AccountPanel accPanel){
		remAccsOutput = accPanel;
	}
	public synchronized static void setUsedAccsOutput(AccountPanel accPanel){
		usedAccsOutput = accPanel;
	}

	//loads the specified set of accounts
	public synchronized static void setAccounts(ClientType mode){
		System.out.println("\"" + mode + "\"");
		System.out.println(Config.getLoadAccsType());
		if (!Config.getLoadAccsType().equals(Config.LOAD_ACCS_NORMALLY)){ //user has manually supplied a list of accounts to process
			loadCustomAccs(Config.getLoadAccsType());
		} else {
			if (mode.equals(ClientType.CHECKIN)){
				loadCheckinAccounts();
			} else if (mode.equals(ClientType.LOGIN)){
				loadAllAccounts();
			} else if (mode.equals(ClientType.HARVEST)){
				loadHarvestAccounts();
			} else if (mode.equals(ClientType.GET_MAIL)){
				loadAccountsWithMail();
			} else if (mode.equals(ClientType.POPULATE_ACCS)){
				loadAllAccounts();
			} else {
				loadAccountsFromFile();
			} 
		}

		for (int i=0; i<accounts.size(); i++){
			System.out.println(accounts.get(i).toString());
		}

		totalAccs = accounts.size();

		guiRemAccounts = new ArrayList<Account>(accounts);
		arLabel.update(guiRemAccounts.size(), totalAccs);
		remAccsOutput.update(guiRemAccounts);
	}
	
	private static synchronized void loadCustomAccs(String command){
		if (command.equals(Config.LOAD_ACCS_FROM_FILE)){
			loadAccountsFromFile();
		} else if (command.equals(Config.LOAD_ACCS_WITH_MAIL)){
			loadAccountsWithMail();
		}
		
		
	}
	
	//loads all accounts where at least one character has mail
	private static synchronized void loadAccountsWithMail(){
		try {
			//get all checkin accounts and store them
			ResultSet rs = Database.exec("select * from accounts where type = 'checkin'", "query");
			while (rs.next()){
				accounts.add(new Account(rs));
			}
			
			//weed out the ones that don't have mail
			//set the characters to log to only be ones with mail
			for (int i=accounts.size()-1; i>=0; i--){
				if (!accounts.get(i).hasMail()){
					System.out.println("Removed account " + accounts.get(i));
					accounts.remove(i);
				} else {
					accounts.get(i).setCharsByMail();
				}
			}		
			rs.close();
		} catch (Exception e){
			e.printStackTrace(System.out);
			ErrorMessage.throwError("Fatal error: Unable to load accounts from database.");
			System.exit(0);
		}
	}

	//load accounts from config file, assume password = halo1combat and all characters should be processed
	private static synchronized void loadAccountsFromFile(){
		try {
			Scanner s = new Scanner(new File("config/accounts.txt"));

			while (s.hasNextLine()){
				accounts.add(new Account(s.nextLine()));
			}
			
			s.close();		
		} catch (Exception e){
			e.printStackTrace(System.out);
		}

	}

	//loads all accounts that haven't logged in today
	private static synchronized void loadCheckinAccounts(){
		try {
			//get all checkin accounts and store them
			ResultSet rs = Database.exec("select * from accounts where type = 'checkin' and loggedIn !=" + cal.get(Calendar.DAY_OF_WEEK) , "query");
			while (rs.next()){
				accounts.add(new Account(rs));
			}
			rs.close();
			
			
			
		} catch (Exception e){
			e.printStackTrace(System.out);
			ErrorMessage.throwError("Fatal error: Unable to load accounts from database.");
			System.exit(0);
		}

	}
	
	//loads all accounts
	private static synchronized void loadAllAccounts(){
		try {
			//get all checkin accounts and store them
			ResultSet rs = Database.exec("select * from accounts" , "query");
			while (rs.next()){
				accounts.add(new Account(rs));
			}
			rs.close();
			
			
			
		} catch (Exception e){
			e.printStackTrace(System.out);
			ErrorMessage.throwError("Fatal error: Unable to load accounts from database.");
			System.exit(0);
		}

	}

	//loads the 30 accounts with the most days logged
	private static synchronized void loadHarvestAccounts(){
		try {	
			//get all checkin accounts and store them
			ResultSet rs = Database.exec("select * from accounts where type = 'checkin'", "query");
			while (rs.next()){
				accounts.add(new Account(rs));
			}
			rs.close();

			//remove the account with the least number of days logged until only 30 remain
			while (accounts.size() > 30){
				int lowestDays = 0;
				for (int i=0; i<accounts.size(); i++){
					if (accounts.get(i).getTotalDaysLogged() < accounts.get(lowestDays).getTotalDaysLogged()){
						lowestDays = i;
					}
				}
				accounts.remove(lowestDays);
			}
			
			for (int i=0; i<accounts.size(); i++){
				if (accounts.get(i).getTotalDaysLogged()/8 < 20){
					accounts.remove(i);
					i--;
				}
			}
			
			if (accounts.size() <= 0){				
				ErrorMessage.throwFatalError("No accounts are ready for harvesting");
			}
			

		} catch (Exception e){
			e.printStackTrace(System.out);
			ErrorMessage.throwError("Fatal error: Unable to load accounts from database.");
			System.exit(0);
		}

	}
	
	//returns the next account and removes it from the arraylist
	public static synchronized String nextAccount(){
		String account = "";
		
		if (accounts.size() > 0){
			account += accounts.get(0).toString() + ";";
			accounts.remove(0);
		} 

		System.out.println("Sending " + account + "(" + (30-getNumAccsRemaining()) + "/30)");
		numAccsSent++;
		
		return account;
	}

	//returns the number of remaining accounts
	public static synchronized int getNumAccsRemaining(){
		return accounts.size();
	}

	public static void setBanned(String username, boolean banned){

		try {
			//load all the account names
			if (banned){
				Database.exec("update accounts set banned=1 where username = '" + username + "'", "update");
			} else {
				Database.exec("update accounts set banned=0 where username = '" + username + "'", "update");
			}

		} catch (Exception e){
			e.printStackTrace(System.out);
			ErrorMessage.throwFatalError("Fatal error: Unable to access database.");
		}
	}

	public static void setZone(String username, int character, String zone){
		if (!validateCharId(character)){
			return;
		}

		try {
			//load all the account names
			Database.exec("update accounts set zone" + character + "='" + zone + "' where username = '" + username + "'", "update");

		} catch (Exception e){
			e.printStackTrace(System.out);
			ErrorMessage.throwFatalError("Fatal error: Unable to access database.");
		}
	}

	public static void setPort(String username, int character, String port){
		if (!validateCharId(character)){
			return;
		}
		try {
			//load all the account names
			Database.exec("update accounts set port" + character + "='" + port + "' where username = '" + username + "'", "update");

		} catch (Exception e){
			e.printStackTrace(System.out);
			ErrorMessage.throwFatalError("Fatal error: Unable to access database.");
		}
	}

	public static void setMail(String username, int character, boolean mail){
		if (!validateCharId(character)){
			return;
		}
		System.out.println("Setting mail"+ character + " " + username + " = " + mail);
		try {
			//load all the account names
			if (mail){
				Database.exec("update accounts set mail" + character + "=1 where username = '" + username + "'", "update");
			} else {
				Database.exec("update accounts set mail" + character + "=0 where username = '" + username + "'", "update");
			}

		} catch (Exception e){
			e.printStackTrace(System.out);
			ErrorMessage.throwFatalError("Fatal error: Unable to access database.");
		}
	}
	
	public static void setHasOrbs(String username, int character, boolean orbs){
		if (!validateCharId(character)){
			return;
		}
		System.out.println("Setting orbs"+ character + " " + username + " = " + orbs);
		try {
			//load all the account names
			if (orbs){
				Database.exec("update accounts set orbs" + character + "=1 where username = '" + username + "'", "update");
			} else {
				Database.exec("update accounts set orbs" + character + "=0 where username = '" + username + "'", "update");
			}

		} catch (Exception e){
			e.printStackTrace(System.out);
			ErrorMessage.throwFatalError("Fatal error: Unable to access database.");
		}
	}

	public static void setNumBagUsed(String username, int character, int slots){
		if ((!validateCharId(character)) || (slots == 0)){
			return;
		}
		System.out.println("Setting slotsUsed"+ character + " " + username + " = " + slots);
		try {
			//load all the account names
			Database.exec("update accounts set slotsUsed" + character + "=" + slots + " where username = '" + username + "'", "update");
		} catch (Exception e){
			e.printStackTrace(System.out);
			ErrorMessage.throwFatalError("Fatal error: Unable to access database.");
		}
	}

	public static void setNumBagUnlocked(String username, int character, int slots){
		if ((!validateCharId(character)) || (slots == 0)){
			return;
		}
		System.out.println("Setting slotsUnlocked"+ character + " " + username + " = " + slots);
		try {
			//load all the account names
			Database.exec("update accounts set slotsUnlocked" + character + "=" + slots + " where username = '" + username + "'", "update");
		} catch (Exception e){
			e.printStackTrace(System.out);
			ErrorMessage.throwFatalError("Fatal error: Unable to access database.");
		}
	}

	public static void incrementDays(String username, int character){
		if (!validateCharId(character)){
			return;
		}
		System.out.println("Incrementing days " + username + "(" + character +")");
		try {
			//load the current number of days
			ResultSet rs = Database.exec("select days" + character + " from accounts where username = '" + username + "'", "query");
			rs.next();
			int days = Integer.valueOf(rs.getString("days"+character))+1;
			rs.close();
			//save the updated number of days
			Database.exec("update accounts set days" + character + "=" + days + " where username = '" + username + "'", "update");

		} catch (Exception e){
			e.printStackTrace(System.out);
			ErrorMessage.throwFatalError("Fatal error: Unable to access database.");
		}
	}

	public static void resetDays(String username, int character){
		validateCharId(character);
		System.out.println("Resetting days " + username + "(" + character +")");
		try {
			Database.exec("update accounts set days" + character + "=0 where username = '" + username + "'", "update");

		} catch (Exception e){
			e.printStackTrace(System.out);
			ErrorMessage.throwFatalError("Fatal error: Unable to access database.");
		}
	}

	private static boolean validateCharId(int character){
		if (!((character >= 1) && (character <= 8))){
			System.out.println("Invalid charID, ignoring command");
			return false;
		} 
		return true;
	}

	public static synchronized void removeGuiAccount(String username){
		setLastLoggedInDate(username);

		for (int i=0; i<guiRemAccounts.size(); i++){
			if (guiRemAccounts.get(i).getUsername().equals(username)){
				guiUsedAccounts.add(guiRemAccounts.get(i));
				guiRemAccounts.remove(i);
				i--;
			}
		}

		remAccsOutput.update(guiRemAccounts);
		usedAccsOutput.update(guiUsedAccounts);	
		arLabel.update(guiRemAccounts.size(), totalAccs);
	}

	private static synchronized void setLastLoggedInDate(String username){
			
		Database.exec("update accounts set loggedIn=" + cal.get(Calendar.DAY_OF_WEEK) + " where username = '" + username + "'", "update");

	}

	public static void setArLabel(AccsRemainingLabel label){
		arLabel = label;
	}

}
