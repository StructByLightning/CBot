package server;

import gui.TextPanel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import accounts.AccountHandler;

class ClientWorker implements Runnable {
	private Socket client;
	private String ip;
	private TextPanel textPanel;

    ClientWorker(Socket client, TextPanel textPanel) {
    	this.client = client;
    	this.ip = getIp();
    	this.textPanel = textPanel;
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
		    	System.out.println(e);
		    }
	
		    while(true){
		    	try{
		    		line = in.readLine();
		    		System.out.println(line);
	    			if (line == null){
	    				return;
	    			} else if(line.equals("Next account")){
	    				String account = AccountHandler.nextAccount();
	    				out.println(account);
	    				textPanel.addText("Sent accounts to client " + this.ip);
	    				return;
	    			} else {
	    				textPanel.addText("Received: " + line);
	    			}
		    	} catch (Exception e) {
		    		System.out.println(e);
		    	}
		    }
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    }
}