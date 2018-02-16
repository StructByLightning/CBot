package account;

import java.util.ArrayList;

public class Account {
	private String username;
	private String password;
	
	private ArrayList<Boolean> characters = new ArrayList<Boolean>();
	
	//username password true true true true true true true true;
	public Account(String data){
		data = data.substring(0, data.length());
		data += " ";
		
		username = data.substring(0, data.indexOf(' '));
		data = data.substring(data.indexOf(' ')+1);
		password = data.substring(0, data.indexOf(' '));
		data = data.substring(data.indexOf(' ')+1);
		
		for (int i=0; i<8; i++){
			characters.add(Boolean.valueOf(data.substring(0, data.indexOf(' '))));
			data = data.substring(data.indexOf(' ')+1);
		}
	}
	
	public Account(String username, String password, boolean[] characters){
		this.username = username;
		this.password = password;

		for (int i=0; i<characters.length; i++){
			this.characters.add(characters[i]);
		}
		
	}
	
	
	public String toString(){
		String result = username + " " + password;		
		for (int i=0; i<characters.size(); i++){
			result += " " + characters.get(i);
		}
		return result;
	}
	
	public String getUsername(){
		return this.username;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	//gets the next character, returns 8 if there are no more
	public int nextChar(){
		for (int i=0; i<characters.size(); i++){
			if (characters.get(i)){
				characters.set(i, false);
				return i;
			}
		}
		return 8;
	}
	
}
