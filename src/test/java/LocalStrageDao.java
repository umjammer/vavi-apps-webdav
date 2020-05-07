/*
 * Copyright (c) 2019 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * LocalStrageDao.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/06/23 umjammer initial version <br>
 */
class LocalStrageDao {

    Path file;

    LocalStrageDao(String file) {
        this.file = Paths.get(file);
    }

    public List<String> load() {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}

/* */
