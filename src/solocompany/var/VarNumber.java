/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.var;

import org.jetbrains.annotations.NotNull;

/**
 * Variant for a floating point which stored in double precision.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class VarNumber extends Variant {

    final double impl;


    public VarNumber(double value) {
        this.impl = value;
    }


    @Override
    protected String getTypeName() {
        return "NUM";
    }


    @Override
    public String toString() {
        return String.valueOf(impl);
    }


    @Override
    public int getInt() {
        if (impl >= Integer.MIN_VALUE && impl <= Integer.MAX_VALUE) {
            return (int) impl;
        }
        throw new IllegalStateException("Convert to int overflow: " + getDebugInfo());
    }


    @Override
    public long getLong() {
        if (impl >= Long.MIN_VALUE && impl <= Long.MAX_VALUE) {
            return (long) impl;
        }
        throw new IllegalStateException("Convert to long overflow: " + getDebugInfo());
    }


    @Override
    public Object normalize() { return impl; }

    // simple wrapper for float (传输过程将不保留类型信息)
    static class FLOAT extends VarNumber {
        FLOAT(float value) { super(value); }

        @NotNull
        @Override
        public Object normalize() { return (float) impl; }
    }
}
