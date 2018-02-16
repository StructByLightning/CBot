package party;

import client.Client;
import fScript.FScript;

public class Party {
	public static final int COLLECTOR_ID = 2;
	private Client owner;
	
	public Party(Client owner){
		this.owner = owner;
	}

	//invites on a linker
	public void inviteOnLinker(String name){
		openTMenu();
		
		//click the name box, type in the linker name, send the invite, and close the T menu
		int[] coords = FScript.waitForImage("partyInvite.bmp", 5, owner.getId(), 200, 5000);
		if (coords[0] == 0){
			FScript.execTask("click " + (coords[1]-58) + " " + (coords[2]+3) + " left false false false", owner.getId());
			FScript.sleep(1000);
			FScript.execTask("sendInput " + name + "", owner.getId());
			FScript.sleep(1000);
			FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left false false false", owner.getId());
			FScript.sleep(1000);
			
		}
		closeTMenu();
		FScript.sleep(5000);

		//click the flashing invite and accept it on the collector
		FScript.execTask("click 318 144 left false false false", COLLECTOR_ID);
		FScript.sleep(2000);
		FScript.execTask("click 294 292 left false false false", COLLECTOR_ID);
	}
	
	//moves to the linker's location
	public void moveToLinker(int delay){
		//send the link
		FScript.execTask("click 133 505 left false false false", COLLECTOR_ID);
		FScript.sleep(1000);
		FScript.execTask("sendInput ~", COLLECTOR_ID);
		FScript.sleep(1000);
		FScript.execTask("click 192 502 left false false false", COLLECTOR_ID);
		FScript.sleep(1000);
		FScript.execTask("click 234 357 left false false false", COLLECTOR_ID);
		FScript.sleep(1000);
		FScript.execTask("click 192 502 left false false false", COLLECTOR_ID);
		FScript.sleep(1000);
		FScript.execTask("sendInput {ENTER}", COLLECTOR_ID);
		FScript.sleep(1000);
		
		//click the link
		FScript.execTask("click 78 434 left false false false", owner.getId());
		FScript.execTask("click 136 446 left false false false", owner.getId());
		FScript.sleep(delay);
		
	}

	
	//open T menu if it's not already open
	public void openTMenu(){
		if ((FScript.imageSearch("partyInvite.bmp", 5, owner.getId())[0] != 0) && ((FScript.imageSearch("partyInviteRollover.bmp", 5, owner.getId())[0] != 0))){
			FScript.execTask("click 484 38 left false false false", owner.getId());
			FScript.sleep(1000);
		}		
	}

	//close T menu if it's not already closed
	public void closeTMenu(){
		if ((FScript.imageSearch("partyInvite.bmp", 5, owner.getId())[0] == 0) || (FScript.imageSearch("partyInviteRollover.bmp", 5, owner.getId())[0] == 0)){
			FScript.execTask("click 484 38 left false false false", owner.getId());
			FScript.sleep(1000);
		}
	}
	
}
