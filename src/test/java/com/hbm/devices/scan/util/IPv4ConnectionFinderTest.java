package com.hbm.devices.scan.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPv4ConnectionFinderTest {

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
}
