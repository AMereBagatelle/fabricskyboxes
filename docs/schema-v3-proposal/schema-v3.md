# Schema V3

This specification aims to set a standard for custom sky rendering.  
This schema envelops the entire file structure of a resource pack being used to load skyboxes.

## File Structure

The basic structure of a resource pack implementing custom skyboxes will be as follows:

```
assets/namespace/sky...
    /skyboxes.json
    /skyboxes/...
        (all skybox JSON files here)
    /decorations/...
        (all decoration JSON files here)
    /fog/
        (all fog JSON files here)
    /images/...
        (all images here)
```

The overarching `skyboxes.json` file will define the layer priority for all skyboxes.
It also defines several general conflict resolution strategies for multiple skyboxes.

The details for the files in each of the sub-folders can be found in their respectively named docs:

- [Skyboxes](skybox.md)
- [Decorations](decorations.md)
- [Fog](fog.md)
- [Images](images.md)

## Objects

This section defines all common objects and their data structures.  
When a common object is referenced, it is guaranteed to be parsed as described in this section.

### Identifier

Specifies the location of a file as a string in the format `namespace:path`. The string `namespace:path` translates
to `assets/namespace/path` (at least in the scenarios present in FabricSkyboxes). More info can be found on
the [Minecraft Wiki](https://minecraft.fandom.com/wiki/Resource_location).

**Specification**

Does not contain any fields. The value must consist of a valid namespace and path, separated by a colon (`:`)

### RGBA

Contains a list of four floats representing a RGBA color.

**Specification**

|  Name   | Datatype |                                    Description                                     |      Required      | Default |
|:-------:|:--------:|:----------------------------------------------------------------------------------:|:------------------:|:-------:|
|  `red`  |  Float   |  Specifies the amount of red color to be used. Must be a value between 0 and 255.  | :white_check_mark: |    -    |
| `green` |  Float   | Specifies the amount of green color to be used. Must be a value between 0 and 255. | :white_check_mark: |    -    |
| `blue`  |  Float   | Specifies the amount of blue color to be used. Must be a value between 0 and 255.  | :white_check_mark: |    -    |
| `alpha` |  Float   |     Specifies the amount of alpha to be used. Must be a value between 0 and 1.     | :white_check_mark: |    -    |

**Example**

```json
{
  "red": 0,
  "blue": 100,
  "green": 255,
  "alpha": 0.8
}
```

### Fade

### Range

Specifies a range of values.

**Specification**

Does not contain any fields.
Defined as an array of two numbers, where the first is the minimum and the second is the maximum.

**Examples**

```json
[
  60,
  120
]
```

### Rotation
TODO

### Weather

Specifies a kind of weather as a String.

**Specification**

Does not contain any fields. The value must be one of `clear`, `rain`, `thunder` or `snow`.

### Loop

Specifies the loop condition.

**Specification**

|   Name   |       Datatype        |                   Description                    | Required |     Default Value      |
|:--------:|:---------------------:|:------------------------------------------------:|:--------:|:----------------------:|
|  `days`  |         Float         |      Specifies the number of days to loop.       |   :x:    |           7            |
| `ranges` | [Range](#range) Array | Specifies the days where the skybox is rendered. |   :x:    | Empty Array (all days) |

**Example**

```json
{
  "days": 30.0,
  "ranges": [
    {
      "min": 0,
      "max": 7
    },
    {
      "min": 14,
      "max": 21
    }
  ]
}
```

### Condition

Represents a union of a whitelist and a blacklist.
May contain values of any type, represented by "Value" in specification.

**Specification**

| Name        | Datatype    | Description                                                  | Required | Default Value |
|-------------|-------------|--------------------------------------------------------------|----------|---------------|
| `whitelist` | Value Array | Specifies a list of values that allow the condition to pass. | :x:      | Everything    |
| `blacklist` | Value Array | Specifies a list of values that block the condition passing. | :x:      | None          |

**Example**
With Value being an [Identifier](#identifier).
```json
{
  "whitelist": [
    "minecraft:desert",
    "minecraft:desert_hills"
  ],
  "blacklist": [
    "minecraft:desert_lakes"
  ]
}
```

### Conditions

Specifies when and where a layer should render. All fields are optional.

**Specification**

|     Name     |                     Datatype                      |                                 Description                                  |          Default value          |
|:------------:|:-------------------------------------------------:|:----------------------------------------------------------------------------:|:-------------------------------:|
|   `biomes`   | [Identifier](#identifier) [Condition](#condition) |       Specifies a list of biomes that the skybox should be rendered in       | Default [Condition](#condition) |
|   `worlds`   | [Identifier](#identifier) [Condition](#condition) | Specifies a list of worlds sky effects that the skybox should be rendered in | Default [Condition](#condition) |
| `dimensions` | [Identifier](#identifier) [Condition](#condition) |     Specifies a list of dimension that the skybox should be rendered in      | Default [Condition](#condition) |
|  `effects`   | [Identifier](#identifier) [Condition](#condition) |      Specifies a list of effects that the skybox should be rendered in       | Default [Condition](#condition) |
|  `weather`   |   [Weathers](#weather) [Condition](#condition)    | Specifies a list of weather conditions that the skybox should be rendered in | Default [Condition](#condition) |
|  `xRanges`   |      [Range](#range) [Condition](#condition)      |  Specifies a list of coordinates that the skybox should be rendered between  | Default [Condition](#condition) |
|  `yRanges`   |      [Range](#range) [Condition](#condition)      |  Specifies a list of coordinates that the skybox should be rendered between  | Default [Condition](#condition) |
|  `zRanges`   |      [Range](#range) [Condition](#condition)      |  Specifies a list of coordinates that the skybox should be rendered between  | Default [Condition](#condition) |
|    `loop`    |                   [Loop](#loop)                   |     Specifies the loop object that the skybox should be rendered between     |      Default [Loop](#loop)      |

**Example**

```json
{
  "biomes": [
    "minecraft:desert",
    "minecraft:desert_hills",
    "minecraft:desert_lakes"
  ],
  "worlds": [
    "minecraft:overworld"
  ],
  "dimensions": [
    "my_datapack:custom_world"
  ],
  "effects": [
    "minecraft:jump_boost",
    "minecraft:speed",
    "minecraft:slowness"
  ],
  "weather": [
    "rain",
    "thunder"
  ],
  "xRanges": [
    {
      "min": -100.0,
      "max": 100.0
    }
  ],
  "yRanges": [
    {
      "min": 50.0,
      "max": 60.0
    },
    {
      "min": 100.0,
      "max": 110.0
    }
  ],
  "zRanges": [
    {
      "min": -100.0,
      "max": 100.0
    }
  ],
  "loop": {
    "days": 8,
    "ranges": [
      {
        "min": 1,
        "max": 7
      }
    ]
  }
}
```

### Layer Type

Describes a layer type.

**Specification**

A string, with any valid skybox type or `decorations` for decorations layers.

### Layer

Describes a skybox layer.

**Specification**

| Name         | Datatype                  | Description                               | Required           |
|--------------|---------------------------|-------------------------------------------|--------------------|
| `file`       | [Identifier](#identifier) | The file to reference for the layer data. | :white_check_mark: |
| `type`       | [Layer Type](#layer-type) | The type of the layer.                    | :white_check_mark: |
| `conditions` | [Conditions](#conditions) | The conditions for the layer.             | :white_check_mark: |

**Example**

```json
{
  "file": "skybox1.json",
  "type": "square-textured",
  "conditions": {
  }
}
```


## Skybox Configuration JSON

This file's purpose is to handle all interactions between skyboxes and when the skyboxes should appear.

**Specification**

| Name   | Datatype              | Description                                                                                                    | Required           |
|--------|-----------------------|----------------------------------------------------------------------------------------------------------------|--------------------|
| Layers | [Layer](#layer) Array | Defines priority of layers being rendered.  Layers will be rendered with the first object in the array on top. | :white_check_mark: |
| Fog    | String Array          | Defines fog.  Valid values reference [Fog](#fog.md) files.                                                     | :white_check_mark: |


**Example**

```json5
{
  "layers": [
    {
      "file": "skybox1.json",
      "type": "square-textured",
      "conditions": {
        "biomes": []
      }
    },
    {
      "file": "decorations.json",
      "type": "decorations",
      "conditions": {
        "skybox": "skybox1"
      }
    }
  ],
  "fog": [
    "fog.json"
  ]
}
```

(Developer's Notes Below)

The reason that the type is in the skybox description in the skyboxes.json is to facilitate easier parsing.
Instead of having to get the metadata of the skybox and then parsing it again for the rest of the data - it can all be
done once.

The idea of the skyboxes.json file is that it defines what is being applied when and in what order, while the files
themselves describe specific effects.