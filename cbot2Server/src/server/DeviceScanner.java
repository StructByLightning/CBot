package server;

import java.util.ArrayList;

public class DeviceScanner {

	public ArrayList<String> getClients(){
		ArrayList <String> hosts = new ArrayList<String>();
		for (int i=1; i<255; i++){
			new Thread(new HostChecker("192.168.2." + i, 4445, hosts)).start();
			new Thread(new HostChecker("192.168.3." + i, 4445, hosts)).start();
		}
		try {
			long oldTime = System.currentTimeMillis();
			
			System.out.println("Scanning network for clients");
			while ((System.currentTimeMillis()-oldTime < 10000) && (hosts.size()<2)){
				Thread.sleep(10);
			}
			for (int i=0; i<hosts.size(); i++){
				System.out.println("Found client " + hosts.get(i));
			}
			
			
		} catch (Exception e){
			
		}
		return hosts;
	}
}
