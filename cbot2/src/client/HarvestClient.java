package client;

import config.Config;
import party.Party;
import bag.Bag;
import fScript.FScript;

public class HarvestClient extends LoginClient {
	private String linkerName;
	private int collectorId = 2;

	public HarvestClient(int id) {
		super(id, true, true, false);
		this.linkerName = Config.getLinkerName();
		setRealm(2);

	}
	
	public void test(){
		System.out.println("Trading...");
		getBag().openLoot();
		System.out.println("Trade complete");
	}

	// gets dreamlands and drinks them
	public void doIngameTask() {
		setFinishedIngame(true);
		FScript.execTask("sendInput {ESCAPE}", getId());

		//performance mode + wait 3 minutes for safety lock
		if (getCharIndex() == 0) {
			FScript.execTask("sendInput {ALTK}", getId());
			FScript.sleep(180000);
		}
		

		getParty().inviteOnLinker(linkerName);
		townPortal();
		getParty().moveToLinker(25000);
		clickOnOtis();
		getCloverKeys();
		
		long oldTime = System.currentTimeMillis();
		boolean openingLoot = true;
		while (openingLoot){
			
			//send esc 5 times. fixes bug where the bot can get stuck opening boxes with the otis window open
			FScript.execTask("lock", getId());
			for (int i=0; i<5; i++){
				FScript.execTask("sendInput {ESCAPE}", getId());
				FScript.sleep(500);
			}
			FScript.execTask("unlock", getId());
			
			getBag().openGodGiftBox();
			getBag().openLockboxes();
			getBag().openGiftpacks();
			getBag().openLoot();	
			getBag().close();
			clickOnOtis();
			getReid();	
			getBag().trade(true);
			openingLoot = getBag().lootExists();
		}
	}
	
	public void townPortal(){
		FScript.execTask("sendInput 0", getId());
	}

	public void tradeLoot(){
		int[] coords = FScript.imageSearch("partyAnchor.bmp", 5, getId());
		if (coords[0] == 0){
			//send trade request
			FScript.execTask("click " + (coords[1]+4) + " " + (coords[2]-8) + " right", getId());
			FScript.sleep(1000);
			FScript.execTask("click " + (coords[1]+48) + " " + (coords[2]+42) + " left false false false", getId());
			FScript.sleep(5000);

			//accept it
			FScript.execTask("click 318 144 left false false false", collectorId);
			FScript.sleep(2000);
			FScript.execTask("click 300 285 left false false false", collectorId);
			FScript.sleep(5000);

			for (int y=0; y<4; y++){
				for (int x=0; x<6; x++){
					FScript.execTask("click " + (438 + (x*26)) + " " + (164 + (y*26)) + " right", getId());
					FScript.sleep(1000);
					FScript.execTask("click 325 288 left false false false", getId());
					FScript.sleep(500);
				}	
			}
			coords = FScript.imageSearch("tradeCancel.bmp", 5, getId());
			FScript.execTask("click " + (coords[1]+49) + " " + (coords[2]+4) + " left false false false", getId()); //lock
			FScript.sleep(1000);

			coords = FScript.imageSearch("tradeCancel.bmp", 5, 1);
			FScript.execTask("click " + (coords[1]+49) + " " + (coords[2]+4) + " left false false false", collectorId); //lock
			FScript.sleep(1000);
			FScript.execTask("click " + (coords[1]+81) + " " + (coords[2]+4) + " left false false false", collectorId); //trade
			FScript.sleep(1000);

			coords = FScript.imageSearch("tradeCancel.bmp", 5, getId());
			FScript.execTask("click " + (coords[1]+81) + " " + (coords[2]+4) + " left false false false", getId()); //trade
			FScript.sleep(1000);
		}
	}

