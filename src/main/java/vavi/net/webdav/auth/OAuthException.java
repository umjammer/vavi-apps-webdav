/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth;


/**
 * OAuthException.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/22 umjammer initial version <br>
 */
public class OAuthException extends RuntimeException {

    /**
     * @param email id for scheme
     */
    public OAuthException(String email) {
        super(email);
    }
}

/* */
