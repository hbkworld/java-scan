package com.hbm.devices.scan.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Observer;
import java.util.Observable;

import com.hbm.devices.scan.FakeMessageReceiver;
import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.messages.AnnounceDeserializer;

public class IPv6ConnectionFinderTest {

    private Announce announce;
    private FakeMessageReceiver fsmmr;
    
    @Before
    public void setUp() {
        announce = null;
        fsmmr = new FakeMessageReceiver();
        AnnounceDeserializer parser = new AnnounceDeserializer();
        fsmmr.addObserver(parser);
        parser.addObserver(new Observer() {
            public void update(Observable o, Object arg) {
                if (arg instanceof Announce) {
                    announce = (Announce) arg;
                } 
            }
        });
    }

    @Test
    public void sameNetTest() {
        try {
            InetAddress announceAddress = InetAddress.getByName("fe80::222:4dff:feaa:4c1e");
            int announcePrefix = 64;
            InetAddress interfaceAddress = InetAddress.getByName("fe80::333:4dff:feaa:4c1f");
            int interfacePrefix = 64;

            assertTrue("Addresses should be in the same net", IPv6ConnectionFinder.sameNet(announceAddress, announcePrefix, interfaceAddress, interfacePrefix));
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }

    @Test
    public void differentTest() {
        try {
            InetAddress announceAddress = InetAddress.getByName("fe80::222:4dff:feaa:4c1e");
            int announcePrefix = 64;
            InetAddress interfaceAddress = InetAddress.getByName("fe80::333:4dff:feaa:4c1f");
            int interfacePrefix = 57;

            assertFalse("Addresses should not be in the same net", IPv6ConnectionFinder.sameNet(announceAddress, announcePrefix, interfaceAddress, interfacePrefix));
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }

    @Test
    public void notSameNetTest() {
        try {
            InetAddress announceAddress = InetAddress.getByName("fdfb::222:4dff:feaa:4c1e");
            int announcePrefix = 64;
            InetAddress interfaceAddress = InetAddress.getByName("fe80::333:4dff:feaa:4c1f");
            int interfacePrefix = 64;

            assertFalse("Addresses should not be in the same net", IPv6ConnectionFinder.sameNet(announceAddress, announcePrefix, interfaceAddress, interfacePrefix));
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }

    @Test
    public void noIPv6AddressTest() {
        try {
            InetAddress ipv4 = InetAddress.getByName("127.19.1.2");
            int announcePrefix = 64;
            InetAddress ipv6 = InetAddress.getByName("fe80::333:4dff:feaa:4c1f");
            int interfacePrefix = 64;

            assertFalse("IPv4 and IPv6 addresses can't be in the same IP net", IPv6ConnectionFinder.sameNet(ipv4, announcePrefix, ipv6, interfacePrefix));
            assertFalse("IPv4 and IPv6 addresses can't be in the same IP net", IPv6ConnectionFinder.sameNet(ipv6, announcePrefix, ipv4, interfacePrefix));
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }

    @Test
    public void findIPAddressInList() {
        LinkedList<NetworkInterfaceAddress> list = new LinkedList<NetworkInterfaceAddress>();
        try {
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("fe80::222:4dff:feaa:4c1e"), 64));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("fdfb:84a3:9d2d:0:d890:1567:3af6:974e"), 64));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("2a01:238:20a:202:6660:0000:0198:0033"), 48));
            IPv6ConnectionFinder finder = new IPv6ConnectionFinder(list);

            fsmmr.emitSingleCorrectMessage();
            assertNotNull("No Announce object after correct message", announce);
            InetAddress addr = finder.getConnectableAddress(announce);
            assertNotNull("Device not connectable", addr);
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }
 
    @Test
    public void noIpv6AddressInAnnounce() {
        LinkedList<NetworkInterfaceAddress> list = new LinkedList<NetworkInterfaceAddress>();
        try {
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("fe80::222:4dff:feaa:4c1e"), 64));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("fdfb:84a3:9d2d:0:d890:1567:3af6:974e"), 64));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("2a01:238:20a:202:6660:0000:0198:0033"), 48));
            IPv6ConnectionFinder finder = new IPv6ConnectionFinder(list);

            fsmmr.emitSingleCorrectMessageNoIpv6();
            assertNotNull("No Announce object after correct message", announce);
            InetAddress addr = finder.getConnectableAddress(announce);
            assertNull("Device connectable", addr);
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }

    @Test
    public void ipv4InIPv6AddressTest() {
        LinkedList<NetworkInterfaceAddress> list = new LinkedList<NetworkInterfaceAddress>();
        try {
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("fe80::222:4dff:feaa:4c1e"), 64));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("fdfb:84a3:9d2d:0:d890:1567:3af6:974e"), 64));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("2a01:238:20a:202:6660:0000:0198:0033"), 48));
            IPv6ConnectionFinder finder = new IPv6ConnectionFinder(list);

            fsmmr.emitSingleMessageIpv4InIpv6();
            assertNotNull("No Announce object after correct message", announce);
            assertNull("Device connectable", finder.getConnectableAddress(announce));
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }

    @Test
    public void illegalIpv6AddressTest() {
        LinkedList<NetworkInterfaceAddress> list = new LinkedList<NetworkInterfaceAddress>();
        try {
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("fe80::222:4dff:feaa:4c1e"), 64));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("fdfb:84a3:9d2d:0:d890:1567:3af6:974e"), 64));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("2a01:238:20a:202:6660:0000:0198:0033"), 48));
            IPv6ConnectionFinder finder = new IPv6ConnectionFinder(list);

            fsmmr.emitIllegalIpv6();
            assertNotNull("No Announce object after correct message", announce);
            assertNull("Device connectable", finder.getConnectableAddress(announce));
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }

    @Test
    public void noAddressesInList() {
        LinkedList<NetworkInterfaceAddress> list = new LinkedList<NetworkInterfaceAddress>();
        IPv6ConnectionFinder finder = new IPv6ConnectionFinder(list);

        fsmmr.emitSingleCorrectMessage();
        assertNotNull("No Announce object after correct message", announce);
        assertNull("Device not connectable", finder.getConnectableAddress(announce));
    }
}
