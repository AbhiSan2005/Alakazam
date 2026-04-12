package com.project.backend;

import java.util.Map;
import com.project.backend.routes.MediaRoutes;
import io.javalin.Javalin;
import java.util.TimeZone;

public class Server {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> cors.addRule(it -> it.anyHost()));
            config.http.maxRequestSize = 500_000_000L;
        }).start(8000);

        System.out.println("Server started on port 8000");

        app.get("/", ctx -> {
            ctx.json(Map.of(
                "status", "Server Running on Port: 8000",
                "service", "Alakazam DB"
            ));
        });

        MediaRoutes.registerRoutes(app);
    }
}