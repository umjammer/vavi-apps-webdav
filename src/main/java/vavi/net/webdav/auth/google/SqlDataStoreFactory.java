/*
 * Copyright (c) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package vavi.net.webdav.auth.google;

import java.io.IOException;
import java.io.Serializable;

import com.google.api.client.util.store.AbstractDataStoreFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.SqlDataStore;

import vavi.net.webdav.auth.StrageDao;


/**
 * Thread-safe file implementation of a credential store.
 *
 * TODO AbstractMemoryDataStore is package private
 *
 * @since 1.16
 * @author Yaniv Inbar
 */
public class SqlDataStoreFactory extends AbstractDataStoreFactory {

//    private static final Logger LOGGER = Logger.getLogger(SqlDataStoreFactory.class.getName());

    private StrageDao dao;

    /**
     * @param dao
     */
    public SqlDataStoreFactory(StrageDao dao) {
        this.dao = dao;
    }

    @Override
    protected <V extends Serializable> DataStore<V> createDataStore(String id) throws IOException {
//      AutowireCapableBeanFactory.
        return new SqlDataStore<>(this, dao, id);
    }
}

/* */