/*
 * https://stackoverflow.com/questions/12537851/accessing-spring-beans-in-static-method
 */

package vavi.net.webdav;

import java.io.IOException;
import java.nio.file.FileSystem;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vavi.net.webdav.auth.OAuthException;
import vavi.net.webdav.auth.WebOAuth2;


/**
 * gather spring dependencies.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/08 umjammer initial version <br>
 */
@Service
public class MyBeanFactory implements InitializingBean {
    private static MyBeanFactory instance;

    @Autowired
    BeanFactory beanFactory;

    private FileSystem getFileSystemInternal(String id) throws IOException {
        try {
            FileSystem fs = beanFactory.getBean(FileSystem.class, id);
            return fs;
        } catch (BeanCreationException e) {
            OAuthException t = findRootCause(e, OAuthException.class);
            throw t != null ? t : e;
        }
    }

    private  <T extends Throwable> T findRootCause(Throwable t, Class<T> clazz) {
        while (t.getCause() != null) {
            t = t.getCause();
            if (clazz.isInstance(t)) {
                return clazz.cast(t);
            }
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        instance = this;
    }

    public static FileSystem getFileSystem(String id) throws IOException {
        return instance.getFileSystemInternal(id);
    }

    public static WebOAuth2<?, ?> getWebOAuth2(String scheme) {
        return instance.beanFactory.getBean(WebOAuth2.class, scheme);
    }
}