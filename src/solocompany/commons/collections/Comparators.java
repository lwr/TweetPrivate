/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.commons.collections;

import java.io.*;
import java.util.*;

/**
 * Comparators.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class Comparators {

    private Comparators() {}


    private static final Comparator NATURAL_ORDER = new NullableComparator<Object>(null);


    private static class NullableComparator<T> implements Comparator<T>, Serializable {

        private Comparator<? super T> comparator;


        private NullableComparator(Comparator<? super T> comparator) {
            this.comparator = comparator;
        }


        @SuppressWarnings({"unchecked"})
        @Override
        public int compare(T v1, T v2) {
            if (v1 == null) {
                return (v2 == null) ? 0 : -1 /* Integer.MIN_VALUE */;

            } else if (v2 == null) {
                return 1 /* Integer.MAX_VALUE */;

            } else if (comparator != null) {
                return comparator.compare(v1, v2);

            } else {
                return ((Comparable) v1).compareTo(v2);
            }
        }
    }


    @SuppressWarnings({"unchecked"})
    public static <T> Comparator<T> naturalOrder() {
        return (Comparator<T>) NATURAL_ORDER;
    }


    public static <T> Comparator<T> nullableOrder(Comparator<T> cmp) {
        if (cmp == null) {
            return naturalOrder();
        }

        if (cmp instanceof NullableComparator) {
            return cmp;
        }

        return new NullableComparator<T>(cmp);
    }


    public static <T> Comparator<T> reverseOrder() {
        return Collections.reverseOrder(Comparators.<T>naturalOrder());
    }


    public static <T> Comparator<T> reverseOrder(Comparator<T> cmp) {
        return Collections.reverseOrder(nullableOrder(cmp));
    }
}
