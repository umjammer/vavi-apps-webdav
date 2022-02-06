/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.webdav;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import vavi.net.webdav.StrageDao;


/**
 * SqlStrageDao.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/23 umjammer initial version <br>
 */
public class SqlStrageDao implements StrageDao {

    private static final Logger LOG = LoggerFactory.getLogger(SqlStrageDao.class);

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
            PreparedStatement pstmt = connection.prepareStatement("UPDATE credential SET token = ? WHERE id = ?");
            pstmt.setString(1, token);
            pstmt.setString(2, id);
            pstmt.execute();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String select(String id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement("SELECT token FROM credential WHERE id = ?");
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
LOG.debug("[" + id + "]: " + rs.getString("token"));
                return rs.getString("token");
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Map<String, String> selectAll() {
        try (Connection connection = dataSource.getConnection()) {
            Map<String, String> result = new HashMap<>();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, token FROM credential");
            if (rs.next()) {
                String id = rs.getString("id");
                String token = rs.getString("token");
LOG.debug("[" + id + "]: " + token);
                result.put(id, token);
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean delete(String id) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM credential WHERE id = ?");
            pstmt.setString(1, id);
            return pstmt.execute();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean deleteAll() {
        try (Connection connection = dataSource.getConnection()) {
            Statement stmt = connection.createStatement();
            return stmt.execute("DELETE FROM credential");
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}

/* */
