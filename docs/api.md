# Nuit API

Nuit exposes a small client-side API for mods that want to register skybox schemas or manage skyboxes at runtime.
The main entry points live under `me.flashyreese.mods.nuit.api` and `me.flashyreese.mods.nuit.api.skyboxes`.
On Minecraft 1.21.1, the compatibility types described below remain under `me.flashyreese.mods.nuit.skybox`.

The API is beta for the 1.0.0 line. Prefer the documented entry points below and avoid depending on internal render classes.

## Dependency

Use the loader artifact for the platform you are building against.

```gradle
repositories {
    maven {
        url = "https://maven.flashyreese.me/snapshots"
    }
}

dependencies {
    modImplementation "me.flashyreese.mods:nuit-fabric:${nuit_version}"
    // or: modImplementation "me.flashyreese.mods:nuit-neoforge:${nuit_version}"
}
```

## Main Entry Point

```java
NuitApi api = NuitApi.getInstance();
```

`NuitApi.getApiVersion()` currently returns `1`.

## Registering A Skybox Type

The canonical registration path is `NuitApi.registerSkyboxType(...)`.

```java
public final class ExampleClient {
    public static final SkyboxType<ExampleSkybox> EXAMPLE_SKYBOX = NuitApi.registerSkyboxType(
            ResourceLocation.fromNamespaceAndPath("example", "example_skybox"),
            1,
            ExampleSkybox.CODEC
    );
}
```

Skybox type identifiers are normal Minecraft identifiers. If the namespace is omitted in resource JSON, Nuit resolves the type under the `nuit` namespace.

On Minecraft 1.21.1, `SkyboxType` lives in `me.flashyreese.mods.nuit.skybox`.

## Runtime Skyboxes

```java
NuitApi api = NuitApi.getInstance();
ResourceLocation id = ResourceLocation.fromNamespaceAndPath("example", "runtime_skybox");

api.addSkybox(id, skybox);              // cleared on resource reload
api.addPermanentSkybox(id, skybox);     // survives resource reload
api.removeSkybox(id);
api.removePermanentSkybox(id);
api.getSkybox(id);                      // searches reloadable and permanent skyboxes
api.getSkyboxes();                      // read-only map of reloadable skyboxes
api.getActiveSkyboxes();
api.getCurrentSkybox();                 // nullable Skybox
```

Use `parseSkybox(id, jsonObject)` when you want Nuit to decode JSON without immediately adding the result.

## Skybox Interfaces

### `Skybox`

Base lifecycle contract:

```java
public interface Skybox {
    default int getLayer() { return 0; }
    void render(
            SkyRendererAccessor renderer,
            PoseStack poseStack,
            Matrix4f projectionMatrix,
            float tickDelta,
            Camera camera,
            boolean thickFog,
            Runnable restoreFog
    );
    void tick(ClientLevel level);
    boolean isActive();
}
```

Lower layers render first. Minecraft 1.21.1 puts rendering directly on `Skybox`; it does not provide the newer
`RenderableSkybox` split or `SkyboxRenderContext`.

### `NuitSkybox`

Use this for Nuit-style skyboxes with alpha, properties, and conditions:

```java
public interface NuitSkybox extends Skybox {
    float getAlpha();
    void updateAlpha(ClientLevel level);
    Properties getProperties();
    Conditions getConditions();
}
```

`AbstractSkybox` already implements the standard Nuit alpha and condition behavior.

### `TextureRegistrar`

Implement this when Nuit should preload and release textures used by your skybox:

```java
public interface TextureRegistrar {
    List<ResourceLocation> getTexturesToRegister();
}
```

## Render Contract

The 1.21.1 render boundary passes the frame state and vanilla sky buffers directly to `Skybox.render(...)`.
`NuitRenderBackend` provides the internal immediate-buffer implementation used by built-in skyboxes.

Do not depend on Nuit's internal `NuitRenderBackend` as public API unless you are working inside Nuit itself. The
class may change with Minecraft renderer changes.

## Minimal Renderable Example

```java
public final class ExampleSkybox implements Skybox {
    public static final Codec<ExampleSkybox> CODEC = Codec.unit(ExampleSkybox::new);

    @Override
    public void tick(ClientLevel level) {
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public int getLayer() {
        return 100;
    }

    @Override
    public void render(
            SkyRendererAccessor renderer,
            PoseStack poseStack,
            Matrix4f projectionMatrix,
            float tickDelta,
            Camera camera,
            boolean thickFog,
            Runnable restoreFog
    ) {
        // Draw sky geometry here, or use the supplied vanilla sky buffers.
    }
}
```

## JSON For A Registered Type

```json
{
  "schemaVersion": 1,
  "type": "example:example_skybox"
}
```
