package com.project.backend;

import java.util.Map;
import com.project.backend.routes.MediaRoutes;
import com.project.exceptions.*;
import io.javalin.Javalin;
import java.util.TimeZone;

public class Server {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> cors.addRule(it -> it.anyHost()));
            config.http.maxRequestSize = 500_000_000L; // 500MB allowance for admin seeding
        }).start(8000);

        System.out.println("Server started on port 8000");

        app.exception(MediaDurationExceededException.class, (e, ctx) -> {
            ctx.status(400).json(new ErrorResponse(400, "DURATION_EXCEEDED", e.getMessage()));
        });

        app.exception(UnsupportedMediaFormatException.class, (e, ctx) -> {
            ctx.status(400).json(new ErrorResponse(400, "UNSUPPORTED_FORMAT", e.getMessage()));
        });

        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500).json(new ErrorResponse(500, "SERVER_ERROR", "An unexpected system error occurred: " + e.getMessage()));
            e.printStackTrace();
        });

        app.get("/", ctx -> {
            ctx.json(Map.of(
                "status", "Server Running on Port: 8000",
                "service", "Alakazam DB"
            ));
        });

        MediaRoutes.registerRoutes(app);
    }

    public static class ErrorResponse {
        public int status;
        public String errorCode;
        public String errorMessage;

        public ErrorResponse(int status, String errorCode, String errorMessage) {
            this.status = status;
            this.errorCode = errorCode;
            this.errorMessage = errorMessage;
        }
    }
}