/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import javax.sql.DataSource;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import vavi.apps.webdav.SqlStrageDao;
import vavi.net.auth.oauth2.google.GoogleOAuth2;
import vavi.net.webdav.StrageDao;
import vavi.net.webdav.auth.google.GoogleWebAppCredential;
import vavi.net.webdav.auth.google.DaoDataStoreFactory;
import vavi.util.properties.annotation.Env;
import vavi.util.properties.annotation.PropsEntity;


/**
 * DDL for google.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/03 umjammer initial version <br>
 */
@SpringJUnitConfig
@Configuration
@Disabled
@PropsEntity
public class Test3 {

    @Env(name = "TEST_JDBC_URL")
    String url = "jdbc:hsqldb:file:/tmp/tokendb";
    @Env(name = "TEST_JDBC_USER")
    String user = "sa";
    @Env(name = "TEST_JDBC_PASSWORD")
    String password = "";
    @Env(name = "TEST_BLOB_TYPE")
    String blob = "BLOB";

    public static void main(String[] args) throws Exception {
        Test3 app = new Test3();
        PropsEntity.Util.bind(app);
System.err.println(app.url);
//        app.drop();
//        app.create();
//        app.insert();
        app.select();

        System.err.println("done");
    }

    void drop() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DROP TABLE IF EXISTS google");
        }
    }

    void create() throws SQLException, IOException {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS google (id VARCHAR(512) UNIQUE, credentials " + blob + ")");
        }
    }

    void insert() throws SQLException, IOException {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            connection.setAutoCommit(false);
            Path path = Paths.get(System.getProperty("user.home"), ".vavifuse", "googledrive", "StoredCredential");
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO google VALUES ('StoredCredential', ?)");
            pstmt.setBytes(1, Files.readAllBytes(path));
            pstmt.execute();
            connection.commit();
        }
    }

    void select() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM google");

            while (rs.next()) {
System.err.println("DB: { id: " + rs.getString("id") + ", credentials: " + rs.getBytes("credentials").length + " }");
//System.err.println(vavi.util.StringUtil.getDump(rs.getBytes("credentials")));
            }
        }
    }

    @Value("jdbc:hsqldb:file:/tmp/tokendb")
    private String dbUrl;

    @Bean
    public DataSource dataSource() throws SQLException {
        if (dbUrl == null || dbUrl.isEmpty()) {
            return new HikariDataSource();
        } else {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            return new HikariDataSource(config);
        }
    }

    @Bean
    public StrageDao strageDao() {
        return new SqlStrageDao();
    }

    @Autowired
    StrageDao strageDao;

    @Test
    void test() throws Exception {
        GoogleWebAppCredential appCredential = new GoogleWebAppCredential();
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                                                       GoogleOAuth2.getHttpTransport(),
                                                       GoogleOAuth2.getJsonFactory(),
                                                       appCredential.getRawData(),
                                                       Arrays.asList(appCredential.getScope()))
                .setDataStoreFactory(new DaoDataStoreFactory(strageDao))
                .setAccessType("offline")
                .build();

        LocalStrageDao local = new LocalStrageDao("tmp/database.properties");
        for (String schemeId : local.load()) {
            if (!schemeId.startsWith("#")) {
                String[] parts = schemeId.split(":");
                String scheme = parts[0];
                String email = parts[1];
                if (scheme.equals("googledrive")) {
                    Credential credential = flow.loadCredential(email);
                    System.err.println(email + ": " + credential);
                }
            }
        }
    }
}

/* */
