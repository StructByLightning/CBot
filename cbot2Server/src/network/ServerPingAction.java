package network;

import java.io.PrintWriter;
import java.net.Socket;

public class ServerPingAction implements NetAction{

	public void execute(String data, Socket client) {
		try {
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			if(data.equals("ping")){
				out.println("ping");
				Network.addClientIp(getIp(client));
			}
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	
	private String getIp(Socket client){
    	String ip = client.getRemoteSocketAddress().toString();
    	ip = ip.substring(1, ip.indexOf(":"));
    	return ip;
    }


}
