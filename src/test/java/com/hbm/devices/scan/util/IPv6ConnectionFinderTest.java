package com.hbm.devices.scan.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPv6ConnectionFinderTest {

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
}
