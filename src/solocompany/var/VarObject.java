/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.var;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Variant for a map container.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class VarObject extends Variant {

    final Map<String, Variant> impl;

    public VarObject() {
        this(new LinkedHashMap<String, Variant>());
    }


    private VarObject(Map<String, Variant> impl) {
        this.impl = impl;
    }


    @Override
    protected String getTypeName() {
        return "MAP";
    }


    @Override
    public String toString() {
        return impl.toString();
    }


    @NotNull
    @Override
    public final Map<String, Object> normalize() {
        return normalize(true);
    }

    @NotNull
    @Override
    public final Map<String, Object> normalize(boolean modifiable) {
        return VarObjectNormalizer.getInstance(this, modifiable);
    }


    public boolean isEmpty() {
        return impl.isEmpty();
    }


    public int size() {
        return impl.size();
    }


    public void clear() {
        impl.clear();
    }


    public Set<String> keySet() {
        return impl.keySet();
    }


    public Set<Map.Entry<String, Variant>> entrySet() {
        return impl.entrySet();
    }


    @Override
    public boolean containsKey(String key) {
        return impl.containsKey(key);
    }


    public Variant remove(String key) {
        return impl.remove(key);
    }


    @NotNull
    public Variant removeOrNone(@Nullable String key) {
        Variant result = remove(key);
        return (result != null) ? result : VarNull.INSTANCE;
    }


    @Nullable
    @Override
    public Variant getOrNull(@Nullable String key) {
        return impl.get(key);
    }


    public Variant put(String key, @Nullable Object value) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        return impl.put(key, Variant.wrap(value));
    }


    public void putAll(VarObject m) {
        impl.putAll(m.impl);
    }


    @NotNull
    public VarObject copyMap() {
        return new VarObject(new LinkedHashMap<String, Variant>(impl));
    }


    @NotNull
    public VarObject deepCopyMap() {
        VarObject result = new VarObject();
        for (Map.Entry<String, Variant> e : impl.entrySet()) {
            result.put(e.getKey(), e.getValue().deepCopyIfRequired());
        }
        return result;
    }


    @NotNull
    private static final Map<String, String> DEFAULT_PROTECTING_MAP = new HashMap<String, String>();

    static {
        DEFAULT_PROTECTING_MAP.put("password", "***");
    }


    @NotNull
    @Override
    public String getDebugInfo(int indentLevel) {
        return getDebugInfo(indentLevel, DEFAULT_PROTECTING_MAP);
    }


    public String getDebugInfo(int indentLevel, @NotNull Map<String, String> protectingMap) {
        StringBuilder buf = new StringBuilder();
        buf.append("[").append(getTypeName()).append(":(size=").append(size()).append("):...]");
        if (indentLevel > 10) {
            return buf.append("(NestedTooDeep)").toString();
        }
        buf.append(':');
        int nextLevel = indentLevel + 1;
        for (Map.Entry<String, Variant> entry : impl.entrySet()) {
            String key = entry.getKey();
            Variant val = entry.getValue();
            buf.append('\n');
            for (int j = indentLevel; j > 0; j--) {
                buf.append('\t');
            }
            buf.append(key).append(": ");
            if (key.equals("attrs") && val instanceof VarString) {
                // convert attrs=(..password=...&...)
                //      to attrs=(..password=***&...)
                String s = val.toString();
                for (Map.Entry<String, String> e : protectingMap.entrySet()) {
                    String match = e.getKey() + '=';
                    int pos = 0;
                    int j;
                    while ((j = s.indexOf(match, pos)) != -1) {
                        j += match.length();
                        buf.append(s, 0, j).append(e.getValue());
                        j = s.indexOf('&', j);
                        pos = (j == -1) ? s.length() : j;
                    }
                    buf.append(s.substring(pos));
                }
                continue;
            }

            String protectedValue = protectingMap.get(key);
            if (protectedValue != null) {
                buf.append(protectedValue);
            } else if (val instanceof VarObject) {
                buf.append(((VarObject) val).getDebugInfo(nextLevel, protectingMap));
            } else {
                buf.append(val.getDebugInfo(nextLevel));
            }
        }
        return buf.toString();
    }
}
