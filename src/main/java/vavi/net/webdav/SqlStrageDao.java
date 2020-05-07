/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import vavi.net.webdav.auth.StrageDao;


/**
 * SqlStrageDao.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/23 umjammer initial version <br>
 */
public class SqlStrageDao implements StrageDao {

//    private static final Logger LOG = LoggerFactory.getLogger(PostgressStrageDao.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public List<String> load() {
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id FROM credential");
            List<String> output = new ArrayList<>();
            while (rs.next()) {
                output.add(rs.getString("id"));
            }
            return output;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void update(String id, String token) {
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("UPDATE credential SET token = '" + token + "' WHERE id = '" + id + "'");
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String select(String id) {
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT token FROM credential WHERE id = '" + id + "'");
            if (rs.next()) {
                return rs.getString("token");
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public byte[] selectGoogle() {
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM google WHERE id = 'StoredCredential'");

            if (rs.next()) {
                return rs.getBytes("credentials");
            }
            throw new NoSuchElementException("credentials");
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void updateGoogle(byte[] credentials) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement pstmt = connection
                    .prepareStatement("UPDATE google SET credentials = ? WHERE id = 'StoredCredential'");
            pstmt.setBytes(1, credentials);
            pstmt.execute();
            connection.commit();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}

/* */
