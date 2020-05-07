
package vavi.net.webdav;

import java.io.IOException;
import java.nio.file.Path;

import javax.servlet.ServletException;

import org.cryptomator.webdav.core.servlet.AbstractNioWebDavServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JavaFsWebDavServlet extends AbstractNioWebDavServlet {

    public static final String REALM = DEFAULT_AUTHENTICATE_HEADER.split("\"")[1];

    private static final Logger LOG = LoggerFactory.getLogger(JavaFsWebDavServlet.class);

    private WebdavService service;

    public JavaFsWebDavServlet(WebdavService service) {
        this.service = service;
    }

    @Override
    public void init() throws ServletException {
        super.init();

        try {
            service.init();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * @param relativeUrl "webdav/onedrive%3Afoo%40hotmail.com/path..."
     */
    @Override
    protected Path resolveUrl(String relativeUrl) throws IllegalArgumentException {
LOG.debug("relativeUrl: " + relativeUrl);
        try {
            return service.resolve(relativeUrl);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
