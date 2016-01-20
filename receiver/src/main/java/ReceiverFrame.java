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
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.hbm.devices.scan.ScanInterfaces;
import com.hbm.devices.scan.announce.Announce;
import com.hbm.devices.scan.announce.AnnounceParams;
import com.hbm.devices.scan.announce.Device;
import com.hbm.devices.scan.announce.IPv4Entry;
import com.hbm.devices.scan.announce.Interface;
import com.hbm.devices.scan.announce.LostDeviceEvent;
import com.hbm.devices.scan.announce.NewDeviceEvent;
import com.hbm.devices.scan.announce.UpdateDeviceEvent;
import com.hbm.devices.scan.configure.ConfigurationCallback;
import com.hbm.devices.scan.configure.ConfigurationDevice;
import com.hbm.devices.scan.configure.ConfigurationInterface;
import com.hbm.devices.scan.configure.ConfigurationMessageReceiver;
import com.hbm.devices.scan.configure.ConfigurationMulticastSender;
import com.hbm.devices.scan.configure.ConfigurationNetSettings;
import com.hbm.devices.scan.configure.ConfigurationParams;
import com.hbm.devices.scan.configure.ConfigurationSerializer;
import com.hbm.devices.scan.configure.ConfigurationService;
import com.hbm.devices.scan.configure.IPv4EntryManual;
import com.hbm.devices.scan.configure.Response;
import com.hbm.devices.scan.configure.ResponseDeserializer;
import com.hbm.devices.scan.configure.ConfigurationInterface.Method;

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

    public synchronized void updateRow(int row, Object [] objects) {
        for (int col=0; col<objects.length; col++) {
            setValueAt(objects[col], row, col);
        }
    }
}

class ExportDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = -7074407833509691970L;

    private JTextArea textarea;

    @Override
    public void actionPerformed(ActionEvent arg0) {
    }

    public ExportDialog() {
        setTitle("Known devices");
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        textarea = new JTextArea();
        textarea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textarea);
        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel);
    }

    public void openDialog(String text) {
        textarea.setText(text);
        setSize(900, 200);
        setVisible(true);
    }
}

class ConfigDialog extends JDialog implements ActionListener, ConfigurationCallback {

    private static final long serialVersionUID = 4381596801176131696L;

    private JTextField txtInterface;
    private JTextField txtCurrentIp;
    private JRadioButton rbnDhcp;
    private JRadioButton rbnManual;
    private ButtonGroup buttons;
    private JTextField txtManualIp;
    private JTextField txtManualNetmask;
    private JButton btnOK;
    private JButton btnCancel;

    private String uuid;
    private final ConfigurationService service;
    private static final int RESPONSE_TIMEOUT_S = 5;
    private final ConfigurationMessageReceiver responseReceiver;

    @Override
    public void actionPerformed(ActionEvent event) {
        if(event.getSource() == btnCancel) {
            this.setVisible(false);
        }
        if(event.getSource() == btnOK) {
            try {
                final ConfigurationDevice device = new ConfigurationDevice(uuid);
                ConfigurationNetSettings settings = null;
                if (rbnDhcp.isSelected()) {
                    settings = new ConfigurationNetSettings(new ConfigurationInterface(txtInterface.getText(), Method.DHCP));
                }
                if (rbnManual.isSelected()) {
                    IPv4EntryManual ipv4 = new IPv4EntryManual(txtManualIp.getText(), txtManualNetmask.getText());
                    settings = new ConfigurationNetSettings(new ConfigurationInterface(txtInterface.getText(), Method.MANUAL, ipv4));
                }

                if (settings != null) {
                    final ConfigurationParams configParams = new ConfigurationParams(device, settings);
                    service.sendConfiguration(configParams, this, TimeUnit.SECONDS.toMillis(RESPONSE_TIMEOUT_S));
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                } else {
                	this.setVisible(false);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,  "Can't create configuration service!", "Exception", JOptionPane.OK_CANCEL_OPTION);
                this.setVisible(false);
            }
        }
    }

