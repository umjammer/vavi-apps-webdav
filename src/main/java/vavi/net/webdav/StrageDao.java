/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.net.webdav;

import java.util.List;
import java.util.Map;


/**
 * StrageDao.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/23 umjammer initial version <br>
 */
public interface StrageDao {
    /** */
    List<String> load();

    /** */
    void update(String id, String token);

    /** */
    String select(String id);

    /** */
    boolean delete(String id);

    /** */
    boolean deleteAll();

    /** */
    Map<String, String> selectAll();
}

/* */
