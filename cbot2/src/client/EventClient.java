package client;

import java.util.Calendar;

import map.Map;
import errorMessage.ErrorMessage;
import fScript.FScript;

public class EventClient extends LoginClient {
	private Calendar cal;
	
	//this needs to contain EVERY possible loot item the mailbox might have to avoid skipping stuff that was missed before.
	public final static String[] MAILBOX_LOOT = {"mercStat", "polystoneOrb", "mistLight", "paragonPack", "hadesOrb", "warlordPack1", "gembox", "giftpack", "seashadeOrb", "phantomOrb", "clockworkOrb", "ichibanOrb", 
		"newYearOrb", "pandaOrb", "leviathanOrb", "solarOrb", "huntsmanOrb", "hadesOrb", "solarLight", "rageLight", "azureLight", "bloodLight", "eagleLight", "mistLight"};
	
	public EventClient(int id){
		super(id, true, false, true); //first true stops the controller from killing timed out clients
	}

	//does henry quest + gets xp aid
	public void doIngameTask(){
		setFinishedIngame(true);
		FScript.execTask("sendInput {ESCAPE}", getId()); //exit out of a cutscene if it's up

		if (getCharIndex() == 0 || this.getDced()) {
			FScript.execTask("sendInput {ALTK}", getId());
			FScript.sleep(180000);
			setDced(false);
		}
		
		
		moveToMailbox();
		FScript.sleep(1000);
		this.getBag().deleteTrash(true);
		
		clickOnMailbox();
		getOrbs();
		
		
		/*
		moveToTrackstone();
		if (clickOnTs()){ //attempt to click on the ts
			setRecallPoint(); //set the recall point to the ts

			FScript.execTask("lock", getId()); //lock and spam escape
			for (int i=0; i<3; i++){
				FScript.execTask("sendInput {ESCAPE}", getId()); //exit the npc panel
				FScript.sleep(1000);
			}
			FScript.execTask("unlock", getId());
			
			recall(); //use recall skill
			FScript.sleep(30000);
			testRealm(); //check to see if the character is still in nightfall 
		} else {
			System.out.println(this.getAccount() + " " + this.getId());
			FScript.log(this.getAccount() + " " + this.getId(), "noTsAccs.txt");
		}
		*/
		
		/*
		recall(); //use recall skill
		FScript.sleep(30000);
		testRealm(); //check to see if the character is still in nightfall 
		*/
		
	}
	
	private void getOrbs(){
		int[] coords = FScript.imageSearch("questSmall.bmp", 5, getId());
		if (coords[0] == 0){
			FScript.execTask("click " + (coords[1]+31) + " " + (coords[2]-8) + " left false false false", getId()); //mail service
			FScript.sleep(1000);
			
			coords = FScript.imageSearch("mailboxCompose.bmp", 5, getId());
			FScript.execTask("click " + (coords[1]-58) + " " + (coords[2]-244) + " left false false false", getId()); //System Mailbox
			FScript.sleep(1000);
			
			FScript.execTask("lock", getId());
			for (int i=0; i<17; i++){
				coords = FScript.imageSearch("mailboxCompose.bmp", 5, getId());
				FScript.execTask("click " + (coords[1]-76) + " " + (coords[2]-210+(i*11)) + " left false false false", getId()); //click the 0/1/2/3/etc mail
				FScript.sleep(10);
				FScript.execTask("click " + (coords[1]-76) + " " + (coords[2]-210+(i*11)) + " left false false false", getId()); //click the 0/1/2/3/etc mail
				FScript.sleep(500);
				
				//verify that the item is something useful -- leave the junk in the mailbox
				boolean useful = false;
				int index = -1;
				for (int j=0; j<MAILBOX_LOOT.length; j++){
					if (FScript.imageSearch(MAILBOX_LOOT[j]+".bmp", 5, getId())[0] == 0){
						useful = true;
						index = j;
						break;
					}
				}
				
				if (useful){//get the item out of it
					coords = FScript.imageSearch("mailboxItem.bmp", 5, getId());
					FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left false false false", getId());
					FScript.sleep(1000);	
					if (FScript.imageSearch(MAILBOX_LOOT[index]+".bmp", 5, getId())[0] == 0){ //bag is full because the item in mail didn't go into bag
						
						//ErrorMessage.throwError("Bag full");
						//i--;
					}
				}
				
			}
			FScript.execTask("unlock", getId());
			
			/*
			
			//repeatedly open mail and take items
			boolean mailExists = true;
			while (mailExists){
							
				//open the next mail
				coords = FScript.imageSearch("mailUnopened.bmp", 5, getId());
				if (coords[0] != 0){
					coords = FScript.imageSearch("mailUnopenedHighlighted.bmp", 5, getId());
					
				}
				FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left false false false", getId()); 
				FScript.sleep(10);
				FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left false false false", getId());
				FScript.sleep(1000);
				
				//verify that the item is something useful -- leave the junk in the mailbox
				boolean useful = false;
				for (int i=0; i<MAILBOX_LOOT.length; i++){
					if (FScript.imageSearch(MAILBOX_LOOT[i]+".bmp", 5, getId())[0] == 0){
						useful = true;
						break;
					}
				}
				
				if (useful){
					//get the item out of it
					coords = FScript.imageSearch("mailboxItem.bmp", 5, getId());
					FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left false false false", getId());
					FScript.sleep(1000);	
				}
				
				coords = FScript.imageSearch("mailUnopened.bmp", 5, getId());
				if (coords[0] != 0){
					coords = FScript.imageSearch("mailUnopenedHighlighted.bmp", 5, getId());
				}
				
				if (coords[0] != 0){
					mailExists = false;
				}
			}
			*/

			coords = FScript.imageSearch("mailboxCompose.bmp", 5, getId());
			FScript.sleep(1000);
			FScript.execTask("click " + (coords[1]+147) + " " + (coords[2]) + " left left left left", getId()); //close mailbox interface
			FScript.sleep(1000);
		}
	}
	
