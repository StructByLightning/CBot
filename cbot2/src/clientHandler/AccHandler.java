package clientHandler;

import network.Network;
import account.Account;

public class AccHandler {
	
	//retrieves the next account from the server
	public synchronized static Account nextAccount(){
		try{
			System.out.println("Requesting account");
			String data = Network.sendData(Network.REQUEST_NEW_ACCS_PORT, ("Finished"), Network.getServerIp());

			Account acc = new Account(data.substring(0,  data.indexOf(";")));
			System.out.println("Assigned " + acc.getUsername());
	
			return acc;
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
		
		return null;
	}
	
	/*
	public synchronized static void loadMoreAccounts(){
		accounts.clear();
		try {
			String data = Network.sendData(4447, "More accounts", Network.getServerIp());
			
			String output = "";
			while(data.length() > 0){
				accounts.add(new Account(data.substring(0,  data.indexOf(";"))));
				output += accounts.get(accounts.size()-1).getUsername() + "\n";
				data = data.substring(data.indexOf(";")+1);
			}
			System.out.println("Loaded " + accounts.size() + " accounts:\n" + output);
		}catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	*/
	
}
