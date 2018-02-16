package fScript;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.WINDOWINFO;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;

public class FScript {
	public static final int NUM_CLIENTS = 3;
	
	//has something to do with minimized windows, don't change this
	private static final int WS_ICONIC = 0x20000000;
	
	//the resolution of the screen
	public static final int SCREEN_RES_X = 1280;
	public static final int SCREEN_RES_Y = 1024;

	//the string used to identify fw windows
	private static final String FW_WINDOW_NAME = "Forsaken";
	
	//string used to identify crash notifications
	public static final String FW_CRASH_NOTIFICATION = "pem.exe";

	//the pixel range each fw client should be in if the client is in its id space(clients ordered: {0, 1, 2, 3})
	public static final Coordinate[] FW_MIN_COORDS = new Coordinate[] {
		new Coordinate(0.165625, 0.1471153846153846),
		new Coordinate(0.40859375, -0.0951923076923077),
		new Coordinate(-0.07734375, 0.3557692307692308),
		new Coordinate(0.40859375, 0.3557692307692308)};
	
	public static final Coordinate[] FW_MAX_COORDS = new Coordinate[] {
		new Coordinate(0.321875, 0.3394230769230769),
		new Coordinate(0.56484375, 0.09615384615384625),
		new Coordinate(0.078125, 0.5480769230769231),
		new Coordinate(0.56484375, 0.5480769230769231)};

	//the range to look for images in for each client
	public static final Coordinate[] imageSearchCoordinates = new Coordinate[] {
		new Coordinate(0.25, 0.2763671875),
		new Coordinate(0.49921875, 0),
		new Coordinate(0, 0.4912109375),
		new Coordinate(0.49921875, 0.4912109375)};

	//the coordinates to move new fw windows to
	public static final Coordinate[] clientCoordinates = new Coordinate[] {
		new Coordinate(0.24375, 0.2470703125),
		new Coordinate(0.49296875, -0.029296875),
		new Coordinate(-0.00546875, 0.4619140625),
		new Coordinate(0.49296875, 0.4619140625)};

	private static ReentrantLock lock;

	private static String PEM_FILEPATH;

	public static void execTask(String cmd, int id){
		lock();
		try{
			activateClient(id);
			//"click x y button shift control alt
			//"imageClick dX dY button image n
			//"sendInput text text text
			//"openClient
			//"moveClient source target
			//"lock
			//"unlock
			//"clickDrag x1 y1 x2 y2 button"
			//"scroll direction ticks"
			//"moveMouse x y"

			//split up the command string into its individual parts and store them in args
			cmd += " ";
			ArrayList<String> args = new ArrayList<String>();
			while (cmd.length() > 0){
				args.add(cmd.substring(0, cmd.indexOf(" ")));
				cmd = cmd.substring(cmd.indexOf(" ")+1);
			}

			//call the appropriate functions
			if (args.get(0).equals("click")){
				click(Integer.parseInt(args.get(1)), Integer.parseInt(args.get(2)), id, args.get(3), Boolean.valueOf(args.get(4)), Boolean.valueOf(args.get(5)), Boolean.valueOf(args.get(6)));
			} else if (args.get(0).equals("moveMouse")){
				moveMouse(Integer.parseInt(args.get(1)), Integer.parseInt(args.get(2)), id);
			} else if (args.get(0).equals("imageClick")){
				imageClick(Integer.parseInt(args.get(1)), Integer.parseInt(args.get(2)), id, args.get(3), args.get(4), Integer.parseInt(args.get(5)));
			} else if (args.get(0).equals("openClient")){
				openClient(id);
			} else if (args.get(0).equals("lock")){
				lock();
			} else if (args.get(0).equals("unlock")){
				unlock();
			} else if (args.get(0).equals("moveClient")){
				moveClient(Integer.parseInt(args.get(1)), Integer.parseInt(args.get(2)));
			} else if (args.get(0).equals("login")){
				loginToClient(id, args.get(1), args.get(2));
			} else if (args.get(0).equals("scroll")){
				scroll(args.get(1), Integer.parseInt(args.get(2)));
			} else if (args.get(0).equals("sendInput")){
				String text = "";
				for (int i=1; i<args.size(); i++){
					text += args.get(i);
					if (i != args.size()-1){
						text += " ";
					}
				}	
				sendInput(text);
			} else if (args.get(0).equals("clickDrag")){
				clickDrag(Integer.parseInt(args.get(1)), Integer.parseInt(args.get(2)), Integer.parseInt(args.get(3)), Integer.parseInt(args.get(4)), args.get(5), id);
			} else {
				System.out.println("Unrecognized command " + args.get(0));
			}


		} catch (Exception e){
			e.printStackTrace(System.out);
		} finally {
			try{
				unlock();
			} catch (Exception e){
				e.printStackTrace(System.out);
			}	
		}
	}
	
