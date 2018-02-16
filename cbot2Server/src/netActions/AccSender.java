package netActions;

import gui.TextPanel;

import java.io.PrintWriter;
import java.net.Socket;

import network.NetAction;
import server.ClientStates;
import accounts.AccountHandler;
import config.Config;

public class AccSender implements NetAction{
	TextPanel textPanel;
	public static final int MAX_ACCS_BEFORE_IP_CHANGE = 30;
	
	public AccSender(TextPanel textPanel){
		this.textPanel = textPanel;
	}
	
	public void execute(String data, Socket client) {
		try {
			System.out.println("(--)" + " Received new account request");
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
	    	String ip = getIp(client);
			
	    	//if already sent too many accs, wait for the clients to be ready for switch
	    	//this is setup so the last call for more accs will also trigger an ip switch
	    	if (AccountHandler.getNumAccsSent() > MAX_ACCS_BEFORE_IP_CHANGE){
	    		System.out.println("(" + ip + ")" + " requested accs; 30 accs sent already");
	    		if(!ClientStates.allClientsReady()){
		    		System.out.println("(" + ip + ")" + " found not all clients ready");
		    		while (!ClientStates.allClientsReady()){
		    			System.out.println(ip + " is waiting");
	    				Thread.sleep(500);
		    		}
	    		}
	    		switchIp(ip);
	    	}

			textPanel.addText("(" + ip + ")" + "Loading account ");
	    	String account = AccountHandler.nextAccount();
			out.println(account);
			textPanel.addText("(" + ip + ")" + "Sent account to client");
			return;
			
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
		return;
		
	}
	
	private synchronized void switchIp(String ip){
		if (AccountHandler.getNumAccsSent() > MAX_ACCS_BEFORE_IP_CHANGE){ //fixes race condition by ensuring the slower thread won't do anything
			if (Config.getSwitchIp()){
				textPanel.addText("(" + ip + ")" + " switching IP");
				IpSwitcher.switchIp();
				textPanel.addText("(" + ip + ")" + " IP Switch complete");
				AccountHandler.resetNumAccsSent();
			} else {
				textPanel.addText("(" + ip + ")" + " skipping IP switch");
			}
		}
	}
	
	private String getIp(Socket client){
    	String ip = client.getRemoteSocketAddress().toString();
    	ip = ip.substring(1, ip.indexOf(":"));
    	return ip;
    }
	
	

}
