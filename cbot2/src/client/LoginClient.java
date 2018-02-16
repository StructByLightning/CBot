package client;

import java.util.Random;

import map.Map;
import network.Network;
import party.Party;
import bag.Bag;
import diagnosis.Diagnosis;
import fScript.FScript;

public class LoginClient extends Client{
	private boolean finishedIngame;
	private int charIndex;
	private int realm = -1;
	private Diagnosis diagnosis;
	private Bag bag;
	private Party party;
	private Map map;

	public LoginClient(int id, boolean isHarvestClient, boolean resetDays, boolean incDays) {
		super(id, isHarvestClient);
		this.finishedIngame = false;
		this.charIndex = 0;
		this.diagnosis = new Diagnosis(resetDays, incDays, this);
		this.bag = new Bag(this);
		this.party = new Party(this);
		this.map = new Map(this);
	}	
	
	public Bag getBag(){
		return this.bag;
	}
	
	public Party getParty(){
		return this.party;
	}
	
	public Map getMap(){
		return this.map;
	}
	
	public Diagnosis getDiag(){
		return this.diagnosis;
	}

	private void updateDb(){
		try {
			this.getDiag().update(this.getId());
			Network.sendData(Network.UPDATE_ACC_PORT, this.getAccount().getUsername() + " " + (this.getCharIndex()+1 + " " + this.getDiag().toString()), Network.getServerIp());
			this.getDiag().reset();
		} catch (Exception e){
			e.printStackTrace(System.out);
		}	
	}
	
	//update the task based on what is visible in the fw client
	public void updateTask(){
		if (getAccount() == null){
			setTask("getAccount");
			return;
		}
		
		if (!FScript.clientExists(getId())){
			//System.out.println("setting openclient task " + getId() + " thread id:" + this.getId());
			setTask("openClient");
		} else if(FScript.imageSearch("client_disconnect.bmp", 5, getId())[0] == 0){ //if the client has disconnected	
			setTask("disconnected");
		} else if(FScript.imageSearch("crashed.bmp", 5, getId())[0] == 0){ //if the client has crashed
			setTask("exitCrashedClient");
		} else if(FScript.imageSearch("client_loginScreen.bmp", 5, getId())[0] == 0){ //if the client is at the loginScreen
			setTask("enterAccInfo");
		} else if((FScript.imageSearch("charScreenLoaded1.bmp", 25, getId())[0] == 0)){ //if the client is at the charScreen and has waited for it to load (scans for empty account charscreen)
			setTask("charScreen");
		} else if((FScript.imageSearch("charScreenLoaded2.bmp", 25, getId())[0] == 0)){ //if the client is at the charScreen and has waited for it to load (scans for empty account charscreen)
			setTask("charScreen");
		} else if((FScript.imageSearch("charScreenLoaded3.bmp", 25, getId())[0] == 0)){ //if the client is at the charScreen and has waited for it to load (scans for empty account charscreen)
			setTask("charScreen");
		} else if((FScript.imageSearch("charScreenLoaded4.bmp", 25, getId())[0] == 0)){ //if the client is at the charScreen and has waited for it to load (scans for empty account charscreen)
			setTask("charScreen");
		} else if((FScript.imageSearch("dcedClientConfirm.bmp", 25, getId())[0] == 0)){ //if the client is at the charScreen and has waited for it to load (scans for empty account charscreen)
			setTask("disconnected");
		} else if((FScript.imageSearch("client_inGame.bmp", 5, getId())[0] == 0) && (!finishedIngame)){ //if the client is inGame and hasn't checked in
			setTask("doIngameTask");
		} else if((FScript.imageSearch("client_inGame2.bmp", 5, getId())[0] == 0) && (!finishedIngame)){ //if the client is inGame and hasn't checked in
			setTask("doIngameTask");
		} else if((FScript.imageSearch("client_inGame3.bmp", 5, getId())[0] == 0) && (!finishedIngame)){ //if the client is inGame and hasn't checked in
			setTask("doIngameTask");
		} else if((FScript.imageSearch("client_inGame4.bmp", 5, getId())[0] == 0) && (!finishedIngame)){ //if the client is inGame and hasn't checked in
			setTask("doIngameTask");
		} else if((FScript.imageSearch("client_inGame5.bmp", 5, getId())[0] == 0) && (!finishedIngame)){ //if the client is inGame and hasn't checked in
			setTask("doIngameTask");
		} else if((FScript.imageSearch("client_inGame6.bmp", 5, getId())[0] == 0) && (!finishedIngame)){ //if the client is inGame and hasn't checked in
			setTask("doIngameTask");
		} else if((FScript.imageSearch("client_inGame7.bmp", 5, getId())[0] == 0) && (!finishedIngame)){ //if the client is inGame and hasn't checked in
			setTask("doIngameTask");
		} else if((FScript.imageSearch("client_inGame8.bmp", 5, getId())[0] == 0) && (!finishedIngame)){ //if the client is inGame and hasn't checked in
			setTask("doIngameTask");
		} else if((FScript.imageSearch("client_inGame.bmp", 5, getId())[0] == 0) && (finishedIngame)){ //if the client is inGame and hasn't checked in
			setTask("logoutChar");
		} else if((FScript.imageSearch("client_inGame2.bmp", 5, getId())[0] == 0) && (finishedIngame)){ //if the client is inGame and hasn't checked in
			setTask("logoutChar");
		} else if((FScript.imageSearch("client_inGame3.bmp", 5, getId())[0] == 0) && (finishedIngame)){ //if the client is inGame and hasn't checked in
			setTask("logoutChar");
		} else if((FScript.imageSearch("client_inGame4.bmp", 5, getId())[0] == 0) && (finishedIngame)){ //if the client is inGame and hasn't checked in
			setTask("logoutChar");
		} else if((FScript.imageSearch("client_inGame5.bmp", 5, getId())[0] == 0) && (finishedIngame)){ //if the client is inGame and hasn't checked in
			setTask("logoutChar");
		} else if((FScript.imageSearch("client_inGame6.bmp", 5, getId())[0] == 0) && (finishedIngame)){ //if the client is inGame and hasn't checked in
			setTask("logoutChar");
		} else if((FScript.imageSearch("client_inGame7.bmp", 5, getId())[0] == 0) && (finishedIngame)){ //if the client is inGame and hasn't checked in
			setTask("logoutChar");
		} else if((FScript.imageSearch("client_inGame8.bmp", 5, getId())[0] == 0) && (finishedIngame)){ //if the client is inGame and hasn't checked in
			setTask("logoutChar");
		} else if(FScript.imageSearch("cutscene.bmp", 5, getId())[0] == 0){ //if the client is inGame and hasn't checked in
			setTask("exitCutscene");
		} else {
			setTask("");
		}
	}

