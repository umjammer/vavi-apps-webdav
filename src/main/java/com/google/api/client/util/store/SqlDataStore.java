/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.google.api.client.util.store;

import java.io.IOException;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.util.IOUtils;

import vavi.net.webdav.auth.StrageDao;
import vavi.net.webdav.auth.google.SqlDataStoreFactory;


/**
 * File data store that inherits from the abstract memory data store because the
 * key-value pairs are stored in a memory cache, and saved in the file (see
 * {@link #save()} when changing values.
 *
 * @param <V> serializable type of the mapped value
 */
public class SqlDataStore<V extends Serializable> extends AbstractMemoryDataStore<V> {

    private static final Logger LOG = LoggerFactory.getLogger(SqlDataStore.class);

    private StrageDao dao;

    public SqlDataStore(SqlDataStoreFactory dataStore, StrageDao dao, String id) throws IOException {
        super(dataStore, id);
        this.dao = dao;
        // load credentials from existing file
        keyValueMap = IOUtils.deserialize(dao.selectGoogle());
LOG.debug("LOAD: " + keyValueMap);
    }

    @Override
    void save() throws IOException {
LOG.debug("SAVE: " + keyValueMap);
        dao.updateGoogle(IOUtils.serialize(keyValueMap));
    }

    @Override
    public SqlDataStoreFactory getDataStoreFactory() {
        return (SqlDataStoreFactory) super.getDataStoreFactory();
    }
}

/* */
