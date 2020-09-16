# FabricSkyboxes

##### Implements custom skyboxes, similar to Optifine!

## Purpose

This mod allows specification of custom skyboxes, with any textures that you may want.
You can even specify textures already in Minecraft's resources!

## Use

An example of a skybox json file, with comments, is provided [here](https://github.com/AMereBagatelle/fabricskyboxes/blob/master/example).
The skybox json file should be placed in `assets/fabricskyboxes/sky` inside a resource pack.

As of 0.2 you can create mono-colored skyboxes.  These are an alternative to textured skyboxes, if you don't need the complexity.
They will take on the shape of the normal skybox, just of a different color.

##### Disclaimer:  Does not support Optifine skybox resource packs.  This is not planned.

MCPatcherPatcher will support this mod's format in the future, and will be able to convert it for you.
You can find it on GitHub [here](https://github.com/LambdAurora/MCPatcherPatcher).
This will not be a perfect translation, you may have to manually fix some skyboxes due to implementation details.

## Plans

- Full optifine conversion support (via MCPatcherPatcher)
- More stuff!