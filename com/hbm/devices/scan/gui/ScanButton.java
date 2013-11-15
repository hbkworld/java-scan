package com.hbm.devices.scan.gui;

import com.google.gson.Gson;
import com.hbm.devices.scan.IPv4ScanInterfaces;
import com.hbm.devices.scan.messages.Scan;
import com.hbm.devices.scan.MulticastSender;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JLabel;

public class ScanButton extends JButton implements ActionListener {
	public ScanButton(JLabel statusLabel) {
		super("Scan");

		this.statusLabel = statusLabel;

		try {
			IPv4ScanInterfaces ifs = new IPv4ScanInterfaces();
			mcss = new MulticastSender(ifs.getInterfaces());
		} catch (IOException e) {
			System.out.println("Could not create ScanButton!");
			System.out.println(e);
			System.exit(-1);
		}

		Gson gson = new Gson();
		Scan scan = new Scan(1);
		scanJson = gson.toJson(scan);
		
		addActionListener(this);
	}

	public void actionPerformed(ActionEvent event) {
		try {
			mcss.sendMessage(scanJson);
			statusLabel.setText("");
		} catch (IOException e) {
			statusLabel.setText("Could not perform scan!");
		}
	}

	private JLabel statusLabel;
	private String scanJson;
	private MulticastSender mcss;
}