	//logs output to a file
	public static void log(String output, String filename){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();

	
		writeFile(new String[] {dateFormat.format(date) + " " + output}, filename);

		
	}
	
	//kills any minimized fw windows
	public static void killCrashNotifications(){
		final User32 user32 = User32.INSTANCE;
		user32.EnumWindows(new WNDENUMPROC() {
			public boolean callback(HWND hWnd, Pointer arg1) {
				char[] windowText = new char[512];
				user32.GetWindowText(hWnd, windowText, 512);
				String wText = new String(windowText);
				// RECT rectangle = new RECT();
				// user32.GetWindowRect(hWnd, rectangle);
				// get rid of this if block if you want all windows regardless
				// of whether
				// or not they have text
				// second condition is for visible and non minimised windows
				if (wText.isEmpty() || !(User32.INSTANCE.IsWindowVisible(hWnd))) {
					return true;
				}

				//check to see if the window is a fw window
				if (wText.indexOf(FW_CRASH_NOTIFICATION) >= 0){
					User32.INSTANCE.PostMessage(hWnd, WinUser.WM_QUIT, null, null);
				} else {

				}



				return true;
			}
		}, null);

	
	}
	
	//write to a file from a string array
	public static void writeFile (String[] text, String filename){
		WriteFile wf = new WriteFile(text, filename);
		wf.writeFile();
	}
	
