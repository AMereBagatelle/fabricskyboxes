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

Currently, this mod does not implement and/or replace all of Optifine's skybox features.
I plan to ensure that Optifine skyboxes can be converted by the first full release of this mod.
With this in mind, it does not mean that all skyboxes will be able to be perfectly translated between the two.

## Plans

- Full optifine conversion support (via MCPatcherPatcher)
- Rotation of skyboxes
- More stuff!