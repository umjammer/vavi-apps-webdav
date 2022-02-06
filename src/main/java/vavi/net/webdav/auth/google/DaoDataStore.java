/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav.auth.google;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import com.google.api.client.util.store.AbstractDataStore;
import com.google.api.client.util.store.DataStore;

import vavi.net.webdav.StrageDao;


/**
 * DaoDataStore.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2021/11/04 umjammer initial version <br>
 */
public class DaoDataStore extends AbstractDataStore<String> {

//    private static final Logger LOG = LoggerFactory.getLogger(DaoDataStore.class);

    private StrageDao dao;

    public DaoDataStore(DaoDataStoreFactory dataStore, StrageDao dao, String id) throws IOException {
        super(dataStore, id);
        this.dao = dao;
    }

    @Override
    public DaoDataStoreFactory getDataStoreFactory() {
        return (DaoDataStoreFactory) super.getDataStoreFactory();
    }

    @Override
    public Set<String> keySet() throws IOException {
        return dao.selectAll().keySet();
    }

    @Override
    public Collection<String> values() throws IOException {
        return dao.selectAll().values();
    }

    @Override
    public String get(String key) throws IOException {
        return dao.select(key);
    }

    @Override
    public DataStore<String> set(String key, String value) throws IOException {
        dao.update(key, value);
        return this;
    }

    @Override
    public DataStore<String> clear() throws IOException {
        dao.deleteAll();
        return this;
    }

    @Override
    public DataStore<String> delete(String key) throws IOException {
        dao.delete(key);
        return this;
    }
}

/* */
