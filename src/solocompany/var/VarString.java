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


    @Override
    public Object normalize() { return impl; }
}
