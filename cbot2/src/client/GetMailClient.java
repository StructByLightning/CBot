package client;

import fScript.FScript;


public class GetMailClient extends LoginClient {
	private String oldAccUsername;

	//this needs to contain EVERY possible loot item the mailbox might have to avoid skipping stuff that was missed before.
	public final static String[] MAILBOX_LOOT = {"mercStat", "polystoneOrb", "mistLight", "paragonPack", "hadesOrb", "warlordPack1", "gembox", "giftpack", "seashadeOrb", "phantomOrb", "clockworkOrb", "ichibanOrb", 
		"newYearOrb", "pandaOrb", "leviathanOrb", "solarOrb", "huntsmanOrb", "hadesOrb", "solarLight", "rageLight", "azureLight", "bloodLight", "eagleLight", "mistLight"};



	public GetMailClient(int id) {
		super(id, true, false, false);
	}

	// gets dreamlands and drinks them
	public void doIngameTask() {

		FScript.standardizeClients();
		setFinishedIngame(true);
		FScript.execTask("sendInput {ESCAPE}", getId());

		//performance mode + wait 3 minutes for safety lock
		if (oldAccUsername != getAccount().getUsername()){
			FScript.execTask("sendInput {ALTK}", getId());
			FScript.sleep(180000);
			oldAccUsername = getAccount().getUsername();
		}


		//move to the mailbox
		FScript.execTask("lock", getId());
		moveToMailbox();
		FScript.execTask("unlock", getId());

		//delete junk while on the way there
		FScript.execTask("lock", getId());
		getBag().deleteTrash(true);
		FScript.execTask("unlock", getId());

		//click the mailbox and get items
		FScript.execTask("lock", getId());
		FScript.execTask("click 396 41 left false false false", getId()); //open bag
		clickOnMailbox();
		getMailItems();
		FScript.execTask("unlock", getId());
	}

	private void getMailItems(){
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
		}
	}

	public void moveToMailbox(){
		getMap().open();
		FScript.sleep(1000);
		int[] coords = FScript.imageSearch("mapAnchor.bmp", 5, getId());
		if (coords[0] == 0){
			FScript.execTask("click " + (coords[1]-306) + " " + (coords[2]+180) + " left false false false", getId());
			FScript.sleep(1000);
			getMap().close();
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
			FScript.execTask("clickDrag 320 240 720 240 right", getId());
		}

	}

}
