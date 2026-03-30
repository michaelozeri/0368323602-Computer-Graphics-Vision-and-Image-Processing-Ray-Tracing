package com.rayTracing.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.rayTracing.RayTracing.RayTracer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Ray tracing", description = "Metadata and scene rendering via HTTP")
public class RayTracingApiController {

    private static final Logger log = LoggerFactory.getLogger(RayTracingApiController.class);

    @GetMapping("/info")
    @Operation(summary = "Service metadata", description = "Basic name, version, and how to call the render endpoint.")
    public ApiInfoResponse info() {
        log.debug("GET /api/v1/info");
        return new ApiInfoResponse(
                "ray-tracing",
                "0.0.1-SNAPSHOT",
                "POST /api/v1/render with scenePath (existing .txt), outputPath (.png), optional width and height (default 500×500).");
    }

    @PostMapping("/render")
    @Operation(summary = "Render a scene file",
            description = "Loads a scene description from disk, ray-traces it, and writes a PNG to the given output path.")
    public RenderResponse render(@RequestBody RenderRequest request) {
        long startNanos = System.nanoTime();
        if (request.scenePath() == null || request.scenePath().isBlank()) {
            log.warn("POST /api/v1/render rejected: scenePath is required");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "scenePath is required");
        }
        if (request.outputPath() == null || request.outputPath().isBlank()) {
            log.warn("POST /api/v1/render rejected: outputPath is required");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "outputPath is required");
        }

        Path scene = Path.of(request.scenePath());
        if (!Files.isRegularFile(scene)) {
            log.warn("POST /api/v1/render rejected: scenePath not a file: {}", scene);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "scenePath must be an existing file: " + scene);
        }

        Path output = Path.of(request.outputPath());
        Path parent = output.getParent();
        if (parent != null && !Files.isDirectory(parent)) {
            log.warn("POST /api/v1/render rejected: output parent is not a directory: {}", parent);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "output directory must exist: " + parent);
        }

        int w = request.width() != null ? request.width() : 500;
        int h = request.height() != null ? request.height() : 500;
        if (w <= 0 || h <= 0) {
            log.warn("POST /api/v1/render rejected: invalid size {}x{}", w, h);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "width and height must be positive");
        }

        log.info(
                "POST /api/v1/render scenePath={} outputPath={} size={}x{}",
                scene,
                output,
                w,
                h);

        try {
            RayTracer tracer = new RayTracer();
            tracer.imageWidth = w;
            tracer.imageHeight = h;
            tracer.parseScene(scene.toString());
            tracer.renderScene(output.toString());
        } catch (IOException e) {
            log.error(
                    "Render failed scenePath={} outputPath={}: {}",
                    scene,
                    output,
                    e.getMessage(),
                    e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }

        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
        log.info(
                "Render completed in {} ms scenePath={} outputPath={}",
                elapsedMs,
                scene,
                output);
        return new RenderResponse(output.toString(), "Rendering finished and image saved.");
    }

    public record ApiInfoResponse(String name, String version, String description) {
    }

    public record RenderRequest(String scenePath, String outputPath, Integer width, Integer height) {
    }

    public record RenderResponse(String outputPath, String message) {
    }
}
