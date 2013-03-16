/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.var;

/**
 * Variant for string object.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class VarString extends Variant {

    final String impl;


    public VarString(String value) {
        this.impl = value;
    }


    public static Variant valueOf(String value) {
        return new VarString(value);
    }


    @Override
    protected String getTypeName() {
        return "STR";
    }

    @Override
    public String toString() {
        return impl;
    }


    public int getInt() {
        try {
            return Integer.parseInt(impl);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Convert to int failed: " + getDebugInfo());
        }
    }


    public long getLong() {
        try {
            return Long.parseLong(impl);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Convert to long failed: " + getDebugInfo());
        }
    }


    @Override
    public Object normalize() { return impl; }
}
