package netActions;

import java.io.PrintWriter;
import java.net.Socket;

import network.NetAction;
import client.CheckinClient;
import client.Client;
import client.ClientType;
import client.GetMailClient;
import client.HarvestClient;
import client.LoginEventClient;
import client.PopulateAccsClient;
import clientHandler.ClientStates;
import config.Config;

public class ClientStarter implements NetAction{
	private boolean started = false;
	
	public ClientStarter(){
	}
	
	//starts clients. commands should be sent in the form "client object name"
	public void execute(String data, Socket client) {
		if (!started){
			started = true;
			
			try {
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				ClientType mode = ClientType.valueOf(data.substring(0, data.indexOf(";")));
				data = data.substring(data.indexOf(";")+1);
				
				Config.setExpressMode(Boolean.valueOf(data));
				
		    	if (mode == ClientType.LOGIN){
		    		startClient(ClientType.LOGIN, 3);
		    	} else if (mode == ClientType.HARVEST){
		    		startClient(ClientType.HARVEST, 1);
		    	} else if (mode == ClientType.GET_MAIL){
		    		startClient(ClientType.GET_MAIL, 2);
		    	} else if (mode == ClientType.CHECKIN){
		    		startClient(ClientType.CHECKIN, 2);
		    	} else if (mode == ClientType.POPULATE_ACCS){
		    		startClient(ClientType.POPULATE_ACCS, 2);
		    	}
				out.println("Started");
				System.out.println("Started " + mode + " bot.");
				return;
				
			} catch (Exception e){
				e.printStackTrace(System.out);
			}
		}
		return;
		
	}
		
	private void startClient(ClientType type, int count){
		for (int i=0; i<count; i++){
			Client client = null;
			
			if (type == ClientType.LOGIN){
				client = new LoginEventClient(i+1);
				Config.setDontKillClients(true);
			} else if (type == ClientType.HARVEST){
				client = new HarvestClient(i+1);
				Config.setDontKillClients(true);
			} else if (type == ClientType.GET_MAIL){
				client = new GetMailClient(i+1);
				Config.setDontKillClients(true);
			} else if (type == ClientType.CHECKIN){
				client = new CheckinClient(i+1);
			} else if (type == ClientType.POPULATE_ACCS){
				client = new PopulateAccsClient(i+1);
			}
			
			if (client == null){
				System.out.println("Unrecognized client type " + type);
			} else {
				ClientStates.addClient(client);
				new Thread(client).start();				
			}	
		}
	}
	
	
	
	
}
