/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.json;

import solocompany.var.VarArray;
import solocompany.var.VarObject;
import solocompany.var.Variant;

import java.io.*;

/**
 * JSONParser.
 *
 * @author <a href="mailto:williamleung2006@gmail.com">William Leung</a>
 */
public class JSONParser {

    public Variant parseJson(InputStream in) throws JSONException {
        try {
            return new JSONReader(new InputStreamReader(in, "UTF-8")).nextValue();
        } catch (UnsupportedEncodingException e) {
            // never happened
            throw new JSONException(e);
        }
    }


    public Variant parseJson(Reader in) throws JSONException {
        return new JSONReader(in).nextValue();
    }


    public Variant parseJson(String s) throws JSONException {
        return new JSONReader(new StringReader(s)).nextValue();
    }


    public VarArray parseArray(String s) throws JSONException {
        return new JSONReader(new StringReader(s)).nextArray();
    }


    public VarObject parseObject(String s) throws JSONException {
        return new JSONReader(new StringReader(s)).nextObject();
    }
}
