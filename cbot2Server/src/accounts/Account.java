package accounts;

import java.sql.ResultSet;

import errorMessage.ErrorMessage;

public class Account {
	public static final String DEFAULT_PASSWORD = "halo1combat";
	public static final int NUM_CHARS = 8;
	
	private String username;
	private String password;
	private String type;
	private boolean banned;
	private int loggedIn;
	
	private String[] port;
	private String[] zone;
	private boolean[] mail;
	private int[] days;
	private boolean[] orbs;
	
	private boolean[] characters;
		
	//username password true true true true true true true true;
	public Account(ResultSet rs){
		try {
			port = new String[8];
			zone = new String[8];
			mail = new boolean[8];
			days = new int[8];
			orbs = new boolean[8];
			characters = new boolean[8];
			
			username = rs.getString("username");
			password = DEFAULT_PASSWORD;
			type = rs.getString("type");
			loggedIn = Integer.valueOf(rs.getString("loggedIn"));
			
			if (Integer.valueOf(rs.getString("banned")) == 0){
				banned = false;
			}
			
			for (int i=1; i<9; i++){
				port[i-1] = rs.getString("port"+i);
				zone[i-1] = rs.getString("zone"+i);
				
				if (Integer.valueOf(rs.getString("mail"+i)) == 0){
					mail[i-1] = false;
				} else {
					mail[i-1] = true;
				}
				
				if (Integer.valueOf(rs.getString("orbs"+i)) == 0){
					orbs[i-1] = false;
				} else {
					orbs[i-1] = true;
				}
				
				days[i-1] = Integer.valueOf(rs.getString("days"+i));
			}		
			
			setCharacters(new boolean[]{true, true, true, true, true, true, true, true});
		} catch (Exception e){
			e.printStackTrace(System.out);
			ErrorMessage.throwFatalError("Unable to access account information");
		}
	}
	
	public Account(String username){
		this.username = username;
		this.password = DEFAULT_PASSWORD;
		
		port = new String[8];
		zone = new String[8];
		mail = new boolean[8];
		days = new int[8];
		orbs = new boolean[8];
		characters = new boolean[8];
		
		type = "";
		loggedIn = 0;
		banned = false;
		
		
		for (int i=1; i<9; i++){
			port[i-1] = "";
			zone[i-1] = "";
			mail[i-1] = false;			
			orbs[i-1] = false;
			days[i-1] = 0;
		}		
		
		setCharacters(new boolean[]{true, true, true, true, true, true, true, true});
	}
	
	public String getUsername(){
		return this.username;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	public boolean getBanned(){
		return this.banned;
	}
	public int loggedIn(){
		return this.loggedIn;
	}
	public String getPort(int i){
		return this.port[i];
	}
	public String getZone(int i){
		return this.zone[i];
	}
	public boolean getMail(int i){
		return this.mail[i];
	}
	public boolean getOrbs(int i){
		return this.orbs[i];
	}
	
	//set characters to be true if that char has mail
	public void setCharsByMail(){
		for (int i=0; i<NUM_CHARS; i++){
			this.characters[i] = this.mail[i];
		}
	}
	
	public boolean hasMail(){
		for (int i=0; i<8; i++){
			if (mail[i]){
				return true;
			}
		}
		return false;
	}
	
	public boolean hasOrbs(){
		for (int i=0; i<8; i++){
			if (orbs[i]){
				return true;
			}
		}
		return false;
	}
	
	public int getTotalDaysLogged(){
		int total = 0;
		for (int i=0; i<8; i++){
			total += days[i];
		}
		return total;
	}
	
	public void setCharacters(boolean[] chars){
		for (int i=0; i<chars.length; i++){
			characters[i] = chars[i];
		}
	}

	public String toString(){
		
		String result = username + " " + password;		
		for (int i=0; i<characters.length; i++){
			result += " " + characters[i];
		}
		return result;
	}
}

