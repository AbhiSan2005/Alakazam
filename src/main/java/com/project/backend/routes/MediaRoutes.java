package com.project.backend.routes;

import com.project.backend.controllers.MediaController;
import io.javalin.Javalin;

public class MediaRoutes {
    public static void registerRoutes(Javalin app) {
        MediaController mediaController = new MediaController();

        app.post("/api/media/seed", ctx -> mediaController.seedMedia(ctx));
        app.post("/api/media/match", ctx -> mediaController.matchMedia(ctx));
    }
}