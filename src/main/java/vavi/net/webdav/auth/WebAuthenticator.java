/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth;

import java.io.IOException;

import vavi.net.auth.AppCredential;
import vavi.net.auth.Authenticator;
import vavi.net.auth.UserCredential;


/**
 * WebAuthenticator.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/02/16 umjammer initial version <br>
 */
public class WebAuthenticator implements Authenticator<UserCredential, Void> {

    /** */
    public WebAuthenticator(AppCredential appCredential) {
    }

    /**
     * @return *URL* the code query parameter included.
     */
    @Override
    public Void authorize(UserCredential userCredential) throws IOException {
        throw new OAuthException(userCredential.getId());
    }
}

/* */
