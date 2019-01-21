package com.hbm.devices.scan.announce;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
    
    @BeforeEach
    public void setUp() {
        announce = null;
        fsmmr = new FakeMessageReceiver();
        AnnounceDeserializer parser = new AnnounceDeserializer();
        fsmmr.addObserver(parser);
        parser.addObserver(new Observer() {
            @Override
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

            assertTrue(ConnectionFinder.sameIPv4Net(announceAddress, announcePrefix, interfaceAddress, interfacePrefix), "Addresses should be in the same net");
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

            assertFalse(ConnectionFinder.sameIPv4Net(announceAddress, announcePrefix, interfaceAddress, interfacePrefix), "Addresses should not be in the same net");
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }
    
    @Test
    public void calculatePrefixTest() {
        try {
            assertEquals(AnnounceDeserializer.calculatePrefix(InetAddress.getByName("0.0.0.0")), 0, "prefix conversion failed");
            assertEquals(AnnounceDeserializer.calculatePrefix(InetAddress.getByName("255.255.255.255")), 32, "prefix conversion failed");
            assertEquals(AnnounceDeserializer.calculatePrefix(InetAddress.getByName("255.63.0.0")), 14, "prefix conversion failed");
            assertEquals(AnnounceDeserializer.calculatePrefix(InetAddress.getByName("127.0.0.0")), 7, "prefix conversion failed");
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }

    @Test
    public void findIPAddressInList() {
        LinkedList<NetworkInterfaceAddress> list = new LinkedList<>();
        try {
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("10.1.2.3"), 8));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("172.19.1.2"), 16));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("192.168.4.5"), 24));
            ConnectionFinder finder = new ConnectionFinder(list, new LinkedList<NetworkInterfaceAddress>());

            fsmmr.emitSingleCorrectMessage();
            assertNotNull(announce, "No Announce object after correct message");
            List<InetAddress> addresses = finder.getSameNetworkAddresses(announce);
            assertFalse(addresses.isEmpty(), "Device not connectable");
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }

    @Test
    public void noIpv4AddressInAnnounce() {
        LinkedList<NetworkInterfaceAddress> list = new LinkedList<>();
        try {
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("10.1.2.3"), 8));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("172.19.1.2"), 16));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("192.168.4.5"), 24));
            ConnectionFinder finder = new ConnectionFinder(list, new LinkedList<NetworkInterfaceAddress>());

            fsmmr.emitSingleCorrectMessageNoIpv4();
            assertNotNull(announce, "No Announce object after correct message");
            List<InetAddress> addresses = finder.getSameNetworkAddresses(announce);
            assertTrue(addresses.isEmpty(), "Device connectable");
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }

    @Test
    public void ipv6InIPv4AddressTest() {
        LinkedList<NetworkInterfaceAddress> list = new LinkedList<>();
        try {
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("10.1.2.3"), 8));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("172.19.1.2"), 16));
            list.push(new NetworkInterfaceAddress(InetAddress.getByName("192.168.4.5"), 24));
            ConnectionFinder finder = new ConnectionFinder(list, new LinkedList<NetworkInterfaceAddress>());

            fsmmr.emitSingleMessageIpv6InIpv4();
            assertNotNull(announce, "No Announce object after correct message");
            assertTrue(finder.getSameNetworkAddresses(announce).isEmpty(), "Device connectable");
        } catch (UnknownHostException e) {
            fail("name resolution failed");
        }
    }

    @Test
    public void noAddressesInList() {
        LinkedList<NetworkInterfaceAddress> list = new LinkedList<>();
        ConnectionFinder finder = new ConnectionFinder(list, new LinkedList<NetworkInterfaceAddress>());

        fsmmr.emitSingleCorrectMessage();
        assertNotNull(announce, "No Announce object after correct message");
        assertTrue(finder.getSameNetworkAddresses(announce).isEmpty(), "Device not connectable");
    }
}
