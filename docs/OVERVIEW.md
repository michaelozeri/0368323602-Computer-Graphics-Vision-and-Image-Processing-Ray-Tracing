# Ray Tracing Project — Repository Overview

This repository is a **Java ray tracer** for 3D image synthesis: it loads scene descriptions from text files, traces rays from a pinhole-style camera, and writes **PNG** images. It aligns with coursework in computer graphics (ray tracing, shading, and visibility).

---

## Purpose

- Render synthetic images using **recursive ray tracing** with **Phong-style** local shading (diffuse and specular), **reflections**, **transparency**, **shadow rays** (with configurable sampling), and optional **super-sampling** anti-aliasing.
- **Primary workflow** is a **command-line** program (`RayTracer`) that takes a scene file and output path. **Spring Boot** (`bootRun`) exposes **`GET /api/v1/info`** and **`POST /api/v1/render`** (see Swagger UI); the HTTP API is optional compared to the CLI.

---

## Tech Stack

| Area | Choice |
|------|--------|
| Language | Java (**toolchain: Java 25** per `build.gradle`) |
| Build | Gradle (root project name: `ray-tracing`) |
| Framework | Spring Boot **4.0.5** (`spring-boot-starter-web`), **SpringDoc OpenAPI** 3.0.2 (Swagger UI) |
| Testing | JUnit Platform via `spring-boot-starter-test` (no `src/test` sources in tree at time of writing) |
| Image output | `javax.imageio.ImageIO` → PNG |

---

## Repository Layout

```
ray-tracing/
├── build.gradle              # Dependencies, Java toolchain, `start` → bootRun alias
├── settings.gradle           # Root project name, Foojay toolchain resolver
├── gradle/wrapper/           # Gradle wrapper
├── README.md                 # Short project blurb
├── docs/
│   └── OVERVIEW.md           # This file
└── src/main/java/com/rayTracing/
    ├── RayTracingApplication.java    # Spring Boot entry point
    ├── web/RayTracingApiController.java  # REST: /api/v1/info, /api/v1/render
    ├── RayTracing/                   # Core ray tracing package
    │   ├── RayTracer.java            # CLI main, parser, render loop, shading
    │   ├── Camera.java
    │   ├── Scene.java                # Global render settings (record)
    │   ├── Light.java                # Point/area-light parameters (record)
    │   ├── Material.java
    │   ├── Surface.java              # Abstract primitive (sphere, plane, triangle)
    │   ├── Sphere.java, Plane.java, Triangle.java
    │   ├── Ray.java, Vector.java, Position.java
    │   └── Intersection.java
    └── scenes/                       # Example scene `.txt` files
```

---

## Entry Points

### 1. Ray tracer (CLI) — main usage

Class: `com.rayTracing.RayTracing.RayTracer`

**Arguments:**

1. **Required:** path to scene description file (`.txt`)
2. **Required:** output image path (PNG)
3. **Optional:** image width (pixels)
4. **Optional:** image height (pixels) — defaults to **500×500** if width/height omitted

**Example** (from project root, after compiling):

```bash
./gradlew classes
java -cp build/classes/java/main \
  com.rayTracing.RayTracing.RayTracer \
  src/main/java/com/rayTracing/scenes/basic.txt \
  output.png \
  800 600
```

Adjust the scene path if you run from another working directory.

### 2. Spring Boot

Class: `com.rayTracing.RayTracingApplication`

```bash
./gradlew bootRun
# or
./gradlew start
```

This starts the embedded web server. Open **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)** for **GET /api/v1/info** and **POST /api/v1/render** (JSON body: `scenePath`, `outputPath`, optional `width` / `height`).

---

## Scene File Format

Scene files are line-oriented text. Lines starting with `#` or empty lines are ignored. Each data line starts with a **three-letter code** (lowercase in the parser), then numeric parameters separated by whitespace.

| Code | Meaning | Parameters (order) |
|------|---------|----------------------|
| `cam` | Camera | `px py pz` (eye), `lx ly lz` (look-at), `ux uy uz` (up), `screen_distance`, `screen_width` |
| `set` | Scene settings | Background RGB (`bgr bgg bgb`), `shadow_rays`, `max_recursion`, `super_sampling_level` |
| `mtl` | Material | Diffuse `dr dg db`, specular `sr sg sb`, reflection `rr rg rb`, Phong exponent, `transparency` |
| `sph` | Sphere | Center `cx cy cz`, `radius`, **1-based** `mat_idx` (stored as 0-based internally) |
| `pln` | Plane | Normal `nx ny nz`, `offset`, **1-based** `mat_idx` |
| `trg` | Triangle | Three vertices `p0x p0y p0z` … `p2x p2y p2z`, **1-based** `mat_idx` |
| `lgt` | Light | Position `px py pz`, color `r g b`, specular contribution, shadow intensity, width (area/soft shadow) |

Material indices in geometry lines are **1-based** in the file; the parser subtracts one when building objects.

Example snippets appear in `src/main/java/com/rayTracing/scenes/basic.txt` (camera, materials, spheres, lights, ground plane).

---

## Rendering Features (Conceptual)

Implemented in `RayTracer` (see class Javadoc and methods such as `renderScene`, `GetColor`, `FindIntersection`):

- **Pinhole camera** with view plane derived from look-at and up vectors.
- **Primitives:** spheres, infinite planes, triangles.
- **Shading:** diffuse (Lambert) and **Phong** specular; per-light visibility via shadow rays.
- **Recursive** reflections and **transparency** (secondary rays with attenuation).
- **Super-sampling:** when `super_sampling_level` is greater than 1, multiple sub-pixel samples are averaged.
- **Soft shadows** controlled by light `width` and `shadow_rays_num` from scene settings.
- Output: accumulated RGB written via `bytes2RGB` / `ImageIO` as sRGB component model.

---

## Bundled Example Scenes

Under `src/main/java/com/rayTracing/scenes/`:

- `basic.txt` — colored spheres and plane
- `Spheres.txt`, `Triangle.txt`, `Triangle2.txt`
- `Transparency.txt`, `Pool.txt`
- `Room1.txt`, `Room10.txt`

Use these as inputs to `RayTracer` to validate features.

---

## Authorship

`RayTracer.java` credits **Michael Ozeri** and **Dor Alt** in the file header.

---

## Related Documentation

- Root **`README.md`**: one-line project description.
- **`build.gradle`**: commented common Gradle tasks (`bootRun`, `build`, `test`, `clean`, `bootJar`).

For deeper behavior (epsilon handling, reflection recursion limits, light intersection helpers), read `RayTracer.java` in full; it is the single orchestration point for parsing, intersection, and shading.
