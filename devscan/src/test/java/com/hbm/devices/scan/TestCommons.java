package com.hbm.devices.scan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

// I'd prefer the default package as location but the compiler didn't accept it
public class TestCommons
{
    /**
     * Verifies that a utility class is well defined.
     * 
     * @param clazz
     *            utility class to verify.
     */
    public static void assertUtilityClassWellDefined(final Class<?> clazz) {
        assertTrue(Modifier.isFinal(clazz.getModifiers()), "class must be final");
        assertEquals(1, clazz.getDeclaredConstructors().length, "There must be only one constructor");
        Constructor<?> constructor;
        try {
            constructor = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            fail("empty constructor expected");
            return;
        } catch (SecurityException e) {
            fail("Test requires access to declared constructor");
            return;
        }
        if (constructor.isAccessible() || !Modifier.isPrivate(constructor.getModifiers())) {
            fail("constructor is not private");
        }
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            // accepted exceptions
        }
        constructor.setAccessible(false);
        for (final Method method : clazz.getMethods()) {
            if (!Modifier.isStatic(method.getModifiers()) && method.getDeclaringClass().equals(clazz)) {
                fail("there exists a non-static method:" + method);
            }
        }
    }
}
