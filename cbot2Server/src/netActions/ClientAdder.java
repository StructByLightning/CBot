package netActions;

import gui.TextPanel;

import java.io.PrintWriter;
import java.net.Socket;

import server.ClientStates;
import network.NetAction;
import network.Network;

public class ClientAdder implements NetAction{
	TextPanel textPanel;
	
	public ClientAdder(TextPanel textPanel){
		this.textPanel = textPanel;
	}
	
	public void execute(String data, Socket client) {
		try {
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
	    	String ip = getIp(client);
			
	    	Network.addClientIp(ip);
			ClientStates.addClient(ip);
	    	
			out.println("Added");
			textPanel.addText(ip + " connected");
			return;
			
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
		return;
		
	}
	
	private String getIp(Socket client){
    	String ip = client.getRemoteSocketAddress().toString();
    	ip = ip.substring(1, ip.indexOf(":"));
    	return ip;
    }
	
	

}
