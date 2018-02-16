package client;

import java.util.Calendar;
import java.util.Date;

import diagnosis.Diagnosis;
import network.Network;
import fScript.FScript;

public class CheckinClient extends LoginClient {
	private Calendar cal;
	

	public CheckinClient(int id){
		super(id, false, false, true);
		this.cal = Calendar.getInstance(); // creates calendar
		cal.setTime(new Date()); // sets calendar time/date
		cal.add(Calendar.HOUR_OF_DAY, 2); // add 2 hours because the server is 2 hrs ahead
	}

	//does henry quest + gets xp aid
	public void doIngameTask(){
		setFinishedIngame(true);
		FScript.execTask("sendInput {ESCAPE}", getId()); //exit out of a cutscene if it's up
		clickOnHenry();
		getCheckin();
		
		update();
		
		//Functions.clickOnHenry(getId(),  getAccount(), getCharIndex());
		//Functions.getCheckin(getId(), getAccount(), getCharIndex());
	
	}
	
	private void update(){
		//perform each of these actions once per week
		if (cal.get(Calendar.DAY_OF_WEEK)%7 == 0){ //monday
			getXpAid();
		}
		
		//if (cal.get(Calendar.DAY_OF_WEEK)%7 == 0){ //friday
			pray();
		//}
		
		
		
	}
	
	//pray and collect loot
	public void pray(){
		FScript.execTask("click 327 43 left false false false", getId());
		FScript.sleep(1000);
		FScript.execTask("click 400 432 left false false false", getId());
		FScript.sleep(1000);
	}
	
	public String toString(){
		
		return "\n    Id: " + this.getId() + 
			   "\n    Char index: " + this.getCharIndex() + 
			   "\n    Realm: " + this.getRealm() + 
			   "\n    Ingame: " + this.getFinishedIngame() + 
			   "\n    Task: " + this.getTask() + 
			   "\n    Old task: " + this.getOldTask() +
			   "\n    Running: " + this.getRunning() + 
			   "\n    Needs new IP: " + this.getNeedsNewIp() +
			   "\n    Time since last change: " + this.getTimeSinceLastChange();
		
				
	}

	public void getXpAid(){
		FScript.execTask("click 116 54 left false false false", getId());
		FScript.sleep(500);
		int[] coords = FScript.imageSearch("unfreezeXpAid.bmp", 5, getId());
		if (coords[0] == 0){
			FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left false false false", getId());
			FScript.sleep(500);
		}
		
		for (int i=0; i<10; i++){
			FScript.execTask("sendInput {ESCAPE}", getId());
			FScript.sleep(500);
		}
	}
	
	public void clickOnHenry(){
		if ( getCharIndex() == 0){
			FScript.execTask("sendInput {ALTK}", getId());
		}
		
		for (int i=1; i<10; i++){
			if (!FScript.clientExists(getId())){ //return if the client is closed (fixes bug when the client crashes/closes/minimizes midway)
				return;
			}
			FScript.execTask("sendInput {ESCAPE}", getId()); //clear any stray panels (fixes bug with f12 menu being ontop of everything and blocking vision)
			//rotate to top down and zoom out
			FScript.execTask("lock", getId());
			FScript.execTask("clickDrag 320 240 320 540 right", getId());
			FScript.execTask("clickDrag 320 240 320 540 right", getId());
			FScript.execTask("clickDrag 320 240 320 540 right", getId());
			FScript.execTask("scroll down 12", getId());
			FScript.execTask("unlock", getId());
			
			//look for henry, if he's found click him
			int[] coords = FScript.imageSearch("henryExpIcon.bmp", 5, getId());
			if (coords[0] == 0){
				//the character is probably hugging henry from the last time so try to click that first
				if (i == 0){
					FScript.execTask("click " + (coords[1]+3) + " " + (coords[2]+30) + " left true false false", getId());
				} else {
					FScript.execTask("click " + (coords[1]+20) + " " + (coords[2]+30) + " left true false false", getId());					
				}
				
				
				//verify that henry was clicked on
				if (FScript.waitForImage("questSmall.bmp", 5, getId(), 500, 2500)[0] == 0){
					//System.out.println(i + " succeeded");
					//Functions.execTask("sendInput {ESCAPE}", getId());
					return;
				} else {
					//the character probably moved towards henry so try to click him again				
					for (int j=0; j<3; j++){
						coords = FScript.imageSearch("henryExpIcon.bmp", 5, getId());
						if (coords[0] == 0){
							FScript.execTask("click " + (coords[1]+3) + " " + (coords[2]+30) + " left true false false", getId());
							//verify that henry was clicked on
							if (FScript.waitForImage("questSmall.bmp", 5, getId(), 500, 2500)[0] == 0){
								//System.out.println(i + " succeeded");
								//Functions.execTask("sendInput {ESCAPE}", getId());
								return;
							}
							//rotate horizontally
							FScript.execTask("clickDrag 320 240 720 240 right", getId());
						}
					}
					
					
					//System.out.println(i + " failed");
					moveToHenry();
					
					FScript.sleep(2000);
				}
			} else if (i%3 == 0){
				moveToHenry();
				
				FScript.sleep(2000);
			}
			
			//then rotate horizontally
			FScript.execTask("clickDrag 320 240 720 240 right", getId());
		}
	}

