/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.commons.collections;

import java.util.*;

/**
 * MapComparator.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class MapFieldComparator<K, V> implements Comparator<Map<K, V>> {

    private K key;
    private Comparator<? super V> comparator;


    public MapFieldComparator(K key, Comparator<? super V> comparator) {
        this.key = key;
        this.comparator = Comparators.nullableOrder(comparator);
    }


    public MapFieldComparator(K key, boolean reverse) {
        this.key = key;
        this.comparator = reverse ? Comparators.reverseOrder() : Comparators.naturalOrder();
    }


    public MapFieldComparator(K key) {
        this(key, false);
    }


    @Override
    public int compare(Map<K, V> m1, Map<K, V> m2) {
        V v1 = (m1 != null) ? m1.get(key) : null;
        V v2 = (m2 != null) ? m2.get(key) : null;

        return comparator.compare(v1, v2);
    }
}