    @Override
    public void onSuccess(Response response) {
    	this.setCursor(Cursor.getDefaultCursor());
        this.setVisible(false);
    }

    @Override
    public void onError(Response response) {
    	this.setCursor(Cursor.getDefaultCursor());
        JOptionPane.showMessageDialog(null, "Error while trying to\nconfigure device "+uuid, "Error", JOptionPane.OK_CANCEL_OPTION);
        this.setVisible(false);
    }

    @Override
    public void onTimeout(long timeout) {
    	this.setCursor(Cursor.getDefaultCursor());
        JOptionPane.showMessageDialog(null, "Timeout while trying to\nconfigure device "+uuid, "Error", JOptionPane.OK_CANCEL_OPTION);
        this.setVisible(false);
    }


    public ConfigDialog() throws IOException {
        btnOK = new JButton("Apply");
        btnOK.addActionListener(this);
        btnOK.setEnabled(false);

        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(this);

        txtInterface = new JTextField();
        txtInterface.setEditable(false);

        txtCurrentIp = new JTextField();
        txtCurrentIp.setEditable(false);

        rbnDhcp = new JRadioButton("Dynamic IP (DHCP)", false);
        rbnDhcp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                btnOK.setEnabled(true);
            }
        });
        rbnManual = new JRadioButton("Static IP:", false);
        rbnManual.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                btnOK.setEnabled(true);
            }
        });
        buttons = new ButtonGroup();
        buttons.add(rbnDhcp);
        buttons.add(rbnManual);

        txtManualIp = new JTextField();
        txtManualNetmask = new JTextField();

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));
        panel.add(new JLabel("Interface:"));
        panel.add(txtInterface);
        panel.add(new JLabel("Current IP:"));
        panel.add(txtCurrentIp);
        panel.add(rbnDhcp);
        panel.add(new JLabel(""));
        panel.add(rbnManual);
        panel.add(txtManualIp);
        panel.add(new JLabel("Static Netmask:"));
        panel.add(txtManualNetmask);
        panel.add(btnOK);
        panel.add(btnCancel);

        add(panel);

        responseReceiver = new ConfigurationMessageReceiver();
        final ResponseDeserializer responseParser = new ResponseDeserializer();
        responseReceiver.addObserver(responseParser);

        final Collection<NetworkInterface> scanInterfaces = new ScanInterfaces().getInterfaces();
        final ConfigurationMulticastSender multicastSender = new ConfigurationMulticastSender(scanInterfaces);
        final ConfigurationSerializer sender = new ConfigurationSerializer(multicastSender);

        service = new ConfigurationService(sender, responseParser);

        final Thread receiverThread = new Thread(responseReceiver);
        receiverThread.start();
    }

    public void openDialog(String uuid, Interface interfaces) {
        this.uuid = uuid;
        txtInterface.setText(interfaces.getName());
        String ip = interfaces.getIPv4().get(0).getAddress();
        setTitle("Configure device "+uuid);
        txtCurrentIp.setText(ip);
        buttons.clearSelection();
        txtManualIp.setText("");
        txtManualNetmask.setText("");
        pack();
        setVisible(true);
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
    private ConfigDialog configdialog;

    private String[] columnNames = {
        "Announcement",
        "Name",
        "Label",
        "Family",
        "Type",
        "UUID", /* set colUUID accordingly */
        "Hardware ID",
        "Firmware Version"
    };

    private static final int colUUID = 5;

    private Interface getInterfaceFromTable(int row) {
        return ((Announce)(tablemodel.getValueAt(row, 0))).getParams().getNetSettings().getInterface();
    }

    private void exportData() {
        StringBuffer text = new StringBuffer(); 
        int column = 0;
        for (int row=0 ; row<tablemodel.getRowCount(); row++) {
            Announce announce = (Announce)(tablemodel.getValueAt(row, column));
            text.append(announce.getJSONString());
            text.append("\n");
        }
        ExportDialog dialog = new ExportDialog();
        dialog.openDialog(text.toString());
    }
    
    private void openWebBrowser(String host) {
        try {
            Desktop.getDesktop().browse(new URI("http://"+host));
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(null, "Can not open web-browser to connect to "+host, "Error", JOptionPane.OK_CANCEL_OPTION);
        } catch (URISyntaxException e1) {
            JOptionPane.showMessageDialog(null, "Can not connect, invalid host \""+host+"\"", "Error", JOptionPane.OK_CANCEL_OPTION);
        }
    }

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
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                exportData();
            }
        });
        menu.add(menuItem);
        menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        menu.add(menuItem);
        frame.setJMenuBar(menuBar);

        tablemodel = new DevicesTableModel(columnNames);
        table = new JTable(tablemodel);
        table.removeColumn(table.getColumnModel().getColumn(0));
        table.setAutoCreateRowSorter(true);
        final JPopupMenu popupMenu = new JPopupMenu();
        final JMenu ipmenu = new JMenu("Open web page");
        popupMenu.add(ipmenu);

        menuItem = new JMenuItem("Configure network settings...");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int row = table.convertRowIndexToModel(table.getSelectedRow());
                if (configdialog != null) {
                    configdialog.openDialog(tablemodel.getValueAt(row, colUUID).toString(), getInterfaceFromTable(row));
                }
            }
        });
        popupMenu.add(menuItem);

        table.addMouseListener(new MouseAdapter() {
            private void showPopup(MouseEvent event) {
                int row = table.rowAtPoint(event.getPoint());
                if (row >= 0 && row < tablemodel.getRowCount()) {
                    table.setRowSelectionInterval(row, row);
                    ipmenu.removeAll();
                    int tablerow = table.convertRowIndexToModel(table.getSelectedRow());
                    for(IPv4Entry entry : getInterfaceFromTable(tablerow).getIPv4()) {
                        final String ip = entry.getAddress();
                        JMenuItem menuitem = new JMenuItem(ip);
                        menuitem.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                openWebBrowser(ip);
                            }
                        });
                        ipmenu.add(menuitem);
                    }
                    popupMenu.show(event.getComponent(), event.getX(), event.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent event) {
                if (event.isPopupTrigger()) {
                    showPopup(event);
                }
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                if (event.isPopupTrigger()) {
                    showPopup(event);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        frame.pack();
        frame.setSize(900, 600);

        try {
            configdialog = new ConfigDialog();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
            final Announce announce = ((NewDeviceEvent)arg).getAnnounce();
            AnnounceParams params = announce.getParams();
            final Device device = params.getDevice();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    final int row = getRowByUuid(device.getUuid());
                    if (row < 0) {
                        tablemodel.addRow(new Object[]{announce, device.getName(), device.getLabel(), device.getFamilyType(), device.getType(), device.getUuid(), device.getHardwareId(), device.getFirmwareVersion()});
                    } else {
                        tablemodel.updateRow(row, new Object[]{announce, device.getName(), device.getLabel(), device.getFamilyType(), device.getType(), device.getUuid(), device.getHardwareId(), device.getFirmwareVersion()});
                    }
                }
            });
        } else if (arg instanceof LostDeviceEvent) {
            final Device device = ((LostDeviceEvent)arg).getAnnounce().getParams().getDevice();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    int row = getRowByUuid(device.getUuid());
                    if (row >= 0) {
                        tablemodel.removeRow(row);
                    }
                }
            });
        } else if (arg instanceof UpdateDeviceEvent) {
            final Announce announce = ((UpdateDeviceEvent)arg).getNewAnnounce();
            AnnounceParams params = announce.getParams();
            final Device device = params.getDevice();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    int row = getRowByUuid(device.getUuid());
                    tablemodel.updateRow(row, new Object[]{announce, device.getName(), device.getLabel(), device.getFamilyType(), device.getType(), device.getUuid(), device.getHardwareId(), device.getFirmwareVersion()});
                }
            });
        }
    }
}
