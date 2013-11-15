package com.hbm.devices.scan.gui;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

class Device {
	public Device(String f, String u, String n) {
		familyType = f;
		uuid = u;
		name = n;
	}

	public String getFamilyType() {
		return familyType;
	}

	public String getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return name + " (" + uuid + ")";
	}

	private String familyType;
	private String uuid;
	private String name;
}

class MapTreeNode {
	
	MapTreeNode(String n) {
		name = n;
		map = new TreeMap<String, Object>();
	}

	public String toString() {
		return name;
	}
	
	void clear() {
		map.clear();
	}

	Object get(String key) {
		return map.get(key);
	}
	
	Object put(String key, Object value) {
		return map.put(key, value);
	}

	int size() {
		return map.size();
	}

	Object valueAt(int index) {
		Iterator<String> it = map.navigableKeySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			if (i == index) {
				return map.get(it.next());
			}
			it.next();
			i++;
		}
		return null;
	}

	private TreeMap<String, Object> map;
	private String name;
}

public class ScanTreeModel implements TreeModel {

	public ScanTreeModel() {
		root = new MapTreeNode("HBM devices");
		listeners = new LinkedList<TreeModelListener>();
	}

	public synchronized void clear() {
		root.clear();
	}

	public synchronized void update(Observable o, Object arg) {
		/*
		if (arg instanceof Announce) {
			Announce a = (Announce)arg;
			addOrUpdate(a);
		} else if (arg instanceof Leave) {
			Leave l = (Leave)arg;
			delete(l);
		}
		*/
		Device d = (Device)arg;
		addOrUpdate(d);
	}

	public void addOrUpdate(Device d) {
		MapTreeNode devicesInFamily = 
			getFamilyMap(d);

		Object old = devicesInFamily.put(d.getUuid(), d);
		if (old == null) {
			// fire new element event with SwingUtilities.invokeLater
		} else {
			// fire element changed event with SwingUtilities.invokeLater
		}
	}

	public Object getRoot() {
		return root;
	}

	public synchronized boolean isLeaf(Object node) {
		System.out.println("isLeaf of node " + node);
		if (node instanceof MapTreeNode) {
			return false;
		} else {
			return true;
		}
	}

	public synchronized int getChildCount(Object parent) {
		int size;
		if (parent instanceof MapTreeNode) {
			MapTreeNode node = (MapTreeNode)parent;
			size = node.size();
		} else {
			size = 0;
		}
		System.out.println("getChildCount of Object " + parent + ": " + size);
		return size;
	}
	
	public synchronized Object getChild(Object parent, int index) {
		System.out.println("getChild: " + index + " of Object " + parent);
		if (parent instanceof MapTreeNode) {
			MapTreeNode node = (MapTreeNode)parent;
			return node.valueAt(index);
		} else {
			return null;
		}
	}

	public synchronized int getIndexOfChild(Object parent, Object child) {
		System.out.println("getIndexOfChild");
		return 0;
	}

	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	private MapTreeNode getFamilyMap(Device d) {
		String key = d.getFamilyType();
		MapTreeNode devicesInFamily = 
			(MapTreeNode)root.get(key);
		if (devicesInFamily == null) {
			devicesInFamily = new MapTreeNode(key);
			root.put(key, devicesInFamily);
		}
		return devicesInFamily;
	}

	private LinkedList<TreeModelListener> listeners;
	private MapTreeNode root;
}
