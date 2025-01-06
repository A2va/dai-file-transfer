package ch.heigvd.dai;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

public class File {
    private File file;
    private String authCode;

    private File(File file) {
        this.file = file;
        // TODO generate authCode
    }

    public boolean checkAuthCode(String authCode) {
        return this.authCode.equals(authCode);
    }

    public static File fromStream(BufferedInputStream stream, String path) {
        // TODO Save the file into a specific path and create the File class
        return null;
    }
}
