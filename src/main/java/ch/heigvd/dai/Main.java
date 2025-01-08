package ch.heigvd.dai;

import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import org.eclipse.jetty.http.HttpTester;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.io.BufferedInputStream;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
  public static final int PORT = 8080;

  public static void main(String[] args) {
    Javalin app = Javalin.create();

    // This will serve as our database
    ConcurrentHashMap<String, FileTransfer> db = new ConcurrentHashMap<>();

    app.put("/upload", ctx -> {
      // Only retrieve to the first one
      InputStream inputStream = ctx.bodyInputStream();
      if (inputStream == null) {
        ctx.status(400);
        ctx.result("No file provided");
      }
      String id = UUID.randomUUID().toString();
      FileTransfer file = FileTransfer.fromInputStream(ctx.bodyInputStream(), id);

      db.put(id, file);
      ctx.status(200);
      ctx.result("Uploaded file with id " + id);
    });

    app.get("/download/{id}", ctx -> {
      String id = ctx.pathParam("id");
      if (id == null) {
        ctx.status(400);
        ctx.result("No id provided");
      }

      FileTransfer file = db.get(id);
      if (file == null) {
        ctx.status(404);
        ctx.result("File not found");
        return;
      }

      try {
        ctx.result(file.toInputStream());
      } catch (Exception e) {
        ctx.status(500);
      }
    });

    app.start(PORT);
  }
}