	//perform a task based on the task
	public void completeTask(){
		if (getTask().equals("getAccount")){
			this.getNextAccount();
		} else if (getTask().equals("openClient")){
			FScript.execTask("openClient", getId());
		} else if (getTask().equals("enterAccInfo")){
			login();
		} else if (getTask().equals("charScreen")){
			selectCharacter();
		} else if (getTask().equals("disconnected")){
			exitDcedClient();
		} else if (getTask().equals("doIngameTask")){
			doIngameTask();
		} else if(getTask().equals("logoutChar")){
			logout();
		} else if(getTask().equals("exitCutscene")){
			exitCutscene();
		} 
	}
	
	//exits a cutscene by pressing escape
	public void exitCutscene(){
		FScript.execTask("lock", getId());
		FScript.execTask("sendInput {ESCAPE}", getId()); //exit out of a cutscene if it's up
		FScript.execTask("sendInput {ESCAPE}", getId()); //exit out of a cutscene if it's up
		FScript.execTask("unlock", getId());
		
	}

	//logs the character out and increments charIndex
	public void logout(){

		updateDb(); //call this before incrementCharIndex()
		
		FScript.execTask("lock", getId());
		FScript.execTask("sendInput {ESCAPE}", getId()); 
		FScript.execTask("click 639 40 left false false false", getId()); //click the gear to bring up the system menu
		int[] results = FScript.waitForImage("systemMenu.bmp", 5, getId(), 200, 1000); //find the system menu anchor
		if (results[0] != -1){
			FScript.execTask("click " + (results[1]-326) + " " + (results[2]+37) + " left false false false", getId()); 
			FScript.execTask("click " + (results[1]-216) + " " + (results[2]+37) + " left false false false", getId()); 
			FScript.execTask("click " + (results[1]-104) + " " + (results[2]+37) + " left false false false", getId()); 
			FScript.execTask("click " + (results[1]-94) + " " + (results[2]+158) + " left false false false", getId()); 
			FScript.execTask("click " + (results[1]-122) + " " + (results[2]+166) + " left false false false", getId()); 
			incrementCharIndex();
			FScript.execTask("unlock", getId());
			FScript.sleep(10000);
		} else {
			FScript.execTask("unlock", getId());
		}
	}