	private void getReid(){
		int[] coords = FScript.imageSearch("questSmall.bmp", 5, getId());
		if (coords[0] == 0){
			FScript.execTask("click " + (coords[1]+36) + " " + (coords[2]-18) + " left false false false", getId());
			FScript.sleep(1000);
			FScript.execTask("click 244 290 left false false false", getId());
			FScript.sleep(1000);
			coords = FScript.imageSearch("otisConfirm.bmp", 5, getId());
			FScript.execTask("click " + (coords[1]-310) + " " + (coords[2]-176) + " left false false false", getId()); //dark sapphire scroll
			for (int i=0; i<10; i++){
				FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left false false false", getId()); //confirm
				FScript.sleep(1000);
			}

			FScript.execTask("click " + (coords[1]-285) + " " + (coords[2]-176) + " left false false false", getId()); //dark amethyst scroll
			for (int i=0; i<5; i++){
				FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left false false false", getId()); //confirm
				FScript.sleep(1000);
			}
			FScript.execTask("click " + (coords[1]+61) + " " + (coords[2]+2) + " left false false false", getId()); //exit (cancel)
		}		
	}

	public void openItems(){
		FScript.execTask("click 396 41 left false false false", getId()); //open bag
		
		openLockboxes();
		openGiftpacks();
		openLoot();
		
		FScript.execTask("click 396 41 left false false false", getId()); //close bag
	}

	private boolean lootRemains(){
		String[] lootTypes = {"seashadeOrb.bmp", "phantomOrb.bmp", "gembox.bmp", "clockworkOrb.bmp", "ichibanOrb.bmp", "newYearOrb.bmp", "pandaOrb.bmp", "oceanOrb.bmp"};
		
		for (int i=0; i<10; i++){
			for (int j=0; j<lootTypes.length; j++){
				if (FScript.imageSearch(lootTypes[j], 5, getId())[0] == 0){
					return true;
				}
			}
			FScript.sleep(750);
		}

		return false;
	}

	//opens orbs and gemboxes
	public void openLoot(){
		deleteJunk();
		
		while (lootRemains()){
			String[] lootTypes = {"seashadeOrb.bmp", "phantomOrb.bmp", "gembox.bmp", "clockworkOrb.bmp", "ichibanOrb.bmp", "newYearOrb.bmp", "pandaOrb.bmp", "oceanOrb.bmp"};

			for (int i=0; i<lootTypes.length; i++){
				FScript.execTask("moveMouse 0 0", getId());
				int[] lootCoords = FScript.imageSearch(lootTypes[i], 5, getId());
				if (lootCoords[0] == 0){
					FScript.execTask("click " + lootCoords[1] + " " + lootCoords[2] + " right", getId());
					FScript.sleep(500);
				}
				
				int[] confirmCoords = FScript.imageSearch("bagFullConfirm.bmp", 5, getId());
				if (confirmCoords[0] == 0){
					FScript.execTask("click " + confirmCoords[1] + " " + confirmCoords[2] + " left false false false", getId());
					deleteJunk();
				}
				
			}
		}
		deleteJunk();
	}

	//opens all giftpacks
	public void openGiftpacks(){
		deleteJunk();

		while (FScript.waitForImage("giftbox.bmp", 5, getId(), 500, 5000)[0] == 0){ //while a giftpack is visible
			FScript.execTask("moveMouse 0 0", getId());

			//look for giftpacks and confirm button
			int[] giftpackCoords = FScript.imageSearch("giftbox.bmp", 5, getId()); 
			int[] confirmCoords = FScript.imageSearch("bagFullConfirm.bmp", 5, getId());

			//repeat a max of 100 times or until either no giftpack is seen or confirm is seen
			int i = 0;
			while ((i<100) && (FScript.waitForImage("giftbox.bmp", 5, getId(), 500, 5000)[0] == 0) && (confirmCoords[0] != 0)){ 
				FScript.execTask("moveMouse 0 0", getId());
				giftpackCoords = FScript.imageSearch("giftbox.bmp", 5, getId()); //look for a signin pack
				if (giftpackCoords[0] != 0){
					System.out.println("Failure");
				}

				FScript.execTask("click " + giftpackCoords[1] + " " + giftpackCoords[2] + " right", getId()); //open giftpack
				confirmCoords = FScript.imageSearch("bagFullConfirm.bmp", 5, getId()); //look for confirm button
				i++;
			}

			//click confirm if it exists
			if (confirmCoords[0] == 0){
				FScript.execTask("click " + confirmCoords[1] + " " + confirmCoords[2] + " left false false false", getId()); //click confirm
			}

			deleteJunk();

		}
	}

