/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth.box;

import vavi.net.webdav.auth.WebAppCredential;
import vavi.util.properties.annotation.Env;
import vavi.util.properties.annotation.PropsEntity;


/**
 * BoxWebAppCredential.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/11 umjammer initial version <br>
 */
@PropsEntity
public class BoxWebAppCredential extends WebAppCredential {

    @Env(name = "BOX_CLIENT_ID")
    private transient String clientId;
    @Env(name = "BOX_CLIENT_SECRET")
    private transient String clientSecret;
    @Env(name = "BOX_REDIRECT_URL")
    private String redirectUrl;

    @Override
    public String getApplicationName() {
        return "vavi-apps-webdav";
    }

    @Override
    public String getScheme() {
        return "box";
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
