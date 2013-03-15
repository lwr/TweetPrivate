package solocompany.commons.collections;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

@SuppressWarnings("ALL")
public abstract class AbstractTestCollection extends AbstractTestObject {

    //
    // NOTE:
    //
    // Collection doesn't define any semantics for equals, and recommends you
    // use reference-based default behavior of Object.equals.  (And a test for
    // that already exists in AbstractTestObject).  Tests for equality of lists, sets
    // and bags will have to be written in test subclasses.  Thus, there is no
    // tests on Collection.equals nor any for Collection.hashCode.
    //


    // These fields are used by reset() and verify(), and any test
    // method that tests a modification.

    /**
     *  A collection instance that will be used for testing.
     */
    protected Collection collection;

    /**
     *  Confirmed collection.  This is an instance of a collection that is
     *  confirmed to conform exactly to the java.util.Collection contract.
     *  Modification operations are tested by performing a mod on your
     *  collection, performing the exact same mod on an equivalent confirmed
     *  collection, and then calling verify() to make sure your collection
     *  still matches the confirmed collection.
     */
    protected Collection confirmed;

    /**
     * JUnit constructor.
     *
     * @param testName  the test class name
     */
    public AbstractTestCollection(String testName) {
        super(testName);
    }

    //-----------------------------------------------------------------------
    /**
     *  Specifies whether equal elements in the collection are, in fact,
     *  distinguishable with information not readily available.  That is, if a
     *  particular value is to be removed from the collection, then there is
     *  one and only one value that can be removed, even if there are other
     *  elements which are equal to it.
     *
     *  <P>In most collection cases, elements are not distinguishable (equal is
     *  equal), thus this method defaults to return false.  In some cases,
     *  however, they are.  For example, the collection returned from the map's
     *  values() collection view are backed by the map, so while there may be
     *  two values that are equal, their associated keys are not.  Since the
     *  keys are distinguishable, the values are.
     *
     *  <P>This flag is used to skip some verifications for iterator.remove()
     *  where it is impossible to perform an equivalent modification on the
     *  confirmed collection because it is not possible to determine which
     *  value in the confirmed collection to actually remove.  Tests that
     *  override the default (i.e. where equal elements are distinguishable),
     *  should provide additional tests on iterator.remove() to make sure the
     *  proper elements are removed when remove() is called on the iterator.
     **/
    protected boolean equalElementsAreDistinguishable = false;
    public boolean areEqualElementsDistinguishable() {
        return equalElementsAreDistinguishable;
    }

    /**
     *  Returns true if the collections produced by
     *  {@link #makeCollection()} and {@link #makeFullCollection()}
     *  support the <code>add</code> and <code>addAll</code>
     *  operations.<P>
     *  Default implementation returns true.  Override if your collection
     *  class does not support add or addAll.
     */
    protected boolean addIsSupported = true;
    public boolean isAddSupported() {
        return addIsSupported;
    }

    /**
     *  Returns true if the collections produced by
     *  {@link #makeCollection()} and {@link #makeFullCollection()}
     *  support the <code>remove</code>, <code>removeAll</code>,
     *  <code>retainAll</code>, <code>clear</code> and
     *  <code>iterator().remove()</code> methods.
     *  Default implementation returns true.  Override if your collection
     *  class does not support removal operations.
     */
    protected boolean removeIsSupported = true;
    public boolean isRemoveSupported() {
        return removeIsSupported;
    }

    /**
     * Returns true to indicate that the collection supports holding null.
     * The default implementation returns true;
     */
    protected boolean nullIsSupported = true;
    public boolean isNullSupported() {
        return nullIsSupported;
    }

    /**
     * Returns true to indicate that the collection supports equals() comparisons.
     * This implementation returns false;
     */
    protected boolean equalsIsCheckable = false;
    public boolean isEqualsCheckable() {
        return equalsIsCheckable;
    }


