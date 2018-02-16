package network;

import java.io.PrintWriter;
import java.net.Socket;

public class ClientPingAction implements NetAction{

	public void execute(String data, Socket client) {
		try {
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			if(data.equals("ping")){
				out.println("ping"); 
			}
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}



}
