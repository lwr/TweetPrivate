/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.var;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Wrapper map for {@link VarObject} to be used as normal java map.
 * All values in the map are normal java objects (instead of variants).
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
class VarVarObjectNormalizer extends AbstractMap<String, Object> {


    @NotNull
    VarObject impl;


    private VarVarObjectNormalizer(VarObject impl) {
        if (impl == null) {
            throw new NullPointerException();
        }
        this.impl = impl;
    }

    @Override
    public int size() {
        return impl.size();
    }

    @Override
    public Object get(Object key) {
        return (key instanceof String)
                ? impl.get((String) key).normalize(isModifiable())
                : null;
    }

    @Override
    public boolean containsKey(Object key) {
        return (key instanceof String) && impl.containsKey((String) key);
    }

    @Override
    public Set<String> keySet() {
        return impl.keySet();
    }

    private transient Set<Entry<String, Object>> entrySet;

    public Set<Entry<String, Object>> entrySet() {
        if (entrySet == null) {
            entrySet = new EntrySet();
        }
        return entrySet;
    }


    final boolean isModifiable() {
        return this instanceof Modifiable;
    }

    static Map<String, Object> getInstance(@NotNull VarObject impl, boolean modifiable) {
        return modifiable
                ? new VarVarObjectNormalizer.Modifiable(impl)
                : new VarVarObjectNormalizer(impl);
    }


    private static class Modifiable extends VarVarObjectNormalizer {
        Modifiable(@NotNull VarObject impl) {
            super(impl);
        }

        @Override
        public Object put(String key, Object value) {
            Variant oldVariant = impl.put(key, Variant.wrap(value));
            return (oldVariant == null) ? null : oldVariant.normalize(isModifiable());
        }

        @Override
        public void clear() {
            impl.clear();
        }

        @Override
        public Object remove(Object key) {
            if (!(key instanceof String)) {
                return null;
            }
            Variant value = impl.remove((String) key);
            return (value == null) ? null : value.normalize(isModifiable());
        }
    }


    private class EntrySet extends AbstractSet<Entry<String, Object>> {


        @Override
        public int size() {
            return impl.size();
        }


        public Iterator<Entry<String, Object>> iterator() {

            final Iterator<Entry<String, Variant>> itr = impl.impl.entrySet().iterator();


            return new Iterator<Entry<String, Object>>() {

                public boolean hasNext() {
                    return itr.hasNext();
                }

                public Entry<String, Object> next() {
                    return new SimpleEntry(itr.next());
                }

                public void remove() {
                    if (!isModifiable()) {
                        throw new UnsupportedOperationException();
                    }
                    itr.remove();
                }
            };
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry e = (Entry) o;
            if (!(e.getKey() instanceof String)) {
                return false;
            }
            Variant val = impl.getOrNull((String) e.getKey());
            return (val != null)
                    && eq(val.normalize(isModifiable()), e.getValue());
        }

        @Override
        public boolean remove(Object o) {
            if (!isModifiable()) {
                throw new UnsupportedOperationException();
            }
            return contains(o)
                    && (impl.remove((String) ((Entry) o).getKey()) != null);
        }

        @Override
        public void clear() {
            if (!isModifiable()) {
                throw new UnsupportedOperationException();
            }
            impl.clear();
        }
    }


    private class SimpleEntry implements Entry<String, Object> {

        private Entry<String, Variant> e;
        private Object value;


        public SimpleEntry(Entry<String, Variant> impl) {
            this.e = impl;
            this.value = e.getValue().normalize(isModifiable());
        }

        public String getKey() {
            return e.getKey();
        }

        public Object getValue() {
            return value;
        }

        public Object setValue(Object newValue) {
            Object oldValue = value;
            e.setValue(Variant.wrap(newValue));
            this.value = newValue;
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            //noinspection SimplifiableIfStatement
            if (!(o instanceof Entry)) {
                return false;
            }
            return eq(e.getKey(), ((Entry) o).getKey()) && eq(value, ((Entry) o).getValue());
        }

        @Override
        public int hashCode() {
            return e.getKey().hashCode()
                    ^ ((value == null) ? 0 : value.hashCode());
        }

        @Override
        public String toString() {
            return e.getKey() + "=" + value;
        }
    }

    private static boolean eq(@Nullable Object o1, @Nullable Object o2) {
        return (o1 == null ? o2 == null : o1.equals(o2));
    }
}
