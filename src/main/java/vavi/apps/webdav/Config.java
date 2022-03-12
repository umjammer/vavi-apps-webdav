/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.webdav;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.cryptomator.webdav.core.filters.LoggingFilter;
import org.cryptomator.webdav.core.filters.MacChunkedPutCompatibilityFilter;
import org.cryptomator.webdav.core.filters.UnicodeResourcePathNormalizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import com.github.fge.filesystem.box.BoxFileSystemProvider;
import com.github.fge.fs.dropbox.DropBoxFileSystemProvider;
import com.google.api.client.util.store.DataStoreFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import vavi.net.webdav.JavaFsWebDavServlet;
import vavi.net.webdav.StrageDao;
import vavi.net.webdav.WebdavService;
import vavi.net.webdav.auth.WebOAuth2;
import vavi.net.webdav.auth.WebUserCredential;
import vavi.net.webdav.auth.box.BoxWebAppCredential;
import vavi.net.webdav.auth.box.BoxWebOAuth2;
import vavi.net.webdav.auth.dropbox.DropBoxWebAppCredential;
import vavi.net.webdav.auth.dropbox.DropBoxWebOAuth2;
import vavi.net.webdav.auth.google.DaoDataStoreFactory;
import vavi.net.webdav.auth.google.GoogleWebAppCredential;
import vavi.net.webdav.auth.google.GoogleWebAuthenticator;
import vavi.net.webdav.auth.microsoft.MicrosoftWebAppCredential;
import vavi.net.webdav.auth.microsoft.MicrosoftWebOAuth2;
import vavi.nio.file.googledrive.GoogleDriveFileSystemProvider;
import vavi.nio.file.onedrive4.OneDriveFileSystemProvider;


/**
 * Config.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/05 umjammer initial version <br>
 */
@Configuration
@ComponentScan("vavi.net.webdav")
public class Config {

//    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    @Value("${spring.datasource.url}")
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

    private static final String WEBDAV_MAPPING = "/" + WebdavService.URL_ROOT_PATH + "/*";

    /** @see Configuration */
    @Bean
    public ServletRegistrationBean<JavaFsWebDavServlet> webdavServletRegistrationBean(@Autowired WebdavService service) {
        ServletRegistrationBean<JavaFsWebDavServlet> bean = new ServletRegistrationBean<>(new JavaFsWebDavServlet(service));
        bean.addUrlMappings(WEBDAV_MAPPING);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilterRegistrationBean() {
        FilterRegistrationBean<LoggingFilter> bean = new FilterRegistrationBean<>(new LoggingFilter());
        bean.addUrlPatterns(WEBDAV_MAPPING);
        bean.addUrlPatterns("/redirect/*");
        return bean;
    }

    @Bean
    public FilterRegistrationBean<MacChunkedPutCompatibilityFilter> macChunkedPutCompatibilityFilterRegistrationBean() {
        FilterRegistrationBean<MacChunkedPutCompatibilityFilter> bean = new FilterRegistrationBean<>(new MacChunkedPutCompatibilityFilter());
        bean.addUrlPatterns(WEBDAV_MAPPING);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<UnicodeResourcePathNormalizationFilter> unicodeResourcePathNormalizationFilterRegistrationBean() {
        FilterRegistrationBean<UnicodeResourcePathNormalizationFilter> bean = new FilterRegistrationBean<>(new UnicodeResourcePathNormalizationFilter());
        bean.addUrlPatterns(WEBDAV_MAPPING);
        return bean;
    }

    @Bean
    public HttpFirewall allowUrlEncodedPercentHttpFirewall() {
        // https://stackoverflow.com/questions/53300497/encoded-precent25-with-spring-requestmapping-path-param-gives-http-400
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedPercent(true);
        firewall.setUnsafeAllowAnyHttpMethod(true);
        return firewall;
    }

    @Bean
    public StrageDao strageDao() {
        return new SqlStrageDao();
    }

    @Bean
    public MicrosoftWebAppCredential microsoftAppCredential() {
        return new MicrosoftWebAppCredential();
    }

    @Bean
    public GoogleWebAppCredential googleAppCredential() {
        return new GoogleWebAppCredential();
    }

    @Bean
    public BoxWebAppCredential boxAppCredential() {
        return new BoxWebAppCredential();
    }

    @Bean
    public DropBoxWebAppCredential dropboxAppCredential() {
        return new DropBoxWebAppCredential();
    }

    @Bean
    public WebdavService webdavService() {
        return new WebdavService();
    }

    @Autowired
    MicrosoftWebAppCredential microsoftAppCredential;
    @Autowired
    GoogleWebAppCredential googleAppCredential;
    @Autowired
    BoxWebAppCredential boxAppCredential;
    @Autowired
    DropBoxWebAppCredential dropboxAppCredential;

    @Bean
    DataStoreFactory dataStoreFactory() {
        return new DaoDataStoreFactory(strageDao()); // TODO strageDao
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public WebOAuth2<?, ?> getWebOAuth2(String scheme) throws IOException {
        switch (scheme) {
        case "onedrive":
            return new MicrosoftWebOAuth2(microsoftAppCredential);
        case "googledrive":
            return new GoogleWebAuthenticator(googleAppCredential);
        case "box":
            return new BoxWebOAuth2(boxAppCredential);
        case "dropbox":
            return new DropBoxWebOAuth2(dropboxAppCredential);
        default:
            throw new IllegalArgumentException("unsupported scheme: " + scheme);
        }
    }

    /**
     * @param id 'scheme:id' i.e. 'onedrive:foo@bar.com'
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public FileSystem getFileSystem(String id) throws IOException {
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
            env.put(OneDriveFileSystemProvider.ENV_APP_CREDENTIAL, microsoftAppCredential);
            env.put(OneDriveFileSystemProvider.ENV_USER_CREDENTIAL, new WebUserCredential(idForScheme));
            env.put("ignoreAppleDouble", true);
            break;
        case "googledrive":
            env.put(GoogleDriveFileSystemProvider.ENV_APP_CREDENTIAL, googleAppCredential);
            env.put(GoogleDriveFileSystemProvider.ENV_USER_CREDENTIAL, new WebUserCredential(idForScheme));
            env.put("ignoreAppleDouble", true);
            break;
        case "box":
            env.put(BoxFileSystemProvider.ENV_APP_CREDENTIAL, boxAppCredential);
            env.put(BoxFileSystemProvider.ENV_USER_CREDENTIAL, new WebUserCredential(idForScheme));
            env.put("ignoreAppleDouble", true);
            break;
        case "dropbox":
            env.put(DropBoxFileSystemProvider.ENV_APP_CREDENTIAL, dropboxAppCredential);
            env.put(DropBoxFileSystemProvider.ENV_USER_CREDENTIAL, new WebUserCredential(idForScheme));
            env.put("ignoreAppleDouble", true);
            break;
        default:
            throw new IllegalArgumentException("unsupported scheme: " + scheme);
        }

        // https://github.com/spring-projects/spring-boot/issues/7110#issuecomment-252247036
        FileSystem fs = FileSystems.newFileSystem(uri, env, Thread.currentThread().getContextClassLoader());
        return fs;
    }
}

/* */
