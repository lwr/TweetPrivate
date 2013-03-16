/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.var;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The general variant base class.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public abstract class Variant {

    @NotNull
    protected abstract String getTypeName();


    public int getInt() {
        throw new IllegalStateException("Not int type: " + getDebugInfo());
    }


    @NotNull
    public VarObject getMap() {
        if (this instanceof VarObject) {
            return (VarObject) this;
        }
        if (this instanceof VarNull) {
            return VarObject.emptyMap();
        }
        throw new IllegalStateException("Not map type: " + getDebugInfo());
    }


    @NotNull
    public Variant get(@Nullable String key) {
        Variant value = getOrNull(key);
        return value != null ? value : VarNull.INSTANCE;
    }


    public boolean containsKey(@Nullable String key) {
        return getOrNull(key) != null;
    }


    @Nullable
    public Variant getOrNull(@Nullable String key) {
        throw new IllegalStateException("Not Structural type: " + getDebugInfo());
    }


    @NotNull
    public abstract String toString();


    public abstract Object normalize();


    public Object normalize(boolean modifiable) { return normalize(); }


    Variant deepCopyIfRequired() {
        if (this instanceof VarObject) {
            return ((VarObject) this).deepCopyMap();
        }
        if (this instanceof VarArray) {
            return ((VarArray) this).deepCopyArray();
        }
        return this;
    }


    @NotNull
    public final String getDebugInfo() {
        return getDebugInfo(1);
    }


    @NotNull
    public String getDebugInfo(int indentLevel) {
        if (this instanceof VarNull) {
            return "[" + getTypeName() + ']';
        } else {
            return "[" + getTypeName() + ':' + toString() + ']';
        }
    }


    @NotNull
    public static Variant wrap(@Nullable Object obj) throws IllegalArgumentException {
        if (obj instanceof Variant) {
            return (Variant) obj;

        } else if (obj != null && !(obj instanceof byte[]) && obj.getClass().isArray()) {
            return VarArray.valueOfArray(obj);

        } else if (obj instanceof String) {
            return VarString.valueOf((String) obj);
        } else if (obj instanceof Boolean) {
            return VarBool.valueOf((Boolean) obj);
        } else if (obj instanceof Integer) {
            return new VarInt((Integer) obj);
        } else if (obj instanceof Short) {
            return new VarInt.SHORT((Short) obj);
        } else if (obj instanceof Byte) {
            return new VarInt.BYTE(((Byte) obj));
        } else if (obj instanceof Long
                || obj instanceof java.math.BigInteger) {
            return new VarLong(((Number) obj).longValue());
        } else if (obj instanceof Float) {
            return new VarNumber.FLOAT(((Float) obj));
        } else if (obj instanceof Number) {
            return new VarNumber(((Number) obj).doubleValue());

        } else if (obj == null) {
            return VarNull.INSTANCE;
        } else if (obj instanceof VarArrayNormalizer) {
            return VarArray.valueOf(((VarArrayNormalizer) obj).impl);
        } else if (obj instanceof VarObjectNormalizer) {
            return ((VarObjectNormalizer) obj).impl;
        }
        throw new IllegalArgumentException("Unsupported narrow type: " + obj.getClass());
    }
}
