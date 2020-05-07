/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.fge.filesystem.box.provider.BoxFileSystemProvider;
import com.github.fge.fs.dropbox.provider.DropBoxFileSystemProvider;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import vavi.net.auth.oauth2.AppCredential;
import vavi.net.http.HttpUtil;
import vavi.net.webdav.auth.OAuthException;
import vavi.net.webdav.auth.StrageDao;
import vavi.net.webdav.auth.WebOAuth2;
import vavi.net.webdav.auth.WebUserCredential;
import vavi.net.webdav.auth.box.BoxWebAppCredential;
import vavi.net.webdav.auth.box.BoxWebOAuth2;
import vavi.net.webdav.auth.dropbox.DropBoxWebAppCredential;
import vavi.net.webdav.auth.dropbox.DropBoxWebOAuth2;
import vavi.net.webdav.auth.google.GoogleWebAppCredential;
import vavi.net.webdav.auth.google.GoogleWebAuthenticator;
import vavi.net.webdav.auth.microsoft.MicrosoftWebAppCredential;
import vavi.net.webdav.auth.microsoft.MicrosoftWebOAuth2;
import vavi.nio.file.gathered.GatheredFileSystemProvider;
import vavi.nio.file.googledrive.GoogleDriveFileSystemProvider;
import vavi.nio.file.onedrive4.OneDriveFileSystemProvider;


/**
 * WebdavService.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/23 umjammer initial version <br>
 */
public class WebdavService {

    private static final Logger LOG = LoggerFactory.getLogger(WebdavService.class);

    /** */
    public static final String URL_ROOT_PATH = "webdav";

    /** <scheme, Path> */
    private Map<String, Path> rootPaths = new HashMap<>();

    /** */
    private Path gatheredFsRoot;

    /** <id, FileSystem> */
    private Map<String, FileSystem> fileSystems = new HashMap<>();

    /** <scheme, BasicAppCredential> */
    private Map<String, AppCredential> appCredentials = new HashMap<>();

    /** <scheme, WebOAuth2> */
    private Map<String, WebOAuth2<?, ?>> oauth2s = new HashMap<>();

    @Autowired
    MicrosoftWebAppCredential microsoftAppCredential;
    @Autowired
    GoogleWebAppCredential googleAppCredential;
    @Autowired
    BoxWebAppCredential boxAppCredential;
    @Autowired
    DropBoxWebAppCredential dropboxAppCredential;

    @Autowired
    MicrosoftWebOAuth2 microsoftWebOAuth2;
    @Autowired
    GoogleWebAuthenticator googleWebOAuth2;
    @Autowired
    BoxWebOAuth2 boxWebOAuth2;
    @Autowired
    DropBoxWebOAuth2 dropBoxWebOAuth2;

    private boolean inited = false;

    /** @see #inited */
    private void preInit() {
        if (!inited) {
            appCredentials.put("onedrive", microsoftAppCredential);
            appCredentials.put("box", boxAppCredential);
            appCredentials.put("dropbox", dropboxAppCredential);
            appCredentials.put("googledrive", googleAppCredential);

appCredentials.forEach((k, v) -> System.err.println(k + ": " + v));

            oauth2s.put("onedrive", microsoftWebOAuth2);
            oauth2s.put("googledrive", googleWebOAuth2);
            oauth2s.put("box", boxWebOAuth2);
            oauth2s.put("dropbox", dropBoxWebOAuth2);

oauth2s.forEach((k, v) -> System.err.println(k + ": " + v));

            Map<String, String> nameMap = new HashMap<>();
            this.nameMap = HashBiMap.create(nameMap);

            dao.load().forEach(id -> {
                try {
LOG.debug("add: " + id);
                    FileSystem fs = getFileSystem(id);
                    fileSystems.put(id, fs);
                } catch (OAuthException e) {
LOG.info("locked: " + id + ": " + e.getMessage());
                } catch (Exception e) {
LOG.warn(id, e);
                }
            });

            inited = true;
        }
    }

    /** <id, String> */
    private BiMap<String, String> nameMap;

    @Autowired
    private StrageDao dao;

    /** */
    public void init() throws IOException {
        preInit();

        URI uri = URI.create("gatheredfs:///");
        Map<String, Object> env = new HashMap<>();
        env.put(GatheredFileSystemProvider.ENV_FILESYSTEMS, fileSystems);
        env.put(GatheredFileSystemProvider.ENV_NAME_MAP, nameMap);
        FileSystem fs = FileSystems.newFileSystem(uri, env, Thread.currentThread().getContextClassLoader());

        gatheredFsRoot = fs.getRootDirectories().iterator().next();
    }

