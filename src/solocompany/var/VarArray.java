/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.var;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Variant for an array container.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class VarArray extends Variant {


    final List<Variant> impl;

    public VarArray() {
        this(new ArrayList<Variant>(), false);
    }


    VarArray(@NotNull List<Variant> impl, boolean copy) {
        this.impl = copy ? new ArrayList<Variant>(impl) : impl;
    }


    @Override
    protected String getTypeName() {
        return "ARY";
    }


    @Override
    public String toString() {
        return String.valueOf(impl);
    }


    @NotNull
    @Override
    public final List<Object> normalize() {
        return normalize(true);
    }

    @NotNull
    @Override
    public final List<Object> normalize(boolean modifiable) {
        return VarArrayNormalizer.getInstance(impl, modifiable);
    }

    @NotNull
    @SuppressWarnings({"unchecked"})
    public static <T> List<T> newNormalizeList() {
        return (List<T>) new VarArray().normalize(true);
    }


    public final void ensureSize(int size) {
        for (int i = size(); i < size; i++) {
            impl.add(VarNull.INSTANCE);
        }
    }

    public final void setSize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException();
        } else if (size < size()) {
            impl.subList(size, size()).clear();
        } else {
            ensureSize(size);
        }
    }


    public int size() {
        return impl.size();
    }


    @NotNull
    public Variant get(int index) {
        return (index >= 0 && index < size()) ? impl.get(index) : VarNull.INSTANCE;
    }


    public void add(int item) {
        add(new VarInt(item));
    }


    public void add(long item) {
        add(new VarLong(item));
    }


    public void add(Object item) {
        impl.add(Variant.wrap(item));
    }


    @Nullable
    public final Variant set(int index, @Nullable Object item) {
        if (impl.size() <= index) {
            ensureSize(index + 1);
        }
        return impl.set(index, Variant.wrap(item));
    }


    @NotNull
    public final VarArray copyArray() {
        return new VarArray(impl, false);
    }


    @NotNull
    public final VarArray deepCopyArray() {
        VarArray result = copyArray();
        for (int i = 0; i < result.size(); i++) {
            Variant v1 = result.get(i);
            Variant v2 = v1.deepCopyIfRequired();
            if (v1 != v2) {
                result.set(i, v2);
            }
        }
        return result;
    }


    @NotNull
    @Override
    public String getDebugInfo(int indentLevel) {
        StringBuilder buf = new StringBuilder();
        buf.append("[").append(getTypeName()).append(":(size=").append(size()).append("):...]");
        if (indentLevel > 10) {
            return buf.append("(NestedTooDeep)").toString();
        }
        buf.append(':');
        int nextLevel = indentLevel + 1;
        for (int i = 0; i < size(); i++) {
            Variant val = get(i);
            buf.append('\n');
            for (int j = indentLevel; j > 0; j--) {
                buf.append('\t');
            }
            buf.append('[').append(i);
            buf.append("]: ").append(val.getDebugInfo(nextLevel));
        }
        return buf.toString();
    }


    @NotNull
    public static VarArray valueOfArray(@NotNull Object array) {
        Variant[] data = new Variant[java.lang.reflect.Array.getLength(array)];
        for (int i = 0; i < data.length; i++) {
            data[i] = Variant.wrap(java.lang.reflect.Array.get(array, i));
        }
        return valueOf(data);
    }


    @NotNull
    public static VarArray valueOf(@NotNull Object... array) {
        return valueOfArray(array);
    }


    @NotNull
    public static VarArray valueOf(@NotNull Variant... data) {
        return valueOf(Arrays.asList(data));
    }


    @NotNull
    public static <E extends Variant> VarArray valueOf(@NotNull List<E> data) {
        // Should we protect the list with Collections.checkedList?
        // List<TVariant> checkedList = Collections.checkedList(data, E.class);
        @SuppressWarnings("unchecked")
        List<Variant> typeUncheckedList = (List<Variant>) data;
        return new VarArray(typeUncheckedList, false);
    }
}
