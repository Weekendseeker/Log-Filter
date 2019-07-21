package org.marvin.api;

import java.io.FileNotFoundException;
import java.io.InputStream;

public interface LogSource {

    byte [] getLog(String path);
    byte [] getLog(InputStream stream);

    void saveLog(String log, String path);
    void saveLog(byte[] log, String path);
}
