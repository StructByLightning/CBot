package gui;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import server.BotServer;
import accounts.AccountHandler;
import database.Database;

public class ServerController extends JFrame{
	private static final long serialVersionUID = 7401709081950405058L;
	private MainTabPanel mtPanel;
	private SettingsTabPanel stPanel;
	private ClientTabPanel ctPanel;
	private AccsTabPanel atPanel;
	
	public ServerController(){
		super("Bot Server");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(225, 225);
		this.setVisible(true);
		this.setResizable(false);
	}

	public static void main(String[] args){
		new ServerController().run();
	}

	public void run(){
		initializeGUI(this.getContentPane());
		Database.initialize();
		AccountHandler.initialize(this.getOutputPanel());

		new Thread(new BotServer(this)).start();

	}

	private void initializeGUI(Container panel){
		mtPanel = new MainTabPanel(this);
		stPanel = new SettingsTabPanel(this);
		ctPanel = new ClientTabPanel(this);
		atPanel = new AccsTabPanel(this);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		tabbedPane.addTab("Main", null, mtPanel);
		tabbedPane.addTab("Clients", null, ctPanel);
		tabbedPane.addTab("Accounts", null, atPanel);
		tabbedPane.addTab("Settings", null, stPanel);
					
		this.add(tabbedPane);
		//this.add(new MainTabPanel(this));
		
		this.pack();

	}
	
	public TextPanel getOutputPanel(){
		return mtPanel.getOpPanel().getTextPanel();
	}

}
