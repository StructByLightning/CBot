package map;

import client.LoginClient;
import fScript.FScript;


public class Map {
	public static final String NIGHTFALL_CITADEL = "nightfallCitadel";
	
	private LoginClient owner;
	
	public Map(LoginClient owner){
		this.owner = owner;
	}
	
	public String getZone(){
		open();
		
		try {
			if (FScript.imageSearch("nightfallCitadel.bmp", 5, owner.getId())[0] == 0){ //check for nightfall citadel
				return NIGHTFALL_CITADEL;
			}
				
		} finally{
			close(); //finally executes before returning
		}
		
		return "";
		
	}
	
	public void open(){
		int[] coords = FScript.imageSearch("mapAnchor.bmp", 5, owner.getId());
		int tries = 0;

		FScript.execTask("lock", owner.getId());
		while ((coords[0] != 0) && (tries < 10)){
			FScript.execTask("sendInput {OPENMAP}", owner.getId());
			coords = FScript.waitForImage("mapAnchor.bmp", 5, owner.getId(), 50, 2000);
			tries++;
		}
		FScript.execTask("unlock", owner.getId());
	}
	
	public void close(){
		int[] coords = FScript.imageSearch("mapAnchor.bmp", 5, owner.getId());
		int tries = 0;

		FScript.execTask("lock", owner.getId());
		while ((coords[0] == 0) && (tries < 10)){
			FScript.execTask("sendInput {OPENMAP}", owner.getId());
			coords = FScript.imageSearch("mapAnchor.bmp", 5, owner.getId());
			tries++;
		}
		FScript.execTask("unlock", owner.getId());
	}
	
	
}
