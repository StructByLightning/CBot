package server;

import gui.ServerController;

import java.util.ArrayList;

import network.Network;
import accounts.AccountHandler;
import client.ClientType;
import config.Config;


public class BotStarter implements Runnable{
	private ServerController gui;
	private ClientType mode;
	
	public BotStarter(ServerController gui, ClientType mode){
		this.gui = gui;
		this.mode = mode;
		
		//load the correct type of accounts from the database
		AccountHandler.setAccounts(mode);
		
	}
		
	//start the clients
	public void run(){
		try {
			ArrayList<String> clients = Network.getClients();
			for (int i = 0; i<clients.size(); i++){
				System.out.println("Activating " + clients.get(i));
				if (Network.sendData(Network.START_CLIENTS_PORT, mode + ";" + Config.getExpressMode(), clients.get(i)).equals("Started")){
					gui.getOutputPanel().addText(clients.get(i) + " activated in " + mode + " mode.");
				}
				
			}
		}catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
}
