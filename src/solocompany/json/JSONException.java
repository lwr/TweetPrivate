/*
 * Copyright (c) 2013. All rights Reserved by williamleung2006@gmail.com
 */

package solocompany.json;


/**
 * The JSONException is thrown by the json parser when things go wrong.
 */
public class JSONException extends RuntimeException {

    private Throwable cause;

    /**
     * Constructs a JSONException with an explanatory message.
     *
     * @param message Detail about the reason for the exception.
     */
    public JSONException(String message) {
        super(message);
    }


    public JSONException(Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }


    public Throwable getCause() {
        return this.cause;
    }
}
