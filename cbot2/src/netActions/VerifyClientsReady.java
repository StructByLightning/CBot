package netActions;

import java.io.PrintWriter;
import java.net.Socket;

import network.NetAction;
import clientHandler.AccHandler;
import clientHandler.ClientStates;
import fScript.FScript;

//waits until all clients are ready then tells the server
//a new thread of this is run every time the server contacts the client
public class VerifyClientsReady implements NetAction{
	
	public VerifyClientsReady(){
	}
	
	public void execute(String data, Socket client) {
		try {
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);	
			if ((FScript.getNumFwWindows() == 0) || (ClientStates.dontCloseClient())){
				out.println("Ready");
			} else {
				out.println("Not ready");
			}
						
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
		
		return;
		
	}

}
