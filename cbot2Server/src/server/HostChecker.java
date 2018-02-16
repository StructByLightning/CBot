package server;

import java.net.Socket;
import java.util.ArrayList;

public class HostChecker implements Runnable {
	private String ipAddress;
	private int port;
	private String stringToSend = "ping";
	private ArrayList<String> hosts;
	
	
	public HostChecker(String ipAddress, int port, ArrayList<String> hosts){
		this.ipAddress = ipAddress;
		this.port = port;
		this.hosts = hosts;
	}
	
	
	
	public void run(){
		try {
			//open a socket and send the command
			Socket socket = new Socket(ipAddress, port);
			hosts.add(ipAddress);			
			socket.close();
		} catch (Exception e){
			//e.printStackTrace(System.out);
		}
	}
}
