package network;

public class ClientPinger implements Runnable {
	private String ip;
	
	public ClientPinger(String ip){
		this.ip = ip;
	}
	
	public void run(){
		try {
			Network.sendData(Network.PING_CLIENT_PORT, "ping", ip); //this throws an exception if a connection can't be made
		} catch (Exception e){ //ignore the 244 exceptions that will be generated from pinging every possible ip
			//System.out.println("IP " + ip + " is not a client");
		}
	}
}
