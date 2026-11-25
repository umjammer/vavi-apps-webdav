/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.function.Supplier;

import vavi.net.auth.AppCredential;
import vavi.net.auth.oauth2.BaseTokenRefresher;
import vavi.net.webdav.StrageDao;


/**
 * BasicWebTokenRefresher.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/03 umjammer initial version <br>
 */
public class BasicWebTokenRefresher extends BaseTokenRefresher<String> {

    private static final Logger logger = System.getLogger(BasicWebTokenRefresher.class.getName());

    private final String schemeId;

    private final StrageDao strageDao;

    /** */
    public BasicWebTokenRefresher(AppCredential appCredential, String id, Supplier<Long> refresh) {
        super(refresh);
        this.schemeId = appCredential.getScheme() + ":" + id;
        this.strageDao = ((WebAppCredential) appCredential).getStrageDao();
    }

    @Override
    public void writeRefreshToken(String refreshToken) throws IOException {
logger.log(Level.DEBUG, "save refreshToken [" + schemeId + "]: " + (refreshToken != null));
        strageDao.update(schemeId, refreshToken);
    }

    @Override
    public String readRefreshToken() throws IOException {
        String refreshToken = strageDao.select(schemeId);
logger.log(Level.DEBUG, "load refreshToken [" + schemeId + "]: " + (refreshToken != null));
        return refreshToken;
    }

    @Override
    public void close() {
        strageDao.update(schemeId, null);
    }
}
