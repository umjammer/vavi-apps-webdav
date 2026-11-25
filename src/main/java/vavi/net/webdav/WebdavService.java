/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;

import vavi.net.http.HttpUtil;
import vavi.net.webdav.auth.OAuthException;
import vavi.net.webdav.auth.WebOAuth2;
import vavi.nio.file.gathered.GatheredFileSystemProvider;
import vavi.nio.file.gathered.NameMap;


/**
 * WebdavService.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/23 umjammer initial version <br>
 */
public class WebdavService {

    private static final Logger logger = System.getLogger(WebdavService.class.getName());

    @Autowired
    private StrageDao dao;

    /** <id, FileSystem> */
    private final Map<String, FileSystem> fileSystems = new ConcurrentHashMap<>();

    /** <id, String> */
    private final NameMap nameMap = new NameMap();

    /** */
    private FileSystem registerFileSystem(String id) throws IOException {
        FileSystem fs = MyBeanFactory.getFileSystem(id);
        fileSystems.put(id, fs);
        nameMap.put(id, id.replaceAll("[:@\\.]", "_"));
        return fs;
    }

    /** */
    private void deregisterFileSystem(String id) {
        fileSystems.remove(id);
        nameMap.remove(id);
    }

    /**
     * should call after di
     */
    private Map<String, OAuthException> prepareFileSystems() {

        Map<String, OAuthException> errors= new HashMap<>();

        dao.load().forEach(id -> {
            try {
logger.log(Level.DEBUG, "ADD: " + id + "    ---------------------------------------------------");
                if (!fileSystems.containsKey(id)) {
                    registerFileSystem(id);
                }
            } catch (OAuthException e) {
logger.log(Level.INFO, "NOT LOGINED: " + id + ": " + e.getMessage());
                errors.put(id, e);
                deregisterFileSystem(id);
            } catch (Exception e) {
logger.log(Level.WARNING, id, e);
                deregisterFileSystem(id);
            }
        });

        return errors;
    }

    /** */
    public static final String URL_ROOT_PATH = "webdav";

    /** <scheme, Path> */
    private Map<String, Path> rootPaths = new ConcurrentHashMap<>();

    /** */
    private Path gatheredFsRoot;

    /** */
    public void init() throws IOException {
        prepareFileSystems();

        URI uri = URI.create("gatheredfs:///");
        Map<String, Object> env = new HashMap<>();
        env.put(GatheredFileSystemProvider.ENV_FILESYSTEMS, fileSystems);
        env.put(GatheredFileSystemProvider.ENV_NAME_MAP, nameMap);
        FileSystem fs = FileSystems.newFileSystem(uri, env, Thread.currentThread().getContextClassLoader());

        gatheredFsRoot = fs.getRootDirectories().iterator().next();
    }

    /** */
    public Path resolve(String relativeUrl) throws IOException {
        String[] parts = relativeUrl.split("/");
        if (parts.length == 0 || !URL_ROOT_PATH.equals(parts[0])) {
            throw new IllegalArgumentException("not starts with " + URL_ROOT_PATH);
        }

        if (parts.length == 1) {
            return gatheredFsRoot;
        }

        String id = nameMap.decodeFsName(parts[1]);
logger.log(Level.DEBUG, id);
        if (id == null || !fileSystems.containsKey(id)) {
            throw new IllegalArgumentException("unknown storage: " + parts[1]);
        }
        String path = String.join("/", Arrays.copyOfRange(parts, 2, parts.length));

        Path rootPath;
        if (rootPaths.containsKey(id)) {
            rootPath = rootPaths.get(id);
        } else {
            FileSystem fs;
            if (fileSystems.containsKey(id)) {
                fs = fileSystems.get(id);
            } else {
                fs = registerFileSystem(id);
                // TODO assert false "???";
            }
            rootPath = fs.getRootDirectories().iterator().next();
            if (!Files.exists(rootPath)) {
                throw new IllegalArgumentException("no root path: " + rootPath);
            }

            rootPaths.put(id, rootPath);
        }

        return rootPath.resolve(path);
    }

    /** for view:/admin/list */
    public Map<?, ?>[] getStrageStatus() {
        Map<String, OAuthException> errors = prepareFileSystems();

        return new Map[] { fileSystems, errors, nameMap.map() };
    }

    /** <state, WebOAuth2> */
    private Map<String, WebOAuth2<?, ?>> sessionOAuth2s = new ConcurrentHashMap<>();

    /** <state, idForScheme> */
    private Map<String, String> sessionIds = new ConcurrentHashMap<>();

    /**
     * @param id "scheme:your@id.com"
     */
    public String login(String id) {
        try {
            String[] parts = id.split(":", 2);
            if (parts.length < 2 || parts[0].isBlank() || parts[1].isBlank()) {
                throw new IllegalArgumentException("invalid id format");
            }
            String scheme = parts[0];
            String idForScheme = parts[1];
            WebOAuth2<?, ?> oauth2 = MyBeanFactory.getWebOAuth2(scheme);
            URI uri = oauth2.getAuthorizationUrl();
logger.log(Level.DEBUG, "authorizationUrl: " + uri);
            String state = HttpUtil.splitQuery(uri).get("state")[0];
logger.log(Level.DEBUG, "state: " + state + ", id: " + idForScheme);
            sessionOAuth2s.put(state, oauth2);
            sessionIds.put(state, idForScheme);
            return uri.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /** TODO */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void auth(String code, String state) {
        try {
            if (state == null || code == null) {
                throw new SecurityException("missing state or code");
            }
logger.log(Level.DEBUG, "state: " + state + ", code: " + (code != null));
            WebOAuth2 oauth2 = sessionOAuth2s.remove(state);
            String id = sessionIds.remove(state);
            if (oauth2 == null || id == null) {
                throw new SecurityException("invalid or expired state");
            }
            oauth2.setResult(code, state);
            oauth2.authorize(id);
logger.log(Level.DEBUG, "auth done: " + id);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
