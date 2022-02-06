/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth;

import java.io.IOException;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vavi.net.auth.oauth2.AppCredential;
import vavi.net.auth.oauth2.BaseTokenRefresher;
import vavi.net.webdav.StrageDao;


/**
 * BasicWebTokenRefresher.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/03 umjammer initial version <br>
 */
public class BasicWebTokenRefresher extends BaseTokenRefresher<String> {

    private static final Logger LOG = LoggerFactory.getLogger(BasicWebTokenRefresher.class);

    private String schemeId;

    private StrageDao strageDao;

    /** */
    public BasicWebTokenRefresher(AppCredential appCredential, String id, Supplier<Long> refresh) {
        super(refresh);
        this.schemeId = appCredential.getScheme() + ":" + id;
        this.strageDao = WebAppCredential.class.cast(appCredential).getStrageDao();
    }

    @Override
    public void writeRefreshToken(String refreshToken) throws IOException {
LOG.debug("save refreshToken [" + schemeId + "]: " + refreshToken);
        strageDao.update(schemeId, refreshToken);
    }

    @Override
    public String readRefreshToken() throws IOException {
        String refreshToken = strageDao.select(schemeId);
LOG.debug("load refreshToken [" + schemeId + "]: " + refreshToken);
        return refreshToken;
    }

    @Override
    public void dispose() throws IOException {
        strageDao.update(schemeId, null);
    }
}

/* */
