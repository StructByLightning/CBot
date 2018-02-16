package config;

public class Config {
	public static final String LOAD_ACCS_NORMALLY = "Load accounts normally";
	public static final String LOAD_ACCS_FROM_FILE = "Load accounts from file";
	public static final String LOAD_ACCS_WITH_MAIL = "Load accounts with mail";
	
	
	private static int numAccounts = 10;
	private static boolean switchIp = true;
	private static boolean startedBot = false;
	private static boolean expressMode = false;
	private static String loadAccsType = Config.LOAD_ACCS_NORMALLY; //don't set this to "normal"...
	
	public static void setNumAccounts(int value){numAccounts = value;}
	public static int getNumAccounts(){return numAccounts;}
	
	public static void setSwitchIp(boolean value){switchIp = value;}
	public static boolean getSwitchIp(){return switchIp;}
	
	public static synchronized boolean startedBot(){ return startedBot;}
	public static synchronized void start(){startedBot = true;}
	
	public static void setExpressMode(boolean value){expressMode = value;}
	public static boolean getExpressMode(){return expressMode;}

	public static void setLoadAccsType(String value){loadAccsType = value;}
	public static String getLoadAccsType(){return loadAccsType;}
	
	
}
