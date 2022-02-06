/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import vavi.net.auth.oauth2.BasicAppCredential;
import vavi.net.webdav.StrageDao;
import vavi.util.properties.annotation.PropsEntity;


/**
 * WebAppCredential.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/05 umjammer initial version <br>
 */
public abstract class WebAppCredential implements BasicAppCredential {

    /** bind automatically using {@link PropsEntity.Util#bind(Object, String...)} */
    protected WebAppCredential() {
        try {
            PropsEntity.Util.bind(this);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /** TODO not good */
    @Autowired
    private StrageDao strageDao;

    /** TODO not good */
    public StrageDao getStrageDao() {
        return strageDao;
    }
}

/* */
