package bag;

import party.Party;
import client.Client;
import config.Config;
import errorMessage.ErrorMessage;
import fScript.FScript;

//object representing a character's bag
public class Bag {
	public final static String[] LOOT_FILEPATHS = {"gembox", "giftpack", "seashadeOrb", "phantomOrb", "clockworkOrb", "ichibanOrb", "newYearOrb", "pandaOrb", 
		"leviathanOrb", "huntsmanOrb", "polystoneOrb", "hadesOrb", "solarLight", "rageLight", "azureLight", "bloodLight", "eagleLight", "mistLight", 
		"paragonPack", "warlordPack1", "oceanOrb", "solarOrb"};

	public final static String[] TRASH_FILEPATHS = {"phoenixOrder", "mmStarterBoots", "mmStarterWeapon", "mmStarterChest", "mmStarterPants", "seedOfTwilightOpened", 
		"eliteStarOfFate", "astralReforgeCharm", "astralReforgeStone", "astralReforgeCrystal", "azureSkyEmblem", "conscriptionScroll",  "lesserPetEssence", 
		"lionheartScroll", "petEssence", "petsFissureScroll", "petsTrialScroll", "starFragment", "starOfDefiance", "starOfLife", "starOfMorale", "worldFlute", 
		"starOfPrecision", "starOfFate", "soulResolution", "virtueCoin", "edgeBadge", "mediumPetEssence", "sapphireCrystal", "eliteStarOfHope", "starOfHope", "advancedFiendslayer",
		"advancedWondrousBullet", "basicMegaSlayer", "bankExtension", "dimStarDebris", "greenCombatLicense", "lesserShadowhornRam", "ragefire3", "soulPackMedium", 
		"scrollOfSages", "soulPurse", "soulPurseLarge", "spatialCompass", "starFragment", "zoomingSpyglass"}; //"crystalLight" removed this cuz of that one login event
	
	public final static String[] EPIC_TRASH_FILEPATHS = {"lionheartChromaticBadge", "starDiamond", "skillbookPray4", "warlordCoin"};

	public final static String LEGEND_OF_EYRDA = "theLegendOfEyrda";
	public final static String GOD_GIFT_BOX = "godGiftBox";
	public final static String SEED_OF_TWILIGHT_CLOSED_FILEPATH = "seedOfTwilightClosed";
	public final static String PRAY_SKILLBOOK_FILEPATH = "skillbookPrayLv1";
	public final static String FLARE_CARD_FILEPATH = "flareGemCard";
	public final static String BAG_EXTENSION = "bagExtension";

	private boolean dontCloseBag = false; //fixes all those annoying bugs where the bag gets closed before being looked at...

	//items are highlighted with a thick border when they spawn in the bag
	//this can mess up imagesearch checks
	private boolean itemsHighlighted = true; 
	
	private Client owner;

	public Bag(Client owner){
		this.owner = owner;
	}

	//returns an Item object containing exists, x, and y
	//wait means wait 5s checking constantly, otherwise, only one check is performed
	//checkbag controls whether the bag is confirmed to be open
	public Item getItem(String item, boolean wait, boolean checkBag){
		if (checkBag){ //faster to not check but safer to check
			open();
			dontCloseBag = true;			
		}

		int[] result; 
		if (wait){
			result = FScript.waitForImage(item+".bmp", 5, owner.getId(), 200, 5000);	
		} else {
			result = FScript.imageSearch(item+".bmp", 5, owner.getId());	
		}
		
		//paired with above
		if (checkBag){
			dontCloseBag = false;
			close();			
		}

		return new Item(result[0] == 0, result[1], result[2]);
	}
	
	//uses a god pack to get all available bag expansion items
	//also deletes any trash leftover
	public void openGodGiftBox(){
		while (getItem(GOD_GIFT_BOX, true, true).exists()){
			useItem(GOD_GIFT_BOX);
			
			while (getItem(BAG_EXTENSION, true, true).exists()){
				useItem(BAG_EXTENSION);
			}
			
			deleteTrash(false);
			
			for (int i=0; i<10; i++){
				FScript.execTask("sendInput {ESCAPE}", owner.getId()); //exit out of anything in the way (C panel from last god box)
				FScript.sleep(500);
			}
			
		}
	}

