/**
 * Copyright (c) 2008 Mailtech.cn, Ltd. All Rights Reserved.
 */

package solocompany.var;

import junit.framework.Test;
import solocompany.commons.collections.AbstractTestList;
import solocompany.utils.LangUtils;

import java.util.*;

/**
 * Test-case for VarArray.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class VarArrayTest extends AbstractTestList {

    public VarArrayTest(String name) {
        super(name);
        disallowedElementTypes = new Class[]{Character.class};
    }


    @Override
    public Object[] getFullNonNullElements() {
        return LangUtils.concatArray(Object.class,
                // --- Hack AbstractTestList's bulkTestSubList ---
                // 该 List 测试框架要求 ListIterator 的 next() 和 previous() 保持实例的引用不变, 而不仅仅是值不变
                // 而 XListNormalizer 的实现只对部分类型的 Variant 能保持引用不变
                //      比如 XString / XBool 以及部分小的整数 (基于 Integer.values() 的实现)
                LangUtils.concatArray(Object.class, new Object[]{"hacker1", "hacker2"}, super.getFullNonNullElements()),
                true
        );
    }


    public List makeEmptyList() {
        return VarArray.newNormalizeList();
    }


    public void testSubList() {
        VarArray array = new VarArray();
        array.add("1");
        array.add(2);
        array.add(3L);
        array.add(Variant.wrap(4.0d));
        array.add(Variant.wrap(5.0f));


        List<Object> list = array.normalize();
        List<Object> sublist = list.subList(1, 4);

        assertEquals(Arrays.asList((Object) 2, 3L, 4.0d), sublist);

        VarArray subarray = (VarArray) Variant.wrap(sublist);
        assertEquals(Arrays.asList((Object) 2, 3L, 4.0d), subarray.normalize());
    }


    public void testSetSize() {
        VarArray array = new VarArray();
        array.set(0, "1");
        array.setSize(2);
        assertEquals(Arrays.asList((Object) "1", null), array.normalize());
        array.set(1, "2");
        array.set(2, "3");
        assertEquals(Arrays.asList((Object) "1", "2", "3"), array.normalize());
        array.setSize(3);
        assertEquals(Arrays.asList((Object) "1", "2", "3"), array.normalize());
        array.setSize(2);
        assertEquals(Arrays.asList((Object) "1", "2"), array.normalize());
    }


    public static Test suite() {
        return makeSuite(VarArrayTest.class);
    }
}
