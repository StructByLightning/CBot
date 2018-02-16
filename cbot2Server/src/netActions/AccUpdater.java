package netActions;

import gui.TextPanel;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import network.NetAction;
import accounts.AccountHandler;
import config.Config;

public class AccUpdater implements NetAction{
	TextPanel textPanel;
	
	public AccUpdater(TextPanel textPanel){
		this.textPanel = textPanel;
	}
	
	//username charId hasMail=boolean incDays=boolean resetDays=boolean
	public void execute(String data, Socket client) {
		try {
			
			ArrayList<String> terms = new ArrayList<String>();
			
			data += " ";
			while (data.length() > 0){
				terms.add(data.substring(0, data.indexOf(' ')));
				data = data.substring(data.indexOf(' ')+1);	
			}
			
			//0 = username, 1 = charId
			String username = terms.get(0);
			int charId = Integer.valueOf(terms.get(1));
			System.out.println(charId);
				
			if (!Config.getExpressMode()){ //only update the database if the server isn't running in express mode because express mode skips sending these values
				for (int i=2; i<terms.size(); i++){
					String var = terms.get(i).substring(0, terms.get(i).indexOf('='));
					String value = terms.get(i).substring(terms.get(i).indexOf('=')+1);
					
					if ((var.equals("hasMail"))){ //char does have mail
						AccountHandler.setMail(username, charId, Boolean.valueOf(value));
					} else if ((var.equals("incDays")) && (Boolean.valueOf(value))){
						AccountHandler.incrementDays(username, charId);
					} else if ((var.equals("resetDays")) && (Boolean.valueOf(value))){
						AccountHandler.resetDays(username, charId);
					} else if (var.equals("hasOrbs")){
						AccountHandler.setHasOrbs(username, charId, Boolean.valueOf(value));
					} else if (var.equals("numBagUsed")){
						AccountHandler.setNumBagUsed(username, charId, Integer.valueOf(value));
					}	else if (var.equals("numBagUnlocked")){
						AccountHandler.setNumBagUnlocked(username, charId, Integer.valueOf(value));
					}						
				}	
			}
			
			if (charId >= 8){
				AccountHandler.removeGuiAccount(username);
			}
			
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
	    	
			out.println("Updated");
			return;
			
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
		return;
		
	}

	
	

}
