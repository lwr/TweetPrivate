/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.json;

import solocompany.var.VarArray;
import solocompany.var.VarBool;
import solocompany.var.VarInt;
import solocompany.var.VarLong;
import solocompany.var.VarNull;
import solocompany.var.VarNumber;
import solocompany.var.VarObject;
import solocompany.var.VarString;
import solocompany.var.Variant;

import java.io.*;


class JSONReader {

    private long character;
    private boolean eof;
    private long index;
    private long line;
    private char previous;
    private Reader reader;
    private boolean usePrevious;


    /**
     * Construct a JSONReader from a Reader.
     *
     * @param reader A reader.
     */
    public JSONReader(Reader reader) {
        this.reader = reader.markSupported() ? reader : new BufferedReader(reader);
        this.eof = false;
        this.usePrevious = false;
        this.previous = 0;
        this.index = 0;
        this.character = 1;
        this.line = 1;
    }


    /**
     * Back up one character. This provides a sort of look-ahead capability,
     * so that you can test for a digit or letter before attempting to parse
     * the next number or identifier.
     */
    public void back() throws JSONException {
        if (this.usePrevious || this.index <= 0) {
            throw new JSONException("Stepping back two steps is not supported");
        }
        this.index -= 1;
        this.character -= 1;
        this.usePrevious = true;
        this.eof = false;
    }


    public boolean end() {
        return this.eof && !this.usePrevious;
    }


    /**
     * Get the next character in the source string.
     *
     * @return The next character, or 0 if past the end of the source string.
     */
    public char next() throws JSONException {
        int c;
        if (usePrevious) {
            usePrevious = false;
            c = previous;
        } else {
            try {
                c = reader.read();
            } catch (IOException e) {
                throw new JSONException(e);
            }

            if (c <= 0) { // End of stream
                eof = true;
                c = 0;
            }
        }
        index += 1;
        if (previous == '\r') {
            line += 1;
            character = c == '\n' ? 0 : 1;
        } else if (c == '\n') {
            line += 1;
            character = 0;
        } else {
            character += 1;
        }
        previous = (char) c;
        return previous;
    }


    /**
     * Get the next n characters.
     *
     * @param n The number of characters to take.
     * @return A string of n characters.
     *
     * @throws JSONException Substring bounds error if there are not
     *                       n characters remaining in the source string.
     */
    public String next(int n) throws JSONException {
        if (n == 0) {
            return "";
        }

        char[] chars = new char[n];
        int pos = 0;

        while (pos < n) {
            chars[pos] = next();
            if (end()) {
                throw exception("Substring bounds error");
            }
            pos += 1;
        }
        return new String(chars);
    }


    /**
     * Get the next char in the string, skipping whitespace.
     *
     * @return A character, or 0 if there are no more characters.
     *
     * @throws JSONException
     */
    public char nextClean() throws JSONException {
        for (; ; ) {
            char c = this.next();
            if (c == 0 || c > ' ') {
                return c;
            }
        }
    }


    /**
     * Return the characters up to the next close quote character.
     * Backslash processing is done. The formal JSON format does not
     * allow strings in single quotes, but an implementation is allowed to
     * accept them.
     *
     * @param quote The quoting character, either
     *              <code>"</code>&nbsp;<small>(double quote)</small> or
     *              <code>'</code>&nbsp;<small>(single quote)</small>.
     * @return A String.
     *
     * @throws JSONException Unterminated string.
     */
    public String nextString(char quote) throws JSONException {
        char c;
        StringBuilder sb = new StringBuilder();
        for (; ; ) {
            c = this.next();
            switch (c) {
            case 0:
            case '\n':
            case '\r':
                throw this.exception("Unterminated string");
            case '\\':
                c = this.next();
                switch (c) {
                case 'b':
                    sb.append('\b');
                    break;
                case 't':
                    sb.append('\t');
                    break;
                case 'n':
                    sb.append('\n');
                    break;
                case 'f':
                    sb.append('\f');
                    break;
                case 'r':
                    sb.append('\r');
                    break;
                case 'u':
                    sb.append((char) Integer.parseInt(this.next(4), 16));
                    break;
                case '"':
                case '\'':
                case '\\':
                case '/':
                    sb.append(c);
                    break;
                default:
                    throw this.exception("Illegal escape.");
                }
                break;
            default:
                if (c == quote) {
                    return sb.toString();
                }
                sb.append(c);
            }
        }
    }


    /**
     * Get the next value. The value can be a Boolean, Double, Integer,
     * JSONArray, JSONObject, Long, or String, or the JSONObject.NULL object.
     *
     * @return An variant.
     *
     * @throws JSONException If syntax error.
     */
    public Variant nextValue() throws JSONException {
        char c = nextClean();
        switch (c) {
        case '{':
            back();
            return nextObject();
        case '[':
            back();
            return nextArray();
        case '"':
        case '\'':
            return VarString.valueOf(nextString(c));
        default:
            back();
            return stringToValue(nextString());
        }
    }


