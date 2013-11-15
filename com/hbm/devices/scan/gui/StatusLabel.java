package com.hbm.devices.scan.gui;

import javax.swing.border.EtchedBorder;
import javax.swing.JLabel;

public class StatusLabel extends JLabel {

	public StatusLabel() {
		super();
		this.setBorder(new EtchedBorder());
	}

	public void setText(String text) {
		super.setText(prefix + text);
	}
	
	private static final String prefix = "Status: ";
}
