package com.hbm.devices.scan;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class DeviceScanTest {

    @Test
    public void versionTest() {
        try {
            String version = DeviceScan.VERSION;
            assertNotNull("Version string is null", version);
            assertFalse("Version string is empty", version.isEmpty());
        } catch (ExceptionInInitializerError e) {
            fail("Could not read version information");
        }
    }
}
