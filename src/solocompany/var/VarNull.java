/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.var;

import org.jetbrains.annotations.Nullable;

/**
 * Variant for null object.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class VarNull extends Variant {


    public static final VarNull INSTANCE = new VarNull();


    private VarNull() {
    }


    @Override
    protected String getTypeName() {
        return "NUL";
    }


    @Override
    public String toString() {
        return "";
    }


    @Override
    public int getInt() {
        return 0;
    }


    @Override
    public long getLong() {
        return 0L;
    }


    @Nullable
    @Override
    public Variant getOrNull(@Nullable String key) {
        return null;
    }


    @Override
    public Object normalize() {
        return null;
    }
}
