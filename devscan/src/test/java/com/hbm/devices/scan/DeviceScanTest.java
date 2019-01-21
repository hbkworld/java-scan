package com.hbm.devices.scan;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

public class DeviceScanTest {
    @Test
    public void utilityClassTest() {
        TestCommons.assertUtilityClassWellDefined(DeviceScan.class);
    }
    
    @Test
    public void versionTest() {
        try {
            String version = DeviceScan.VERSION;
            assertNotNull(version, "Version string is null");
            assertFalse(version.isEmpty(), "Version string is empty");
        } catch (ExceptionInInitializerError e) {
            fail("Could not read version information");
        }
    }
}
