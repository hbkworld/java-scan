package com.hbm.devices.scan.gui;

import com.hbm.devices.scan.gui.Device;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.JPanel;

class InfoPanel extends JPanel implements TreeSelectionListener {

	public void valueChanged(TreeSelectionEvent e) {
		Object o = e.getPath().getLastPathComponent();
		if (o instanceof Device) {
			Device device = (Device)o;
			removeAll();
			System.out.println("----------- And now into the Panel!");


			revalidate();
			repaint();
		}
	}
}
