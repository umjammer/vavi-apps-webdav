/*
 * Copyright (c) 2026 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth.box;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import com.box.sdkgen.box.tokenstorage.TokenStorage;
import com.box.sdkgen.schemas.accesstoken.AccessToken;
import com.box.sdkgen.serialization.json.JsonManager;

import vavi.net.webdav.StrageDao;
import vavi.net.webdav.auth.WebAppCredential;


/**
 * BoxDaoTokenStorage.
 * <p>
 * stores box tokens as json into {@link StrageDao} using a key 'box:id'.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2026/10/25 umjammer initial version <br>
 */
public class BoxDaoTokenStorage implements TokenStorage {

    private static final Logger logger = System.getLogger(BoxDaoTokenStorage.class.getName());

    /** 'scheme:id' */
    private final String schemeId;

    /** */
    private final StrageDao strageDao;

    /**
     * @param id id for scheme
     */
    public BoxDaoTokenStorage(WebAppCredential appCredential, String id) {
        this.schemeId = appCredential.getScheme() + ":" + id;
        this.strageDao = appCredential.getStrageDao();
    }

    @Override
    public void store(AccessToken accessToken) {
logger.log(Level.DEBUG, "store token [" + schemeId + "]");
        strageDao.update(schemeId, JsonManager.sdToJson(JsonManager.serialize(accessToken)));
    }

    @Override
    public AccessToken get() {
        String json = strageDao.select(schemeId);
logger.log(Level.DEBUG, "get token [" + schemeId + "]: " + (json != null));
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return JsonManager.deserialize(JsonManager.jsonToSerializedData(json), AccessToken.class);
        } catch (Exception e) {
logger.log(Level.WARNING, "bad token [" + schemeId + "]: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void clear() {
logger.log(Level.DEBUG, "clear token [" + schemeId + "]");
        strageDao.update(schemeId, null);
    }
}