    //-----------------------------------------------------------------------
    /**
     *  Verifies that {@link #collection} and {@link #confirmed} have
     *  identical state.
     */
    public void verify() {
        int confirmedSize = confirmed.size();
        assertEquals("Collection size should match confirmed collection's",
                confirmedSize, collection.size());
        assertEquals("Collection isEmpty() result should match confirmed "
                + " collection's", confirmed.isEmpty(), collection.isEmpty());

        // verify the collections are the same by attempting to match each
        // object in the collection and confirmed collection.  To account for
        // duplicates and differing orders, each confirmed element is copied
        // into an array and a flag is maintained for each element to determine
        // whether it has been matched once and only once.  If all elements in
        // the confirmed collection are matched once and only once and there
        // aren't any elements left to be matched in the collection,
        // verification is a success.

        // copy each collection value into an array
        Object[] confirmedValues = new Object[confirmedSize];

        Iterator iter;

        iter = confirmed.iterator();
        int pos = 0;
        while (iter.hasNext()) {
            confirmedValues[pos++] = iter.next();
        }

        // allocate an array of boolean flags for tracking values that have
        // been matched once and only once.
        boolean[] matched = new boolean[confirmedSize];

        // now iterate through the values of the collection and try to match
        // the value with one in the confirmed array.
        iter = collection.iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            boolean match = false;
            for (int i = 0; i < confirmedSize; i++) {
                if (matched[i]) {
                    // skip values already matched
                    continue;
                }
                if (o == confirmedValues[i]
                        || (o != null && o.equals(confirmedValues[i]))) {
                    // values matched
                    matched[i] = true;
                    match = true;
                    break;
                }
            }
            // no match found!
            if (!match) {
                fail("Collection should not contain a value that the "
                        + "confirmed collection does not have: " + o
                        + "\nTest: " + collection + "\nReal: " + confirmed);
            }
        }

