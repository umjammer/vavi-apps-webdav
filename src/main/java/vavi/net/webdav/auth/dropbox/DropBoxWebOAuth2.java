/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth.dropbox;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxAuthInfo;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxSessionStore;
import com.dropbox.core.DbxStandardSessionStore;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.DbxWebAuth.BadRequestException;
import com.dropbox.core.DbxWebAuth.BadStateException;
import com.dropbox.core.DbxWebAuth.CsrfException;
import com.dropbox.core.DbxWebAuth.NotApprovedException;
import com.dropbox.core.DbxWebAuth.ProviderException;
import com.dropbox.core.DbxWebAuth.Request;

import vavi.net.http.HttpUtil;
import vavi.net.webdav.auth.WebAppCredential;
import vavi.net.webdav.auth.WebOAuth2;


/**
 * DropBoxLocalOAuth2.
 *
 * DropDBbox API doesn't have a refresh token system.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/04 umjammer initial version <br>
 */
public class DropBoxWebOAuth2 implements WebOAuth2<String, String> {

    /** */
    private WebAppCredential appCredential;

    /** */
    private DbxAppInfo appInfo;

    /** */
    private DbxWebAuth webAuth;

    /** */
    private URI authorizeUrl;

    /** */
    private String redirectUri;

    /** */
    private DbxSessionStore csrfTokenStore;

    /** */
    private HttpSession dummySession = new HttpUtil.DummyHttpSession();

    /** */
    public DropBoxWebOAuth2(WebAppCredential appCredential) {
        this.appCredential = appCredential;

        appInfo = new DbxAppInfo(appCredential.getClientId(), appCredential.getClientSecret());

        // Run through Dropbox API authorization process
        final String sessionKey = "dropbox-auth-csrf-token";
        csrfTokenStore = new DbxStandardSessionStore(dummySession, sessionKey);
        DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder(appCredential.getApplicationName()).withUserLocaleFrom(Locale.getDefault()).build();
        webAuth = new DbxWebAuth(requestConfig, appInfo);
        Request request = Request.newBuilder().withRedirectUri(appCredential.getRedirectUrl(), csrfTokenStore).build();

        authorizeUrl = URI.create(webAuth.authorize(request));
    }

    /** */
    public String authorize(String id) throws IOException {
        try {
            DbxAuthFinish authFinish = webAuth.finishFromRedirect(appCredential.getRedirectUrl(), csrfTokenStore, HttpUtil.splitQuery(URI.create(redirectUri)));

            // Save auth information to output file.
            DbxAuthInfo authInfo = new DbxAuthInfo(authFinish.getAccessToken(), appInfo.getHost());
            appCredential.getStrageDao().update(appCredential.getScheme() + ":" + id, DbxAuthInfo.Writer.writeToString(authInfo, true));
            return authInfo.getAccessToken();
        } catch (DbxException | BadRequestException | BadStateException | CsrfException |
                NotApprovedException | ProviderException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public URI getAuthorizationUrl() {
        return authorizeUrl;
    }

    @Override
    public void setResult(String code, String state) {
        redirectUri = appCredential.getRedirectUrl() + "?code=" + code + "&state=" + state;
    }
}

/* */
