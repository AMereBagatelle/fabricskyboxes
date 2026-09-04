# API documentation (Minecraft 1.21.1)

Nuit's client API can parse, add, inspect, and remove skyboxes at runtime. The public entry point is
`me.flashyreese.mods.nuit.api.NuitApi`.

## Gradle

```groovy
repositories {
    maven { url = "https://api.modrinth.com/maven" }
}

dependencies {
    modImplementation "maven.modrinth:nuit:${project.nuit_version}"
}
```

## Managing skyboxes

```java
import me.flashyreese.mods.nuit.api.NuitApi;
import net.minecraft.resources.ResourceLocation;

NuitApi nuit = NuitApi.getInstance();
ResourceLocation id = ResourceLocation.fromNamespaceAndPath("my_mod", "my_skybox");

// A JSON skybox is parsed and added to the reloadable collection.
nuit.addSkybox(id, skyboxJson);

// Code-created skyboxes can be reloadable or permanent.
nuit.addSkybox(id, skybox);
nuit.addPermanentSkybox(id, skybox);

nuit.getSkybox(id).ifPresent(found -> { /* inspect it */ });
nuit.removeSkybox(id);
nuit.removePermanentSkybox(id);
```

`parseSkybox(id, json)` validates and constructs a skybox without adding it. `getSkyboxes()` and
`getActiveSkyboxes()` return read-only views. `clearSkyboxes()` removes reloadable skyboxes but retains permanent
ones. `getCurrentSkybox()` is the skybox currently participating in the render pass and may return `null`.

All of these calls belong on the Minecraft client thread when they can register or release textures.

## Implementing a skybox

Minecraft 1.21.1 uses the following version-native render contract:

```java
import com.mojang.blaze3d.vertex.PoseStack;
import me.flashyreese.mods.nuit.api.skyboxes.Skybox;
import me.flashyreese.mods.nuit.mixin.SkyRendererAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Matrix4f;

public final class MySkybox implements Skybox {
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
        // Submit the sky geometry here.
    }

    @Override
    public void tick(ClientLevel level) {
        // Update condition/animation state once per client-world tick.
    }

    @Override
    public boolean isActive() {
        return true;
    }
}
```

Implement `me.flashyreese.mods.nuit.skybox.TextureRegistrar` when the skybox owns textures that Nuit should register
and release with its lifecycle. Built-in data-driven skyboxes normally extend `AbstractSkybox`, which supplies fade,
condition, layer, and active-state behavior.

The newer 1.21.11/26.x render-context and GPU-pipeline APIs are intentionally not exposed on this branch because
those Minecraft classes do not exist in 1.21.1.
