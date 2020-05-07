/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.webdav;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.cryptomator.webdav.core.filters.LoggingFilter;
import org.cryptomator.webdav.core.filters.MacChunkedPutCompatibilityFilter;
import org.cryptomator.webdav.core.filters.UnicodeResourcePathNormalizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import vavi.net.webdav.JavaFsWebDavServlet;
import vavi.net.webdav.SqlStrageDao;
import vavi.net.webdav.WebdavService;
import vavi.net.webdav.auth.StrageDao;
import vavi.net.webdav.auth.box.BoxWebAppCredential;
import vavi.net.webdav.auth.box.BoxWebOAuth2;
import vavi.net.webdav.auth.dropbox.DropBoxWebAppCredential;
import vavi.net.webdav.auth.dropbox.DropBoxWebOAuth2;
import vavi.net.webdav.auth.google.GoogleWebAppCredential;
import vavi.net.webdav.auth.google.GoogleWebAuthenticator;
import vavi.net.webdav.auth.microsoft.MicrosoftWebAppCredential;
import vavi.net.webdav.auth.microsoft.MicrosoftWebOAuth2;


/**
 * Config.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/05 umjammer initial version <br>
 */
@Configuration
public class Config {

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
    public MicrosoftWebOAuth2 microsoftWebOAuth2(@Autowired MicrosoftWebAppCredential microsoftAppCredential) {
        return new MicrosoftWebOAuth2(microsoftAppCredential);
    }

    @Bean
    public GoogleWebAuthenticator googleWebOAuth2(@Autowired GoogleWebAppCredential googleAppCredential) {
        return new GoogleWebAuthenticator(googleAppCredential);
    }

    @Bean
    public BoxWebOAuth2 boxWebOAuth2(@Autowired BoxWebAppCredential boxAppCredential) {
        return new BoxWebOAuth2(boxAppCredential);
    }

    @Bean
    public DropBoxWebOAuth2 dropBoxWebOAuth2(@Autowired DropBoxWebAppCredential dropboxAppCredential) {
        return new DropBoxWebOAuth2(dropboxAppCredential);
    }

    @Bean
    public WebdavService webdavService() {
        return new WebdavService();
    }
}

/* */
