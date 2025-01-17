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

    // Add a default route to get the api documentation
    app.get(
        "/",
        ctx -> {
          String html =
              "<html><head><title>File Transfer API</title></head><body><h1>Welcome to the file transfer API</h1><p>Here are the available endpoints:</p><ul><li>POST /upload/{filename} - Upload a file</li><li>GET /download/{id} - Download a file</li><li>PATCH /modify/{id} - Modify a file</li><li>DELETE /delete/{id} - Delete a file</li></ul></body></html>";

          ctx.contentType("text/html");
          ctx.status(200);
          ctx.result(html);
        });

    app.put(
        "/{filename}",
        ctx -> {
          String filename = ctx.pathParam("filename");
          ctx.redirect("/upload/" + filename);
        });

    app.put(
        "/upload/{filename}",
        ctx -> {
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
          ctx.json(new FileTransfer.UploadResponse(file, id));
        });

    app.get(
        "/download/{id}",
        ctx -> {
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
            ctx.header(
                "Content-Disposition", "attachment; filename=\"" + file.getFile().getName() + "\"");
            ctx.result(file.toInputStream());
          } catch (Exception e) {
            ctx.status(500);
          }
        });

    // Add a PATCH endpoint to modify a file (swaping the file content)
    // Withouth modifying the download id
    app.patch(
        "/modify/{id}",
        ctx -> {
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

          if (!file.checkAuthCode(ctx.queryParam("authCode"))) {
            ctx.status(401);
            ctx.result("Unauthorized");
            return;
          }

          FileTransfer newFile =
              FileTransfer.fromInputStream(ctx.bodyInputStream(), file.getFile().getName());
          db.replace(id, newFile);
          ctx.status(200);
          ctx.json(new FileTransfer.UploadResponse(newFile, id));
        });

    // Add a DELETE endpoint to delete a file
    app.delete(
        "/delete/{id}",
        ctx -> {
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

          if (!file.checkAuthCode(ctx.queryParam("authCode"))) {
            ctx.status(401);
            ctx.result("Unauthorized");
            return;
          }

          // remove the file from the tmp folder
          file.getFile().delete();

          // remove the file from the database
          db.remove(id);
          ctx.status(200);
          ctx.result("File deleted");
        });

    app.start(PORT);
  }
}
