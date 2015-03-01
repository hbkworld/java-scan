package com.hbm.devices.scan.util;

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
}
