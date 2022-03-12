/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.StringReader;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * Test.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/07/13 umjammer initial version <br>
 */
@Disabled
@PropsEntity(url = "file:.env")
class Test01 {

    /** Global instance of the JSON factory. */
    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Property(name = "GOOGLEDRIVE_CLIENT_SECRET")
    private transient String appCredential;

    @Test
    void test() throws Exception {
        PropsEntity.Util.bind(this);

System.err.println(appCredential);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new StringReader(appCredential));
        System.err.println(clientSecrets.getDetails().getClientId());
        System.err.println(clientSecrets.getDetails().getAuthUri());
        System.err.println(clientSecrets.getDetails().getRedirectUris().stream().filter(s -> String.class.cast(s).startsWith("https://")).findFirst().get());
        System.err.println(clientSecrets.getDetails().getClientSecret());

        System.err.println(clientSecrets.getWeb().getClientId());
        System.err.println(clientSecrets.getWeb().getAuthUri());
        System.err.println(clientSecrets.getWeb().getRedirectUris().stream().filter(s -> String.class.cast(s).startsWith("https://")).findFirst().get());
        System.err.println(clientSecrets.getWeb().getClientSecret());
    }

    /** */
    public static void main(String[] args) throws Exception {
        Test01 app = new Test01();
        app.test();
    }
}

/* */
