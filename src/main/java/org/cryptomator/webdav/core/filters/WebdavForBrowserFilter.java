package org.cryptomator.webdav.core.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebdavForBrowserFilter implements HttpFilter {

    private static final Logger LOG = LoggerFactory.getLogger(WebdavForBrowserFilter.class);
    private static final String METHOD_GET = "GET";
    private static final String METHOD_PROPFIND = "PROPFIND";
    private static final String USER_AGENT = "user-agent";

    @Override
    public void doFilterHttp(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String ua = request.getHeader(USER_AGENT);
LOG.debug(USER_AGENT + ": " + ua);
        boolean isBrowser = ua.indexOf("Mozilla") >= 0;
        if (METHOD_GET.equalsIgnoreCase(request.getMethod()) && isBrowser) {
LOG.debug("client is browser: " + ua);
            request = new HttpServletRequestWrapper(request) {
                public String getMethod() {
                    return METHOD_PROPFIND;
                }
            };
        }
        chain.doFilter(request, response);
    }
}
