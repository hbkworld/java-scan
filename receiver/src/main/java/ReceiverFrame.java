/*
 * Java Scan, a library for scanning and configuring HBM devices.
 *
 * The MIT License (MIT)
 *
 * Copyright (C) Hottinger Baldwin Messtechnik GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.hbm.devices.scan.announce.Device;
import com.hbm.devices.scan.announce.LostDeviceEvent;
import com.hbm.devices.scan.announce.NewDeviceEvent;
import com.hbm.devices.scan.announce.UpdateDeviceEvent;

/**
 * Table model for the device table.
 * 
 * Like DefaultTableModel, but cells are not editable.
 */
class DevicesTableModel extends DefaultTableModel {

    private static final long serialVersionUID = -3392357507823855458L;

    public DevicesTableModel(Object[] columnNames) {
        super(columnNames, 0);
    }

    public boolean isCellEditable(int row, int col) {
        return false;
    }
}

/**
 * Application main frame
 *
 * Basically show a table of all devices, each in its own row.
 * Devices (rows) are added and removed dynamically.  There is a context
 * menu on each row to issue commands on the device, for example open the
 * web browser or configure the device.
 */
public class ReceiverFrame implements Observer {
    private JTable table;
    private DevicesTableModel tablemodel;

    private String[] columnNames = {
        "Name",
        "Family",
        "Type",
        "UUID",
        "Firmware Version"
    };

    private static final int colUUID = 3;

    public ReceiverFrame() {
        final JFrame frame = new JFrame("Device Discovery");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu;
        JMenuItem menuItem;

        menu = new JMenu("File");
        menuBar.add(menu);
        menu.setMnemonic(KeyEvent.VK_F);
        menuItem = new JMenuItem("Export...", KeyEvent.VK_E);
        menu.add(menuItem);
        menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        menu.add(menuItem);
        
        menu = new JMenu("Device Table");
        menuBar.add(menu);
        
        frame.setJMenuBar(menuBar);

        tablemodel = new DevicesTableModel(columnNames);
        table = new JTable(tablemodel);
        table.setAutoCreateRowSorter(true);
        final JPopupMenu popupMenu = new JPopupMenu();
        menuItem = new JMenuItem("Open");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Desktop.isDesktopSupported()) {
                    int selectedRow = table.getSelectedRow();
                    try {
                        Desktop.getDesktop().browse(new URI("http://www.example.com/?row="+selectedRow));
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (URISyntaxException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        });
        popupMenu.add(menuItem);
        menuItem = new JMenuItem("Configure...");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                // TODO: Add device configure dialog
                JOptionPane.showMessageDialog(frame, "Open device configure dialog here. row="+selectedRow);
            }
        });
        popupMenu.add(menuItem);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                int row = table.rowAtPoint(event.getPoint());
                if (row >= 0 && row < tablemodel.getRowCount()) {
                    table.setRowSelectionInterval(row, row);
                    if (event.isPopupTrigger()) {
                        popupMenu.show(event.getComponent(), event.getX(), event.getY());
                    }
                }
            }
        });

        frame.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

        frame.pack();
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    protected int getRowByUuid(String uuid) {
        for(int row=0 ; row < tablemodel.getRowCount(); row++) {
            String rowuuid = (String)(tablemodel.getValueAt(row, colUUID));
            if (rowuuid.equals(uuid)) {
                return row;
            }
        }
        return -1;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof NewDeviceEvent) {
            Device device = ((NewDeviceEvent)arg).getAnnounce().getParams().getDevice();
            int row = getRowByUuid(device.getUuid());
            if (row < 0) {
                tablemodel.addRow(new Object[]{device.getName(), device.getFamilyType(), device.getType(), device.getUuid(), device.getFirmwareVersion()});
            } else {
                // TODO: tablemodel.updateRow
            }
        } else if (arg instanceof LostDeviceEvent) {
            Device device = ((LostDeviceEvent)arg).getAnnounce().getParams().getDevice();
            int row = getRowByUuid(device.getUuid());
            if (row >= 0) {
                tablemodel.removeRow(row);
            }
        } else if (arg instanceof UpdateDeviceEvent) {
            Device device = ((UpdateDeviceEvent)arg).getNewAnnounce().getParams().getDevice();
            // TODO: tablemodel.updateRow
        }
    }
}
