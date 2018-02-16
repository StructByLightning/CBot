package network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ClientWorker implements Runnable {
	private Socket client;
	private String ip;
	private NetAction action;

	
	
	
    ClientWorker(Socket client, NetAction action) {
    	this.client = client;
    	this.ip = getIp();
    	this.action = action;
    }
    
    private String getIp(){
    	String ip = client.getRemoteSocketAddress().toString();
    	ip = ip.substring(1, ip.indexOf(":"));
    	return ip;
    }

    public void run(){
    	try{
		    String line;
		    BufferedReader in = null;
		    PrintWriter out = null;
		    try{
		    	in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		    	out = new PrintWriter(client.getOutputStream(), true);
		    } catch (Exception e) {
		    	e.printStackTrace(System.out);
		    }
		    

    		line = in.readLine();
		    action.execute(line, client);
		    
		    

    	} catch (Exception e) {
    		e.printStackTrace(System.out);
    	}
    }
}