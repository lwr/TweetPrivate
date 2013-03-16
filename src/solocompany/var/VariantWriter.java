/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.var;

import java.io.*;
import java.util.*;

/**
 * VariantWriter.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public abstract class VariantWriter {


    // Writer to use for output;
    //
    protected Appendable out = null;


    protected TimeZone timeZone;


    protected boolean keepInvalidChar;



    /**
     * Constructor sets the <code>ResponseWriter</code>.
     *
     * @param out the respons writer
     */
    public VariantWriter(Appendable out) {
        this.out = out;
    }


    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }


    public void setKeepInvalidChar(boolean keepInvalidChar) {
        this.keepInvalidChar = keepInvalidChar;
    }


    public abstract void writeVariant(Variant var) throws IOException;
}
