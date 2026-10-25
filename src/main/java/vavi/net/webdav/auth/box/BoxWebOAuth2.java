/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth.box;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URI;

import com.box.sdkgen.box.errors.BoxSDKError;
import com.box.sdkgen.box.oauth.BoxOAuth;
import com.box.sdkgen.box.oauth.GetAuthorizeUrlOptions;
import com.box.sdkgen.box.oauth.OAuthConfig;
import com.box.sdkgen.client.BoxClient;

import vavi.net.webdav.auth.WebAppCredential;
import vavi.net.webdav.auth.WebOAuth2;
import vavi.util.StringUtil;


/**
 * BoxWebOAuth2.
 * <p>
 * the web login flow for box. obtained tokens are stored by {@link BoxDaoTokenStorage}.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/04 umjammer initial version <br>
 */
public class BoxWebOAuth2 implements WebOAuth2<String, BoxClient> {

    private static final Logger logger = System.getLogger(BoxWebOAuth2.class.getName());

    /** */
    private final WebAppCredential appCredential;

    /** */
    public BoxWebOAuth2(WebAppCredential appCredential) {
        this.appCredential = appCredential;
    }

    /**
     * exchanges the authorization code to tokens and stores them.
     * @param id id for scheme
     */
    public BoxClient authorize(String id) throws IOException {
        OAuthConfig config = new OAuthConfig.Builder(appCredential.getClientId(), appCredential.getClientSecret())
                .tokenStorage(new BoxDaoTokenStorage(appCredential, id))
                .build();
        BoxOAuth auth = new BoxOAuth(config);
        try {
            auth.getTokensAuthorizationCodeGrant(code);
        } catch (BoxSDKError e) {
            throw new IOException(e);
        }
logger.log(Level.DEBUG, "authorized [" + appCredential.getScheme() + ":" + id + "]");

        return new BoxClient(auth);
    }

    /** Get the authorization URL and open it in a WebView */
    public URI getAuthorizationUrl() {
        state = StringUtil.getRandomString();
        OAuthConfig config = new OAuthConfig.Builder(appCredential.getClientId(), appCredential.getClientSecret())
                .build();
        GetAuthorizeUrlOptions options = new GetAuthorizeUrlOptions.Builder()
                .redirectUri(appCredential.getRedirectUrl())
                .state(state)
                .scope("root_readwrite")
                .build();
        return URI.create(new BoxOAuth(config).getAuthorizeUrl(options));
    }

    /** */
    private String code, state;

    /** */
    public void setResult(String code, String state) {
        if (!this.state.equals(state)) {
            throw new SecurityException("state is not same");
        }
        this.code = code;
    }
}