	//starts an ahk script that will pause the bot if ctrl-alt-shift esc is pressed
	private static void pause(){
		try{
			//run the imageSearch.exe ahk script
			String[] cmdList = {"ahk/AutoHotKey.exe", "pause.ahk"};
			ProcessBuilder pb = new ProcessBuilder(cmdList);
			pb.directory(new File("ahk"));
			pb.start();

		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	
	//moves the mouse
	private static void moveMouse(int x, int y, int id){
		
		try{
			Robot r = new Robot();
			int dX = 0;
			int dY = 0;
			
			HWND client = getClient(id);
			final User32 user32 = User32.INSTANCE;
			RECT rect = new RECT();
			user32.GetWindowRect(client, rect);
			Rectangle rRect = rect.toRectangle();

			dX = (int)rRect.getX();
			dY = (int)rRect.getY();
			
			r.mouseMove(x+dX, y+dY);
		} catch (Exception e){
			e.printStackTrace(System.out);

		}
	}

	//scrolls the mousewheel the given number of ticks
	private static void scroll(String direction, int ticks){
		try{
			Robot r = new Robot();
			for (int i=0; i<ticks; i++){
				if (direction.equals("down")){
					r.mouseWheel(1);
				} else {
					r.mouseWheel(-1);
				}
				sleep(100);
			}
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	
	//click the coordinates
	private static void clickDrag(int x1, int y1, int x2, int y2, String button, int id){
		try{
			Robot r = new Robot();
			HWND client = getClient(id);

			final User32 user32 = User32.INSTANCE;
			RECT rect = new RECT();
			user32.GetWindowRect(client, rect);
			Rectangle rRect = rect.toRectangle();

			int dX = (int)rRect.getX();
			int dY = (int)rRect.getY();

			int mask;
			if (button.equals("left")){
				mask = InputEvent.BUTTON1_MASK;
			} else if (button.equals("right")){
				mask = InputEvent.BUTTON3_MASK;
			}  else if (button.equals("middle")){
				mask = InputEvent.BUTTON2_MASK;
			} else {
				mask = InputEvent.BUTTON3_MASK;
			}

			r.mouseMove(x1+dX, y1+dY);
			r.mousePress(mask);
			sleep(50);
			r.mouseMove(x2+dX, y2+dY);
			sleep(50);
			r.mouseRelease(mask);

		} catch (Exception e){
			e.printStackTrace(System.out);

		}
	}
	
	//force kills all fw (pem.exe) processes
	public static synchronized void killAllFw(){
		try {
			Runtime.getRuntime().exec("taskkill /F /IM pem.exe");
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}

	}
		
	//kills any minimized fw windows
	public static void killMinimizedFwWindows(){

		//get handles for all fw windows
		ArrayList<HWND> windows = getFWHWnds();

		//user32 instance for JNA
		final User32 user32 = User32.INSTANCE;

		//loop through the windows
		for (int i=0; i<windows.size(); i++){

			//check if the window is minimized
			WINDOWINFO info = new WINDOWINFO();
			user32.GetWindowInfo(windows.get(i), info);


			//close window  if it is minimized
			if ((info.dwStyle & WS_ICONIC) == WS_ICONIC) {
				User32.INSTANCE.PostMessage(windows.get(i), WinUser.WM_QUIT, null, null);
			}
		}
	}

	private static void loginToClient(int id, String acc, String pass){
		if (getClient(id) == null){
			openClient(id);
		}

		click(378, 200, id, "left", false, false, false);
		sendInput(acc);

		click(356, 219, id, "left", false, false, false);
		sendInput(pass);

		click(418, 269, id, "left", false, false, false);
		sleep(5000);
		click(118, 96, id, "left", false, false, false);
		click(333, 479, id, "left", false, false, false);
	}

	//turns on a lock
	private static void lock(){
		lock.lock();
	}

	//turns off a lock
	private static void unlock(){
		lock.unlock();
	}

	//clicks the mouse at the coordinates relative to id
	private static synchronized void click(int x, int y, int id, String button, boolean shift, boolean control, boolean alt){
		if ((x<0) || (y<0)){ //don't allow clicks outside the client
			return;
		}
		
		try{
			Robot r = new Robot();

			//convert window relative coordinates to screen coordinates
			HWND client = getClient(id);
			final User32 user32 = User32.INSTANCE;
			RECT rect = new RECT();
			user32.GetWindowRect(client, rect);
			Rectangle rRect = rect.toRectangle();

			int dX = (int)rRect.getX();
			int dY = (int)rRect.getY();

			//find the button to press
			int mask;
			if (button.equals("left")){
				mask = InputEvent.BUTTON1_MASK;
			} else if (button.equals("right")){
				mask = InputEvent.BUTTON3_MASK;
			}  else if (button.equals("middle")){
				mask = InputEvent.BUTTON2_MASK;
			} else {
				r.mouseMove(x+dX, y+dY);
				return;
			}


			//press modifier keys
			if (shift){
				r.keyPress(KeyEvent.VK_SHIFT);
			} 
			if (control){
				r.keyPress(KeyEvent.VK_CONTROL);
			} 
			if (alt){
				r.keyPress(KeyEvent.VK_ALT);
			} 

			//move and click
			r.mouseMove(x+dX, y+dY);
			r.mousePress(mask);
			sleep(100);
			r.mouseRelease(mask);

			//release modifier keys
			if (shift){
				r.keyRelease(KeyEvent.VK_SHIFT);
			} 
			if (control){
				r.keyRelease(KeyEvent.VK_CONTROL);
			} 
			if (alt){
				r.keyRelease(KeyEvent.VK_ALT);
			} 


		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

	//locates and clicks an image, returns true if the image was found
	private static synchronized boolean imageClick(int dX, int dY, int id, String button, String image, int n){
		int[] coords = imageSearch(image, n, id);
		if (coords[0] == 0){
			click(coords[1]+dX, coords[2]+dY, id, button, false, false, false);
			return true;
		}
		return false;
	}

	//sends text keypresses
	private static void sendInput(String text){
		try {
			Robot r = new Robot();
			//check for special keys
			if (text.equals("{ESCAPE}")){
				r.keyPress(KeyEvent.VK_ESCAPE);
				sleep(500);
				r.keyRelease(KeyEvent.VK_ESCAPE);
			} else if (text.equals("{ENTER}")){
				r.keyPress(KeyEvent.VK_ENTER);
				sleep(500);
				r.keyRelease(KeyEvent.VK_ENTER);
			} else if (text.equals("{BS 100}")){
				for (int i=0; i<100; i++){
					r.keyPress(KeyEvent.VK_BACK_SPACE);
					sleep(50);
					r.keyRelease(KeyEvent.VK_BACK_SPACE);
				}
			} else if (text.equals("{OPENMAP}")){
				r.keyPress(KeyEvent.VK_M);
				sleep(500);
				r.keyRelease(KeyEvent.VK_M);
			} else if (text.equals("{ALTK}")){
				r.keyPress(KeyEvent.VK_ALT);
				sleep(500);
				r.keyPress(KeyEvent.VK_K);
				sleep(500);
				r.keyRelease(KeyEvent.VK_K);
				sleep(500);
				r.keyRelease(KeyEvent.VK_ALT);

			} else if (text.equals("{ACTIVATEBOT}")){
				r.keyPress(KeyEvent.VK_SHIFT);
				sleep(100);
				r.keyPress(KeyEvent.VK_Z);
				sleep(100);
				r.keyRelease(KeyEvent.VK_Z);
				r.keyRelease(KeyEvent.VK_SHIFT);

			} else {
				char[] chars = text.toCharArray();

				for (int c=0; c<chars.length; c++){
					switch (chars[c]){
					case 'a' : sendKey(r, KeyEvent.VK_A, false); break;
					case 'b' : sendKey(r, KeyEvent.VK_B, false); break;
					case 'c' : sendKey(r, KeyEvent.VK_C, false); break;
					case 'd' : sendKey(r, KeyEvent.VK_D, false); break;
					case 'e' : sendKey(r, KeyEvent.VK_E, false); break;
					case 'f' : sendKey(r, KeyEvent.VK_F, false); break;
					case 'g' : sendKey(r, KeyEvent.VK_G, false); break;
					case 'h' : sendKey(r, KeyEvent.VK_H, false); break;
					case 'i' : sendKey(r, KeyEvent.VK_I, false); break;
					case 'j' : sendKey(r, KeyEvent.VK_J, false); break;
					case 'k' : sendKey(r, KeyEvent.VK_K, false); break;
					case 'l' : sendKey(r, KeyEvent.VK_L, false); break;
					case 'm' : sendKey(r, KeyEvent.VK_M, false); break;
					case 'n' : sendKey(r, KeyEvent.VK_N, false); break;
					case 'o' : sendKey(r, KeyEvent.VK_O, false); break;
					case 'p' : sendKey(r, KeyEvent.VK_P, false); break;
					case 'q' : sendKey(r, KeyEvent.VK_Q, false); break;
					case 'r' : sendKey(r, KeyEvent.VK_R, false); break;
					case 's' : sendKey(r, KeyEvent.VK_S, false); break;
					case 't' : sendKey(r, KeyEvent.VK_T, false); break;
					case 'u' : sendKey(r, KeyEvent.VK_U, false); break;
					case 'v' : sendKey(r, KeyEvent.VK_V, false); break;
					case 'w' : sendKey(r, KeyEvent.VK_W, false); break;
					case 'x' : sendKey(r, KeyEvent.VK_X, false); break;
					case 'y' : sendKey(r, KeyEvent.VK_Y, false); break;
					case 'z' : sendKey(r, KeyEvent.VK_Z, false); break;
					case 'A' : sendKey(r, KeyEvent.VK_A, true); break;
					case 'B' : sendKey(r, KeyEvent.VK_B, true); break;
					case 'C' : sendKey(r, KeyEvent.VK_C, true); break;
					case 'D' : sendKey(r, KeyEvent.VK_D, true); break;
					case 'E' : sendKey(r, KeyEvent.VK_E, true); break;
					case 'F' : sendKey(r, KeyEvent.VK_F, true); break;
					case 'G' : sendKey(r, KeyEvent.VK_G, true); break;
					case 'H' : sendKey(r, KeyEvent.VK_H, true); break;
					case 'I' : sendKey(r, KeyEvent.VK_I, true); break;
					case 'J' : sendKey(r, KeyEvent.VK_J, true); break;
					case 'K' : sendKey(r, KeyEvent.VK_K, true); break;
					case 'L' : sendKey(r, KeyEvent.VK_L, true); break;
					case 'M' : sendKey(r, KeyEvent.VK_M, true); break;
					case 'N' : sendKey(r, KeyEvent.VK_N, true); break;
					case 'O' : sendKey(r, KeyEvent.VK_O, true); break;
					case 'P' : sendKey(r, KeyEvent.VK_P, true); break;
					case 'Q' : sendKey(r, KeyEvent.VK_Q, true); break;
					case 'R' : sendKey(r, KeyEvent.VK_R, true); break;
					case 'S' : sendKey(r, KeyEvent.VK_S, true); break;
					case 'T' : sendKey(r, KeyEvent.VK_T, true); break;
					case 'U' : sendKey(r, KeyEvent.VK_U, true); break;
					case 'V' : sendKey(r, KeyEvent.VK_V, true); break;
					case 'W' : sendKey(r, KeyEvent.VK_W, true); break;
					case 'X' : sendKey(r, KeyEvent.VK_X, true); break;
					case 'Y' : sendKey(r, KeyEvent.VK_Y, true); break;
					case 'Z' : sendKey(r, KeyEvent.VK_Z, true); break;
					case '1' : sendKey(r, KeyEvent.VK_1, false); break;
					case '2' : sendKey(r, KeyEvent.VK_2, false); break;
					case '3' : sendKey(r, KeyEvent.VK_3, false); break;
					case '4' : sendKey(r, KeyEvent.VK_4, false); break;
					case '5' : sendKey(r, KeyEvent.VK_5, false); break;
					case '6' : sendKey(r, KeyEvent.VK_6, false); break;
					case '7' : sendKey(r, KeyEvent.VK_7, false); break;
					case '8' : sendKey(r, KeyEvent.VK_8, false); break;
					case '9' : sendKey(r, KeyEvent.VK_9, false); break;
					case '0' : sendKey(r, KeyEvent.VK_0, false); break;
					case '-' : sendKey(r, KeyEvent.VK_MINUS, false); break;
					case '=' : sendKey(r, KeyEvent.VK_EQUALS, false); break;
					case '`' : sendKey(r, KeyEvent.VK_BACK_QUOTE, false); break;
					case '~' : sendKey(r, KeyEvent.VK_BACK_QUOTE, true); break;
					case '!' : sendKey(r, KeyEvent.VK_1, true); break;
					case '@' : sendKey(r, KeyEvent.VK_2, true); break;
					case '#' : sendKey(r, KeyEvent.VK_3, true); break;
					case '$' : sendKey(r, KeyEvent.VK_4, true); break;
					case '%' : sendKey(r, KeyEvent.VK_5, true); break;
					case '^' : sendKey(r, KeyEvent.VK_6, true); break;
					case '&' : sendKey(r, KeyEvent.VK_7, true); break;
					case '*' : sendKey(r, KeyEvent.VK_8, true); break;
					case '(' : sendKey(r, KeyEvent.VK_9, true); break;
					case ')' : sendKey(r, KeyEvent.VK_0, true); break;
					case '_' : sendKey(r, KeyEvent.VK_MINUS, true); break;
					case '+' : sendKey(r, KeyEvent.VK_EQUALS, true); break;
					case '\t' : sendKey(r, KeyEvent.VK_TAB, false); break;
					case '\n' : sendKey(r, KeyEvent.VK_ENTER, false); break;
					case '[' : sendKey(r, KeyEvent.VK_OPEN_BRACKET, false); break;
					case ']' : sendKey(r, KeyEvent.VK_CLOSE_BRACKET, false); break;
					case '\\' : sendKey(r, KeyEvent.VK_BACK_SLASH, false); break;
					case '{' : sendKey(r, KeyEvent.VK_OPEN_BRACKET, true); break;
					case '}' : sendKey(r, KeyEvent.VK_CLOSE_BRACKET, true); break;
					case '|' : sendKey(r, KeyEvent.VK_BACK_SLASH, true); break;
					case ';' : sendKey(r, KeyEvent.VK_SEMICOLON, false); break;
					case ':' : sendKey(r, KeyEvent.VK_SEMICOLON, true); break;
					case '\'' : sendKey(r, KeyEvent.VK_QUOTE, false); break;
					case '"' : sendKey(r, KeyEvent.VK_QUOTE, true); break;
					case ',' : sendKey(r, KeyEvent.VK_COMMA, false); break;
					case '<' : sendKey(r, KeyEvent.VK_COMMA, true); break;
					case '.' : sendKey(r, KeyEvent.VK_PERIOD, false); break;
					case '>' : sendKey(r, KeyEvent.VK_PERIOD, true); break;
					case '/' : sendKey(r, KeyEvent.VK_SLASH, false); break;
					case '?' : sendKey(r, KeyEvent.VK_SLASH, true); break;
					case ' ' : sendKey(r, KeyEvent.VK_SPACE, false); break;
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}

	//used by sendInput, sends a single character
	private static void sendKey(Robot robot, int key, boolean shift){
		if (shift){
			robot.keyPress(KeyEvent.VK_SHIFT);
		}
		robot.keyPress(key);
		robot.keyRelease(key);
		if (shift){
			robot.keyRelease(KeyEvent.VK_SHIFT);
		}
		sleep(50);
	}

	//opens a new FW client
	private synchronized static void openClient(int id){
		try {
			//check that there isn't a client at id and that there aren't 3+ fw windows open already
			if (clientExists(id) || (getFWHWnds().size() >= 3)){
				return;
			}

			//verify that there is no opened client, then open a new one if there isn't
			if (!clientExists(0)){
				String[] cmdList = {PEM_FILEPATH + "\\pem.exe"};
				ProcessBuilder pb = new ProcessBuilder(cmdList);
				pb.directory(new File(PEM_FILEPATH));
				pb.start();
			} else { //otherwise activate the previously opened client
				activateClient(0);
			}

			//wait up to two minutes for the client to load, then move it
			boolean found = false;
			int i = 0;
			while ((!found) && (i<120)){
				sleep(500);
				//attempt to activate the client 
				activateClient(0);

				//see if the client has loaded
				if ((imageSearch("client_loginScreen.bmp", 5, 0)[0] == 0) && clientExists(0)){ //check the 0 position to see if it loaded
					found = true;
					
					//move the client
					moveClient(0, id);

				} else if (imageSearch("client_loginScreen.bmp", 5, id)[0] == 0) { //also check the position the client is going to be put to (fixes an infinite loop bug)
					found = true;
				} else if (imageSearch("clientFailed.bmp", 5, 0)[0] == 0){
					System.out.println("Killed fourth client");
					execTask("click 427 161 left;", 0);
					sleep(5000);
				}
				i++;
			}
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
		return;


	}

	//attempts to move sourceId client to targetId space, returns true if sourceId client was successfully moved
	private static boolean moveClient(int sourceId, int targetId){
		//retrieve the handle for the client sourceId
		HWND client = getClient(sourceId);

		//if the target space is already occupied don't move the client, unless the client is being moved to the same position (used to fix manually moved clients)
		if ((client == null) && (sourceId != targetId)){
			return false;
		}

		//move the client without changing its size
		final User32 user32 = User32.INSTANCE;
		RECT rect = new RECT();
		user32.GetWindowRect(client, rect);
		Rectangle rRect = rect.toRectangle();


		user32.MoveWindow(client, clientCoordinates[targetId].x(), clientCoordinates[targetId].y(), (int)rRect.getWidth(), (int)rRect.getHeight(), true);

		return true;

	}

	//active a client, returns true if the client was activated
	private static boolean activateClient(int id){
		try{
			//attempt to activate the window programmatically first
			final User32 user32 = User32.INSTANCE;
			HWND client = getClient(id);
			if (user32.GetForegroundWindow() != client){
				user32.SetForegroundWindow(client);
				while ((user32.GetForegroundWindow() == null)){
					sleep(25);//DOES THIS CAUSE AN INFINITE LOOP IN COMBINATION WITH OPEN()?
				}
			}

			//return true if the window was activated
			if (user32.GetForegroundWindow().equals(client)){
				return true;
			}

			//return true if the window was activated
			if (user32.GetForegroundWindow().equals(client)){
				return true;
			}

			//otherwise return false
			return false;

		} catch (Exception e){
			e.printStackTrace(System.out);
		}
		return false;
	}

	//returns the hwnd of the client occupying the space given by id
	public static HWND getClient(int id){

		ArrayList<HWND> hWnds = getFWHWnds();

		final User32 user32 = User32.INSTANCE;

		//loop over all fw hwnds and check each one to see if it is at the correct position for the id
		for (int i=0; i<hWnds.size(); i++){
			RECT rect = new RECT();
			user32.GetWindowRect(hWnds.get(i), rect);
			Rectangle rRect = rect.toRectangle();

			if ((rRect.getX() >= FW_MIN_COORDS[id].x()) && (rRect.getX() <= FW_MAX_COORDS[id].x())){ //check x coord
				if ((rRect.getY() >= FW_MIN_COORDS[id].y()) && (rRect.getY() <= FW_MAX_COORDS[id].y())){ //check y coord
					return hWnds.get(i);
				}	
			}
		}
		return null;
	}

	//retrieve the hWnds of all FW windows
	public synchronized static ArrayList<HWND> getFWHWnds(){
		FScriptConfigs.hWnds.clear();

		final User32 user32 = User32.INSTANCE;
		user32.EnumWindows(new WNDENUMPROC() {
			public boolean callback(HWND hWnd, Pointer arg1) {
				char[] windowText = new char[512];
				user32.GetWindowText(hWnd, windowText, 512);
				String wText = new String(windowText);
				// RECT rectangle = new RECT();
				// user32.GetWindowRect(hWnd, rectangle);
				// get rid of this if block if you want all windows regardless
				// of whether
				// or not they have text
				// second condition is for visible and non minimised windows
				if (wText.isEmpty() || !(User32.INSTANCE.IsWindowVisible(hWnd))) {
					return true;
				}

				//check to see if the window is a fw window
				if (wText.indexOf(FW_WINDOW_NAME) >= 0){
					FScriptConfigs.hWnds.add(hWnd); 
				} else {

				}



				return true;
			}
		}, null);

		ArrayList<HWND> hWndsCopy = new ArrayList<HWND>();

		for (int i=0; i<FScriptConfigs.hWnds.size(); i++){
			hWndsCopy.add(FScriptConfigs.hWnds.get(i));
		}

		return hWndsCopy;
	}

	//returns the number of FW windows
	public synchronized static int getNumFwWindows(){
		return getFWHWnds().size();
	}
	
	//sleep for X ms
	public static void sleep (int ms){
		try{
			Thread.sleep(ms);
		} catch (InterruptedException e){
			e.printStackTrace(System.out);
		}
	}

	//search for a given image with coordinates based on id (-1 = global, 0 = middle, 1 = top right, 2 = bottom left, 3 = bottom right)
	public static int[] imageSearch(String filepath, int n, int id){

		int[] results = {-1, -1, -1};

		try{
			int x1 = 0;
			int y1 = 0;
			int x2 = 0;
			int y2 = 0;

			if (id == -1){
				x1 = y1 = 0;
				x2 = SCREEN_RES_X;
				y2 = SCREEN_RES_X;
			} else if (id == 0){
				x1 = imageSearchCoordinates[id].x(); 
				y1 = imageSearchCoordinates[id].y();
				x2 = imageSearchCoordinates[id].x() + 639;
				y2 = imageSearchCoordinates[id].y() + 479;
			} else if (id == 1){
				x1 = imageSearchCoordinates[id].x(); 
				y1 = imageSearchCoordinates[id].y();
				x2 = imageSearchCoordinates[id].x() + 640;
				y2 = imageSearchCoordinates[id].y() + 472;
			} else if (id == 2){
				x1 = imageSearchCoordinates[id].x(); 
				y1 = imageSearchCoordinates[id].y();
				x2 = imageSearchCoordinates[id].x() + 630;
				y2 = imageSearchCoordinates[id].y() + 479;
			} else if (id == 3){
				x1 = imageSearchCoordinates[id].x(); 
				y1 = imageSearchCoordinates[id].y();
				x2 = imageSearchCoordinates[id].x() + 640;
				y2 = imageSearchCoordinates[id].y() + 479;
			}
			//run the imageSearch.exe ahk script
			String[] cmdList = {"ahk/AutoHotKey.exe", "imageSearch.ahk", ""+x1, ""+y1, ""+x2, ""+y2, ""+n, filepath};
			ProcessBuilder pb = new ProcessBuilder(cmdList);
			pb.directory(new File("ahk"));
			Process p = pb.start();

			//wait for the script to finish
			p.waitFor();

			int exitCode = p.exitValue();


			results[2] = exitCode%10000;
			exitCode /= 10000;
			results[1] = exitCode%10000;
			exitCode /= 10000;
			results[0] = exitCode-1;


			if (results[0] == 2){
				System.out.println(filepath);
			}


			//offsets are to compensate for window borders (30p top, 8p sides as well as a 39p bottom bar)
			results[1] -= imageSearchCoordinates[id].x()-8;
			results[2] -= imageSearchCoordinates[id].y();

			
			results[2] += 30;
			

		} catch (Exception e){
			e.printStackTrace(System.out);
		}


		return results;
	}

	//waits for an image to appear, checking each delay milliseconds and waiting a maximum of max milliseconds
	public static int[] waitForImage(String filepath, int n, int id, int delay, int max){

		Timer timer = new Timer();
		while(timer.elapsed < max){
			timer.update();
			int[] results = imageSearch(filepath, n, id);
			if (results[0] == 0){
				return results;
			}
			results = null;
			sleep(delay);
		}

		int[] results = {-1, -1, -1};
		return results;
	}

	public static void initialize(String filepath){
		PEM_FILEPATH = filepath;
		standardizeClients();
		lock = new ReentrantLock();
		pause();
	}
	
	//moves each existing client to its proper location (usually adjusts for human error in setting up harvest clients)
	public static synchronized void standardizeClients(){
		for (int i=0; i<=NUM_CLIENTS; i++){
			HWND handle = getClient(i);
			if (handle != null){
				moveClient(i, i);
			}
		}
	}

	//returns true if there is a client at id
	public static boolean clientExists(int id){
		HWND handle = getClient(id);
		if (handle == null){
			return false;
		} 
		return true;
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------
	//Functions that use a window handle instead of an id------------------------------------------------------------------------------------------------------
	//---------------------------------------------------------------------------------------------------------------------------------------------------------


}

