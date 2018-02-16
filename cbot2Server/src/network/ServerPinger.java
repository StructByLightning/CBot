package network;

public class ServerPinger implements Runnable {
	private String ip;
	
	public ServerPinger(String ip){
		this.ip = ip;
	}
	
	public void run(){
		try {
			Network.sendData(Network.PING_SERVER_PORT, "ping", ip); //this throws an exception if a connection can't be made
			Network.setServerIp(ip);
		} catch (Exception e){ //ignore the 244 exceptions that will be generated from pinging every possible ip
			//System.out.println("IP " + ip + " is not the server");
		}
	}
}
