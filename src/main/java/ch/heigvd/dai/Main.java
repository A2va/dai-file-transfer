package ch.heigvd.dai;

import io.javalin.Javalin;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
  public static final int PORT = 8080;

  public static void main(String[] args) {
    Javalin app = Javalin.create();

    // This will serve as our database
    ConcurrentHashMap<String, FileTransfer> db = new ConcurrentHashMap<>();

    app.put("/{filename}", ctx -> {
        String filename = ctx.pathParam("filename");
        ctx.redirect("/upload/" + filename);
    });

    app.put("/upload/{filename}", ctx -> {
      InputStream inputStream = ctx.bodyInputStream();
      if (inputStream == null) {
        ctx.status(400);
        ctx.result("No file provided");
      }
      String filename = ctx.pathParam("filename");
      FileTransfer file = FileTransfer.fromInputStream(ctx.bodyInputStream(), filename);

      String id = UUID.randomUUID().toString();
      db.put(id, file);
      ctx.status(200);
      ctx.json(new FileTransfer.Response(file, id));
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
        String mimeType = Files.probeContentType(file.getFile().toPath());
        ctx.contentType(mimeType);
        ctx.header("Content-Disposition", "attachment; filename=\"" + file.getFile().getName() + "\"");
        ctx.result(file.toInputStream());
      } catch (Exception e) {
        ctx.status(500);
      }
    });

    app.start(PORT);
  }
}