	//opens all lockboxes
	public void openLockboxes(){
		deleteJunk();

		while ((FScript.waitForImage("lockbox.bmp", 5, getId(), 500, 5000)[0] == 0) && (FScript.waitForImage("clover.bmp", 5, getId(), 500, 5000)[0] == 0)){ //while a lockbox and a clover are visible
			FScript.execTask("moveMouse 0 0", getId());

			//look for giftpacks and confirm button
			int[] lockboxCoords = FScript.imageSearch("giftbox.bmp", 5, getId()); 
			int[] confirmCoords = FScript.imageSearch("bagFullConfirm.bmp", 5, getId()); 

			//repeat a max of 100 times or until either no giftpack is seen or confirm is seen
			int i = 0;
			while ((i<100) && (FScript.waitForImage("lockbox.bmp", 5, getId(), 500, 5000)[0] == 0) && (confirmCoords[0] != 0) && (FScript.waitForImage("clover.bmp", 5, getId(), 500, 5000)[0] == 0)){ 
				FScript.execTask("moveMouse 0 0", getId());
				lockboxCoords = FScript.imageSearch("lockbox.bmp", 5, getId()); //look for a signin pack
				if (lockboxCoords[0] != 0){
					System.out.println("Failure");
				}

				FScript.execTask("click " + lockboxCoords[1] + " " + lockboxCoords[2] + " right", getId()); //open giftpack
				confirmCoords = FScript.imageSearch("bagFullConfirm.bmp", 5, getId()); //look for confirm button
				i++;
				FScript.execTask("moveMouse 0 0", getId());
			}

			//click confirm if it exists
			if (confirmCoords[0] == 0){
				FScript.execTask("click " + confirmCoords[1] + " " + confirmCoords[2] + " left false false false", getId()); //click confirm
			}

			deleteJunk();
			FScript.execTask("moveMouse 0 0", getId());
		}
	}

