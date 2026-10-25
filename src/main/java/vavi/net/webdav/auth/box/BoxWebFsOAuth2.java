/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth.box;

import java.io.IOException;

import com.box.sdkgen.box.oauth.BoxOAuth;
import com.box.sdkgen.box.oauth.OAuthConfig;
import com.box.sdkgen.box.tokenstorage.TokenStorage;
import com.box.sdkgen.client.BoxClient;

import vavi.net.auth.UserCredential;
import vavi.net.auth.oauth2.OAuth2;
import vavi.net.auth.oauth2.OAuth2AppCredential;
import vavi.net.webdav.auth.OAuthException;
import vavi.net.webdav.auth.WebAppCredential;


/**
 * BoxWebFsOAuth2.
 * <p>
 * used by vavi-nio-file-box instead of {@code vavi.net.auth.oauth2.box.BoxOAuth2}
 * (set by a system property {@link #PROPERTY_OAUTH2}).
 * tokens are read from {@link BoxDaoTokenStorage}, if not exists throws {@link OAuthException}
 * to let a user login via {@link BoxWebOAuth2}.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2026/10/25 umjammer initial version <br>
 */
public class BoxWebFsOAuth2 implements OAuth2<UserCredential, BoxClient> {

    /** @see "com.github.fge.filesystem.box.BoxFileSystemRepository" */
    public static final String PROPERTY_OAUTH2 = "vavi.nio.file.box.BoxFileSystemRepository.oauth2";

    /** */
    private final WebAppCredential appCredential;

    /** called by reflection */
    public BoxWebFsOAuth2(OAuth2AppCredential appCredential) {
        this.appCredential = (WebAppCredential) appCredential;
    }

    @Override
    public BoxClient authorize(UserCredential userCredential) throws IOException {
        TokenStorage tokenStorage = new BoxDaoTokenStorage(appCredential, userCredential.getId());
        if (tokenStorage.get() == null) {
            throw new OAuthException(userCredential.getId());
        }

        OAuthConfig config = new OAuthConfig.Builder(appCredential.getClientId(), appCredential.getClientSecret())
                .tokenStorage(tokenStorage)
                .build();
        return new BoxClient(new BoxOAuth(config));
    }
}
