package org.example.connection;

import java.io.Serializable;

/**
 * Helper class used to check whether both server and client have the same version of the connection package classes.
 * Starting protocol should look like this:
 * Client connects to the server and send this REQUEST_RESPONSE; server send an Integer (java.net. requires sending something through inputStream)
 * then server responds with: VERSION_MATCH or VERSION_MISMATCH then continues connection or closes it (respectively)
 * server should read said integer, but should not do anything with it
 */
public class ConnectionHelper implements Serializable {
    public static final int DEFAULT_PORT = 4413;
    /**
     * Used by Serializable interface, do not change
     */
    private static final long serialVersionUID = 1003L;
    public final String version;
    public final Message message;

    /**
     * Default constructor
     *
     * @param message message to send, see this class' documentation for more information
     */
    public ConnectionHelper(Message message, String version) {
        this.message = message;
        this.version = version;
    }

    /**
     * Possible uses
     */
    public enum Message implements Serializable {
        VERSION_MATCH, //sent to client after connection is established
        VERSION_MISMATCH, //sent to client after connection is established
        REQUEST_RESPONSE, //used by client ot start connection
        STREAM_START //used by server to start connection
    }
}