    /**
     * @param path apparent name
     * @return real id
     */
    private String decode(String path) throws IOException {
        if (nameMap.size() > 0) {
            return nameMap.inverse().get(path);
        } else {
            return URLDecoder.decode(path, Charset.forName("utf-8").name());
        }
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

        String id = decode(parts[1]);
System.err.println(id);
        String path = String.join("/", Arrays.copyOfRange(parts, 2, parts.length));

        Path rootPath;
        if (rootPaths.containsKey(parts[1])) {
            rootPath = rootPaths.get(parts[1]);
        } else {
            FileSystem fs;
            if (fileSystems.containsKey(id)) {
                fs = fileSystems.get(id);
            } else {
                fs = getFileSystem(id);
            }
            rootPath = fs.getRootDirectories().iterator().next();
            if (!Files.exists(rootPath)) {
                throw new IllegalArgumentException("no root path: " + rootPath);
            }

            rootPaths.put(id, rootPath);
        }

        return rootPath.resolve(path);
    }

    /**
     * @param id "scheme:your@id.com"
     */
    private FileSystem getFileSystem(String id) throws IOException {
        String[] part1s = id.split(":");
        if (part1s.length < 2) {
            throw new IllegalArgumentException("bad 2nd path component: should be 'scheme:id' i.e. 'onedrive:foo@bar.com'");
        }
        String scheme = part1s[0];
        String idForScheme = part1s[1];

        URI uri = URI.create(scheme + ":///");
        Map<String, Object> env = new HashMap<>();
        switch (scheme) {
        case "onedrive":
            env.put(OneDriveFileSystemProvider.ENV_APP_CREDENTIAL, appCredentials.get(scheme));
            env.put(OneDriveFileSystemProvider.ENV_USER_CREDENTIAL, new WebUserCredential(idForScheme));
            env.put("ignoreAppleDouble", true);
            break;
        case "googledrive":
            env.put(GoogleDriveFileSystemProvider.ENV_APP_CREDENTIAL, appCredentials.get(scheme));
            env.put(GoogleDriveFileSystemProvider.ENV_USER_CREDENTIAL, new WebUserCredential(idForScheme));
            env.put("ignoreAppleDouble", true);
            break;
        case "box":
            env.put(BoxFileSystemProvider.ENV_APP_CREDENTIAL, appCredentials.get(scheme));
            env.put(BoxFileSystemProvider.ENV_USER_CREDENTIAL, new WebUserCredential(idForScheme));
            env.put("ignoreAppleDouble", true);
            break;
        case "dropbox":
            env.put(DropBoxFileSystemProvider.ENV_APP_CREDENTIAL, appCredentials.get(scheme));
            env.put(DropBoxFileSystemProvider.ENV_USER_CREDENTIAL, new WebUserCredential(idForScheme));
            env.put("ignoreAppleDouble", true);
            break;
        case "vfs":
        default:
            throw new IllegalArgumentException("unsupported scheme: " + scheme);
        }

        nameMap.put(id, id.replaceAll("[:@\\.]", "_"));

        // https://github.com/spring-projects/spring-boot/issues/7110#issuecomment-252247036
        FileSystem fs = FileSystems.newFileSystem(uri, env, Thread.currentThread().getContextClassLoader());
        return fs;
    }

    /** for view:/admin/list */
    public Map<?, ?>[] getStrageStatus() {
        preInit();

        Map<String, OAuthException> errors= new HashMap<>();

        dao.load().forEach(id -> {
            try {
LOG.debug("LIST: " + id + "    ---------------------------------------------------");
                if (!fileSystems.containsKey(id)) {
                    fileSystems.put(id, getFileSystem(id));
                }
            } catch (OAuthException e) {
LOG.debug("NOT LOGINED: " + id);
                errors.put(id, e);
            } catch (Exception e) {
                LOG.warn(id, e);
            }
        });

        return new Map[] { fileSystems, errors, nameMap };
    }

    /** <state, WebOAuth2> */
    private Map<String, WebOAuth2<?, ?>> sessionOAuth2s = new HashMap<>();

    /** <state, idForScheme> */
    private Map<String, String> sessionIds = new HashMap<>();

    /**
     * @param id "scheme:your@id.com"
     */
    public String login(String id) {
        try {
            String[] parts = id.split(":");
            String scheme = parts[0];
            String idForScheme = parts[1];
            WebOAuth2<?, ?> oauth2 = oauth2s.get(scheme);
            URI uri = oauth2.getAuthorizationUrl();
LOG.debug("authorizationUrl: " + uri);
            String state = HttpUtil.splitQuery(uri).get("state")[0];
LOG.debug("state: " + state + ", id: " + idForScheme);
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
LOG.debug("state: " + state + ", code: " + code);
            WebOAuth2 oauth2 = sessionOAuth2s.get(state);
            String id = sessionIds.get(state);
            oauth2.setResult(code, state);
            oauth2.authorize(id);
LOG.debug("auth done: " + id);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}

/* */
