package gui;

import input.SwitchIpCheckboxHandler;

import java.awt.GridBagConstraints;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class Checkbox extends JCheckBox{
	private int x;
	private int y;
	private int width;
	private int height;
	
	
	public Checkbox(String name, int x, int y, int width, int height){
		super(name);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void addToPanel(JPanel panel, GridBagConstraints c){
		c.weightx = .5;
		c.weighty = 0;
		c.gridwidth = width;
		c.gridheight = height;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = x;
		c.gridy = y;
		this.setSelected(true);
		panel.add(this, c);
		addItemListener(new SwitchIpCheckboxHandler());
	}
}
