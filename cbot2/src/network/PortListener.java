package network;

import java.net.ServerSocket;

public class PortListener implements Runnable{
	private int port;
	private NetAction action;
	
	public PortListener(int port, NetAction action){
		this.port = port;
		this.action = action;
	}
	
	public void run(){
		listenForData();
	}
	

	private void listenForData(){
		try{
			ServerSocket server = new ServerSocket(port);
			
			
			while(true){
				try{
					new Thread(new ClientWorker(server.accept(), action)).start();
					
				} catch (Exception e) {
					e.printStackTrace(System.out);
					server.close();
					System.exit(-1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
			System.exit(-1);
		}
	}


}
