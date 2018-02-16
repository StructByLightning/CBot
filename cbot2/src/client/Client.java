package client;



import account.Account;
import clientHandler.AccHandler;
import clientHandler.ClientStates;
import diagnosis.Diagnosis;
import fScript.FScript;

public abstract class Client implements Runnable {
	private final int id;
	private String task;
	private String oldTask;
	private boolean running;
	private Account account;
	private boolean needsNewIp;
	private long timeSinceLastChange;
	private final boolean dontCloseClient;
	private boolean dced = false;
	private int dcCount = 0;
	
	public Client(int id, boolean dontCloseClient){
		this.id = id;
		this.running = true;
		this.needsNewIp = false;
		this.account = null;
		this.dontCloseClient = dontCloseClient;
	}
	

	
	public void run(){
		while (getRunning()){
			updateTask();
			completeTask();
		}
	}
	
	public boolean dontCloseClient(){
		return this.dontCloseClient;
	}
	
	//these need to be fully defined in the child classes
	public abstract void updateTask();
	public abstract void completeTask();
	
	
	public boolean getNeedsNewIp(){
		return needsNewIp;
	}
	
	public void setNeedsNewIp(boolean value){
		needsNewIp = value;
	}
	
	//returns true if oldTask and task are equal
	public boolean isTaskUnchanged(){
		if (task.equals(oldTask)){
			return true;
		}
		return false;
	}
	
	//exits a dced client by clicking confirm
	public void exitDcedClient(){
		FScript.log("DC on " + this.getAccount().getUsername(), "dced.txt");
		int[] coord = FScript.imageSearch("dcedClientConfirm.bmp", 25, getId());
		if (coord[0] == 0){
			FScript.execTask("click " + coord[1] + " " + coord[2] + " left false false false", getId()); //click exit
			//this.getNextAccount();
			FScript.sleep(10000);
			this.setDced(true);
			
			//prevents infinite loops if the username/password is wrong or the account is logged in or banned
			dcCount++;
			if (dcCount > 10){
				getNextAccount();
				dcCount = 0;
			}
		}
	}
	public void setDced(boolean value){
		this.dced = value;
	}
	
	public boolean getDced(){
		return this.dced;
	}
	
	//logs the client in if it's at the login screen
	public void login(){
		FScript.execTask("lock", getId());
		FScript.execTask("click 380 196 left false false false", getId());
		FScript.execTask("sendInput " + getAccount().getUsername() + "", getId());
		FScript.execTask("click 335 220 left false false false", getId());
		FScript.execTask("sendInput " + getAccount().getPassword() + "", getId());
		FScript.execTask("click 418 266 left false false false", id);
		FScript.execTask("unlock", getId());
		FScript.sleep(500);
	}
	
	public void getNextAccount(){
		if (account != null){
			System.out.println("Finished " + account.getUsername());
		}
		
		Account acc = AccHandler.nextAccount();
				
		setAccount(acc);
	}
	
	
	//accessor and mutator methods
	public String getTask(){return task;}
	public int getId(){return id;}
	public boolean getRunning(){return running;}
	public Account getAccount(){return account;}
	public String getOldTask(){return this.oldTask;}
	
	
	
	
	public void setTask(String task){
		this.oldTask = this.task;
		this.task = task;

		if (!task.equals("")){
			this.setTimeSinceLastChange(System.currentTimeMillis());
		}
	}
	public void setRunning(boolean running){this.running = running;}
	public void setAccount(Account account){this.account = account;}
	
	public void setTimeSinceLastChange(long time){this.timeSinceLastChange = time;}
	public long getTimeSinceLastChange(){return this.timeSinceLastChange;}
	
	
	
	
}
