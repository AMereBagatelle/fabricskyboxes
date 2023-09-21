# Skybox specification

**Schema Version 2 (DRAFT)**

This specification defines a format for a set of rules for the purpose of custom sky rendering.

**This format will be updated in the future, and may break existing skyboxes.**

# Table of Contents

- [Structure](#structure)
- [Schema Versions](#schema-versions)
- [Skyboxes](#skyboxes)
    - [Types](#types)
    - [Shared Data](#shared-data)
    - [`monocolor`](#mono-color-skybox)
    - [`overworld`](#overworld-skybox)
    - [`end`](#end-skybox)
    - [Textured](#textured-skyboxes)
    - [`square-textured`](#square-textured-skybox)
    - [`single-sprite-square-textured`](#single-sprite-square-textured-skybox)
    - [Animated](#animated-skyboxes)
    - [`animated-square-textured`](#animated-square-textured-skybox)
    - [`single-sprite-animated-square-textured`](#single-sprite-animated-square-textured-skybox)
    - [`multi-texture`](#multi-texture-skybox)
- [Data Types](#data-types)
    - [Properties Object](#properties-object)
    - [Conditions Object](#conditions-object)
    - [Decorations Object](#decorations-object)
    - [RGBA Object](#rgba-object)
    - [Fade Object](#fade-object)
    - [MinMax Entry Object](#minmax-entry-object)
    - [Integer Vector](#integer-vector)
    - [Float Vector](#float-vector)
    - [Rotation Object](#rotation-object)
    - [Weather](#weather)
    - [Namespaced Id](#namespaced-id)
    - [Textures Object](#textures-object)
    - [Blend Object](#blend-object)
    - [Blender Object](#blender-object)
    - [Loop Object](#loop-object)
    - [Animation Object](#animation-object)
    - [UV Ranges Object](#uv-ranges-object)
- [Full Example](#full-example)

# Structure

The basic structure of a fabricskyboxes skybox file may look something like this:

```json5
{
  "schemaVersion": 0,
  /* version (int) */
  "type": "",
  /* skybox type (string) */
  "conditions": // conditions object (optional)
  {
    "biomes": [],
    /* biomes (string array, optional) */
    "worlds": [],
    /* worlds sky effects (string array, optional) */
    "dimensions": [],
    /* dimensions (string array, optional) */
    "weather": [],
    /* weathers (string array, optional) */
    "effects": [],
    /* effects (namespaced id array, optional) */
    // Here, a "MinMax" type refers to an object containing "min" and "max" keys, both floats
    "xRanges": [],
    /* x ranges (MinMax array, optional) */
    "yRanges": [],
    /* y ranges (MinMax array, optional) */
    "zRanges": [],
    /* z ranges (MinMax array, optional) */
    "loop": // loop object (optional)
    {
      "days": 0,
      /* days to loop (double, optional)*/
      "range": []
      /* day ranges (MinMax array, optional)*/
    }
  },
  "decorations": // decorations object (optional)
  {
    "sun": "",
    /* sun texture path (string, optional) */
    "moon": "",
    /* moon texture path (string, optional) */
    "showSun": true,
    /* render sun (bool, optional) */
    "showMoon": true,
    /* render moon (bool, optional) */
    "showStars": true,
    /* render stars (bool, optional) */
    "rotation": // rotation object FOR SUN/MOON/STARS (optional)
    {
      // Here, a "Float Vector" type refers to an array of 3 floats
      "static": [],
      /* static rotation in degrees (Float Vector, optional) */
      "axis": [],
      /* axis rotation in degrees (Float Vector, optional) */
      "timeShift": [],
      /* time shifted in rotation (Integer Vector, optional) */
      "rotationSpeedX": 0,
      /* speed of rotation pitch (float, optional) */
      "rotationSpeedY": 0,
      /* speed of rotation yaw (float, optional) */
      "rotationSpeedZ": 0
      /* speed of rotation roll (float, optional) */
    },
    "blend": // blend object (textured types only, optional)
    {
      "type": "",
      /* blend type (string, optional) */
      "blender": {
        // blender objects (optional) requires "type" to be "custom"
        "sourceFactor": 0,
        /* sFactor number (int, optional) */
        "destinationFactor": 0,
        /* dFactor number (int, optional) */
        "equation": 0,
        /* equation number (int, optional) */
        "sourceFactorAlpha": 0,
        /* sFactor alpha number (int, optional) */
        "destinationFactorAlpha": 0,
        /* dFactor alpha number (int, optional) */
        "redAlphaEnabled": false,
        /* red alpha state (boolean, optional) */
        "greenAlphaEnabled": false,
        /* green alpha state (boolean, optional) */
        "blueAlphaEnabled": false,
        /* blue alpha state (boolean, optional) */
        "alphaEnabled": true
        /* alpha state (boolean, optional) */
      }
    }
  },
  "properties": // default properties object
  {
    "priority": 0,
    /* integer (optional) */
    "fade": // fade object (optional)
    {
      "startFadeIn": 0,
      /* fade-in start time in ticks (int, optional) */
      "endFadeIn": 0,
      /* fade-in end time in ticks (int, optional) */
      "startFadeOut": 0,
      /* fade-out start time in ticks (int, optional) */
      "endFadeOut": 0,
      /* fade-out end time in ticks (int, optional) */
      "alwaysOn": true
      /* always show skybox (bool, optional) */
    },
    "maxAlpha": 0,
    /* max alpha value (0-1 float, optional) */
    "transitionInDuration": 20,
    /* fade in speed (1-8760000 float, optional) */
    "transitionOutDuration": 20,
    /* fade out speed (1-8760000 float, optional) */
    "changeFog": false,
    /* change fog color (bool, optional) */
    "fogColors": // RGBA object for fog color (optional)
    {
      "red": 0,
      /* amount of red (0-1 float, optional) */
      "blue": 0,
      /* amount of blue (0-1 float, optional) */
      "green": 0,
      /* amount of green (0-1 float, optional) */
      "alpha": 0
      /* the fog density (0-1 float, optional) */
    },
    "sunSkyTint": true,
    /* tint sky yellow during sunrise/sunset (bool, optional) */
    "inThickFog": true,
    /* renders skybox in thick fog ex. nether (bool, optional) */
    "rotation": // rotation object FOR SKYBOX (optional)
    {
      /* Rotation speed of skybox or decorations (bool, optional) */
      "skyboxRotation": true,
      // Here, a "Float Vector" type refers to an array of 3 floats
      "static": [],
      /* static rotation in degrees (Float Vector, optional) */
      "axis": [],
      /* axis rotation in degrees (Float Vector, optional) */
      "timeShift": [],
      /* time shifted in rotation (Integer Vector, optional) */
      "rotationSpeedX": 0,
      /* speed of rotation pitch (float, optional) */
      "rotationSpeedY": 0,
      /* speed of rotation yaw (float, optional) */
      "rotationSpeedZ": 0
      /* speed of rotation roll (float, optional) */
    }
  },
  // The following objects are for specific types, not all of them should be used
  "color": // RGBA object for sky color (monocolor type only, optional)
  {
    "red": 0,
    /* amount of red (0-1 float, optional) */
    "blue": 0,
    /* amount of blue (0-1 float, optional) */
    "green": 0,
    /* amount of green (0-1 float, optional) */
    "alpha": 0
    /* alpha value (0-1 float, optional) */
  },
  "blend": // blend object (textured types only, optional)
  {
    "type": "",
    /* blend type (string, optional) */
    "blender": {
      // blender objects (optional) requires "type" to be "custom"
      "sourceFactor": 0,
      /* sFactor number (int, optional) */
      "destinationFactor": 0,
      /* dFactor number (int, optional) */
      "equation": 0,
      /* equation number (int, optional) */
      "sourceFactorAlpha": 0,
      /* sFactor alpha number (int, optional) */
      "destinationFactorAlpha": 0,
      /* dFactor alpha number (int, optional) */
      "redAlphaEnabled": false,
      /* red alpha state (boolean, optional) */
      "greenAlphaEnabled": false,
      /* green alpha state (boolean, optional) */
      "blueAlphaEnabled": false,
      /* blue alpha state (boolean, optional) */
      "alphaEnabled": true
      /* alpha state (boolean, optional) */
    }
  },
  "textures": // textures object (square-textured type only)
  {
    "north": "",
    /* texture to use for north direction (string, optional) */
    "south": "",
    /* texture to use for south direction (string, optional) */
    "east": "",
    /* texture to use for east direction (string, optional) */
    "west": "",
    /* texture to use for west direction (string, optional) */
    "top": "",
    /* texture to use for top direction (string, optional) */
    "bottom": "",
    /* texture to use for bottom direction (string, optional) */
  },
  "texture": "",
  /* path to single-sprite texture (string, single-sprite-square-textured type only) */
  "fps": 0,
  /* frames per second for animation (float, animated-square-textured, single-sprite-animated-square-textured types only) */
  "animationTextures": // textures to use for animation (animated-square-textured type only)
  [
    {
      // textures for first frame
      "north": "",
      /* texture to use for north direction (string, optional) */
      "south": "",
      /* texture to use for south direction (string, optional) */
      "east": "",
      /* texture to use for east direction (string, optional) */
      "west": "",
      /* texture to use for west direction (string, optional) */
      "top": "",
      /* texture to use for top direction (string, optional) */
      "bottom": "",
      /* texture to use for bottom direction (string, optional) */
    }
    // ...
  ],
  "animationTextures": // textures to use for animation (single-sprite-animated-square-textured type only)
  [
    /* single sprite texture for first frame (string) */
    // ...
  ],
  "animations": [ // animation objects for animation (multi-texture)
    {
      "texture": "", // animation sprite sheet texture (string)
      "uvRanges": { // uv ranges for animation (uv-ranges-object)
        "minU": 0.25,
        "minV": 0.25,
        "maxU": 0.50,
        "maxV": 0.50
      },
      "gridColumns": 32, // number of columns in sprite sheet
      "gridRows": 1, // number of rows in sprite sheet
      "duration": 40, // duration of each sprite in milliseconds
      "frameDuration": { // map of frame duration in milliseconds
        "1": 20,
        "5": 10
      }
    }
  ]
}
```

Note that this isn't actually a valid json file as some fields are blank and there are comments. Instead, it serves to
illustrate the general structure of a skybox file and gives basic descriptions of each item.

## Schema Versions

| Version |    Date    |
|:-------:|:----------:|
|    2    | 10/14/2020 |

## Skyboxes

### Types

There currently exist 5 types of skyboxes:

|                   |  Monocolor  |            Textured             |            Animated Textured             |
|:------------------|:-----------:|:-------------------------------:|:----------------------------------------:|
| **Normal**        | `monocolor` |        `square-textured`        |        `animated-square-textured`        |
| **Single Sprite** |      -      | `single-sprite-square-textured` | `single-sprite-animated-square-textured` |

Normal textured skyboxes require 6 image files (1 for each direction), and are recommended. Single sprite textured
skyboxes only require 1 image file which follows the optifine specification, but they are sometimes buggy.

### Shared Data

All skybox types use these fields

|      Name       |                 Datatype                  |                              Description                              |      Required      |                       Default value                       |
|:---------------:|:-----------------------------------------:|:---------------------------------------------------------------------:|:------------------:|:---------------------------------------------------------:|
|  `properties`   |  [Properties object](#properties-object)  |      Specifies the properties to be used when rendering a skybox      | :white_check_mark: |                             -                             |
|  `conditions`   |  [Conditions object](#conditions-object)  | Specifies conditions about when and where a skybox should be rendered |        :x:         |                       No conditions                       |
|  `decorations`  | [Decorations object](#decorations-object) |          Specifies information about the sun, moon and stars          |        :x:         | Default sun and moon texture with all decorations enabled |
|     `type`      |                  String                   |                Specifies the kind of skybox to be used                | :white_check_mark: |                             -                             |
| `schemaVersion` |                  Integer                  |      Specifies the schema version to be used for deserialization      |        :x:         |                      Falls back to 1                      |

### Mono color skybox

Only the `monocolor` skybox type uses these fields

|  Name   |          Datatype           |            Description            | Required |  Default value   |
|:-------:|:---------------------------:|:---------------------------------:|:--------:|:----------------:|
| `color` | [RGBA Object](#rgba-object) | Specifies the color of the skybox |   :x:    | 0 for each value |

### Overworld skybox

Uses fields from shared data, renders vanilla's overworld skybox.

### End skybox

Uses fields from shared data, renders vanilla's end skybox.

### Textured skyboxes

All `-textured` (non-`monocolor`) skybox types use these fields

|  Name   |           Datatype            |                    Description                     | Required | Default value |
|:-------:|:-----------------------------:|:--------------------------------------------------:|:--------:|:-------------:|
| `blend` | [Blend Object](#blend-object) | Specifies how the skybox should blend into the sky |   :x:    |       -       |

### Square Textured skybox

Only the `square-textured` skybox type uses these fields

|    Name    |              Datatype               |                     Description                      |      Required      | Default value |
|:----------:|:-----------------------------------:|:----------------------------------------------------:|:------------------:|:-------------:|
| `textures` | [Textures object](#textures-object) | Specifies the textures to be used for each direction | :white_check_mark: |       -       |

### Single sprite Square Textured skybox

Only the `single-sprite-square-textured` skybox type uses these fields

|   Name    |            Datatype             |                   Description                    |      Required      | Default value |
|:---------:|:-------------------------------:|:------------------------------------------------:|:------------------:|:-------------:|
| `texture` | [Namespaced Id](#namespaced-id) | Specifies the location of the texture to be used | :white_check_mark: |       -       |

### Animated skyboxes

Animated skybox types (`animated-square-textured` and `single-sprite-animated-square-textured`) use these fields

| Name  |    Datatype    |                       Description                        |      Required      | Default value |
|:-----:|:--------------:|:--------------------------------------------------------:|:------------------:|:-------------:|
| `fps` | Floating Point | Specifies the number of frames to be rendered per second | :white_check_mark: |       -       |

### Animated Square Textured skybox

Only the `animated-square-textured` skybox type uses these fields

|        Name         |                   Datatype                    |                         Description                          |      Required      | Default value |
|:-------------------:|:---------------------------------------------:|:------------------------------------------------------------:|:------------------:|:-------------:|
| `animationTextures` | Array of [Textures objects](#textures-object) | Specifies the list of textures to be used for each direction | :white_check_mark: |       -       |

### Single sprite Animated Square Textured skybox

Only the `single-sprite-animated-square-textured` skybox type uses these fields

|        Name         |                 Datatype                  |                     Description                      |      Required      | Default value |
|:-------------------:|:-----------------------------------------:|:----------------------------------------------------:|:------------------:|:-------------:|
| `animationTextures` | Array of [Namespaced Ids](#namespaced-id) | Specifies a list of locations to textures to be used | :white_check_mark: |       -       |

### Multi Texture Skybox

Only the `multi-texture` skybox type uses these fields

|     Name     |                    Datatype                     |                   Description                    | Required | Default value |
|:------------:|:-----------------------------------------------:|:------------------------------------------------:|:--------:|:-------------:|
| `animations` | Array of [Animation objects](#animation-object) | Specifies a list of animation objects to be used |   :x:    |       -       |

## Data types

### Properties Object

Specifies common properties used by all types of skyboxes.

**Specification**

|          Name           |              Datatype               |                                                                                                           Description                                                                                                            |      Required      | Default value                                |
|:-----------------------:|:-----------------------------------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:------------------:|----------------------------------------------|
|       `priority`        |               Integer               | Specifies the order which skybox will be rendered. If there are multiple skyboxes with identical priority, those skyboxes are not re-ordered therefore being dependant of Vanilla's alphabetical namespaced identifiers loading. |        :x:         | 0                                            |
|         `fade`          |     [Fade object](#fade-object)     |                                                                    Specifies the time of day in ticks that the skybox should start and end fading in and out.                                                                    | :white_check_mark: | -                                            |
|       `maxAlpha`        |                Float                |                                                                       Specifies the maximum value that the alpha can be. The value must be within 0 and 1.                                                                       |        :x:         | 1.0                                          |
| `transitionInDuration`  |               Integer               |                                   Specifies the duration in ticks that skybox will fade in when valid conditions are changed. The value must be within 1 and 8760000 (365 days * 24000 ticks).                                   |        :x:         | 20                                           |
| `transitionOutDuration` |               Integer               |                                   Specifies the duration in ticks that skybox will fade in when valid conditions are changed. The value must be within 1 and 8760000 (365 days * 24000 ticks).                                   |        :x:         | 20                                           |
|       `changeFog`       |               Boolean               |                                                                                    Specifies whether the skybox should change the fog color.                                                                                     |        :x:         | `false`                                      |
|       `fogColors`       |     [RGBA Object](#rgba-object)     |                                                                                        Specifies the colors to be used for rendering fog.                                                                                        |        :x:         | 0 for each value                             |
|      `sunSkyTint`       |               Boolean               |                                                                            Specifies whether the skybox should disable sunrise/set sky color tinting                                                                             |        :x:         | `true`                                       |
|      `inThickFog`       |               Boolean               |                                                                                  Specifies whether the skybox should be rendered in thick fog.                                                                                   |        :x:         | `true`                                       |
|       `rotation`        | [Rotation object](#rotation-object) |                                                                                           Specifies the rotation angles of the skybox.                                                                                           |        :x:         | [0,0,0] for static/axis, 1 for rotationSpeed |

**Example**

```json
{
  "priority": 1,
  "fade": {
    "startFadeIn": 1000,
    "endFadeIn": 2000,
    "startFadeOut": 3000,
    "endFadeOut": 4000
  },
  "maxAlpha": 0.5,
  "transitionInDuration": 20,
  "transitionOutDuration": 20,
  "sunSkyTint": false,
  "inThickFog": true,
  "changeFog": true,
  "fogColors": {
    "red": 0.2,
    "green": 0.9,
    "blue": 0.6,
    "alpha": 1.0
  },
  "rotation": {
    "static": [
      216,
      288,
      144
    ],
    "axis": [
      36,
      108,
      72
    ],
    "rotationSpeedX": 0,
    "rotationSpeedY": 1,
    "rotationSpeedZ": 0
  }
}
```

### Conditions Object

Specifies when and where a skybox should render. All fields are optional.

**Specification**

|     Name     |                    Datatype                     |                                 Description                                  |             Default value             |
|:------------:|:-----------------------------------------------:|:----------------------------------------------------------------------------:|:-------------------------------------:|
|   `biomes`   |    Array of [Namespaced Ids](#namespaced-id)    |       Specifies a list of biomes that the skybox should be rendered in       |       Empty Array (all biomes)        |
|   `worlds`   |    Array of [Namespaced Ids](#namespaced-id)    | Specifies a list of worlds sky effects that the skybox should be rendered in |       Empty Array (all worlds)        |
| `dimensions` |    Array of [Namespaced Ids](#namespaced-id)    |     Specifies a list of dimension that the skybox should be rendered in      |     Empty Array (all dimensions)      |
|  `effects`   |    Array of [Namespaced Ids](#namespaced-id)    |      Specifies a list of effects that the skybox should be rendered in       |     Empty Array (default effects)     |
|  `weather`   |          Array of [Weathers](#weather)          | Specifies a list of weather conditions that the skybox should be rendered in |   Empty Array (vanilla conditions)    |
|  `xRanges`   | Array of [MinMax Entries](#minmax-entry-object) |  Specifies a list of coordinates that the skybox should be rendered between  |    Empty Array (all x coordinates)    |
|  `yRanges`   | Array of [MinMax Entries](#minmax-entry-object) |  Specifies a list of coordinates that the skybox should be rendered between  |    Empty Array (all y coordinates)    |
|  `zRanges`   | Array of [MinMax Entries](#minmax-entry-object) |  Specifies a list of coordinates that the skybox should be rendered between  |    Empty Array (all z coordinates)    |
|    `loop`    |           [Loop Object](#loop-object)           |     Specifies the loop object that the skybox should be rendered between     | Default Loop Object which is disabled |

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

### Decorations Object

Stores all specifications for sun and moon configuration. For optimum results, the moon texture should mimic the vanilla
moon texture.
The Default value stores the overworld sun and moon textures and sets all enabled to true.

**Specification**

|       Name       |              Datatype               |                                  Description                                  | Required |                              Default value                              |
|:----------------:|:-----------------------------------:|:-----------------------------------------------------------------------------:|:--------:|:-----------------------------------------------------------------------:|
| `skyboxRotation` |               Boolean               | Rotates symmetrically if enabled, otherwise rotate trajectory of the sun/moon |   :x:    |                                 `true`                                  |
|      `sun`       |   [Namespaced Id](#namespaced-id)   |    Specifies the location of the texture to be used for rendering the sun     |   :x:    |     Default sun texture (`minecraft:textures/environment/sun.png`)      |
|      `moon`      |   [Namespaced Id](#namespaced-id)   |    Specifies the location of the texture to be used for rendering the moon    |   :x:    | Default moon texture (`minecraft:textures/environment/moon_phases.png`) |
|    `showSun`     |               Boolean               |                 Specifies whether the sun should be rendered                  |   :x:    |                                 `false`                                 |
|    `showMoon`    |               Boolean               |                 Specifies whether the moon should be rendered                 |   :x:    |                                 `false`                                 |
|   `showStars`    |               Boolean               |                  Specifies whether stars should be rendered                   |   :x:    |                                 `false`                                 |
|    `rotation`    | [Rotation Object](#rotation-object) |                  Specifies the rotation of the decorations.                   |   :x:    |              [0,0,0] for static/axis, 1 for rotationSpeed               |
|     `blend`      |    [Blend Object](#blend-object)    |                 Specifies the blend mode for the decorations.                 |   :x:    |                  `type` and `blender` of `decorations`                  |

**Example**

```json
{
  "sun": "minecraft:textures/environment/sun.png",
  "moon": "minecraft:textures/atlas/blocks.png",
  "showSun": true,
  "showMoon": true,
  "showStars": false,
  "rotation": {
    "skyboxRotation": true,
    "static": [
      216,
      288,
      144
    ],
    "axis": [
      36,
      108,
      72
    ],
    "timeShift": [
      6000,
      0,
      0
    ],
    "rotationSpeedX": 0,
    "rotationSpeedY": 1,
    "rotationSpeedZ": 0
  },
  "blend": {
    "type": "decorations"
  }
}
```

### RGBA Object

Stores a list of four floating-point literals, each for a specific color. The fourth, alpha, is not required. The value
of these literals must be between 0 and 1.
The default value for RGBA objects is the RGBA Zero, whose values are zeroes.

**Specification**

|  Name   |    Datatype    |                                   Description                                    |      Required      | Default |
|:-------:|:--------------:|:--------------------------------------------------------------------------------:|:------------------:|:-------:|
|  `red`  | Floating point |  Specifies the amount of red color to be used. Must be a value between 0 and 1.  | :white_check_mark: |    -    |
| `green` | Floating point | Specifies the amount of green color to be used. Must be a value between 0 and 1. | :white_check_mark: |    -    |
| `blue`  | Floating point | Specifies the amount of blue color to be used. Must be a value between 0 and 1.  | :white_check_mark: |    -    |
| `alpha` | Floating point |    Specifies the amount of alpha to be used. Must be a value between 0 and 1.    |        :x:         |   1.0   |

**Example**

```json
{
  "red": 0.5,
  "blue": 0.4,
  "green": 0.6,
  "alpha": 0.8
}
```

### Fade Object

Stores a list of four integers which specify the time in ticks to start and end fading the skybox in and out.

**Specification**

|      Name      | Datatype |                       Description                       | Required | Default |
|:--------------:|:--------:|:-------------------------------------------------------:|:--------:|:-------:|
| `startFadeIn`  | Integer  | The times in ticks when a skybox will start to fade in  |   :x:    |    0    |
|  `endFadeIn`   | Integer  |   The times in ticks when a skybox will end fading in   |   :x:    |    0    |
| `startFadeOut` | Integer  | The times in ticks when a skybox will start to fade out |   :x:    |    0    |
|  `endFadeOut`  | Integer  |  The times in ticks when a skybox will end fading out   |   :x:    |    0    |
|   `alwaysOn`   | Boolean  | Whether the skybox should always be at full visibility  |   :x:    |  false  |

**Conversion Table**

| Time in Ticks | Clock Time |
|:-------------:|:----------:|
|       0       |    6 AM    |
|     6000      |   12 PM    |
|     12000     |    6 PM    |
|     18000     |   12 AM    |

**Example**

```json
{
  "startFadeIn": 1000,
  "endFadeIn": 2000,
  "startFadeOut": 3000,
  "endFadeOut": 4000
}
```

### MinMax Entry Object

Specifies a minimum and maximum x/y/z value. All fields are required.

**Specification**'

| Name  |    Datatype    |              Description               |
|:-----:|:--------------:|:--------------------------------------:|
| `min` | Floating point | Specifies the minimum value, inclusive |
| `max` | Floating point | Specifies the maximum value, exclusive |

**Examples**

```json
{
  "min": 60.0,
  "max": 120.5
}
```

### Integer Vector

Specifies a list of three integer literals.

**Specification**
Does not contain any fields.

**Examples**

```json
[
  6000,
  12000,
  0
]
```

### Float Vector

Specifies a list of three floating-point literals.

**Specification**
Does not contain any fields.

**Examples**

```json
[
  0.0,
  1.0,
  0.5
]
```

### Map Object

Represents an object consisting of key-value pairs.

**Specification**

This object does not have specific predefined fields. It's a flexible structure that can hold various types of keys and values.

**Examples**

Example 1: Map with integer keys and integer values
```json
{
  "100": 0,
  "2000": 512
}
```
Example 2: Map with [Namespaced Ids](#namespaced-id) as keys and boolean values
```json
{
  "minecraft:the_nether": false,
  "minecraft:overworld": true
}
```


### Rotation Object

Specifies static and axis rotation for a skybox.

**Specification**

|       Name       |             Datatype              |                                    Description                                    | Required | Default value |
|:----------------:|:---------------------------------:|:---------------------------------------------------------------------------------:|:--------:|:-------------:|
|     `static`     |   [Float Vector](#float-vector)   |                     Specifies the static rotation in degrees                      |   :x:    |    [0,0,0]    |
|      `axis`      |   [Float Vector](#float-vector)   |                      Specifies the axis rotation in degrees                       |   :x:    |    [0,0,0]    |
|   `timeshift`    | [Integer Vector](#integer-vector) |                      Specifies the time shifted for rotation                      |   :x:    |    [0,0,0]    |
| `rotationSpeedX` |          Floating Point           | Specifies the speed of the skybox rotation in pitch, in rotations per 24000 ticks |   :x:    |       0       |
| `rotationSpeedY` |          Floating Point           |  Specifies the speed of the skybox rotation in yaw, in rotations per 24000 ticks  |   :x:    |       0       |
| `rotationSpeedZ` |          Floating Point           | Specifies the speed of the skybox rotation in roll, in rotations per 24000 ticks  |   :x:    |       0       |

The skybox is initially rotated according to `static`, then is rotated around `axis` `rotationSpeed` times per full,
in-game day.

**Example**

```json
{
  "static": [
    216,
    288,
    144
  ],
  "axis": [
    36,
    108,
    72
  ],
  "timeshift": [
    0,
    0,
    0
  ],
  "rotationSpeedX": 0,
  "rotationSpeedY": 1,
  "rotationSpeedZ": 0
}
```

### Weather

Specifies a kind of weather as a String.

**Specification**

Does not contain any fields. The value must be one of `clear`, `rain`, `thunder` or `snow`.

### Namespaced Id

Specifies the location of a file as a string in the format `namespace:path`. The string `namespace:path` translates
to `assets/namespace/path` (at least in the scenarios present in FabricSkyboxes). More info can be found on
the [Minecraft Wiki](https://minecraft.fandom.com/wiki/Resource_location).

**Specification**

Does not contain any fields. The value must consist of a valid namespace and path, separated by a colon (`:`)

### Textures Object

Specifies a texture for each of the six cardinal directions.

**Specification**

|   Name   |            Datatype             |                                   Description                                    |
|:--------:|:-------------------------------:|:--------------------------------------------------------------------------------:|
| `north`  | [Namespaced Id](#namespaced-id) | Specifies the location of the texture to be used when rendering the skybox north |
| `south`  | [Namespaced Id](#namespaced-id) | Specifies the location of the texture to be used when rendering the skybox south |
|  `east`  | [Namespaced Id](#namespaced-id) | Specifies the location of the texture to be used when rendering the skybox east  |
|  `west`  | [Namespaced Id](#namespaced-id) | Specifies the location of the texture to be used when rendering the skybox west  |
|  `top`   | [Namespaced Id](#namespaced-id) |  Specifies the location of the texture to be used when rendering the skybox up   |
| `bottom` | [Namespaced Id](#namespaced-id) | Specifies the location of the texture to be used when rendering the skybox down  |

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

### Blend Object

Specifies the blend type or equation.

**Specification**

|   Name    |             Datatype              |                                       Description                                        | Required | Default value |
|:---------:|:---------------------------------:|:----------------------------------------------------------------------------------------:|:--------:|:--------------|
|  `type`   |              String               |                             Specifies the type of the blend.                             |   :x:    |               |
| `blender` | [Blender Object](#blender-object) | Specifies the custom blender function to be used. Requires `type` to be set to `custom`. |   :x:    |               |

Valid types are: `add`, `subtract`, `multiply`, `screen`, `replace`, `alpha`, `dodge`, `burn`, `decorations`, `disable`
and `custom`.

More information on custom blend can be found in the [blend documentation](blend.md).

**Example**

```json
{
  "type": "add"
}
```

**OR**

```json
{
  "type": "custom",
  "blender": {
    "separateFunction": false,
    "sourceFactor": 0,
    "destinationFactor": 0,
    "equation": 0,
    "sourceFactorAlpha": 0,
    "destinationFactorAlpha": 0,
    "redAlphaEnabled": true,
    "greenAlphaEnabled": true,
    "blueAlphaEnabled": true,
    "alphaEnabled": false
  }
}
```

### Blender Object

Specifies a custom blender.

**Specification**

|           Name           | Datatype |                                                                                                                  Description                                                                                                                  | Required | Default value |
|:------------------------:|:--------:|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:--------:|:--------------|
|    `separateFunction`    | Boolean  | Specifies the whether OpenGL `blendFuncSeparate` will be used instead of `blendFunc`. When enabled `sourceFactor` and `destinationFactor` will be RGB channels and alpha channel is separate to `sourceFactorAlpha`/`destinationFactorAlpha`. |   :x:    | false         |
|      `sourceFactor`      | Integer  |                                                                                                  Specifies the OpenGL source factor to use.                                                                                                   |   :x:    | 770           |
|   `destinationFactor`    | Integer  |                                                                                                Specifies the OpenGL destination factor to use.                                                                                                |   :x:    | 1             |
|        `equation`        | Integer  |                                                                                                  Specifies the OpenGL blend equation to use.                                                                                                  |   :x:    | 32774         |
|   `sourceFactorAlpha`    | Integer  |                                                                       Specifies the OpenGL source factor to use for alpha channel. Requires `separateFunction` enabled.                                                                       |   :x:    | 0             |
| `destinationFactorAlpha` | Integer  |                                                                    Specifies the OpenGL destination factor to use for alpha channel. Requires `separateFunction` enabled.                                                                     |   :x:    | 0             |
|    `redAlphaEnabled`     | Boolean  |                                                                        Specifies whether alpha state will be used in red shader color or predetermined value of `1.0`.                                                                        |   :x:    | false         |
|   `greenAlphaEnabled`    | Boolean  |                                                                       Specifies whether alpha state will be used in green shader color or predetermined value of `1.0`.                                                                       |   :x:    | false         |
|    `blueAlphaEnabled`    | Boolean  |                                                                       Specifies whether alpha state will be used in blue shader color or predetermined value of `1.0`.                                                                        |   :x:    | false         |
|      `alphaEnabled`      | Boolean  |                                                                          Specifies whether alpha state will be used in shader color or predetermined value of `1.0`.                                                                          |   :x:    | true          |

More information on custom blend can be found in the [blend documentation](blend.md).

**Example**

```json
{
  "separateFunction": false,
  "sourceFactor": 0,
  "destinationFactor": 0,
  "equation": 0,
  "sourceFactorAlpha": 0,
  "destinationFactorAlpha": 0,
  "redAlphaEnabled": true,
  "greenAlphaEnabled": true,
  "blueAlphaEnabled": true,
  "alphaEnabled": false
}
```

### Loop Object

Specifies the loop condition.

**Specification**

|   Name   |                    Datatype                     |                   Description                    | Required |        Default         |
|:--------:|:-----------------------------------------------:|:------------------------------------------------:|:--------:|:----------------------:|
|  `days`  |                     Double                      |      Specifies the number of days to loop.       |   :x:    |           7            |
| `ranges` | Array of [MinMax Entries](#minmax-entry-object) | Specifies the days where the skybox is rendered. |   :x:    | Empty Array (all days) |

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

### Animation Object

Specifies an animation object.

**Specification**

|      Name       |                  Datatype                   |                                  Description                                  |      Required      | Default Value |
|:---------------:|:-------------------------------------------:|:-----------------------------------------------------------------------------:|:------------------:|:-------------:|
|    `texture`    |       [Namespaced Id](#namespaced-id)       | Specifies the location of the texture to be used when rendering the animation | :white_check_mark: |       -       |
|   `uvRanges`    |    [UV Ranges Object](#uv-ranges-object)    |          Specifies the location in UV ranges to render the animation          | :white_check_mark: |       -       |
|  `gridColumns`  |                   Integer                   |           Specifies the amount of columns the animation texture has           | :white_check_mark: |       -       |
|   `gridRows`    |                   Integer                   |            Specifies the amount of rows the animation texture has             | :white_check_mark: |       -       |
|   `duration`    |                   Integer                   |    Specifies the default duration of each animation frame in milliseconds     | :white_check_mark: |       -       |
| `frameDuration` | [Map Object](#map-object)<Integer, Integer> |              Specifies the specific duration per animation frame              |        :x:         |       -       |

**Example**

```json
{
  "texture": "fabricskyboxes:/sky/anim_texture.png",
  "uvRanges": {
    "minU": 0.25,
    "minV": 0.25,
    "maxU": 0.50,
    "maxV": 0.50
  },
  "gridColumns": 32,
  "gridRows": 1,
  "duration": 40,
  "frameDuration": {
    "1": 20,
    "5": 10
  }
}
```

### UV Ranges Object

Specifies a UV range object for defining texture coordinates.

**Specification**

|  Name  |   Data Type    |            Description             |      Required      | Default Value |
|:------:|:--------------:|:----------------------------------:|:------------------:|:-------------:|
| `minU` | Floating Point | Specifies the minimum U coordinate | :white_check_mark: |       -       |
| `minV` | Floating Point | Specifies the minimum V coordinate | :white_check_mark: |       -       |
| `maxU` | Floating Point | Specifies the maximum U coordinate | :white_check_mark: |       -       |
| `maxV` | Floating Point | Specifies the maximum V coordinate | :white_check_mark: |       -       |

**Example**

```json
{
  "minU": 0.25,
  "minV": 0.25,
  "maxU": 0.50,
  "maxV": 0.50
}
```

# Full Example

Here is a full skybox file for example purposes:

```json
{
  "schemaVersion": 2,
  "type": "square-textured",
  "properties": {
    "priority": 1,
    "fade": {
      "startFadeIn": 1000,
      "endFadeIn": 2000,
      "startFadeOut": 11000,
      "endFadeOut": 13000,
      "alwaysOn": true
    },
    "maxAlpha": 1.0,
    "transitionInDuration": 20,
    "transitionOutDuration": 20,
    "sunSkyTint": true,
    "inThickFog": true,
    "changeFog": true,
    "fogColors": {
      "red": 0,
      "green": 0,
      "blue": 0,
      "alpha": 0
    },
    "rotation": {
      "static": [
        0,
        0,
        0
      ],
      "axis": [
        0,
        0,
        0
      ],
      "timeshift": [
        0,
        0,
        0
      ],
      "rotationSpeedX": 0,
      "rotationSpeedY": 1,
      "rotationSpeedZ": 0
    }
  },
  "conditions": {
    "biomes": [],
    "worlds": [],
    "dimensions": [],
    "weather": [],
    "xRanges": [],
    "yRanges": [],
    "zRanges": [],
    "loop": {
      "days": 1.0,
      "ranges": []
    }
  },
  "decorations": {
    "sun": "minecraft:textures/environment/sun.png",
    "moon": "minecraft:textures/environment/moon.png",
    "showSun": true,
    "showMoon": true,
    "showStars": true,
    "rotation": {
      "static": [
        0,
        0,
        0
      ],
      "axis": [
        0,
        0,
        0
      ],
      "timeshift": [
        0,
        0,
        0
      ],
      "rotationSpeedX": 0,
      "rotationSpeedY": 1,
      "rotationSpeedZ": 0
    },
    "blend": {
      "type": "decorations"
    }
  },
  "blend": {
    "type": "add"
  },
  "textures": {
    "north": "minecraft:textures/block/blue_ice.png",
    "south": "minecraft:textures/block/blue_ice.png",
    "east": "minecraft:textures/block/dark_oak_log.png",
    "west": "minecraft:textures/block/dark_oak_log.png",
    "top": "minecraft:textures/block/diamond_ore.png",
    "bottom": "minecraft:textures/block/diamond_ore.png"
  }
}
```
