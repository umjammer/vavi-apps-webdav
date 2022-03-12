/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth;

import java.net.URI;

import vavi.net.auth.oauth2.OAuth2;


/**
 * WebOAuth2.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/11 umjammer initial version <br>
 */
public interface WebOAuth2<I, O> extends OAuth2<I, O> {

    /**
     * Gets the authorization URL and open it in a WebView.
     * @throws OAuthException when authorization is failed
     */
    URI getAuthorizationUrl();

    /** TODO */
    void setResult(String code, String state);
}

/* */
