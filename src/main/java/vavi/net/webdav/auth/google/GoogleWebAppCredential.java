/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth.google;

import java.io.IOException;
import java.io.StringReader;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.services.drive.DriveScopes;

import vavi.net.auth.oauth2.google.GoogleBaseAppCredential;
import vavi.net.auth.oauth2.google.GoogleOAuth2;
import vavi.net.webdav.auth.StrageDao;


/**
 * GoogleWebAppCredential.
 *
 * <li> env: GOOGLEDRIVE_CLIENT_SECRET </li>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/03 umjammer initial version <br>
 */
public class GoogleWebAppCredential extends GoogleBaseAppCredential {

    /** Application name. TODO app credential? */
    private static final String APPLICATION_NAME = "vavi-apps-webdav";

    @Autowired
    private StrageDao dao;

    /** */
    public GoogleWebAppCredential() {
        try {
            // Load client secrets.
            String appCredential = System.getenv("GOOGLEDRIVE_CLIENT_SECRET");
            clientSecrets = GoogleClientSecrets.load(GoogleOAuth2.JSON_FACTORY, new StringReader(appCredential));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected Predicate<? super String> getFilter() {
        return s -> String.class.cast(s).startsWith("https://");
    }

    @Override
    public String getApplicationName() {
        return APPLICATION_NAME; // TODO we can get the name from json
    }

    /* */
    public String getScope() {
        return DriveScopes.DRIVE;
    }

    @Override
    public String getAccessType() {
        return "offline";
    }

    /** */
    public DataStoreFactory getDataStoreFactory() throws IOException {
        return new SqlDataStoreFactory(dao);
    }
}

/* */
