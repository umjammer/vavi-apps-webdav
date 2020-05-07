/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth.microsoft;

import vavi.net.webdav.auth.WebAppCredential;
import vavi.util.Debug;
import vavi.util.properties.annotation.Env;
import vavi.util.properties.annotation.PropsEntity;


/**
 * MicrosoftWebAppCredential.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/19 umjammer initial version <br>
 * @see "https://account.live.com/developers/applications/"
 */
@PropsEntity
public class MicrosoftWebAppCredential extends WebAppCredential {

    @Env(name = "ONEDRIVE_CLIENT_ID")
    private String clientId;
    @Env(name = "ONEDRIVE_CLIENT_SECRET")
    private transient String clientSecret;
    @Env(name = "ONEDRIVE_REDIRECT_URL")
    private String redirectUrl;

    @Override
    public String getApplicationName() {
        return "vavi-apps-webdav";
    }

    @Override
    public String getScheme() {
        return "onedrive";
    }

    @Override
    public String getClientId() {
Debug.println(clientId);
        return clientId;
    }

    @Override
    public String getClientSecret() {
Debug.println(clientSecret);
        return clientSecret;
    }

    @Override
    public String getRedirectUrl() {
Debug.println(redirectUrl);
        return redirectUrl;
    }

    @Override
    public String getOAuthAuthorizationUrl() {
        return "https://login.microsoftonline.com/common/oauth2/v2.0/authorize";
    }

    @Override
    public String getOAuthTokenUrl() {
        return "https://login.microsoftonline.com/common/oauth2/v2.0/token";
    }

    @Override
    public String getScope() {
        return "Files.ReadWrite.All Sites.ReadWrite.All User.Read offline_access";
    }
}

/* */
