/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.var;

import org.jetbrains.annotations.NotNull;

/**
 * Variant for a 32-bit integer.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class VarInt extends Variant {


    final int impl;


    public VarInt(int value) {
        this.impl = value;
    }


    @Override
    protected String getTypeName() {
        return "INT";
    }


    @Override
    public String toString() {
        return String.valueOf(impl);
    }


    @Override
    public int getInt() {
        return impl;
    }


    @Override
    public long getLong() {
        return impl;
    }


    @Override
    public Object normalize() { return impl; }


    // simple wrapper for byte (传输过程将不保留类型信息)
    static class BYTE extends VarInt {
        BYTE(byte value) { super(value); }

        @NotNull
        @Override
        public Object normalize() { return (byte) impl; }
    }

    // simple wrapper for short (传输过程将不保留类型信息)
    static class SHORT extends VarInt {
        SHORT(short value) { super(value); }

        @NotNull
        @Override
        public Object normalize() { return (short) impl; }
    }
}
