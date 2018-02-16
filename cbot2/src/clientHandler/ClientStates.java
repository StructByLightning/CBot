package clientHandler;

import java.util.ArrayList;

import client.CheckinClient;
import client.Client;
import config.Config;
import fScript.FScript;
import fScript.Timer;

public class ClientStates {
	public final static int MAX_TIME_FROZEN = 300000;
	private static ArrayList<Client> clients = new ArrayList<Client>();
	private static ArrayList<Timer> frozenTimers = new ArrayList<Timer>();
	private static ArrayList<Boolean> readies = new ArrayList<Boolean>();
	
	
	//adds a client to clients
	public static synchronized void addClient(Client c){
		clients.add(c);
		frozenTimers.add(new Timer());
		readies.add(false);
	}
	
	public static synchronized boolean readyForIpSwitch(){
		for (int i=0; i<readies.size(); i++){
			if (!readies.get(i)){
				return false;
			}
		}
		return true;
	}
	
	public static synchronized void resetReadies(){
		for (int i=0; i<readies.size(); i++){
			readies.set(i, false);
		}
	}
	
	public static synchronized boolean getReady(int id){
		return readies.get(id-1);
	}
	
	public static synchronized void setReady(int id, boolean value){
		readies.set(id-1, value);
	}
	
	//returns the size of clients
	public static synchronized int getNumClients(){
		return clients.size();
	}
	
	//returns true if there is a HarvestClient in clients
	public static boolean dontCloseClient(){
		for (int i=0; i<clients.size(); i++){
			if (clients.get(i).dontCloseClient()){
				return true;
			}
		}
		return false;
	}
	

	/*
	//resets all the client tasks
	public static synchronized void reset(){
		for (int i=0; i<clients.size(); i++){
			clients.get(i).setTask("");
		}
	}
	*/
	
	//kills all fw windows if nothing is happening (allows recovery from rare freezing bugs)
	public static synchronized void checkFrozenClients(){
		for (int i=0; i<clients.size(); i++){
			if (!Config.getDontKillClients()){
				frozenTimers.get(i).update();
				if ((System.currentTimeMillis()-clients.get(i).getTimeSinceLastChange() > MAX_TIME_FROZEN)){
					System.out.println("Killing all FW");
					FScript.killAllFw();
					resetFrozenTimers();
					
					if (clients.get(i) instanceof CheckinClient){
						//Functions.log("killed.txt", clients.get(i).getAccount() + " Client data: " + ((CheckinClient)clients.get(i)).toString());
						
					} else{
						//Functions.log("killed.txt", clients.get(i).getAccount().toString());
					}
					
					return;
				}
			}
		}
	}
	
	//resets all frozen timers to zero
	public static synchronized void resetFrozenTimers(){
		for (int i=0; i<clients.size(); i++){
			clients.get(i).setTimeSinceLastChange(System.currentTimeMillis());	
		}
	}
	
	
}
