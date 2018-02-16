package client;

import party.Party;
import bag.Bag;
import fScript.FScript;

public class HarvestCheckinOrbs extends LoginClient {
	private String linkerName;
	private int collectorId = 2;
	boolean firstChar = true;

	public HarvestCheckinOrbs(int id, String linkerName) {
		super(id, true, true, false);
		this.linkerName = linkerName;
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
		if (firstChar) {
			FScript.execTask("sendInput {ALTK}", getId());
			FScript.sleep(180000);
			firstChar = false;
		}
		

		getParty().inviteOnLinker(linkerName);
		moveToLinker();
		
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
			
			
			getBag().openLockboxes();
			getBag().openGiftpacks();
			getBag().openLoot();	
			
			getBag().trade(true);
			FScript.sleep(1000);
			openingLoot = getBag().lootExists();
		}
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

}
