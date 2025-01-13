package ch.heigvd.dai;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileTransfer {
    private File file;
    private String authCode;
    private String type;
    private String size;

    private static Path storageFolder = Paths.get(System.getProperty("java.io.tmpdir"), "file-transfer");

    static  {
        if (!storageFolder.toFile().exists()) {
            storageFolder.toFile().mkdirs();
        }
    }

    private FileTransfer(File file) {
        this.file = file;
        this.authCode = UUID.randomUUID().toString();
    }

    public File getFile() {
        return file;
    }

    public boolean checkAuthCode(String authCode) {
        return this.authCode.equals(authCode);
    }

    public BufferedInputStream toInputStream() throws FileNotFoundException {
        return new BufferedInputStream(new FileInputStream(file));
    }

    public static FileTransfer fromInputStream(InputStream stream, String filename) {
        BufferedInputStream inputStream = new BufferedInputStream(stream);
        String filePath = storageFolder.resolve(filename).toAbsolutePath().toString();

        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath))) {
            int bytesRead;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new FileTransfer(new File(filePath));
    }

    public static class UploadResponse {
        private FileTransfer fileTransfer;
        private String id;

        public UploadResponse(FileTransfer fileTransfer, String id) {
            this.fileTransfer = fileTransfer;
            this.id = id;
        }

        public String getDownloadId() {
            return this.id;
        }

        public String getAuthCode() {
            return this.fileTransfer.authCode;
        }
    }
}
