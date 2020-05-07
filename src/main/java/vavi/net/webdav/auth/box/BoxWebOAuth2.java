/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth.box;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import com.box.sdk.BoxAPIConnection;

import vavi.net.webdav.auth.WebAppCredential;
import vavi.net.webdav.auth.WebOAuth2;
import vavi.util.Debug;
import vavi.util.StringUtil;


/**
 * BoxWebOAuth2.
 *
 * DropDBbox API doesn't have a refresh token system.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/04 umjammer initial version <br>
 */
public class BoxWebOAuth2 implements WebOAuth2<String, BoxAPIConnection> {

    /** */
    private WebAppCredential appCredential;

    /** */
    private BoxAPIConnection api;

    /** */
    public BoxWebOAuth2(WebAppCredential appCredential) {
        this.appCredential = appCredential;
        this.api = new BoxAPIConnection(appCredential.getClientId(), appCredential.getClientSecret());
        api.setExpires(60 * 24 * 60 * 60 * 1000);
    }

    /** */
    public BoxAPIConnection authorize(String id) throws IOException {
        api.authenticate(code);
        api.setAutoRefresh(true);

        String save = api.save();
Debug.println("save: " + save);
        appCredential.getStrageDao().update(appCredential.getScheme() + ":" + id, save);

        return api;
    }

    /** Get the authorization URL and open it in a WebView */
    public URI getAuthorizationUrl() {
        state = StringUtil.getRandomString();
        return URI.create(BoxAPIConnection.getAuthorizationURL(appCredential.getClientId(),
                URI.create(appCredential.getRedirectUrl()), state, Arrays.asList("root_readwrite"))
                .toString());
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

/* */
