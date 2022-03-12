/*
 * Copyright (c) 2016 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth.google;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;

import vavi.net.auth.Authenticator;
import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.google.GoogleOAuth2AppCredential;
import vavi.net.auth.oauth2.google.GoogleOAuth2;
import vavi.net.webdav.auth.OAuthException;
import vavi.net.webdav.auth.WebOAuth2;
import vavi.util.Debug;
import vavi.util.StringUtil;


/**
 * GoogleWebAuthenticator.
 *
 * TODO in spite of using generics, Object...
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2016/02/16 umjammer initial version <br>
 * @see "https://yoghurt1131.hatenablog.com/entry/2019/07/08/091905"
 */
public class GoogleWebAuthenticator implements Authenticator<Object, Credential>, WebOAuth2<Object, Credential> {

//    private static final Logger LOG = LoggerFactory.getLogger(GoogleWebAuthenticator.class);

    /** */
    private GoogleAuthorizationCodeFlow flow;

    /** */
    private String redirectUrl;

    /** */
    private String url;

    /** */
    private String code, state;

    /** */
    public GoogleWebAuthenticator(GoogleOAuth2AppCredential appCredential) {
        try {
            flow = new GoogleAuthorizationCodeFlow.Builder(GoogleOAuth2.getHttpTransport(),
                                                           GoogleOAuth2.getJsonFactory(),
                                                           appCredential.getRawData(),
                                                           Arrays.asList(appCredential.getScope()))
                    .setDataStoreFactory(appCredential.getDataStoreFactory())
                    .setAccessType(appCredential.getAccessType())
                    .build();

            redirectUrl = appCredential.getRedirectUrl();
            url = new AuthorizationCodeRequestUrl(appCredential.getOAuthAuthorizationUrl(), appCredential.getClientId())
                    .setRedirectUri(redirectUrl)
                    .setScopes(Arrays.asList(appCredential.getScope()))
                    .build();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @param in UserCredential from WebAuth2, String from OAuth2 (inside FileSystem)
     */
    @Override
    public Credential authorize(Object in) throws IOException {
Debug.println("authorize arg: " + in.getClass());
        if (code == null) {
            // first time: from OAuth2 as Authenticator
            return authorizeFromOAurh2(UserCredential.class.cast(in));
        } else {
            // second time: from WebAuth2
            return authorizeFromWebAuth2(String.class.cast(in));
        }
    }

    /** @see "vavi.net.auth.oauth2.Authenticator" */
    private Credential authorizeFromOAurh2(UserCredential userCredential) throws IOException {
        Credential credential = flow.loadCredential(userCredential.getId());
//Debug.println("credential: " + userCredential.getId() + ", " + credential);
//if (credential != null) {
//Debug.println("refreshToken: " + credential.getRefreshToken());
//Debug.println("expiresInSeconds: " + credential.getExpiresInSeconds());
//}
        // web app doesn't return refresh token (null)
        if (credential != null && (credential.getExpiresInSeconds() == null || credential.getExpiresInSeconds() > 60)) {
Debug.println("refreshToken: " + (credential.getRefreshToken() != null) + ", expiresInSeconds: " + credential.getExpiresInSeconds());
            return credential;
        } else {
            throw new OAuthException(userCredential.getId());
        }
    }

    /** @see "vavi.net.WebAuth2" */
    private Credential authorizeFromWebAuth2(String id) throws IOException {
        // another instance aside when code is null
        TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUrl).execute();
        // store credential and return it
        Credential credential = flow.createAndStoreCredential(response, id);
//Debug.println("credential: " + id + ", " + credential);
//if (credential != null) {
//Debug.println("refreshToken: " + credential.getRefreshToken());
//Debug.println("expiresInSeconds: " + credential.getExpiresInSeconds());
//}
        return credential;
    }

    @Override
    public URI getAuthorizationUrl() {
        state = StringUtil.getRandomString();
        return URI.create(url + "&state=" + state);
    }

    @Override
    public void setResult(String code, String state) {
        if (!this.state.equals(state)) {
            throw new SecurityException("state is not same");
        }
        this.code = code;
    }
}

/* */
