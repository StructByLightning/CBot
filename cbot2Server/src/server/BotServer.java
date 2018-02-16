package server;

import gui.ServerController;
import netActions.AccSender;
import netActions.AccUpdater;
import netActions.ClientAdder;
import netActions.IpSwitcher;
import network.Network;


public class BotServer implements Runnable{
	private ServerController gui;
	
	public BotServer(ServerController gui){
		this.gui = gui;
	}
	
	public void run(){
		Network.startPingServer();
		Network.initialize();
		Network.listenForData(Network.REQUEST_NEW_ACCS_PORT, new AccSender(gui.getOutputPanel()));
		Network.listenForData(Network.ADD_CLIENT_PORT, new ClientAdder(gui.getOutputPanel()));
		Network.listenForData(Network.UPDATE_ACC_PORT, new AccUpdater(gui.getOutputPanel()));
		
	}
}