        // make sure there aren't any unmatched values
        for (int i = 0; i < confirmedSize; i++) {
            if (!matched[i]) {
                // the collection didn't match all the confirmed values
                fail("Collection should contain all values that are in the confirmed collection"
                        + "\nTest: " + collection + "\nReal: " + confirmed);
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     *  Resets the {@link #collection} and {@link #confirmed} fields to empty
     *  collections.  Invoke this method before performing a modification
     *  test.
     */
    public void resetEmpty() {
        this.collection = makeCollection();
        this.confirmed = makeConfirmedCollection();
    }

    /**
     *  Resets the {@link #collection} and {@link #confirmed} fields to full
     *  collections.  Invoke this method before performing a modification
     *  test.
     */
    public void resetFull() {
        this.collection = makeFullCollection();
        this.confirmed = makeConfirmedFullCollection();
    }

    //-----------------------------------------------------------------------
    /**
     *  Returns a confirmed empty collection.
     *  For instance, an {@link java.util.ArrayList} for lists or a
     *  {@link java.util.HashSet} for sets.
     *
     *  @return a confirmed empty collection
     */
    public abstract Collection makeConfirmedCollection();

    /**
     *  Returns a confirmed full collection.
     *  For instance, an {@link java.util.ArrayList} for lists or a
     *  {@link java.util.HashSet} for sets.  The returned collection
     *  should contain the elements returned by {@link #getFullElements()}.
     *
     *  @return a confirmed full collection
     */
    public abstract Collection makeConfirmedFullCollection();

    /**
     * Return a new, empty {@link java.util.Collection} to be used for testing.
     */
    public abstract Collection makeCollection();

    /**
     *  Returns a full collection to be used for testing.  The collection
     *  returned by this method should contain every element returned by
     *  {@link #getFullElements()}.  The default implementation, in fact,
     *  simply invokes <code>addAll</code> on an empty collection with
     *  the results of {@link #getFullElements()}.  Override this default
     *  if your collection doesn't support addAll.
     */
    public Collection makeFullCollection() {
        Collection c = makeCollection();
        c.addAll(Arrays.asList(getFullElements()));
        return c;
    }

    /**
     *  Returns an empty collection for Object tests.
     */
    public Object makeObject() {
        return makeCollection();
    }

    /**
     * Creates a new Map Entry that is independent of the first and the map.
     */
    public Map.Entry cloneMapEntry(Map.Entry entry) {
        Map<Object, Object> map = new HashMap<Object, Object>();
        map.put(entry.getKey(), entry.getValue());
        return map.entrySet().iterator().next();
    }

    //-----------------------------------------------------------------------
    /**
     *  Returns an array of objects that are contained in a collection
     *  produced by {@link #makeFullCollection()}.  Every element in the
     *  returned array <I>must</I> be an element in a full collection.<P>
     *  The default implementation returns a heterogenous array of
     *  objects with some duplicates. null is added if allowed.
     *  Override if you require specific testing elements.  Note that if you
     *  override {@link #makeFullCollection()}, you <I>must</I> override
     *  this method to reflect the contents of a full collection.
     */
    public Object[] getFullElements() {
        if (isNullSupported()) {
            List<Object> list = new ArrayList<Object>();
            list.addAll(Arrays.asList(getFullNonNullElements()));
            list.add(4, null);
            return list.toArray();
        } else {
            return getFullNonNullElements().clone();
        }
    }

    /**
     *  Returns an array of elements that are <I>not</I> contained in a
     *  full collection.  Every element in the returned array must
     *  not exist in a collection returned by {@link #makeFullCollection()}.
     *  The default implementation returns a heterogenous array of elements
     *  without null.  Note that some of the tests add these elements
     *  to an empty or full collection, so if your collection restricts
     *  certain kinds of elements, you should override this method.
     */
    public Object[] getOtherElements() {
        return getOtherNonNullElements();
    }

    //-----------------------------------------------------------------------
    /**
     *  Returns a list of elements suitable for return by
     *  {@link #getFullElements()}.  The array returned by this method
     *  does not include null, but does include a variety of objects
     *  of different types.  Override getFullElements to return
     *  the results of this method if your collection does not support
     *  the null element.
     */
    public Object[] getFullNonNullElements() {
        return getAllowed("full",
                "",
                "One",
                2,
                "Three",
                4,
                "One",
                (double) 5,
                (float) 6,
                "Seven",
                "Eight",
                "Nine",
                10,
                (short) 11,
                (long) 12,
                "Thirteen",
                "14",
                "15",
                (byte) 16);
    }

    /**
     *  Returns the default list of objects returned by
     *  {@link #getOtherElements()}.  Includes many objects
     *  of different types.
     */
    public Object[] getOtherNonNullElements() {
        return getAllowed("other",
                0,
                (float) 0,
                (double) 0,
                "Zero",
                (short) 0,
                (byte) 0,
                (long) 0,
                '\u0000',
                "0");
    }

    protected Class[] disallowedElementTypes;

    private Object[] getAllowed(String disallowPrefix, Object... elements) {
        if (disallowedElementTypes == null) {
            return elements;
        }
        List<Class> disallows = Arrays.asList(disallowedElementTypes);
        for (int i = 0; i < elements.length; i++) {
            if (disallows.contains(elements[i].getClass())) {
                elements[i] = disallowPrefix + i;
            }
        }
        return elements;
    }

    /**
     *  Returns a list of string elements suitable for return by
     *  {@link #getFullElements()}.  Override getFullElements to return
     *  the results of this method if your collection does not support
     *  heterogenous elements or the null element.
     */
    public Object[] getFullNonNullStringElements() {
        return new Object[]{"If", "the", "dull", "substance", "of", "my", "flesh", "were", "thought",
                            "Injurious", "distance", "could", "not", "stop", "my", "way",
        };
    }

    /**
     *  Returns a list of string elements suitable for return by
     *  {@link #getOtherElements()}.  Override getOtherElements to return
     *  the results of this method if your collection does not support
     *  heterogenous elements or the null element.
     */
    public Object[] getOtherNonNullStringElements() {
        return new Object[]{"For", "then", "despite", /* of */"space", "I", "would", "be", "brought",
                            "From", "limits", "far", "remote", "where", "thou", "dost", "stay"
        };
    }

    // Tests
    //-----------------------------------------------------------------------
    /**
     *  Tests {@link java.util.Collection#add(Object)}.
     */
    public void testCollectionAdd() {
        if (!isAddSupported()) {
            return;
        }

        Object[] elements = getFullElements();
        for (Object element : elements) {
            resetEmpty();
            boolean r = collection.add(element);
            confirmed.add(element);
            verify();
            assertTrue("Empty collection changed after add", r);
            assertEquals("Collection size is 1 after first add", 1, collection.size());
        }

        resetEmpty();
        int size = 0;
        for (Object element1 : elements) {
            boolean r = collection.add(element1);
            confirmed.add(element1);
            verify();
            if (r) {
                size++;
            }
            assertEquals("Collection size should grow after add",
                    size, collection.size());
            assertTrue("Collection should contain added element",
                    collection.contains(element1));
        }
    }


    /**
     *  Tests {@link java.util.Collection#addAll(java.util.Collection)}.
     */
    public void testCollectionAddAll() {
        if (!isAddSupported()) {
            return;
        }

        resetEmpty();
        Object[] elements = getFullElements();
        boolean r = collection.addAll(Arrays.asList(elements));
        confirmed.addAll(Arrays.asList(elements));
        verify();
        assertTrue("Empty collection should change after addAll", r);
        for (Object element1 : elements) {
            assertTrue("Collection should contain added element",
                    collection.contains(element1));
        }

        resetFull();
        int size = collection.size();
        elements = getOtherElements();
        r = collection.addAll(Arrays.asList(elements));
        confirmed.addAll(Arrays.asList(elements));
        verify();
        assertTrue("Full collection should change after addAll", r);
        for (Object element : elements) {
            assertTrue("Full collection should contain added element",
                    collection.contains(element));
        }
        assertEquals("Size should increase after addAll",
                     size + elements.length, collection.size());

        resetFull();
        size = collection.size();
        r = collection.addAll(Arrays.asList(getFullElements()));
        confirmed.addAll(Arrays.asList(getFullElements()));
        verify();
        if (r) {
            assertTrue("Size should increase if addAll returns true",
                       size < collection.size());
        } else {
            assertEquals("Size should not change if addAll returns false",
                         size, collection.size());
        }
    }


    /**
     *  If {@link #isAddSupported()} returns false, tests that add operations
     *  raise <code>UnsupportedOperationException.
     */
    public void testUnsupportedAdd() {
        if (isAddSupported()) {
            return;
        }

        resetEmpty();
        try {
            collection.add(new Object());
            fail("Emtpy collection should not support add.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        // make sure things didn't change even if the expected exception was
        // thrown.
        verify();

        try {
            collection.addAll(Arrays.asList(getFullElements()));
            fail("Emtpy collection should not support addAll.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        // make sure things didn't change even if the expected exception was
        // thrown.
        verify();

        resetFull();
        try {
            collection.add(new Object());
            fail("Full collection should not support add.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        // make sure things didn't change even if the expected exception was
        // thrown.
        verify();

        try {
            collection.addAll(Arrays.asList(getOtherElements()));
            fail("Full collection should not support addAll.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        // make sure things didn't change even if the expected exception was
        // thrown.
        verify();
    }


    /**
     *  Test {@link java.util.Collection#clear()}.
     */
    public void testCollectionClear() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();
        collection.clear(); // just to make sure it doesn't raise anything
        verify();

        resetFull();
        collection.clear();
        confirmed.clear();
        verify();
    }


    /**
     *  Tests {@link java.util.Collection#contains(Object)}.
     */
    public void testCollectionContains() {
        Object[] elements;

        resetEmpty();
        elements = getFullElements();
        for (int i = 0; i < elements.length; i++) {
            assertTrue("Empty collection shouldn't contain element[" + i + "]",
                       !collection.contains(elements[i]));
        }
        // make sure calls to "contains" don't change anything
        verify();

        elements = getOtherElements();
        for (int i = 0; i < elements.length; i++) {
            assertTrue("Empty collection shouldn't contain element[" + i + "]",
                       !collection.contains(elements[i]));
        }
        // make sure calls to "contains" don't change anything
        verify();

        resetFull();
        elements = getFullElements();
        for (int i = 0; i < elements.length; i++) {
            assertTrue("Full collection should contain element[" + i + "]",
                       collection.contains(elements[i]));
        }
        // make sure calls to "contains" don't change anything
        verify();

        resetFull();
        elements = getOtherElements();
        for (Object element : elements) {
            assertTrue("Full collection shouldn't contain element",
                    !collection.contains(element));
        }
    }


    /**
     *  Tests {@link java.util.Collection#containsAll(java.util.Collection)}.
     */
    public void testCollectionContainsAll() {
        resetEmpty();
        Collection<Object> col = new HashSet<Object>();
        assertTrue("Every Collection should contain all elements of an "
                + "empty Collection.", collection.containsAll(col));
        col.addAll(Arrays.asList(getOtherElements()));
        assertTrue("Empty Collection shouldn't contain all elements of "
                + "a non-empty Collection.", !collection.containsAll(col));
        // make sure calls to "containsAll" don't change anything
        verify();

        resetFull();
        assertTrue("Full collection shouldn't contain other elements",
                   !collection.containsAll(col));

        col.clear();
        col.addAll(Arrays.asList(getFullElements()));
        assertTrue("Full collection should containAll full elements",
                   collection.containsAll(col));
        // make sure calls to "containsAll" don't change anything
        verify();

        int min = (getFullElements().length < 2 ? 0 : 2);
        int max = (getFullElements().length == 1 ? 1
                : (getFullElements().length <= 5 ? getFullElements().length - 1 : 5));
        col = Arrays.asList(getFullElements()).subList(min, max);
        assertTrue("Full collection should containAll partial full "
                + "elements", collection.containsAll(col));
        assertTrue("Full collection should containAll itself",
                   collection.containsAll(collection));
        // make sure calls to "containsAll" don't change anything
        verify();

        col = new ArrayList<Object>();
        col.addAll(Arrays.asList(getFullElements()));
        col.addAll(Arrays.asList(getFullElements()));
        assertTrue("Full collection should containAll duplicate full "
                + "elements", collection.containsAll(col));

        // make sure calls to "containsAll" don't change anything
        verify();
    }

    /**
     *  Tests {@link java.util.Collection#isEmpty()}.
     */
    public void testCollectionIsEmpty() {
        resetEmpty();
        assertEquals("New Collection should be empty.",
                     true, collection.isEmpty());
        // make sure calls to "isEmpty() don't change anything
        verify();

        resetFull();
        assertEquals("Full collection shouldn't be empty",
                     false, collection.isEmpty());
        // make sure calls to "isEmpty() don't change anything
        verify();
    }


    /**
     *  Tests the read-only functionality of {@link java.util.Collection#iterator()}.
     */
    public void testCollectionIterator() {
        resetEmpty();
        Iterator it1 = collection.iterator();
        assertEquals("Iterator for empty Collection shouldn't have next.",
                     false, it1.hasNext());
        try {
            it1.next();
            fail("Iterator at end of Collection should throw "
                    + "NoSuchElementException when next is called.");
        } catch (NoSuchElementException e) {
            // expected
        }
        // make sure nothing has changed after non-modification
        verify();

        resetFull();
        it1 = collection.iterator();
        for (int i = 0; i < collection.size(); i++) {
            assertTrue("Iterator for full collection should haveNext",
                       it1.hasNext());
            it1.next();
        }
        assertTrue("Iterator should be finished", !it1.hasNext());

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        List<Object> list = new ArrayList<Object>();
        it1 = collection.iterator();
        for (int i = 0; i < collection.size(); i++) {
            Object next = it1.next();
            assertTrue("Collection should contain element returned by "
                    + "its iterator", collection.contains(next));
            list.add(next);
        }
        try {
            it1.next();
            fail("iterator.next() should raise NoSuchElementException "
                    + "after it finishes");
        } catch (NoSuchElementException e) {
            // expected
        }
        // make sure nothing has changed after non-modification
        verify();
    }


    /**
     *  Tests removals from {@link java.util.Collection#iterator()}.
     */
    public void testCollectionIteratorRemove() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();
        try {
            collection.iterator().remove();
            fail("New iterator.remove should raise IllegalState");
        } catch (IllegalStateException e) {
            // expected
        }
        verify();

        try {
            Iterator iter = collection.iterator();
            iter.hasNext();
            iter.remove();
            fail("New iterator.remove should raise IllegalState "
                    + "even after hasNext");
        } catch (IllegalStateException e) {
            // expected
        }
        verify();

        resetFull();
        int size = collection.size();
        Iterator iter = collection.iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            // TreeMap reuses the Map Entry, so the verify below fails
            // Clone it here if necessary
            if (o instanceof Map.Entry) {
                o = cloneMapEntry((Map.Entry) o);
            }
            iter.remove();

            // if the elements aren't distinguishable, we can just remove a
            // matching element from the confirmed collection and verify
            // contents are still the same.  Otherwise, we don't have the
            // ability to distinguish the elements and determine which to
            // remove from the confirmed collection (in which case, we don't
            // verify because we don't know how).
            //
            // see areEqualElementsDistinguishable()
            if (!areEqualElementsDistinguishable()) {
                confirmed.remove(o);
                verify();
            }

            size--;
            assertEquals("Collection should shrink by one after "
                    + "iterator.remove", size, collection.size());
        }
        assertTrue("Collection should be empty after iterator purge",
                   collection.isEmpty());

        resetFull();
        iter = collection.iterator();
        iter.next();
        iter.remove();
        try {
            iter.remove();
            fail("Second iter.remove should raise IllegalState");
        } catch (IllegalStateException e) {
            // expected
        }
    }


    /**
     *  Tests {@link java.util.Collection#remove(Object)}.
     */
    public void testCollectionRemove() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();
        Object[] elements = getFullElements();
        for (Object element : elements) {
            assertTrue("Shouldn't remove nonexistent element",
                    !collection.remove(element));
            verify();
        }

        Object[] other = getOtherElements();

        resetFull();
        for (Object anOther : other) {
            assertTrue("Shouldn't remove nonexistent other element",
                    !collection.remove(anOther));
            verify();
        }

        int size = collection.size();
        for (Object element1 : elements) {
            resetFull();
            assertTrue("Collection should remove extant element: " + element1,
                    collection.remove(element1));

            // if the elements aren't distinguishable, we can just remove a
            // matching element from the confirmed collection and verify
            // contents are still the same.  Otherwise, we don't have the
            // ability to distinguish the elements and determine which to
            // remove from the confirmed collection (in which case, we don't
            // verify because we don't know how).
            //
            // see areEqualElementsDistinguishable()
            if (!areEqualElementsDistinguishable()) {
                confirmed.remove(element1);
                verify();
            }

            assertEquals("Collection should shrink after remove",
                    size - 1, collection.size());
        }
    }


    /**
     *  Tests {@link java.util.Collection#removeAll(java.util.Collection)}.
     */
    public void testCollectionRemoveAll() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();
        assertTrue("Emtpy collection removeAll should return false for "
                + "empty input",
                !collection.removeAll(Collections.EMPTY_SET));
        verify();

        assertTrue("Emtpy collection removeAll should return false for "
                + "nonempty input",
                !collection.removeAll(new ArrayList<Object>(collection)));
        verify();

        resetFull();
        assertTrue("Full collection removeAll should return false for "
                + "empty input",
                !collection.removeAll(Collections.EMPTY_SET));
        verify();

        assertTrue("Full collection removeAll should return false for other elements",
                !collection.removeAll(Arrays.asList(getOtherElements())));
        verify();

        assertTrue("Full collection removeAll should return true for full elements",
                collection.removeAll(new HashSet<Object>(collection)));
        confirmed.removeAll(new HashSet<Object>(confirmed));
        verify();

        resetFull();
        int size = collection.size();
        int min = (getFullElements().length < 2 ? 0 : 2);
        int max = (getFullElements().length == 1 ? 1
                : (getFullElements().length <= 5 ? getFullElements().length - 1 : 5));
        Collection all = Arrays.asList(getFullElements()).subList(min, max);
        assertTrue("Full collection removeAll should work",
                   collection.removeAll(all));
        confirmed.removeAll(all);
        verify();

        assertTrue("Collection should shrink after removeAll",
                   collection.size() < size);
        for (Object anAll : all) {
            assertTrue("Collection shouldn't contain removed element",
                    !collection.contains(anAll));
        }
    }


    /**
     *  Tests {@link java.util.Collection#retainAll(java.util.Collection)}.
     */
    public void testCollectionRetainAll() {
        if (!isRemoveSupported()) {
            return;
        }

        resetEmpty();
        List<Object> elements = Arrays.asList(getFullElements());
        List<Object> other = Arrays.asList(getOtherElements());

        assertTrue("Empty retainAll() should return false",
                   !collection.retainAll(Collections.EMPTY_SET));
        verify();

        assertTrue("Empty retainAll() should return false",
                   !collection.retainAll(elements));
        verify();

        resetFull();
        assertTrue("Collection should change from retainAll empty",
                   collection.retainAll(Collections.EMPTY_SET));
        confirmed.retainAll(Collections.EMPTY_SET);
        verify();

        resetFull();
        assertTrue("Collection changed from retainAll other",
                   collection.retainAll(other));
        confirmed.retainAll(other);
        verify();

        resetFull();
        int size = collection.size();
        assertTrue("Collection shouldn't change from retainAll elements",
                   !collection.retainAll(elements));
        verify();
        assertEquals("Collection size shouldn't change", size,
                     collection.size());

        if (getFullElements().length > 1) {
            resetFull();
            int min = (getFullElements().length < 2 ? 0 : 2);
            int max = (getFullElements().length <= 5 ? getFullElements().length - 1 : 5);
            assertTrue("Collection should changed by partial retainAll",
                       collection.retainAll(elements.subList(min, max)));
            confirmed.retainAll(elements.subList(min, max));
            verify();

            for (Object aCollection : collection) {
                assertTrue("Collection only contains retained element",
                        elements.subList(min, max).contains(aCollection));
            }
        }

        resetFull();
        Set<Object> set = new HashSet<Object>(elements);
        size = collection.size();
        assertTrue("Collection shouldn't change from retainAll without "
                + "duplicate elements", !collection.retainAll(set));
        verify();
        assertEquals("Collection size didn't change from nonduplicate "
                + "retainAll", size, collection.size());
    }


    /**
     *  Tests {@link java.util.Collection#size()}.
     */
    public void testCollectionSize() {
        resetEmpty();
        assertEquals("Size of new Collection is 0.", 0, collection.size());

        resetFull();
        assertTrue("Size of full collection should be greater than zero",
                   collection.size() > 0);
    }


    /**
     *  Tests {@link java.util.Collection#toArray()}.
     */
    public void testCollectionToArray() {
        resetEmpty();
        assertEquals("Empty Collection should return empty array for toArray",
                     0, collection.toArray().length);

        resetFull();
        Object[] array = collection.toArray();
        assertEquals("Full collection toArray should be same size as "
                + "collection", array.length, collection.size());
        Object[] confirmedArray = confirmed.toArray();
        assertEquals("length of array from confirmed collection should "
                + "match the length of the collection's array",
                confirmedArray.length, array.length);
        boolean[] matched = new boolean[array.length];

        for (int i = 0; i < array.length; i++) {
            assertTrue("Collection should contain element in toArray",
                    collection.contains(array[i]));

            boolean match = false;
            // find a match in the confirmed array
            for (int j = 0; j < array.length; j++) {
                // skip already matched
                if (matched[j]) {
                    continue;
                }
                if (array[i] == confirmedArray[j]
                        || (array[i] != null && array[i].equals(confirmedArray[j]))) {
                    matched[j] = true;
                    match = true;
                    break;
                }
            }
            if (!match) {
                fail("element " + i + " in returned array should be found "
                        + "in the confirmed collection's array");
            }
        }
        for (boolean aMatched : matched) {
            assertEquals("Collection should return all its elements in "
                    + "toArray", true, aMatched);
        }
    }


    /**
     *  Tests {@link java.util.Collection#toArray(Object[])}.
     */
    public void testCollectionToArray2() {
        resetEmpty();
        Object[] a = new Object[]{new Object(), null, null};
        Object[] array = collection.toArray(a);
        assertEquals("Given array shouldn't shrink", array, a);
        assertEquals("Last element should be set to null", a[0], null);
        verify();

        resetFull();
        try {
            //noinspection ToArrayCallWithZeroLengthArrayArgument
            collection.toArray(new Void[0]);
            fail("toArray(new Void[0]) should raise ArrayStore");
        } catch (ArrayStoreException e) {
            // expected
        }
        verify();

        try {
            collection.toArray(null);
            fail("toArray(null) should raise NPE");
        } catch (NullPointerException e) {
            // expected
        }
        verify();

        //noinspection ToArrayCallWithZeroLengthArrayArgument
        array = collection.toArray(new Object[0]);
        a = collection.toArray();
        assertEquals("toArrays should be equal",
                     Arrays.asList(array), Arrays.asList(a));

        // Figure out if they're all the same class
        // TODO: It'd be nicer to detect a common superclass
        Set<Object> classes = new HashSet<Object>();
        for (Object anArray : array) {
            classes.add((anArray == null) ? null : anArray.getClass());
        }
        if (classes.size() > 1) {
            return;
        }

        Class cl = (Class) classes.iterator().next();
        if (Map.Entry.class.isAssignableFrom(cl)) {  // check needed for protective cases like Predicated/Unmod map entrySet
            cl = Map.Entry.class;
        }
        a = (Object[]) Array.newInstance(cl, 0);
        array = collection.toArray(a);
        assertEquals("toArray(Object[]) should return correct array type",
                     a.getClass(), array.getClass());
        assertEquals("type-specific toArrays should be equal",
                     Arrays.asList(array),
                     Arrays.asList(collection.toArray()));
        verify();
    }


    /**
     *  Tests <code>toString</code> on a collection.
     */
    public void testCollectionToString() {
        resetEmpty();
        assertTrue("toString shouldn't return null",
                   collection.toString() != null);

        resetFull();
        assertTrue("toString shouldn't return null",
                   collection.toString() != null);
    }


    /**
     *  If isRemoveSupported() returns false, tests to see that remove
     *  operations raise an UnsupportedOperationException.
     */
    public void testUnsupportedRemove() {
        if (isRemoveSupported()) {
            return;
        }

        resetEmpty();
        try {
            collection.clear();
            fail("clear should raise UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        verify();

        try {
            collection.remove(null);
            fail("remove should raise UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        verify();

        try {
            collection.removeAll(null);
            fail("removeAll should raise UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        verify();

        try {
            collection.retainAll(null);
            fail("removeAll should raise UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        verify();

        resetFull();
        try {
            Iterator iterator = collection.iterator();
            iterator.next();
            iterator.remove();
            fail("iterator.remove should raise UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        verify();

    }


    /**
     *  Tests that the collection's iterator is fail-fast.
     */
    public void testCollectionIteratorFailFast() {
        if (!isFailFastSupported()) {
            return;
        }

        if (isAddSupported()) {
            resetFull();
            try {
                Iterator iter = collection.iterator();
                Object o = getOtherElements()[0];
                collection.add(o);
                confirmed.add(o);
                iter.next();
                fail("next after add should raise ConcurrentModification");
            } catch (ConcurrentModificationException e) {
                // expected
            }
            verify();

            resetFull();
            try {
                Iterator iter = collection.iterator();
                collection.addAll(Arrays.asList(getOtherElements()));
                confirmed.addAll(Arrays.asList(getOtherElements()));
                iter.next();
                fail("next after addAll should raise ConcurrentModification");
            } catch (ConcurrentModificationException e) {
                // expected
            }
            verify();
        }

        if (!isRemoveSupported()) {
            return;
        }

        resetFull();
        try {
            Iterator iter = collection.iterator();
            collection.clear();
            iter.next();
            fail("next after clear should raise ConcurrentModification");
        } catch (ConcurrentModificationException e) {
            // expected
        } catch (NoSuchElementException e) {
            // (also legal given spec)
        }

        resetFull();
        try {
            Iterator iter = collection.iterator();
            collection.remove(getFullElements()[0]);
            iter.next();
            fail("next after remove should raise ConcurrentModification");
        } catch (ConcurrentModificationException e) {
            // expected
        }

        resetFull();
        try {
            Iterator iterator = collection.iterator();
            List subList = Arrays.asList(getFullElements()).subList(2, 5);
            collection.removeAll(subList);
            iterator.next();
            fail("next after removeAll should raise ConcurrentModification");
        } catch (ConcurrentModificationException e) {
            // expected
        }

        resetFull();
        try {
            Iterator iterator = collection.iterator();
            List subList = Arrays.asList(getFullElements()).subList(2, 5);
            collection.retainAll(subList);
            iterator.next();
            fail("next after retainAll should raise ConcurrentModification");
        } catch (ConcurrentModificationException e) {
            // expected
        }
    }

    public void testSerializeDeserializeThenCompare() throws Exception {
        Object obj = makeCollection();
        if (obj instanceof Serializable && isTestSerialization()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(buffer);
            out.writeObject(obj);
            out.close();

            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            Object dest = in.readObject();
            in.close();
            if (isEqualsCheckable()) {
                assertEquals("obj != deserialize(serialize(obj)) - EMPTY Collection", obj, dest);
            }
        }
        obj = makeFullCollection();
        if (obj instanceof Serializable && isTestSerialization()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(buffer);
            out.writeObject(obj);
            out.close();

            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            Object dest = in.readObject();
            in.close();
            if (isEqualsCheckable()) {
                assertEquals("obj != deserialize(serialize(obj)) - FULL Collection", obj, dest);
            }
        }
    }


    public boolean isTestSerialization() {
        return false;
    }
}
