package client;

import java.util.Random;

import config.Config;
import fScript.FScript;

public class PopulateAccsClient extends Client{
	private boolean finishedIngame;
	private int charIndex;

	public PopulateAccsClient(int id) {
		super(id, true);
		this.charIndex = 0; 
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
		}
	}
	
	//exits a cutscene by pressing escape
	public void exitCutscene(){
		FScript.execTask("lock", getId());
		FScript.execTask("sendInput {ESCAPE}", getId()); //exit out of a cutscene if it's up
		FScript.execTask("sendInput {ESCAPE}", getId()); //exit out of a cutscene if it's up
		FScript.execTask("unlock", getId());
		
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

	//creates the next character
	public void selectCharacter(){		
		
		//get the next characters
		if ((getCharIndex() < 8) ){
			FScript.execTask("click 56 450 left false false false", getId()); //click create button
			FScript.sleep(2000);
			
			FScript.execTask("click 63 256 left false false false", getId()); //click dwarf
			FScript.sleep(2000);
			
			FScript.execTask("click 352 458 left false false false", getId()); //click next button
			FScript.sleep(2000);
			
			FScript.execTask("click 165 385 left false false false", getId()); //click random button
			FScript.sleep(2000);

			FScript.execTask("click 356 459 left false false false", getId()); //click next button
			FScript.sleep(2000);
			
			FScript.execTask("click 91 103 left false false false", getId()); //click month menu dropdown
			FScript.sleep(2000);
			
			//click a random month
			Random r = new Random(System.currentTimeMillis());
			int y = r.nextInt(96)+119;
			FScript.execTask("click 79 " + y + " left false false false", getId()); //click month 
			FScript.sleep(2000);
			
			FScript.execTask("click 114 103 left false false false", getId()); //click day menu dropdown
			FScript.sleep(2000);
			
			//click a random day
			r = new Random(System.currentTimeMillis());
			y = r.nextInt(269)+119;
			FScript.execTask("click 130 " + y + " left false false false", getId()); //click day 
			FScript.sleep(2000);

			boolean created = false;
			while (!created){
				FScript.execTask("click 120 365 left false false false", getId()); //click name field
				FScript.sleep(1000);
				FScript.execTask("sendInput " + Config.nextName(), getId()); //enter the next name
				FScript.sleep(1000);
				FScript.execTask("click 354 459 left false false false", getId()); //click create button
				FScript.sleep(5000);
				
				//if confirm was found then name is invalid
				int[] coords = FScript.imageSearch("confirm.bmp", 5, getId());
				if (coords[0] == 0){
					FScript.execTask("click " + coords[1] + " " + coords[2] + " left false false false", getId()); //click confirm
					FScript.sleep(1000);
					
					FScript.execTask("click 120 365 left false false false", getId()); //click name field
					FScript.sleep(1000);
					
					FScript.execTask("sendInput {BS 100}", getId()); //delete the current name
				} else {
					created = true;
					incrementCharIndex();
				}
			}
			
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
	
}
