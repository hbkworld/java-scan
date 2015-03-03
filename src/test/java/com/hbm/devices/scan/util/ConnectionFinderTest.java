package com.hbm.devices.scan.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.net.SocketException;

import static com.hbm.devices.scan.util.ConnectionFinder.LookupPreference.*;

public class ConnectionFinderTest {

    @Test
    public void listSizeTest() {
        try {
            ScanInterfaces interfaces = new ScanInterfaces();
            ConnectionFinder finder = new ConnectionFinder(interfaces.getInterfaces(), PREFER_IPV6);
            assertNotNull("ConnectionFinder cannot be constructed", finder);
        } catch (SocketException e) {
            fail("Got SocketException while building list of scan interfaces");
        }
    }
}
