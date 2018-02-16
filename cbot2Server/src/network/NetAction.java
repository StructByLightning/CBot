package network;

import java.net.Socket;

public interface NetAction {
		
	public void execute(String data, Socket client);
	
}
