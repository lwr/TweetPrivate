/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.var;

import solocompany.commons.collections.AbstractTestMap;
import solocompany.utils.LangUtils;

import java.util.*;

/**
 * Test-case for VarObject.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class VarObjectTest extends AbstractTestMap {

    public VarObjectTest(String name) {
        super(name);
        isAllowNullKey = false;
    }


    // CS:AllowPrintln


    @Override
    public Object[] getSampleKeys() {
        return LangUtils.concatArray(Object.class, super.getSampleKeys(),
                "",
                "1 string",
                "2 float",
                "3 double",
                "4 byte",
                "5 short",
                "6 int",
                "7 long",
                "8 boolean",
                "11 null");
    }


    @Override
    public Object[] getSampleValues() {
        return LangUtils.concatArray(Object.class, super.getSampleValues(),
                "",
                "1",
                (float) 2,
                (double) 3,
                (byte) 4,
                (short) 5,
                6,
                (long) 7,
                true,
                null
        );
    }


    @Override
    public Object[] getNewSampleValues() {
        return LangUtils.concatArray(Object.class, super.getNewSampleValues(),
                "new empty",
                "new string",
                "new float",
                "new double",
                "new byte",
                "new short",
                "new int",
                "new long",
                "new boolean",
                "new null"
        );
    }


    VarObject makeEmptyXMap() {
        return new VarObject();
    }


    public void testBasicAccess() {
        VarObject instance = makeEmptyXMap();
        instance.put("1", 1);
        assertEquals(1, instance.get("1").getInt());
        instance.put("2", 2);
        assertEquals(2, instance.get("2").getInt());
        assertEquals(2, instance.size());
        assertTrue(instance.remove("1") instanceof VarInt);
        assertEquals(1, instance.size());
        assertEquals(2, instance.get("2").getInt());
        assertEquals(2, instance.removeOrNone("2").getInt());
        assertTrue(instance.isEmpty());
        instance.put("3", 3);
        assertFalse(instance.isEmpty());
        assertEquals(1, instance.size());
        instance.clear();
        assertTrue(instance.isEmpty());
        for (int i = 0; i < 100; i++) {
            instance.put(String.valueOf(i), i);
        }
        System.out.println(instance.getDebugInfo());
        instance.clear();
    }


    public void testCopy() {
        VarObject instance = makeEmptyXMap();
        for (int i = 0; i < 100; i++) {
            instance.put(String.valueOf(i), i);
        }

        VarObject copied = instance.copyMap();
        assertNotSame(instance, copied);
        assertEquals(instance.normalize(), copied.normalize());

        VarArray a123 = VarArray.valueOf("1", "2", "3");
        instance.put("30", a123);

        assertSame(a123, instance.copyMap().get("30"));

        copied = instance.deepCopyMap();
        assertNotSame(instance, copied);
        assertEquals(instance.size(), copied.size());
        assertSame(instance.get("1"), copied.get("1"));
        assertSame(instance.get("29"), copied.get("29"));
        assertNotSame(a123, copied.get("30"));
        assertSame(instance.get("31"), copied.get("31"));
        assertSame(instance.get("99"), copied.get("99"));
    }


    public void testGetDebugInfoWithPasswordAttrs() {
        VarObject instance = makeEmptyXMap();

        instance.put("attrs", makeEmptyXMap());
        instance.get("attrs").getMap().put("foo", "x");
        instance.get("attrs").getMap().put("password", "foobar");
        instance.get("attrs").getMap().put("bar", "y");
        System.out.println(instance.getDebugInfo());
        assertTrue(instance.getDebugInfo().contains("\tpassword: ***\n"));
        assertFalse(instance.getDebugInfo().contains("\tfoobar\n"));

        instance.put("attrs", "foo=x&password=foobar&bar=y");
        System.out.println(instance.getDebugInfo());
        assertTrue(instance.getDebugInfo().endsWith("\tattrs: foo=x&password=***&bar=y"));
        assertFalse(instance.getDebugInfo().contains("\tfoobar\n"));
    }


    public void testKeySet() {
        VarObject instance;
        instance = makeEmptyXMap();
        instance.put("1", 1);
        instance.put("2", 2);
        instance.put("3", 3);

        assertFalse(instance.keySet().removeAll(Arrays.asList("A", "B")));
        assertTrue(instance.keySet().removeAll(Arrays.asList("1", "2")));
        assertEquals("{3=3}", instance.toString());

        instance = makeEmptyXMap();
        instance.put("1", 1);
        instance.put("2", 2);
        instance.put("3", 3);
        assertFalse(instance.keySet().retainAll(Arrays.asList("1", "2", "3")));
        assertTrue(instance.keySet().retainAll(Arrays.asList("1", "2")));
        assertEquals("{1=1, 2=2}", instance.toString());
    }


    //-----------------------------------------------------------------------
    public Map makeEmptyMap() {
        return makeEmptyXMap().normalize(true);
    }
}