	public void clickOnMailbox(){
		//rotate to top down and zoom out
		FScript.execTask("lock", getId());
		FScript.execTask("clickDrag 320 240 320 540 right", getId());
		FScript.execTask("clickDrag 320 240 320 540 right", getId());
		FScript.execTask("clickDrag 320 240 320 540 right", getId());
		FScript.execTask("scroll down 12", getId());
		//Functions.execTask("scroll up 6;", getId()); //zoom back in since there's a second npc next to otis
		FScript.execTask("unlock", getId());
		
		for (int i=0; i<10; i++){

			//look for mailbox, if he's found click him
			int[] coords = FScript.imageSearch("mailbox.bmp", 25, getId());
			if (coords[0] == 0){

				FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left true false false", getId());			
				//verify that the mailbox was clicked on
				if (FScript.waitForImage("questSmall.bmp", 5, getId(), 500, 2500)[0] == 0){
					return;
				} 
					
			} else if (i%3 == 0){
				moveToMailbox();
				FScript.sleep(2000);
			}

			//then rotate horizontally
			FScript.execTask("clickDrag 320 240 720 240 right;", getId());

		}
		FScript.log("Failed to find mailbox at " + getCharIndex() + " " + getAccount().getUsername(), "failedtoFindMailbox.txt");

	}
	
	//move to the mailbox using the map and autoroute
	public void moveToMailbox(){
		this.getMap().open();
		int[] coords = FScript.imageSearch("mapAnchor.bmp", 5, getId());
		if (coords[0] == 0){
			FScript.execTask("click " + (coords[1]-306) + " " + (coords[2]+180) + " left false false false", getId());
			FScript.sleep(1000);
		} else {
			System.out.println("Map not open");
		}

		this.getMap().close(); //put this outside the if statement to ensure the map is always closed
	}
	
	public void testRealm(){
		this.getMap().open();
		if (!this.getMap().getZone().equals(Map.NIGHTFALL_CITADEL)){
			FScript.log(this.getAccount().getUsername() + " " + this.getId(), "wrongRealmChars.txt");
		}
		this.getMap().close();
	}
	public void recall(){
		int[] coords = FScript.waitForImage("recallIcon.bmp", 5, getId(), 500, 2000);
		if (coords[0] == 0){
			FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left false false false", getId());
		} else {
			FScript.execTask("click 504 485 left false false false", getId());
		}
	}

	public void setRecallPoint(){
		int[] coords = FScript.waitForImage("setTsIconUnselected.bmp", 5, getId(), 500, 2000);
		if (coords[0] != 0){
			coords = FScript.waitForImage("setTsIconSelected.bmp", 5, getId(), 500, 2000);
		}
		//click set track
		FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left false false false", getId());

		coords = FScript.waitForImage("confirm.bmp", 5, getId(), 500, 2000);
		FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left false false false", getId());

	}

	public boolean clickOnTs(){
		for (int i=0; i<20; i++){
			//rotate to top down and zoom out
			FScript.execTask("lock", getId());
			FScript.execTask("clickDrag 320 240 320 540 right", getId());
			FScript.execTask("clickDrag 320 240 320 540 right", getId());
			FScript.execTask("clickDrag 320 240 320 540 right", getId());
			FScript.execTask("scroll down 12", getId());
			FScript.execTask("scroll up 6", getId()); //zoom back in so the window doesn't show other npcs
			FScript.execTask("unlock", getId());

			//attempt to click the top right corner
			FScript.execTask("click 384 169 left true false false", getId());

			//verify that the ts was clicked one
			if (FScript.waitForImage("questSmall.bmp", 5, getId(), 500, 2500)[0] == 0){
				return true;
			} 

			FScript.execTask("sendInput {ESCAPE}", getId());
			FScript.sleep(250);
			FScript.execTask("sendInput {ESCAPE}", getId());


			if (i%3 == 0){
				moveToTrackstone();
				FScript.sleep(10000);
			}

			//then rotate horizontally
			FScript.execTask("clickDrag 320 240 720 240 right", getId());
		}
		return false;
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

	private void moveToTrackstone(){
		FScript.execTask("lock", getId());
		this.getMap().open(); //use the newer map class instead of using sendInput
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
		FScript.execTask("click " + (coords[1]-20) + " " + (coords[2]+137) + " left false false false", getId()); //first click of a doubleclick
		FScript.execTask("click " + (coords[1]-20) + " " + (coords[2]+137) + " left false false false", getId()); //send click of a doubleclick
		this.getMap().close(); //use the newer map class instead of using sendInput
		FScript.execTask("unlock", getId());
		FScript.sleep(10000);
	}



}
