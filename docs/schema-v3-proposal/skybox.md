# Skyboxes

Replaces the vanilla skybox with a different skybox.

## Texture Objects

### Square Texture

This is the base square texture format.

**Specification**

| Name     | Datatype                              | Description                                                                      | Required           |
|----------|---------------------------------------|----------------------------------------------------------------------------------|--------------------|
| `north`  | [Identifier](schema-v3.md#identifier) | Specifies the location of the texture to be used when rendering the skybox north | :white_check_mark: |
| `south`  | [Identifier](schema-v3.md#identifier) | Specifies the location of the texture to be used when rendering the skybox south | :white_check_mark: |
| `east`   | [Identifier](schema-v3.md#identifier) | Specifies the location of the texture to be used when rendering the skybox east  | :white_check_mark: |
| `west`   | [Identifier](schema-v3.md#identifier) | Specifies the location of the texture to be used when rendering the skybox west  | :white_check_mark: |
| `top`    | [Identifier](schema-v3.md#identifier) | Specifies the location of the texture to be used when rendering the skybox up    | :white_check_mark: |
| `bottom` | [Identifier](schema-v3.md#identifier) | Specifies the location of the texture to be used when rendering the skybox down  | :white_check_mark: |

OR

A individual [Identifier](schema-v3.md#identifier) representing a Optifine format texture.

**Example**

```json
{
  "north": "minecraft:textures/block/blue_ice.png",
  "south": "minecraft:textures/block/blue_ice.png",
  "east": "minecraft:textures/block/dark_oak_log.png",
  "west": "minecraft:textures/block/dark_oak_log.png",
  "top": "minecraft:textures/block/diamond_ore.png",
  "bottom": "minecraft:textures/block/diamond_ore.png"
}
```

## Texture

This section specifies the texture object format for each type of skybox.

### Square Textured Skybox

A single-texture skybox of the type `square-textured`.

**Specification**

A single [Square Texture](#square-texture) object.

### Animated Square Textured Skybox

A multi-texture skybox of the type `animated-square-textured`.

**Specification**

| Name       | Datatype                                | Description                       | Required           |
|------------|-----------------------------------------|-----------------------------------|--------------------|
| `textures` | [Square Texture](#square-texture) Array | The array of textures to display. | :white_check_mark: |
| `fps`      | Integer                                 | Frames per second for animation.  | :white_check_mark: |

**Example**

```json
{
  "textures": [
    {
      "north": "minecraft:textures/block/blue_ice.png",
      "south": "minecraft:textures/block/blue_ice.png",
      "east": "minecraft:textures/block/dark_oak_log.png",
      "west": "minecraft:textures/block/dark_oak_log.png",
      "top": "minecraft:textures/block/diamond_ore.png",
      "bottom": "minecraft:textures/block/diamond_ore.png"
    }
  ],
  "fps": 1
}
```

## Textured Skybox

All textured skybox types share this structure.

**Specification**

| Name       | Datatype                          | Description                                                   | Required           |
|------------|-----------------------------------|---------------------------------------------------------------|--------------------|
| `texture`  | [Texture](#texture)               | See textures object description.  Different for every type.   | :white_check_mark: |
| `blend`    | [Blend](#blend)                   | Specifies how this skybox blends with other skyboxes.         | :white_check_mark: |
| `rotation` | [Rotation](schema-v3.md#rotation) | Specifies how this skybox should rotate with the time of day. | :white_check_mark: |

```json5
{
  "texture": {
  },
  "blend": {
    "type": "",
    // I still like this, low-effort defaults is good
    "blender": {
      // Maybe we only require one or the other - I think this is powerful, but usually not needed
    }
  },
  "rotation": {
    // This needs a lot of work - as evidenced by the weeks of discussion about how confusing it is
    "static": {},
    "axis": {}
  }
}
```

## Monocolor Skybox

A single-color skybox of the type `monocolor`.

**Specification**

| Name    | Datatype                  | Description               | Required           |
|---------|---------------------------|---------------------------|--------------------|
| `color` | [RGBA](schema-v3.md#RGBA) | The color of this skybox. | :white_check_mark: |

**Example**

```json
{
  "color": {
    "red": 0,
    "blue": 100,
    "green": 255,
    "alpha": 1
  }
}
```