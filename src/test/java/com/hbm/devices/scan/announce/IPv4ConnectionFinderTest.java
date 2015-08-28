package com.hbm.devices.scan.announce;

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
import java.util.List;
import java.util.Observer;
import java.util.Observable;

import com.hbm.devices.scan.FakeMessageReceiver;

public class IPv4ConnectionFinderTest {

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
            InetAddress announceAddress = InetAddress.getByName("172.19.1.2");
            int announcePrefix = 16;
            InetAddress interfaceAddress = InetAddress.getByName("172.19.1.2");
            int interfacePrefix = 16;

            assertTrue("Addresses should be in the same net", IPv4ConnectionFinder.sameNet(announceAddress, announcePrefix, interfaceAddress, interfacePrefix));
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }

    @Test
    public void notSameNetTest() {
        try {
            InetAddress announceAddress = InetAddress.getByName("172.19.1.2");
            int announcePrefix = 16;
            InetAddress interfaceAddress = InetAddress.getByName("172.19.1.2");
            int interfacePrefix = 15;

            assertFalse("Addresses should not be in the same net", IPv4ConnectionFinder.sameNet(announceAddress, announcePrefix, interfaceAddress, interfacePrefix));
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }
    
    @Test
    public void calculatePrefixTest() {
        try {
            assertEquals("prefix conversion failed", IPv4ConnectionFinder.calculatePrefix(InetAddress.getByName("0.0.0.0")), 0);
            assertEquals("prefix conversion failed", IPv4ConnectionFinder.calculatePrefix(InetAddress.getByName("255.255.255.255")), 32);
            assertEquals("prefix conversion failed", IPv4ConnectionFinder.calculatePrefix(InetAddress.getByName("255.63.0.0")), 14);
            assertEquals("prefix conversion failed", IPv4ConnectionFinder.calculatePrefix(InetAddress.getByName("127.0.0.0")), 7);
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }

    @Test
    public void findIPAddressInList() {
        LinkedList<NetworkInterfaceAddress> list = new LinkedList<NetworkInterfaceAddress>();
        try {
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("10.1.2.3"), 8));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("172.19.1.2"), 16));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("192.168.4.5"), 24));
            IPv4ConnectionFinder finder = new IPv4ConnectionFinder(list);

            fsmmr.emitSingleCorrectMessage();
            assertNotNull("No Announce object after correct message", announce);
            List<InetAddress> addresses = finder.getConnectableAddresses(announce);
            assertFalse("Device not connectable", addresses.isEmpty());
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }

    @Test
    public void noIpv4AddressInAnnounce() {
        LinkedList<NetworkInterfaceAddress> list = new LinkedList<NetworkInterfaceAddress>();
        try {
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("10.1.2.3"), 8));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("172.19.1.2"), 16));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("192.168.4.5"), 24));
            IPv4ConnectionFinder finder = new IPv4ConnectionFinder(list);

            fsmmr.emitSingleCorrectMessageNoIpv4();
            assertNotNull("No Announce object after correct message", announce);
            List<InetAddress> addresses = finder.getConnectableAddresses(announce);
            assertTrue("Device connectable", addresses.isEmpty());
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }

    @Test
    public void ipv6InIPv4AddressTest() {
        LinkedList<NetworkInterfaceAddress> list = new LinkedList<NetworkInterfaceAddress>();
        try {
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("10.1.2.3"), 8));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("172.19.1.2"), 16));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("192.168.4.5"), 24));
            IPv4ConnectionFinder finder = new IPv4ConnectionFinder(list);

            fsmmr.emitSingleMessageIpv6InIpv4();
            assertNotNull("No Announce object after correct message", announce);
            assertTrue("Device connectable", finder.getConnectableAddresses(announce).isEmpty());
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }

    @Test
    public void illegalIpv4AddressTest() {
        LinkedList<NetworkInterfaceAddress> list = new LinkedList<NetworkInterfaceAddress>();
        try {
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("10.1.2.3"), 8));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("172.19.1.2"), 16));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("192.168.4.5"), 24));
            IPv4ConnectionFinder finder = new IPv4ConnectionFinder(list);

            fsmmr.emitIllegalIpv4();
            assertNotNull("No Announce object after correct message", announce);
            assertTrue("Device connectable", finder.getConnectableAddresses(announce).isEmpty());
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }

    @Test
    public void noAddressesInList() {
        LinkedList<NetworkInterfaceAddress> list = new LinkedList<NetworkInterfaceAddress>();
        IPv4ConnectionFinder finder = new IPv4ConnectionFinder(list);

        fsmmr.emitSingleCorrectMessage();
        assertNotNull("No Announce object after correct message", announce);
        assertTrue("Device not connectable", finder.getConnectableAddresses(announce).isEmpty());
    }
}
