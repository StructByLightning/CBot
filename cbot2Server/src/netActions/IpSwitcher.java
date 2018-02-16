package netActions;

import gui.TextPanel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;

import network.NetAction;

import org.apache.commons.codec.binary.Base64;

import server.ClientStates;
import accounts.AccountHandler;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import config.Config;

public class IpSwitcher{
	
	public static void switchIp(){
		if (AccountHandler.getNumAccsRemaining() != 0){
			//try to change the ip up to 6 times
			for (int attempts=0; attempts<6; attempts++){
				try {
					//get the current external ip by pinging a server and reading it's response
					URL whatismyip = new URL("http://checkip.amazonaws.com");
					BufferedReader in = new BufferedReader(new InputStreamReader( whatismyip.openStream()));
					String oldIp = in.readLine();
					
					//create the webclient
					final WebClient webClient = new WebClient(); 
				    
				    //adds an authentication that will be sent for every request by default (won't wait for the server to ask for it)
				    String authString = "admin" + ":" + "Tim1yh";
					byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
					String authStringEnc = new String(authEncBytes);
					webClient.addRequestHeader("Authorization", "Basic " + authStringEnc);
		
					//get the page
				    HtmlPage page = webClient.getPage("http://192.168.2.1/StaRouter.htm");
				    	
				    //locate and click dhcp release
				    HtmlElement dhcpRelease = page.getElementByName("dhcprelease");
				    dhcpRelease.click();
				    
				    //wait a bit (helps with getting a different ip address?)
				    Thread.sleep(1000); 
				    
				    //reload the page, locate dhcp renew, and click it
				    page = webClient.getPage("http://192.168.2.1/StaRouter.htm");
				    HtmlElement dhcpRenew = page.getElementByName("dhcprenew");
				    dhcpRenew.click();
		
				    //close the webclient
				    webClient.closeAllWindows();
				    
					for (int i=0; i<120; i++){
					    //get the current external ip by pinging a server and reading it's response
						String newIp = new BufferedReader(new InputStreamReader( whatismyip.openStream())).readLine();
						
						//return if the old ip and the new ip are different and the new ip isn't 0.0.0.0
						if ( (!newIp.equals(oldIp)) && (!newIp.equals("0.0.0.0")) ){
							return;
						}
						
						//delay so the server isn't spammed
						Thread.sleep(500);
					}
				    
				    
				} catch (Exception e){
					e.printStackTrace(System.out);
				}
			}
		}
	}

}
