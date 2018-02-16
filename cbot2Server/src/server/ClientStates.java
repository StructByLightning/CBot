package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import network.Network;

public class ClientStates {

	//holds ip addresses and readiness variables
	private static Map<String, Boolean> clientsReady = new HashMap<String, Boolean>();
	
	//list of all connected clients
	private static ArrayList<String> clients = new ArrayList<String>();

	//returns true if all clients are ready otherwise returns false
	public static synchronized boolean allClientsReady(){
		try{
			boolean ready = true;
			for (String client : clients){
				String reply = Network.sendData(Network.VERIFY_CLIENTS_READY_PORT, "Ready check", client);
				if (!reply.equals("Ready")){
					ready = false;
				}
			}
			
			return ready;
			

		} catch (Exception e){
			e.printStackTrace(System.out);
		}
		return true;
	}

	
	//adds a new client
	public static synchronized void addClient(String ip){
		clientsReady.put(ip, false);
		clients.add(ip);
	}

	

	/*
	public static synchronized void setReady(String ip, boolean value){
		for (Map.Entry<String, Boolean> entry : clientsReady.entrySet()) {
			if (entry.getKey().equals(ip)){
				System.out.println("Set " + ip + " to true");
				entry.setValue(value);
			}
		}
	}
	*/
	
	
	
	public static synchronized ArrayList<String> getClientIps(){
		ArrayList<String> copy = new ArrayList<String>();
		for (int i=0; i<clients.size(); i++){
			copy.add(clients.get(i));
		}
		return copy;

	}

}
