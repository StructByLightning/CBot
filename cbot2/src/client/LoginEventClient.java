package client;

import java.util.Calendar;
import java.util.Date;

import diagnosis.Diagnosis;
import network.Network;
import fScript.FScript;

public class LoginEventClient extends LoginClient {
	public static final int LOGIN_EVENT_DELAY = 30*60*1000; //minutes -> seconds -> milliseconds
	
	private Calendar cal;
	

	public LoginEventClient(int id){
		super(id, true, false, false);
		this.cal = Calendar.getInstance(); // creates calendar
		cal.setTime(new Date()); // sets calendar time/date
		cal.add(Calendar.HOUR_OF_DAY, 2); // add 2 hours because the server is 2 hrs ahead
	}

	//does henry quest + gets xp aid
	public void doIngameTask(){
		setFinishedIngame(true);
		FScript.execTask("sendInput {ESCAPE}", getId()); //exit out of a cutscene if it's up
		FScript.sleep(LOGIN_EVENT_DELAY);	
	}
}
