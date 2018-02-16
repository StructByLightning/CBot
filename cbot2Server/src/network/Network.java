package network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;


public class Network {
	public static final int PING_SERVER_PORT        	= 4444;
	public static final int PING_CLIENT_PORT        	= 4445;
	public static final int SEND_ACCOUNT_PORT       	= 4446;
	public static final int SEND_ACC_DATA_PORT      	= 4447;
	public static final int REQUEST_NEW_ACCS_PORT   	= 4448;
	public static final int START_CLIENTS_PORT      	= 4449;
	public static final int ADD_CLIENT_PORT         	= 4450;
	public static final int UPDATE_ACC_PORT        	 	= 4451;
	public static final int VERIFY_CLIENTS_READY_PORT 	= 4452;
	
	

	private static String SERVER_IP = "";
	private static ArrayList<String> clientIps = new ArrayList<String>();

	//listens for a ping on port 4445. used by the server to make sure clients are still active
	public static void startPingClient(){
		listenForData(PING_CLIENT_PORT, new ClientPingAction());
		
	}

	//listens for a ping on port 4444. used by clients to locate the server
	public static void startPingServer(){
		listenForData(PING_SERVER_PORT, new ServerPingAction());
	}
	
	//listens for a ping on port 4444. used by clients to locate the server
	public static void startPingServer(NetAction action){
		listenForData(PING_SERVER_PORT, action);
	}
	
	//scan the network for the server and clients
	public static void initialize(){
		updateServer();
	}
	
	//scans the network for clients
	public static void updateClients(int expectedNumber){
		try { 
			//gets the router (third number in the ip address) 
			String localIp = InetAddress.getLocalHost().getHostAddress();
			String router = localIp.substring(localIp.indexOf(".")+1);
			router = router.substring(router.indexOf(".")+1);
			router = router.substring(0, router.indexOf("."));

			
			//attempts to connect to every ip on the router
			for (int i=2; i<255; i++){ //skips 1 and 255 because 1 is the router and 255 sends data to all computers
				String possibleIp = "192.168." + router + "." + i;
				new Thread(new ClientPinger(possibleIp)).start();
			}
			
			//wait for one of the ips to respond (times out after 10s)
			for (int i=0; i<101; i++){
				if (clientIps.size() >= expectedNumber){
					break;
				}
				Thread.sleep(100);
			}
			
			/*
			for (int i=0; i<CLIENT_IPS.size(); i++){
				System.out.println("Found client " + CLIENT_IPS.get(i));
			}
			*/
			
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	
	//scans the network for the server
	public static void updateServer(){
		try { 
			//gets the router (third number in the ip address) 
			String localIp = InetAddress.getLocalHost().getHostAddress();
			String router = localIp.substring(localIp.indexOf(".")+1);
			router = router.substring(router.indexOf(".")+1);
			router = router.substring(0, router.indexOf("."));

			String oldIp = SERVER_IP;
			
			//attempts to connect to every ip on the router
			for (int i=1; i<256; i++){
				String possibleIp = "192.168." + router + "." + i;
				new Thread(new ServerPinger(possibleIp)).start();
			}
			
			//wait for one of the ips to respond (times out after 5s
			for (int i=0; i<101; i++){
				if (!oldIp.equals(SERVER_IP)){
					System.out.println("Found server " + SERVER_IP);
					break;
				}
				Thread.sleep(100);
				if (i == 100){
					throw new Exception("Unable to find server");
				}
			}
			
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}


	//sets up a server socket that executes the given NetAction whenever it receives something
	public static void listenForData(int port, NetAction action){
		new Thread(new PortListener(port, action)).start();
	}

	//sends a string over the given port and returns whatever gets sent back
	public static String sendData(int port, String data, String ip) throws Exception{
		Socket socket = new Socket(ip, port);
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out.println(data);
		return in.readLine();
	}
	
	public static void setServerIp(String ip){Network.SERVER_IP = ip;}
	public static String getServerIp(){return Network.SERVER_IP;}
	public static ArrayList<String> getClients(){return Network.clientIps;}
	
	//Adds an ip to clientIps if it isn't already in the arraylist
	public static void addClientIp(String ip){	
		for (int i=0; i<clientIps.size(); i++){
			if (ip.equals(clientIps.get(i))){
				return;
			}
		}
		Network.clientIps.add(ip);
		
	}
	
}
