/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth;

import vavi.net.auth.oauth2.WithTotpUserCredential;


/**
 * WebUserCredential.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/05 umjammer initial version <br>
 */
public class WebUserCredential implements WithTotpUserCredential {

    private String id;

    public WebUserCredential(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getTotpSecret() {
        return null;
    }
}

/* */
