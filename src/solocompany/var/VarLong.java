/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.var;

/**
 * Variant for a 64-bit integer.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class VarLong extends Variant {

    final long impl;

    public VarLong(long value) {
        this.impl = value;
    }


    @Override
    protected String getTypeName() {
        return "LNG";
    }


    @Override
    public int getInt() {
        if (impl >= Integer.MIN_VALUE && impl <= Integer.MAX_VALUE) {
            return (int) impl;
        }
        throw new IllegalStateException("Convert to int overflow: " + getDebugInfo());
    }


    @Override
    public String toString() {
        return String.valueOf(impl);
    }


    @Override
    public Object normalize() { return impl; }
}
