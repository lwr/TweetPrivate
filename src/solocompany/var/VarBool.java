/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.var;

import org.jetbrains.annotations.NotNull;

/**
 * Variant for a boolean object.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class VarBool extends Variant {

    public static final VarBool TRUE = new VarBool(true);
    public static final VarBool FALSE = new VarBool(false);

    final boolean impl;


    @NotNull
    public static VarBool valueOf(boolean b) {
        return (b ? TRUE : FALSE);
    }


    private VarBool(boolean value) {
        this.impl = value;
    }


    @Override
    protected String getTypeName() {
        return "BOL";
    }


    @Override
    public int getInt() {
        return impl ? 1 : 0;
    }


    @Override
    public String toString() {
        return String.valueOf(impl);
    }


    @Override
    public Object normalize() { return impl; }
}
