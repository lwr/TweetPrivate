/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.var;

import java.util.*;

/**
 * Wrapper list for {@link VarArray} to be used as normal java list.
 * All elements in the list are normal java objects (instead of variants).
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
class VarArrayNormalizer extends AbstractList<Object> implements RandomAccess {


    List<Variant> impl;


    private VarArrayNormalizer(List<Variant> impl) {
        this.impl = impl;
    }

    public int size() {
        return impl.size();
    }

    public Object get(int index) {
        return impl.get(index).normalize(isModifiable());
    }

    public List<Object> subList(int fromIndex, int toIndex) {
        return getInstance(impl.subList(fromIndex, toIndex), isModifiable());
    }

    private boolean isModifiable() {
        return this instanceof Modifiable;
    }

    private void checkModifiable() {
        if (!(this instanceof Modifiable)) {
            throw new UnsupportedOperationException();
        }
    }

    static List<Object> getInstance(List<Variant> impl, boolean modifiable) {
        return modifiable
                ? new VarArrayNormalizer.Modifiable(impl)
                : new VarArrayNormalizer(impl);
    }

    public Iterator<Object> iterator() {
        return new IteratorDecorator(impl.iterator());
    }

    public ListIterator<Object> listIterator(int index) {
        return new ListIteratorDecorator(impl.listIterator(index));
    }

    public ListIterator<Object> listIterator() {
        return listIterator(0);
    }


    private class IteratorDecorator implements Iterator<Object> {
        Iterator<Variant> implItr;

        private IteratorDecorator(Iterator<Variant> implItr) {
            this.implItr = implItr;
        }

        public boolean hasNext() {
            return implItr.hasNext();
        }

        public Object next() {
            return implItr.next().normalize(isModifiable());
        }

        public void remove() {
            implItr.remove();
        }
    }

    private class ListIteratorDecorator extends IteratorDecorator implements ListIterator<Object> {
        private ListIteratorDecorator(ListIterator<Variant> implItr) {
            super(implItr);
        }

        public boolean hasPrevious() {
            return ((ListIterator<Variant>) implItr).hasPrevious();
        }

        public Object previous() {
            return ((ListIterator<Variant>) implItr).previous().normalize(isModifiable());
        }

        public int nextIndex() {
            return ((ListIterator<Variant>) implItr).nextIndex();
        }

        public int previousIndex() {
            return ((ListIterator<Variant>) implItr).previousIndex();
        }

        public void set(Object o) {
            checkModifiable();
            ((ListIterator<Variant>) implItr).set(Variant.wrap(o));
        }

        public void add(Object o) {
            checkModifiable();
            ((ListIterator<Variant>) implItr).add(Variant.wrap(o));
        }
    }

    private static class Modifiable extends VarArrayNormalizer {

        private Modifiable(List<Variant> impl) {
            super(impl);
        }

        @Override
        public Object set(int index, Object element) {
            return impl.set(index, Variant.wrap(element)).normalize(true);
        }

        @Override
        public void add(int index, Object element) {
            impl.add(index, Variant.wrap(element));
        }

        @Override
        public Object remove(int index) {
            return impl.remove(index).normalize(true);
        }

        @Override
        public void clear() {
            impl.clear();
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            impl.subList(fromIndex, toIndex).clear();
        }
    }

}
