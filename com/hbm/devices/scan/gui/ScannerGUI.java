package com.hbm.devices.scan.gui;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

public class ScannerGUI extends JFrame {

	public ScannerGUI() {
		super("Java-Scanner");

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
				prefs.putInt(DEFAULT_XSIZE_NAME, getWidth());
				prefs.putInt(DEFAULT_YSIZE_NAME, getHeight());
            }
        });

		addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
				prefs.putInt(DEFAULT_X_NAME, (int)getLocation().getX());
				prefs.putInt(DEFAULT_Y_NAME, (int)getLocation().getY());
            }
        });

		int xSize = prefs.getInt(DEFAULT_XSIZE_NAME, defaultXSize);
		int ySize = prefs.getInt(DEFAULT_YSIZE_NAME, defaultYSize);

		setSize(xSize, ySize);

		int x = prefs.getInt(DEFAULT_X_NAME, defaultX);
		int y = prefs.getInt(DEFAULT_Y_NAME, defaultY);
		setLocation(x, y);

		StatusLabel statusLabel = new StatusLabel();
		add(statusLabel, BorderLayout.PAGE_END);

		add(new ScanButton(statusLabel), BorderLayout.PAGE_START);

		InfoPanel panel = new InfoPanel();
		add(panel, BorderLayout.CENTER);

		ScanTreeModel model = new ScanTreeModel();

		Device d1 = new Device("QuantumX", "1", "Horst");
		model.update(null, d1);
		Device d2 = new Device("QuantumX", "2", "Fred");
		model.update(null, d2);
		Device d3 = new Device("PMX", "1", "Tim");
		model.update(null, d3);

		JTree tree = new JTree(model);
		tree.addTreeSelectionListener(panel);
		tree.getSelectionModel().setSelectionMode
		        (TreeSelectionModel.SINGLE_TREE_SELECTION);
		panel.add(tree);
	}

	public static void main(String args[]) {
		ScannerGUI s = new ScannerGUI();
		s.setVisible(true);
	}

	private static final Preferences prefs = Preferences.userNodeForPackage(com.hbm.devices.scan.gui.ScannerGUI.class);

	private static final int defaultXSize = 450;
	private static final String DEFAULT_XSIZE_NAME = "DefaultXSize";
	private static final int defaultYSize = 250;
	private static final String DEFAULT_YSIZE_NAME = "DefaultYSize";

	private static final int defaultX = 0;
	private static final String DEFAULT_X_NAME = "DefaultX";
	private static final int defaultY = 0;
	private static final String DEFAULT_Y_NAME = "DefaultY";
}