    private String nextString() {
        char c = nextClean();
        switch (c) {
        case '{':
        case '[':
            throw exception("Missing value");
        case '"':
        case '\'':
            return nextString(c);
        }

        /*
         * Handle unquoted text. This could be the values true, false, or
         * null, or it can be a number. An implementation (such as this one)
         * is allowed to also accept non-standard forms.
         *
         * Accumulate characters until we reach the end of the text or a
         * formatting character.
         */

        StringBuilder sb = new StringBuilder();
        while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
            sb.append(c);
            c = next();
        }
        back();

        String string = sb.toString().trim();
        if (string.length() == 0) {
            throw exception("Missing value");
        }
        return string;
    }


    /**
     * Read a JSONArray.
     *
     * @throws JSONException If there is a syntax error.
     */
    public VarArray nextArray() throws JSONException {
        if (nextClean() != '[') {
            throw exception("A JSONArray text must start with '['");
        }

        final VarArray myArrayList = new VarArray();
        if (nextClean() != ']') {
            back();
            while (true) {
                if (nextClean() == ',') {
                    back();
                    myArrayList.add(VarNull.INSTANCE);
                } else {
                    back();
                    myArrayList.add(nextValue());
                }
                switch (nextClean()) {
                case ';':
                case ',':
                    if (nextClean() == ']') {
                        return myArrayList;
                    }
                    back();
                    break;
                case ']':
                    return myArrayList;
                default:
                    throw exception("Expected a ',' or ']'");
                }
            }
        }
        return myArrayList;
    }


    /**
     * Read a JSONObject.
     *
     * @throws JSONException If there is a syntax error in the source string or a duplicated key.
     */
    public VarObject nextObject() throws JSONException {
        if (nextClean() != '{') {
            throw exception("A JSONObject text must begin with '{'");
        }

        final VarObject map = new VarObject();
        while (true) {
            switch (nextClean()) {
            case 0:
                throw exception("A JSONObject text must end with '}'");

            case '}':
                return map;

            default:
                back();
                break;
            }

            String key = nextString();

            // The key is followed by ':'. We will also tolerate '=' or '=>'.

            char c = nextClean();
            if (c == '=') {
                if (next() != '>') {
                    back();
                }
            } else if (c != ':') {
                throw exception("Expected a ':' after a key");
            }
            putOnce(map, key, nextValue());

            // Pairs are separated by ','. We will also tolerate ';'.

            switch (nextClean()) {
            case ';':
            case ',':
                if (nextClean() == '}') {
                    return map;
                }
                back();
                break;
            case '}':
                return map;
            default:
                throw exception("Expected a ',' or '}'");
            }
        }
    }

    /**
     * Put a key/value pair in the JSONObject, but only if the key and the
     * value are both non-null, and only if there is not already a member
     * with that name.
     *
     * @throws solocompany.json.JSONException if the key is a duplicate
     */
    private static void putOnce(VarObject m, String key, Variant value) throws JSONException {
        if (key != null && value != null) {
            if (m.containsKey(key)) {
                throw new JSONException("Duplicate key \"" + key + "\"");
            }
            m.put(key, value);
        }
    }

    /**
     * Try to convert a string into a number, boolean, or null. If the string
     * can't be converted, return the string.
     *
     * @param string A String.
     * @return A simple JSON value.
     */
    public static Variant stringToValue(String string) {
        if (string.equals("")) {
            return VarString.valueOf(string);
        }
        if (string.equalsIgnoreCase("true")) {
            return VarBool.TRUE;
        }
        if (string.equalsIgnoreCase("false")) {
            return VarBool.FALSE;
        }
        if (string.equalsIgnoreCase("null")) {
            return VarNull.INSTANCE;
        }

        /*
         * If it might be a number, try converting it.
         * If a number cannot be produced, then the value will just
         * be a string. Note that the plus and implied string
         * conventions are non-standard. A JSON parser may accept
         * non-JSON forms as long as it accepts all correct JSON forms.
         */

        char b = string.charAt(0);
        if ((b >= '0' && b <= '9') || b == '.' || b == '-' || b == '+') {
            try {
                if (string.indexOf('.') > -1
                        || string.indexOf('e') > -1 || string.indexOf('E') > -1) {
                    double value = Double.parseDouble(string);
                    if (!Double.isInfinite(value) && !Double.isNaN(value)) {
                        return new VarNumber(value);
                    }
                } else {
                    long value = Long.parseLong(string);
                    if (value == (int) value) {
                        return new VarInt((int) value);
                    } else {
                        return new VarLong(value);
                    }
                }
            } catch (Exception ignore) {
                // ignore
            }
        }
        return VarString.valueOf(string);
    }


    /**
     * Make a JSONException to signal a syntax error.
     *
     * @param message The error message.
     * @return A JSONException object, suitable for throwing
     */
    public JSONException exception(String message) {
        return new JSONException(message
                + " at " + index + " [character " + character + " line " + line + "]");
    }
}

