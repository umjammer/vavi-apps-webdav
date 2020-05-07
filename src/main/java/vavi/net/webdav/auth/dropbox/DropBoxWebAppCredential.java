/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth.dropbox;

import vavi.net.webdav.auth.WebAppCredential;
import vavi.util.properties.annotation.Env;
import vavi.util.properties.annotation.PropsEntity;


/**
 * DropBoxWebAppCredential.
 *
 * properties file "dropbox.properties"
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/19 umjammer initial version <br>
 */
@PropsEntity
public class DropBoxWebAppCredential extends WebAppCredential {

    @Env(name = "DROPBOX_CLIENT_ID")
    private String clientId;
    @Env(name = "DROPBOX_CLIENT_SECRET")
    private transient String clientSecret;
    @Env(name = "DROPBOX_REDIRECT_URL")
    private String redirectUrl;

    @Override
    public String getApplicationName() {
        return "vavi-apps-webdav";
    }

    @Override
    public String getScheme() {
        return "dropbox";
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public String getRedirectUrl() {
        return redirectUrl;
    }

    @Override
    public String getOAuthAuthorizationUrl() {
        return null;
    }

    @Override
    public String getOAuthTokenUrl() {
        return null;
    }

    @Override
    public String getScope() {
        return null;
    }
}

/* */
