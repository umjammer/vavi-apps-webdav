/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth.dropbox;

import java.io.IOException;
import java.util.function.Supplier;

import com.dropbox.core.DbxAuthInfo;
import com.dropbox.core.json.JsonReadException;

import vavi.net.auth.oauth2.AppCredential;
import vavi.net.auth.oauth2.TokenRefresher;
import vavi.net.webdav.auth.WebAppCredential;
import vavi.util.Debug;


/**
 * DropBoxWebTokenRefresher.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/03 umjammer initial version <br>
 */
public class DropBoxWebTokenRefresher implements TokenRefresher<DbxAuthInfo> {

    private String schemeId;

    private WebAppCredential appCredential;

    /**
     * @param refresh you should call {@link #writeRefreshToken(DbxAuthInfo)} and return new refresh delay.
     */
    public DropBoxWebTokenRefresher(AppCredential appCredential, String id, Supplier<Long> refresh) {
        this.schemeId = appCredential.getScheme() + ":" + id;
        this.appCredential = WebAppCredential.class.cast(appCredential);
    }

    @Override
    public void writeRefreshToken(DbxAuthInfo authInfo) throws IOException {
Debug.println("refreshToken: " + authInfo.getRefreshToken());
        String json = DbxAuthInfo.Writer.writeToString(authInfo, true);
        appCredential.getStrageDao().update(schemeId, json);
    }

    @Override
    public DbxAuthInfo readRefreshToken() throws IOException {
        try {
            String json = appCredential.getStrageDao().select(schemeId);
            if (json == null) {
                return null;
            }
            DbxAuthInfo authInfo = DbxAuthInfo.Reader.readFully(json);
Debug.println("refreshToken: exists: " + authInfo.getRefreshToken());
            return authInfo;
        } catch (JsonReadException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void dispose() throws IOException {
        appCredential.getStrageDao().update(schemeId, null);
    }

    @Override
    public void start(DbxAuthInfo refreshToken, long refreshDelay) throws IOException {
    }

    @Override
    public void terminate() {
    }
}

/* */
