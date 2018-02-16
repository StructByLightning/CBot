package diagnosis;

import java.io.File;

import config.Config;
import client.LoginClient;
import fScript.FScript;

//tracks the state of the character
public class Diagnosis {
	private boolean hasMail = false;
	private boolean resetDays = false;
	private boolean incDays = true;
	private boolean hasOrbs = false;
	private String[] orbs = {"polystoneOrb", "hadesOrb", "huntsmanOrb", "leviathanOrb", "phantomOrb", "seashadeOrb", "solarOrb", "pandaOrb", "newYearOrb", "ichibanOrb", "clockworkOrb"};
	private LoginClient owner;
	private int numSlotsUsed = 0;
	private int numSlotsUnlocked = 0;
	
	public Diagnosis(boolean resetDays, boolean incDays, LoginClient owner){
		this.resetDays = resetDays;
		this.incDays = incDays;
		this.owner = owner;
	}
	
	
	public void setMail(boolean value){
		this.hasMail = value;
	}
	
	//update the state
	public void update(int id){
		if (!Config.getExpressMode()){ //don't waste time finding all this data if the server is set to express mode
			reset();
			
			//look to see if the mail icon is blinking
			if ((FScript.imageSearch("mailIconLit.bmp", 5, id)[0] == 0) || (FScript.imageSearch("mailIconUnlit.bmp", 5, id)[0] == 0)){
				hasMail = true;
			} 
			
			//open the bag and look for orbs
			owner.getBag().open();
			hasOrbs = false;
			for (int i=0; i<orbs.length; i++){
				if ((FScript.imageSearch(orbs[i]+".bmp", 5, id)[0] == 0)){
					hasOrbs = true;
					break;
				}
			}	
			
			//get the bag slots directly from memory
			//WARNING: VERY SLOW (5s+)
			getBagSlots();
			owner.getBag().close();			
		}
	}
	
	private void getBagSlots(){
		try {
			//run the imageSearch.exe ahk script
			String[] cmdList = {"ahk/getBagSlots.exe", ""+FScript.FW_MIN_COORDS[owner.getId()].x(), ""+FScript.FW_MIN_COORDS[owner.getId()].y(), ""+FScript.FW_MAX_COORDS[owner.getId()].x(), ""+FScript.FW_MAX_COORDS[owner.getId()].y()};
			ProcessBuilder pb = new ProcessBuilder(cmdList);
			pb.directory(new File("ahk"));
			Process p = pb.start();
	
			//wait for the script to finish
			p.waitFor();
	
			int exitCode = p.exitValue();
			
			this.numSlotsUnlocked = exitCode%100;
			exitCode /= 100;
			this.numSlotsUsed = exitCode;
			
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
		
	}
	
	public void reset(){
		this.hasMail = false;
	}
	
	public String toString(){
		return "hasMail=" + hasMail +
				" incDays=" + incDays + 
				" resetDays=" + resetDays+
				" hasOrbs=" + hasOrbs +
				" numBagUsed=" + numSlotsUsed +
				" numBagUnlocked=" + numSlotsUnlocked;
	}
}
