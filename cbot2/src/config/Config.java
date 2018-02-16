package config;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import fScript.FScript;

public class Config {
	private static String PEM_FILEPATH;
	private static String LINKER_NAME;
	private static boolean DONT_KILL_CLIENTS = false;
	private static boolean bagIsFull = false;
	private static boolean expressMode = false;
	private static ArrayList<String> names = new ArrayList<String>();
	
	public static void initialize(){
		try{
			Scanner s = new Scanner(new File("names.txt"));
			while (s.hasNextLine()){
				names.add(s.nextLine());
			}
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	
	//get a random name, log it, remove it, and return it
	public static synchronized String nextName(){
		Random r = new Random(System.currentTimeMillis());
		int index = r.nextInt(names.size());
		
		String name = names.get(index);
		FScript.log(name, "usedNames.txt");
		names.remove(index);
		return name;
		
	}
	
	
	public static boolean getBagIsFull(){
		return bagIsFull;
	}
	
	public static void setBagIsFull(boolean value){
		bagIsFull = value;
	}
	
	
	public static void setDontKillClients(boolean value){
		DONT_KILL_CLIENTS = value;
	}

	public static boolean getDontKillClients(){
		return DONT_KILL_CLIENTS;
	}
	
	public static void setFilepath(String path){
		PEM_FILEPATH = path;
	}
	public static String getFilepath(){
		return PEM_FILEPATH;
	}
	
	public static void setLinkerName(String name){
		LINKER_NAME = name;
	}
	public static String getLinkerName(){
		return LINKER_NAME;
	}


	public static void setExpressMode(boolean value){expressMode = value;}
	public static boolean getExpressMode(){return expressMode;}
	
	
}
