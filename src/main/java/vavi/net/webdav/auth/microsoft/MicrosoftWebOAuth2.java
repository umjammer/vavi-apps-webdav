/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth.microsoft;

import java.io.IOException;
import java.net.URI;

import org.dmfs.httpessentials.client.HttpRequestExecutor;
import org.dmfs.httpessentials.exceptions.ProtocolError;
import org.dmfs.httpessentials.exceptions.ProtocolException;
import org.dmfs.httpessentials.httpurlconnection.HttpUrlConnectionExecutor;
import org.dmfs.oauth2.client.BasicOAuth2AuthorizationProvider;
import org.dmfs.oauth2.client.BasicOAuth2Client;
import org.dmfs.oauth2.client.BasicOAuth2ClientCredentials;
import org.dmfs.oauth2.client.OAuth2AccessToken;
import org.dmfs.oauth2.client.OAuth2AuthorizationProvider;
import org.dmfs.oauth2.client.OAuth2Client;
import org.dmfs.oauth2.client.OAuth2ClientCredentials;
import org.dmfs.oauth2.client.OAuth2InteractiveGrant;
import org.dmfs.oauth2.client.grants.AuthorizationCodeGrant;
import org.dmfs.oauth2.client.scope.StringScope;
import org.dmfs.rfc3986.encoding.Precoded;
import org.dmfs.rfc3986.uris.LazyUri;
import org.dmfs.rfc5545.Duration;

import vavi.net.webdav.auth.BasicWebTokenRefresher;
import vavi.net.webdav.auth.WebAppCredential;
import vavi.net.webdav.auth.WebOAuth2;
import vavi.util.Debug;


/**
 * MicrosoftWebOAuth2.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/04 umjammer initial version <br>
 */
public class MicrosoftWebOAuth2 implements WebOAuth2<String, String> {

    /** http client for oauth */
    private static HttpRequestExecutor oauthExecutor = new HttpUrlConnectionExecutor();

    /** */
    private OAuth2InteractiveGrant grant;

    /** */
    private WebAppCredential appCredential;

    /** */
    public MicrosoftWebOAuth2(WebAppCredential appCredential) {
        this.appCredential = appCredential;

        // Create OAuth2 provider
        OAuth2AuthorizationProvider provider = new BasicOAuth2AuthorizationProvider(
            URI.create(appCredential.getOAuthAuthorizationUrl()),
            URI.create(appCredential.getOAuthTokenUrl()),
            new Duration(1, 0, 3600) /* default expiration time in case the server doesn't return any */);

        // Create OAuth2 client credentials
        OAuth2ClientCredentials credentials = new BasicOAuth2ClientCredentials(
            appCredential.getClientId(), appCredential.getClientSecret());

        // Create OAuth2 client
        OAuth2Client oauth = new BasicOAuth2Client(
            provider,
            credentials,
            new LazyUri(new Precoded(appCredential.getRedirectUrl())));

        grant = new AuthorizationCodeGrant(oauth, new StringScope(appCredential.getScope()));
    }

    /** Get the authorization URL and open it in a WebView */
    public URI getAuthorizationUrl() {
        return grant.authorizationUrl();
    }

    /** */
    private String code, state;

    /** */
    public void setResult(String code, String state) {
        this.code = code;
        this.state = state;
    }

    /** */
    public String authorize(String id) throws IOException {
        try {
            // just use dao
            BasicWebTokenRefresher refresher = new BasicWebTokenRefresher(appCredential, id, null);

            String redirectUrl = appCredential.getRedirectUrl() + "?code=" + code + "&state=" + state;
Debug.println("redirectUrl: " + redirectUrl);
            OAuth2AccessToken token = grant.withRedirect(new LazyUri(new Precoded(redirectUrl))).accessToken(oauthExecutor);
Debug.println("scope: " + token.scope());
            refresher.writeRefreshToken(token.refreshToken().toString());

            return token.accessToken().toString();
        } catch (ProtocolError | ProtocolException e) {
            throw new IllegalStateException(e);
        }
    }
}

/* */