	//run the mouse over all items in the bag to de highlight them
	public void unHighlightItems(){
		for (int y=0; y<8; y++){
			for (int x=0; x<6; x++){
				FScript.execTask("moveMouse " + (438 + (x*26)) + " " + (164 + (y*26)), owner.getId());
			}	
		}
	}
	
	//uses (right clicks) an item
	public void useItem(String item){
		open();
		dontCloseBag = true;
		
		if (item.equals("bagExtension")){ //nonstandard use case, must click on bag button instead of right clicking item
			int[] coords = FScript.waitForImage("bagAnchor.bmp", 5, owner.getId(), 200, 5000);
			FScript.execTask("click " + (coords[1]-72) + " " + (coords[2]) + " left false false false", owner.getId()); //click bag extension button
			FScript.sleep(1000);
		} else {
			int[] coords = FScript.waitForImage(item+".bmp", 5, owner.getId(), 200, 5000);
			FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " right false false false", owner.getId()); //right click item
			FScript.sleep(1000);
		}		

		dontCloseBag = false;
		close();
	}

	//sells an item (vendor window must be open)
	public void sellItem(String item){
		int[] coords = FScript.waitForImage(item+".bmp", 5, owner.getId(), 200, 5000);
		FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " right false false false", owner.getId()); //right click item
		FScript.sleep(1000);

		coords = FScript.imageSearch("sellYes.bmp", 5, owner.getId());
		FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left false false false", owner.getId()); //click confirm
	}

	//trades the contents of the bag to a party member
	public void trade(boolean pauseOnFailedTrade){
		long oldTime = System.currentTimeMillis();
		boolean inRangeOfLinker = false;
		int[] coords = FScript.imageSearch("partyAnchor.bmp", 5, owner.getId());
		if (coords[0] == 0){
			//send trade request
			FScript.execTask("click " + (coords[1]+4) + " " + (coords[2]-8) + " right false false false", owner.getId());
			FScript.sleep(500);
			FScript.execTask("click " + (coords[1]+48) + " " + (coords[2]+42) + " left false false false", owner.getId());
			FScript.sleep(5000);

			//accept it
			FScript.execTask("click 318 144 left false false false", Party.COLLECTOR_ID);
			FScript.sleep(2000);
			FScript.execTask("click 300 285 left false false false", Party.COLLECTOR_ID);
			FScript.sleep(5000);

			//click four rows of items into the trade window
			for (int y=0; y<8; y++){
				for (int x=0; x<6; x++){
					FScript.execTask("click " + (438 + (x*26)) + " " + (164 + (y*26)) + " right false false false", owner.getId());
					FScript.sleep(250);

					//look for a failed trade message incase the item was bound
					coords = FScript.imageSearch("tradeFailedConfirm.bmp", 5, owner.getId());
					if (coords[0] == 0){
						FScript.execTask("click " + coords[1] + " " + coords[2] + " left false false false", owner.getId());
						FScript.sleep(500);
						x--; //click the last item again just incase the client is a bit laggy
					}

				}	
			}

			coords = FScript.imageSearch("tradeCancel.bmp", 5, owner.getId());
			if (coords[0] == 0){
				inRangeOfLinker = true;
			}
			FScript.execTask("click " + (coords[1]+19) + " " + (coords[2]-81) + " left false false false", owner.getId()); //money
			FScript.sleep(500);

			coords = FScript.imageSearch("maxMoney.bmp", 5, owner.getId());
			if (coords[0] == 0){
				inRangeOfLinker = true;
			}
			if (coords[0] != 0){
				coords = FScript.imageSearch("maxMoneyRollover.bmp", 5, owner.getId());	
				if (coords[0] == 0){
					inRangeOfLinker = true;
				}
			}
			FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left false false false", owner.getId()); //max
			FScript.sleep(500);
			FScript.execTask("click " + (coords[1]+89) + " " + (coords[2]) + " left false false false", owner.getId()); //confirm
			FScript.sleep(500);
			FScript.execTask("click " + (coords[1]+122) + " " + (coords[2]) + " left false false false", owner.getId()); //cancel
			FScript.sleep(500);

			//check to see if the mnoney bar was clicked again (fixes bug where the money bar can be clicked by the cancel and then block further clicks)
			coords = FScript.imageSearch("maxMoney.bmp", 5, owner.getId());
			if (coords[0] == 0){
				inRangeOfLinker = true;
			}
			if (coords[0] != 0){
				coords = FScript.imageSearch("maxMoneyRollover.bmp", 5, owner.getId());	
				if (coords[0] == 0){
					inRangeOfLinker = true;
				}
			}
			if (coords[0] == 0){
				FScript.execTask("click " + (coords[1]+122) + " " + (coords[2]) + " left false false false", owner.getId()); //cancel
				FScript.sleep(500);
			}

			FScript.execTask("moveMouse 0 0", owner.getId());
			coords = FScript.imageSearch("tradeCancel.bmp", 5, owner.getId());
			if (coords[0] == 0){
				inRangeOfLinker = true;
			}
			FScript.execTask("click " + (coords[1]+49) + " " + (coords[2]+4) + " left false false false", owner.getId()); //lock
			FScript.sleep(500);

			coords = FScript.imageSearch("tradeCancel.bmp", 5, Party.COLLECTOR_ID);
			if (coords[0] == 0){
				inRangeOfLinker = true;
			}
			FScript.execTask("click " + (coords[1]+49) + " " + (coords[2]+4) + " left false false false", Party.COLLECTOR_ID); //lock
			FScript.sleep(500);

			if ((FScript.waitForImage("tradeTrade.bmp", 5, owner.getId(), 500, 30000)[0] == 0) || (FScript.waitForImage("tradeTradeRollover.bmp", 5, owner.getId(), 500, 30000)[0] == 0)){
				FScript.execTask("click " + (coords[1]+81) + " " + (coords[2]+4) + " left false false false", Party.COLLECTOR_ID); //trade
				FScript.sleep(500);

				coords = FScript.imageSearch("tradeCancel.bmp", 5, owner.getId());
				FScript.execTask("click " + (coords[1]+81) + " " + (coords[2]+4) + " left false false false", owner.getId()); //trade
				FScript.sleep(500);
				Config.setBagIsFull(false);
			} else {
				coords = FScript.imageSearch("tradeCancel.bmp", 5, owner.getId());
				FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left false false false", owner.getId()); //cancel trade

				if (inRangeOfLinker){
					if (pauseOnFailedTrade){ //don't skip over any chars. will cause problems if any char cannot reach the linker
						ErrorMessage.throwError("Linker bag full"); //popup error message						
						trade(pauseOnFailedTrade); //attempt the trade again
					} else {
						if (Config.getBagIsFull()){
							ErrorMessage.throwError("Linker bag full"); //popup error message						
							trade(pauseOnFailedTrade); //attempt the trade again
						} else {
							Config.setBagIsFull(true);
						}
					}

				}

				//should tell the server that this character is stuck
			}
		} 
		
		FScript.sleep(1000); //delay to wait for the trade interface to close. can cause bugs if open() is called immediately after
	}

	//opens orbs and gemboxes
	public void openLoot(){
		open();
		dontCloseBag = true;

		deleteTrash(false);

		boolean opening = true;
		int loops=0;
		while (opening && loops<500){
			loops++;
			
			//locate a loot
			String targetLoot = "";
			int lootX = 0;
			int lootY = 0;
			for (int i=0; i<LOOT_FILEPATHS.length; i++){
				FScript.execTask("moveMouse 0 0", owner.getId());
				
				int[] coords = FScript.imageSearch(LOOT_FILEPATHS[i]+".bmp", 5, owner.getId());
				if (coords[0] == 0){
					//some variables to make stuff more legible
					targetLoot = LOOT_FILEPATHS[i]+".bmp";
					lootX = coords[1];
					lootY = coords[2];

					//open the loot until the loot is gone, or the clicking has gone on so long that an error occurred
					for (int j=0; j<100; j++){
						FScript.execTask("click " + lootX + " " + lootY + " right false false false", owner.getId()); //click the loot 
						if (j%5 == 0){
							FScript.execTask("moveMouse 0 0", owner.getId());							
							Item loot = getItem(targetLoot, true, false);
							if (!loot.exists()){
								j = 100;
							} else { //fixes bug with multiple stacks of loot
								lootX = loot.getX();
								lootY = loot.getY();
							}
						} else {
							FScript.sleep(250);
						}

						//check that the bag isn't full, if it is, click confirm and delete any trash
						int[] confirmCoords = FScript.imageSearch("bagFullConfirm.bmp", 5, owner.getId());
						if (confirmCoords[0] == 0){
							FScript.execTask("click " + confirmCoords[1] + " " + confirmCoords[2] + " left false false false", owner.getId()); 
							deleteTrash(false);
						}						
					}
				}
			}
			opening = lootExists();
			
			FScript.execTask("lock", owner.getId());
			close();
			for (int i=0; i<5; i++){
				FScript.execTask("sendInput {ESCAPE}", owner.getId());
				FScript.sleep(50);
			}
			open();
			FScript.execTask("unlock", owner.getId());
			
		}

		dontCloseBag = false;
		close();
	}

	public void openLockboxes(){
		open();
		dontCloseBag = true;
		deleteTrash(false);


		boolean opening = true;
		int loops = 0;
		while (opening && loops<500){
			loops++;
			FScript.execTask("moveMouse 0 0", owner.getId());
			int[] cloverCoords = FScript.waitForImage("clover.bmp", 5, owner.getId(), 500, 2000);
			int[] coords = FScript.imageSearch("lockbox.bmp", 5, owner.getId());

			if ((cloverCoords[0] == 0) && (coords[0] == 0)){

				for (int j=0; j<100; j++){
					FScript.execTask("click " + coords[1] + " " + coords[2] + " right false false false", owner.getId()); //click the loot 
					if (j%5 == 0){
						FScript.execTask("moveMouse 0 0", owner.getId());							
						if (FScript.waitForImage("lockbox.bmp", 5, owner.getId(), 500, 2000)[0] != 0){ //every 10th click, check to be sure the loot is still around
							j=100;
						}
					}

					//check that the bag isn't full, if it is, click confirm and delete any trash
					int[] confirmCoords = FScript.imageSearch("bagFullConfirm.bmp", 5, owner.getId());
					if (confirmCoords[0] == 0){
						FScript.execTask("click " + confirmCoords[1] + " " + confirmCoords[2] + " left false false false", owner.getId()); 
						deleteTrash(false);
					}

					//because the clover doesn't have a cooldown, check it every time
					FScript.execTask("moveMouse 0 0", owner.getId());	
					if (FScript.waitForImage("clover.bmp", 5, owner.getId(), 500, 2000)[0] != 0){ 
						j=100;
					}
				}
				deleteTrash(false);

				//verify that either the clovers or lockboxes ran out
				FScript.execTask("moveMouse 0 0", owner.getId());							
				cloverCoords = FScript.waitForImage("clover.bmp", 5, owner.getId(), 500, 2500);
				coords = FScript.waitForImage("lockbox.bmp", 5, owner.getId(), 500, 2500);
			} else {
				opening = false;
			}
		}

		dontCloseBag = false;
		close();
	}

	//opens all giftpacks
	public void openGiftpacks(){
		open();
		dontCloseBag = true;
		deleteTrash(false);

		while (FScript.waitForImage("giftbox.bmp", 5, owner.getId(), 500, 5000)[0] == 0){ //while a giftpack is visible
			FScript.execTask("moveMouse 0 0", owner.getId());

			//look for giftpacks
			int[] giftpackCoords = FScript.imageSearch("giftbox.bmp", 5, owner.getId()); 

			//repeat a max of 100 times or until either no giftpack is seen or confirm is seen
			for (int i=0; i<100; i++){
				FScript.execTask("click " + giftpackCoords[1] + " " + giftpackCoords[2] + " right false false false", owner.getId()); //open giftpack

				if (i%5 == 0){
					FScript.execTask("moveMouse 0 0", owner.getId());							
					if (FScript.waitForImage("giftbox.bmp", 5, owner.getId(), 500, 2000)[0] != 0){ //every 5th click, check to be sure the loot is still around
						i=100;
					}
				}

				int[] confirmCoords = FScript.imageSearch("bagFullConfirm.bmp", 5, owner.getId());
				if (confirmCoords[0] == 0){
					FScript.execTask("click " + confirmCoords[1] + " " + confirmCoords[2] + " left false false false", owner.getId()); 
					deleteTrash(false);
				}
			}
			deleteTrash(false);
		}
		dontCloseBag = false;
		close();
	}

	//looks for and uses Skillbook: Pray Lv1
	private void usePraySkillbook(){
		int[] coords = FScript.imageSearch(PRAY_SKILLBOOK_FILEPATH+".bmp", 5, owner.getId());
		if (coords[0] == 0){
			FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " right false false false", owner.getId()); //right click skillbook
			FScript.sleep(5000); //wait for it to be used
		}
	}

	//looks for and deletes Seed of Twilight
	private void useSeedOfTwilight(){
		int[] coords = FScript.imageSearch(SEED_OF_TWILIGHT_CLOSED_FILEPATH+".bmp", 5, owner.getId());
		if (coords[0] == 0){
			FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " right false false false", owner.getId()); //right click the seed item
			FScript.sleep(5000); //wait for it to be used
		}						
	}

	//looks for and uses The Legend of Eyrda
	private void useLegendOfEyrda(){
		int[] coords = FScript.imageSearch(LEGEND_OF_EYRDA+".bmp", 5, owner.getId());
		if (coords[0] == 0){
			FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " right false false false", owner.getId()); //right click legend
			FScript.sleep(1000);
			FScript.execTask("click 351 473 left false false false", owner.getId()); //click add

		}
	}

	private void useFlareCard(){
		int[] coords = FScript.imageSearch(FLARE_CARD_FILEPATH+".bmp", 5, owner.getId());
		if (coords[0] == 0){
			FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " right false false false", owner.getId()); //right click legend
			FScript.sleep(5000);
		}
	}
	
	//deletes any trash items in the bag.
	//true if the function should make multiple passes over the bag to ensure there are no trash items left 
	//useful if multiple stacks of trash are suspected
	public void deleteTrash(boolean verify){
		FScript.execTask("lock", owner.getId());
		open();

		int i=10;
		do { //repeat until there is no more trash, or until 10 tries (fixes infinite loop bug on undeletable trash)
			verify = deleteNormalTrash();
			
			deleteEpicTrash();
						
			useFlareCard();
			useSeedOfTwilight(); //don't put this before deleteNormalTrash because if the bag is full it will cause problems trying to open an item that can't be opened
			usePraySkillbook();
			useLegendOfEyrda();
			
			i++;
		}	while ((verify) && (i<10));
		
		close();

		FScript.execTask("unlock", owner.getId());
		FScript.sleep(1000);
	}

	//search for and delete anything in the epicTrash array
	private boolean deleteEpicTrash(){
		boolean trashExisted = false;
		int[] coords = FScript.imageSearch("bagAnchor.bmp", 5, owner.getId());
		if (coords[0] != 0){
			coords = FScript.imageSearch("bagAnchorRollover.bmp", 5, owner.getId());
		}
		FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left false false false", owner.getId()); //Activate delete mode
		for (int i=0; i<EPIC_TRASH_FILEPATHS.length; i++){
			//look for trash and make sure it's in the bag
			int[] trashCoords = FScript.imageSearch(EPIC_TRASH_FILEPATHS[i] + ".bmp", 5, owner.getId());
			if ((trashCoords[0] == 0) && (trashCoords[1] > coords[1]-151) && (trashCoords[2] > coords[2]-227) && (trashCoords[1] < coords[1]+11) && (trashCoords[2] < coords[2]-11)){
				FScript.execTask("click " + (trashCoords[1]) + " " + (trashCoords[2]) + " left false false false", owner.getId()); //click trash
				FScript.sleep(250);
				FScript.execTask("sendInput Yes", owner.getId()); //enter verification code
				FScript.execTask("click 351 287 left false false false", owner.getId()); //confirm deletion
				FScript.sleep(250);
				trashExisted = true;
			}
		}
		coords = FScript.imageSearch("bagAnchor.bmp", 5, owner.getId());
		if (coords[0] != 0){
			coords = FScript.imageSearch("bagAnchorRollover.bmp", 5, owner.getId());
		}
		FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " right false false false",owner.getId()); //deactivate delete mode
		return trashExisted;
	}

	//searches for and deletes anything in the normalTrash array
	private boolean deleteNormalTrash(){
		unHighlightItems(); //make sure everything is visible for image searching
		boolean trashExisted = false;
		int[] coords = FScript.imageSearch("bagAnchor.bmp", 5, owner.getId());
		if (coords[0] != 0){
			coords = FScript.imageSearch("bagAnchorRollover.bmp", 5, owner.getId());
		}
		FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " left false false false", owner.getId()); //Activate delete mode
		
		for (int i=0; i<TRASH_FILEPATHS.length; i++){
			Item trash = getItem(TRASH_FILEPATHS[i], false, false);
			
			if ((trash.exists()) && (trash.getX() > coords[1]-151) && (trash.getY() > coords[2]-227) && (trash.getX() < coords[1]+11) && (trash.getY() < coords[2]-11)){ //if trash was found and it's inside the bag
				FScript.execTask("click " + (trash.getX()) + " " + (trash.getY()) + " left false false false", owner.getId()); //click trash (since delete mode is active this deletes it)
				FScript.sleep(250);
				FScript.execTask("click 292 282 left false false false", owner.getId()); //confirm deletion
				FScript.sleep(250);
				trashExisted = true; //flip this flag to make deleteTrash() go an extra iteration to ensure no trash is left
			}
		}
		
		coords = FScript.imageSearch("bagAnchor.bmp", 5, owner.getId());
		if (coords[0] != 0){
			coords = FScript.imageSearch("bagAnchorRollover.bmp", 5, owner.getId());
		}
		FScript.execTask("click " + (coords[1]) + " " + (coords[2]) + " right false false false",owner.getId()); //deactivate delete mode
		return trashExisted;
		
	}

	//returns true if any loot is in the bag
	public boolean lootExists(){
		open();
		for (int i=0; i<10; i++){
			long oldTime = System.currentTimeMillis();
			for (int j=0; j<LOOT_FILEPATHS.length; j++){
				if (FScript.imageSearch(LOOT_FILEPATHS[j]+".bmp", 5, owner.getId())[0] == 0){
					close();					
					return true;
				}
			}

			long elapsed = System.currentTimeMillis()-oldTime;

			if (elapsed-500 > 0){
				FScript.sleep((int)(elapsed-500));
			}
		}

		close();
		return false;
	}

	//opens the bag
	public void open(){
		int loops = 0;
		while(!isOpen() && loops < 30){
			itemsHighlighted = true;
			loops++;
			FScript.execTask("click 396 41 left false false false", owner.getId()); //click bag button
			FScript.waitForImage("bagAnchor.bmp", 5, owner.getId(), 50, 1000);
		}
		return;
	}

	//closes the bag unless the dontCloseBag flag is true
	public void close(){
		if (dontCloseBag){
			return;
		}
		int loops = 0;
		while (isOpen() && loops < 30){
			loops++;
			FScript.execTask("click 396 41 left false false false", owner.getId()); //top right button
		}
	}

	//return true if the bag is open
	private boolean isOpen(){
		if (FScript.imageSearch("bagAnchor.bmp", 5, owner.getId())[0] == 0){
			return true;
		} else if (FScript.imageSearch("bagAnchorRollover.bmp", 5, owner.getId())[0] == 0){
			return true;
		} 

		return false;
	}
}
