package ch.heigvd.dai;

import io.javalin.Javalin;

import java.util.concurrent.ConcurrentHashMap;

public class Main {
  public static final int PORT = 8080;

  public static void main(String[] args) {
    Javalin app = Javalin.create();

    // This will serve as our database
    ConcurrentHashMap<String, File> db = new ConcurrentHashMap<>();


    app.start(PORT);
  }
}