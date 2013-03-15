/**
 * Copyright (c) 2008 Mailtech.cn, Ltd. All Rights Reserved.
 */

package solocompany.utils;

/**
 * LangUtils.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class LangUtils {

    public static <T> T[] concatArray(Class<T> componentType, T[] a1, T... a2) {
        @SuppressWarnings("unchecked")
        T[] result = (T[]) java.lang.reflect.Array.newInstance(componentType, a1.length + a2.length);
        System.arraycopy(a1, 0, result, 0, a1.length);
        System.arraycopy(a2, 0, result, a1.length, a2.length);
        return result;
    }

}