	private void deleteJunk(){
		//FScript.execTask("click 400 39 left false false false", getId()); //open bag
		String[] trash = new String[] {"starOfDefiance.bmp", "worldFlute.bmp", "starOfMorale.bmp", "starOfPrecision.bmp", "starOfFate.bmp", "starOfLife.bmp", "lhScroll.bmp", "lionheartScroll.bmp", "memoryFruit.bmp", "petEssence.bmp", "petsElementalScroll.bmp", "petsElementalScroll2.bmp", "petsTrialScroll.bmp", "reforgeCharm.bmp", "reforgeCrystal.bmp", "reforgeStone.bmp", "rubyShard.bmp", "sapphireShard.bmp", "scrollOfProphets.bmp", "scrollOfSages.bmp", "starFrag.bmp", "sweetDreamland.bmp", "talentCompass.bmp", "trialScroll.bmp", "turquoiseShard.bmp", "virtueCoin.bmp", "wisdomFruit.bmp", "amethystShard.bmp", "azureEmblem.bmp", "bankExtension.bmp", "blueFlaxDreamland.bmp", "bookOfFaith.bmp", "conscriptionScroll.bmp", "crystalLight.bmp",  "dawnScroll.bmp", "earthScroll.bmp", "edgeBadge.bmp", "exchangeIcon.bmp", "fissureScroll.bmp", "flarebox.bmp", "ghastlyFlareGembox.bmp"};

		int[] coords = FScript.imageSearch("bagAnchor.bmp", 5, getId());
		for (int i=0; i<trash.length; i++){
			//look for trash and make sure it's in the bag
			int[] trashCoords = FScript.imageSearch(trash[i], 5, getId());
			if ((trashCoords[0] == 0) && (trashCoords[1] > coords[1]-151) && (trashCoords[2] > coords[2]-227) && (trashCoords[1] < coords[1]+11) && (trashCoords[2] < coords[2]-11)){
				FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left false false false", getId()); //Activate delete mode
				FScript.sleep(250);
				FScript.execTask("click " + (trashCoords[1]) + " " + (trashCoords[2]) + " left false false false", getId()); //click trash
				FScript.sleep(250);
				FScript.execTask("click 292 282 left false false false", getId()); //confirm deletion
				FScript.sleep(250);
				FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " right", getId()); //deactivate delete mode
				FScript.sleep(250);
			}
		}
		//FScript.execTask("click 400 39 left false false false", getId()); //close bag
		FScript.sleep(1000);
	}

	private void getCloverKeys(){
		int[] coords = FScript.imageSearch("questSmall.bmp", 5, getId());
		if (coords[0] == 0){
			FScript.execTask("click " + (coords[1]+36) + " " + (coords[2]-18) + " left false false false", getId());
			FScript.sleep(1000);
			FScript.execTask("click 244 290 left false false false", getId());
			FScript.sleep(1000);
			coords = FScript.imageSearch("otisConfirm.bmp", 5, getId());
			FScript.execTask("click " + (coords[1]-353) + " " + (coords[2]-156) + " left false false false", getId()); //attendance tab
			FScript.sleep(1000);
			FScript.execTask("click " + (coords[1]-312) + " " + (coords[2]-234) + " left false false false", getId()); //clover key
			FScript.sleep(1000);
			for (int i=0; i<20; i++){
				FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left false false false", getId()); //confirm
				FScript.sleep(1000);
			}
			FScript.execTask("click " + (coords[1]+61) + " " + (coords[2]+2) + " left false false false", getId()); //exit (cancel)
		}
	}

	public void clickOnOtis(){
		for (int i=0; i<10; i++){
			//rotate to top down and zoom out
			FScript.execTask("lock", getId());
			FScript.execTask("clickDrag 320 240 320 540 right", getId());
			FScript.execTask("clickDrag 320 240 320 540 right", getId());
			FScript.execTask("clickDrag 320 240 320 540 right", getId());
			FScript.execTask("scroll down 12", getId());
			FScript.execTask("scroll up 6", getId()); //zoom back in since there's a second npc next to otis
			FScript.execTask("unlock", getId());

			//look for henry, if he's found click him
			int[] coords = FScript.imageSearch("grayQuestionMark.bmp", 5, getId());
			if (coords[0] == 0){
				//the character is probably hugging henry from the last time so try to click that first
				if (i == 0){
					FScript.execTask("click " + (coords[1]+3) + " " + (coords[2]+30) + " left true false false", getId());
				} else {
					FScript.execTask("click " + (coords[1]+20) + " " + (coords[2]+30) + " left true false false", getId());					
				}


				//verify that henry was clicked on
				if (FScript.waitForImage("questSmall.bmp", 5, getId(), 500, 2500)[0] == 0){
					if (FScript.imageSearch("otisQuestPanel.bmp", 5, getId())[0] == 0){
						//System.out.println(i + " succeeded");
						//FScript.execTask("sendInput {ESCAPE}", getId());
						return;
					}
					FScript.execTask("sendInput {ESCAPE}", getId());
					FScript.sleep(250);
					FScript.execTask("sendInput {ESCAPE}", getId());

				} else {
					//the character probably moved towards henry so try to click him again				
					for (int j=0; j<3; j++){
						coords = FScript.imageSearch("grayQuestionMark.bmp", 5, getId());
						if (coords[0] == 0){
							FScript.execTask("click " + (coords[1]+3) + " " + (coords[2]+30) + " left true false false", getId());
							//verify that henry was clicked on
							if (FScript.waitForImage("questSmall.bmp", 5, getId(), 500, 2500)[0] == 0){
								if (FScript.imageSearch("otisQuestPanel.bmp", 5, getId())[0] == 0){
									//System.out.println(i + " succeeded");
									//FScript.execTask("sendInput {ESCAPE}", getId());
									return;
								}
							}
							FScript.execTask("sendInput {ESCAPE}", getId());
							FScript.sleep(250);
							FScript.execTask("sendInput {ESCAPE}", getId());
							//rotate horizontally
							FScript.execTask("clickDrag 320 240 720 240 right", getId());
						}
					}

					clickLink();

					FScript.sleep(2000);
				}
			} else if (i%3 == 0){
				clickLink();

				FScript.sleep(2000);
			}

			//then rotate horizontally
			FScript.execTask("clickDrag 320 240 720 240 right", getId());

		}
		
	}

	private void moveToLinker(){
		FScript.execTask("click 133 505 left false false false", collectorId);
		FScript.sleep(1000);
		FScript.execTask("sendInput ~", collectorId);
		FScript.sleep(1000);
		FScript.execTask("click 192 502 left false false false", collectorId);
		FScript.sleep(1000);
		FScript.execTask("click 234 357 left false false false", collectorId);
		FScript.sleep(1000);
		FScript.execTask("click 192 502 left false false false", collectorId);
		FScript.sleep(1000);
		FScript.execTask("sendInput {ENTER}", collectorId);
		FScript.sleep(1000);

		clickLink();
	}

	private void clickLink(){
		FScript.execTask("click 78 434 left false false false", getId());
		FScript.execTask("click 136 446 left false false false", getId());
		FScript.sleep(25000);
	}

	private void inviteOnLinker(){
		//open T menu
		FScript.execTask("click 484 38 left false false false", getId());

		//click the name box, type in the linker name, send the invite, and close the T menu
		int[] coords = FScript.waitForImage("partyInvite.bmp", 5, getId(), 200, 5000);
		if (coords[0] == 0){
			FScript.execTask("click " + (coords[1]-58) + " " + (coords[2]+3) + " left false false false", getId());
			FScript.sleep(1000);
			FScript.execTask("sendInput " + this.linkerName + "", getId());
			FScript.sleep(1000);
			FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left false false false", getId());
			FScript.sleep(1000);
			FScript.execTask("click 484 38 left false false false", getId());
		}
		FScript.sleep(5000);

		//click the flashing invite and accept it on the collector
		FScript.execTask("click 318 144 left false false false", collectorId);
		FScript.sleep(2000);
		FScript.execTask("click 294 292 left false false false", collectorId);
	}

	public void clickOnHenry() {
		if (getCharIndex() == 0) {
			FScript.execTask("sendInput {ALTK}", getId());
		}

		for (int i = 0; i < 10; i++) {
			// rotate to top down and zoom out
			FScript.execTask("lock", getId());
			FScript.execTask("clickDrag 320 240 320 540 right", getId());
			FScript.execTask("clickDrag 320 240 320 540 right", getId());
			FScript.execTask("clickDrag 320 240 320 540 right", getId());
			FScript.execTask("scroll down 12", getId());
			FScript.execTask("unlock", getId());

			// look for henry, if he's found click him
			int[] coords = FScript.imageSearch("henryExpIcon.bmp", 5, getId());
			if (coords[0] == 0) {
				// the character is probably hugging henry from the last time so
				// try to click that first
				if (i == 0) {
					FScript.execTask("click " + (coords[1] + 3) + " " + (coords[2] + 30) + " left true false false", getId());
				} else {
					FScript.execTask("click " + (coords[1] + 20) + " " + (coords[2] + 30) + " left true false false", getId());
				}

				// verify that henry was clicked on
				if (FScript.waitForImage("questSmall.bmp", 5, getId(), 500,2500)[0] == 0) {
					// System.out.println(i + " succeeded");
					// FScript.execTask("sendInput {ESCAPE}", getId());
					return;
				} else {
					// the character probably moved towards henry so try to
					// click him again
					for (int j = 0; j < 3; j++) {
						coords = FScript.imageSearch("henryExpIcon.bmp", 5, getId());
						if (coords[0] == 0) {
							FScript.execTask("click " + (coords[1] + 3) + " " + (coords[2] + 30) + " left true false false", getId());
							// verify that henry was clicked on
							if (FScript.waitForImage("questSmall.bmp", 5, getId(), 500, 2500)[0] == 0) {
								return;
							}
							// rotate horizontally
							FScript.execTask("clickDrag 320 240 720 240 right", getId());
						}
					}

					moveToHenry();
					
					FScript.sleep(2000);
				}
			} else if (i % 3 == 0) {
				moveToHenry();
				FScript.sleep(2000);
			}

			// then rotate horizontally
			FScript.execTask("clickDrag 320 240 720 240 right", getId());

		}
		

	}

	private void moveToHenry() {
		FScript.execTask("lock", getId());
		FScript.execTask("sendInput {OPENMAP}", getId());
		int[] coords = FScript.imageSearch("mapAnchor.bmp", 5, getId());
		// zoom out
		FScript.execTask("click " + (coords[1] - 272) + " " + (coords[2] + 173) + " right false false false", getId());
		FScript.sleep(1000);
		FScript.execTask("click " + (coords[1] - 272) + " " + (coords[2] + 173) + " right false false false", getId());
		FScript.sleep(1000);
		FScript.execTask("click " + (coords[1] - 272) + " " + (coords[2] + 173) + " right false false false", getId());
		FScript.sleep(1000);

		// go to nightfall citadel
		FScript.execTask("click " + (coords[1] - 312) + " "+ (coords[2] + 182) + " left false false false", getId());
		FScript.sleep(1000);
		FScript.execTask("click " + (coords[1] - 410) + " "+ (coords[2] + 260) + " left false false false", getId());
		FScript.sleep(1000);

		// select display NPC by function
		FScript.execTask("click " + (coords[1] - 10) + " " + (coords[2] + 34)+ " left false false false", getId());
		FScript.sleep(1000);
		FScript.execTask("click " + (coords[1] - 22) + " " + (coords[2] + 47)+ " left false false false", getId());
		FScript.sleep(1000);
		FScript.execTask("click " + (coords[1] - 34) + " " + (coords[2] + 72)+ " left false false false", getId());
		FScript.sleep(1000);

		// select common NPC
		FScript.execTask("click " + (coords[1] - 67) + " " + (coords[2] + 35) + " left false false false", getId());
		FScript.sleep(1000);
		FScript.execTask("click " + (coords[1] - 35) + " " + (coords[2] + 50) + " left false false false", getId());
		FScript.sleep(1000);
		FScript.execTask("click " + (coords[1] - 18) + " " + (coords[2] + 62) + " left false false false", getId());
		FScript.sleep(1000);

		// scroll down and doubleclick henry
		FScript.execTask("click " + (coords[1] - 312) + " " + (coords[2] + 185) + " left false false false", getId());
		FScript.sleep(1000);
		FScript.execTask("clickDrag " + (coords[1] + 43) + " " + (coords[2] + 76) + " " + (coords[1] + 43) + " "+ (coords[2] + 140) + " left", getId());
		FScript.sleep(1000);
		FScript.execTask("click " + (coords[1] - 79) + " " + (coords[2] + 141) + " left false false false", getId());
		FScript.execTask("click " + (coords[1] - 79) + " " + (coords[2] + 141) + " left false false false", getId());
		FScript.execTask("unlock", getId());
		FScript.sleep(1000);
	}

	public void getCheckin() {
		// checkin

		if (FScript.waitForImage("questSmall.bmp", 5, getId(), 200, 5000)[0] == 0) { // wait
			// for
			// the
			// quest
			// panel
			// to
			// popup
			int[] results = FScript.waitForImage("checkinQuest.bmp", 55,
					getId(), 20, 200); // find the checkinquest
			if (results[0] == 0) { // if it was found, click it and complete it
				FScript.execTask("click " + results[1] + " " + results[2]
						+ " left; sleep 1000; click 94 198 left false false false", getId()); // click
				// the
				// checkin
				// quest
			}
		}
		FScript.sleep(1000);
	}

	

}