	public void run(){
		try {
			while (getRunning()){
				updateTask();
				//System.out.println("(" + getId() + ")" + getTask());
				completeTask();
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	//default thing to do
	public void doIngameTask(){
		finishedIngame = true;
		FScript.execTask("sendInput {ESCAPE}", getId()); //exit out of anything in the way
		
		if (getBag().getItem("mercStat", false, true).exists()){
			FScript.log(getAccount().getUsername() + " has merc stats.", "mercStatAccs.txt");
		} else {
			FScript.log(getAccount().getUsername() + " doesn't have merc stats.", "mercStatAccs.txt");
				
		}
	}

	//logs the next character in
	public void selectCharacter(){
		//******************************************************************************************************
		//if there aren't 8 characters

		/*
		
		Functions.sleep(5000);
		
		Functions.execTask("click 233 460 left", getId()); //click last character
		Functions.sleep(1000);
		if ((Functions.imageSearch("mmCharScreenIcon.bmp", 5, getId(), 151, 407, 300, 452)[0] != 0) || ((Functions.imageSearch("mmCharScreenIcon.bmp", 5, getId(), 181, 407, 300, 482)[0] != 0))){
			Functions.log("failedAccs.txt", "Didn't create on " + getAccount());
			setCharIndex(8);
		}
		*/

		//*******************************************************************************************************
		
		//pick a random realm if realm is -1, otherwise choose that realm
		int choice;
		if (getRealm() == -1){
			Random r = new Random(System.currentTimeMillis());
			choice = r.nextInt(98)+63; //get a random number between 63-161
		} else {
			choice = (int)((realm-1)*9.8)+63;
		}
		
		FScript.execTask("click 587 41 left false false false", getId()); //click realm list
		FScript.sleep(500);
		FScript.execTask("click 587 " + choice + " left false false false", getId()); //click a random realm
		FScript.sleep(500);
		
		//get the next characters
		setCharIndex(this.getAccount().nextChar());
		if ((getCharIndex() < 8) ){
			//click the character
			if (getCharIndex() == 0){
				FScript.execTask("click 118 94 left false false false", getId());
			} else if (getCharIndex() == 1){
				FScript.execTask("click 110 146 left false false false", getId());
			} else if (getCharIndex() == 2){
				FScript.execTask("click 110 200 left false false false", getId());
			} else if (getCharIndex() == 3){
				FScript.execTask("click 115 252 left false false false", getId());
			} else if (getCharIndex() == 4){
				FScript.execTask("click 133 305 left false false false", getId());
			} else if (getCharIndex() == 5){
				FScript.execTask("click 158 358 left false false false", getId());
			} else if (getCharIndex() == 6){
				FScript.execTask("click 188 410 left false false false", getId());
			} else if (getCharIndex() == 7){
				FScript.execTask("click 235 460 left false false false", getId());
			}
			//click the start button
			FScript.execTask("click 335 478 left false false false", getId());
			
			//fixes double ingame execution bug
			//the client would complete ingame
			//set ingamefinished = true
			//start logout
			//next state update
			//	ingame = true, finished = true
			//exec ingame task
			//moving this here makes the state update ignore being ingame and attempt to logout again (which is harmless)
			finishedIngame = false; 
			FScript.sleep(5000);
		} else {
			//click the exit button
			FScript.execTask("click 559 482 left false false false", getId());
			resetCharIndex();
			this.getNextAccount();
			FScript.sleep(10000);
		}
	}

	//changes or retrieves charIndex
	public int getCharIndex(){return charIndex;}
	public void setCharIndex(int charIndex){this.charIndex = charIndex;}
	public void resetCharIndex(){this.charIndex = 0;}
	public void incrementCharIndex(){this.charIndex++;}
	
	public void setFinishedIngame(boolean value){this.finishedIngame = value;}
	public boolean getFinishedIngame(){return this.finishedIngame;}
	
	public void setRealm(int value){realm = value;}
	public int getRealm(){return realm;}
}
