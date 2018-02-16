package cbot2;

import java.util.ArrayList;

import client.Client;

import com.sun.jna.platform.win32.WinDef.HWND;

public class Global {
	public static String PEM_FILEPATH;
	public static String INSTANCE_ID;
	public static String LINKER_ACCOUNT;
	public static String LINKER_NAME;
	public static boolean dontKillClients = false;
	
	public static ArrayList<Client> clients = new ArrayList<Client>();
	public static ArrayList<HWND> hWnds = new ArrayList<HWND>();
	
}
