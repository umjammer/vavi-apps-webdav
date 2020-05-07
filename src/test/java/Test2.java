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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import vavi.util.properties.annotation.Env;
import vavi.util.properties.annotation.PropsEntity;


/**
 * DDL for except google
 * <p>
 * $ heroku pg:psql postgresql-flexible-12461 --app lit-plateau-53954
 * </p>
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/03 umjammer initial version <br>
 */
@PropsEntity
public class Test2 {

    @Env(name = "TEST_JDBC_URL")
    String url = "jdbc:hsqldb:file:/tmp/tokendb";
    @Env(name = "TEST_JDBC_USER")
    String user = "sa";
    @Env(name = "TEST_JDBC_PASSWORD")
    String password = "";
    @Env(name = "TEST_BLOB_TYPE")
    String blob = "BLOB";

    public static void main(String[] args) throws Exception {
        Test2 app = new Test2();
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
            stmt.executeUpdate("DROP TABLE IF EXISTS credential");
        }
    }

    void create() throws SQLException, IOException {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS credential (id VARCHAR(512) UNIQUE, token VARCHAR(2048))");
        }
    }

    void insert() throws SQLException, IOException {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Statement stmt = connection.createStatement();

            LocalStrageDao local = new LocalStrageDao("tmp/database.properties");
            for (String schemeId : local.load()) {
                if (!schemeId.startsWith("#")) {
                    String[] parts = schemeId.split(":");
                    String scheme = parts[0];
                    String email = parts[1];
                    Path path = Paths.get(System.getProperty("user.home"), ".vavifuse", scheme, email);
                    String token;
                    if (Files.exists(path) && !scheme.equals("googledrive")) {
                        token = new String(Files.readAllBytes(path));
                    } else {
                        token = null;
                    }
                    if (scheme.equals("msgraph")) {
                        scheme = "onedrive";
                    }
                    stmt.executeUpdate("INSERT INTO credential VALUES ('" + scheme + ":" + email + "', '" + token + "')");
System.err.println("DB: id: " + schemeId);
                }
            }
        }
    }

    void select() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM credential");

            while (rs.next()) {
System.err.println("DB: { id: " + rs.getString("id") + ", token: " + rs.getString("token") + " }");
            }
        }
    }
}

/* */