	private void moveToHenry(){
		FScript.execTask("lock", getId());
		FScript.execTask("sendInput {OPENMAP}", getId());
		int[] coords = FScript.imageSearch("mapAnchor.bmp", 5, getId());
		//zoom out
		FScript.execTask("click " + (coords[1]-272) + " " + (coords[2]+173) + " right false false false", getId());
		FScript.sleep(1000);
		FScript.execTask("click " + (coords[1]-272) + " " + (coords[2]+173) + " right false false false", getId());
		FScript.sleep(1000);
		FScript.execTask("click " + (coords[1]-272) + " " + (coords[2]+173) + " right false false false", getId());
		FScript.sleep(1000);

		//go to nightfall citadel
		FScript.execTask("click " + (coords[1]-312) + " " + (coords[2]+182) + " left false false false", getId());
		FScript.sleep(1000);
		FScript.execTask("click " + (coords[1]-410) + " " + (coords[2]+260) + " left false false false", getId());
		FScript.sleep(1000);

		//select display NPC by function
		FScript.execTask("click " + (coords[1]-10) + " " + (coords[2]+34) + " left false false false", getId());
		FScript.sleep(1000);
		FScript.execTask("click " + (coords[1]-22) + " " + (coords[2]+47) + " left false false false", getId());
		FScript.sleep(1000);
		FScript.execTask("click " + (coords[1]-34) + " " + (coords[2]+72) + " left false false false", getId());
		FScript.sleep(1000);


		//select common NPC
		FScript.execTask("click " + (coords[1]-67) + " " + (coords[2]+35) + " left false false false", getId());
		FScript.sleep(1000);
		FScript.execTask("click " + (coords[1]-35) + " " + (coords[2]+50) + " left false false false", getId());
		FScript.sleep(1000);
		FScript.execTask("click " + (coords[1]-18) + " " + (coords[2]+62) + " left false false false", getId());
		FScript.sleep(1000);

		//scroll down and doubleclick henry
		FScript.execTask("click " + (coords[1]-312) + " " + (coords[2]+185) + " left false false false", getId());
		FScript.sleep(1000);
		FScript.execTask("clickDrag " + (coords[1]+43) + " " + (coords[2]+76) +  " " + (coords[1]+43) + " " + (coords[2]+140) + " left", getId());
		FScript.sleep(1000);
		FScript.execTask("click " + (coords[1]-79) + " " + (coords[2]+141) + " left false false false", getId());
		FScript.execTask("click " + (coords[1]-79) + " " + (coords[2]+141) + " left false false false", getId());
		FScript.execTask("sendInput {OPENMAP}", getId());
		FScript.execTask("unlock", getId());
		FScript.sleep(1000);
	}
	
	public void getCheckin(){
		//checkin
		
		if (FScript.waitForImage("questSmall.bmp", 5, getId(), 200, 5000)[0] == 0){ //wait for the quest panel to popup
			int[] results = FScript.waitForImage("checkinQuest.bmp", 55, getId(), 20, 200); //find the checkinquest
			if (results[0] == 0){ //if it was found, click it and complete it
				FScript.execTask("lock", getId());
				FScript.execTask("click " + results[1] + " " + results[2] + " left false false false", getId());
				FScript.sleep(1000);
				FScript.execTask("click 94 198 left false false false", getId());
				FScript.execTask("unlock", getId());

			}
		} 
		FScript.sleep(1000);
	}

}
