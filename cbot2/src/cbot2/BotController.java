package cbot2;

import java.io.File;
import java.util.Scanner;

import javax.swing.JFrame;

import netActions.ClientStarter;
import netActions.VerifyClientsReady;
import network.Network;
import clientHandler.ClientStates;
import config.Config;
import fScript.FScript;

public class BotController extends JFrame {
	//constants
	private static final long serialVersionUID = 7401709081950405058L;
	private static boolean startedBot = false;

	public BotController(){
		super("Bot Controller");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(false);
		this.setResizable(false);
	}

	public static void main(String[] args){
		new BotController().run();
	}
	
	public static boolean getStarted(){return startedBot;}
	public static void startBot(){startedBot = true;}

	public void run(){
		try {
			//get stuff setup
			loadConfigs();
			Config.initialize();
			FScript.initialize(Config.getFilepath());
			Network.startPingClient();
			Network.initialize();
			FScript.standardizeClients();
			
			
			Network.sendData(Network.ADD_CLIENT_PORT, "Add", Network.getServerIp());
		
			//wait for the server's command
			Network.listenForData(Network.START_CLIENTS_PORT, new ClientStarter());
			Network.listenForData(Network.VERIFY_CLIENTS_READY_PORT, new VerifyClientsReady());
			
			System.out.println("Waiting for command");
			
			//handles overall client health + sending ip switch commands
			superviseClients();
			
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	

	//watches over the clients
	private void superviseClients(){
		boolean running = true;
		while (running){
			//sleep a bit, none of this needs to be run very fast
			FScript.sleep(1000);
			
			//tell the server this client is ready for an ip change if all the fw client objects are ready
			
			/*
			if((ClientHandler.readyForIpSwitch()) && (ClientStates.getNumClients() > 0) && (ClientStates.readyForIpSwitch())){
				System.out.println("Sending switch request");
				try {
					switchIp();
					
				} catch (Exception e){
					e.printStackTrace(System.out);
				}
			} 
			*/
			//System.out.println(ClientStates.getNumClients() + " clients, cs ready: " + ClientStates.readyForIpSwitch() + ", functions ready " + Functions.readyForIpSwitch());
			//System.out.println("Ready for ip: " + Functions.isReadyForIpChange(null) + " Num FW: " + Functions.numFwWindows());
			
			//kills any minimized fw windows, fixes a rare bug where the bot minimizes a window and can't get it back up
			FScript.killMinimizedFwWindows();
			FScript.killCrashNotifications();
			
			//kill all fw clients if they haven't had any activity (usually caused by rare bugs, allows the bot to recover from these)
			ClientStates.checkFrozenClients();
		}
	}
	/*
	//change the ip address
	private void switchIp(){
		if (!ClientStates.dontCloseClient()){
			FScript.killAllFw();
		}
		
		try {
			Network.sendData(4448, "Switch ip", Network.getServerIp());
			AccHandler.loadMoreAccounts();
			ClientStates.resetReadies();

		}catch (Exception e) {
			e.printStackTrace(System.out);
		}

	}
	*/
	
	//loads configs from config/config.txt
	private void loadConfigs(){
		Scanner sc = null;
		try{
			sc = new Scanner(new File("config/config.txt"));

			while(sc.hasNextLine()){
				String line = sc.nextLine();

				if (line.substring(0, line.indexOf("=")).equals("filepath")){
					Config.setFilepath(line.substring(line.indexOf("=")+1));
				} else if (line.substring(0, line.indexOf("=")).equals("linker_name")){
					Config.setLinkerName(line.substring(line.indexOf("=")+1));
				}
			}
		} catch (Exception e){
			e.printStackTrace(System.out);
		} finally {
			if (sc != null){
				sc.close();
			}
		}

	}

}