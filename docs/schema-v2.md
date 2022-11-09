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
  - [Textured](#textured-skyboxes)
  - [`square-textured`](#square-textured-skybox)
  - [`single-sprite-square-textured`](#single-sprite-square-textured-skybox)
  - [Animated](#animated-skyboxes)
  - [`animated-square-textured`](#animated-square-textured-skybox)
  - [`single-sprite-animated-square-textured`](#single-sprite-animated-square-textured-skybox)  
- [Data Types](#data-types)  
  - [Default Properties Object](#default-properties-object)  
  - [Conditions Object](#conditions-object)  
  - [Decorations Object](#decorations-object)
  - [RGBA Object](#rgba-object)
  - [Fade Object](#fade-object)
  - [MinMax Entry Object](#minmax-entry-object)
  - [Float Vector](#float-vector)
  - [Rotation Object](#rotation-object)
  - [Weather](#weather)
  - [Namespaced Id](#namespaced-id)
  - [Textures Object](#textures-object)
  - [Blend Object](#blend-object)
- [Full Example](#full-example)

# Structure
The basic structure of a fabricskyboxes skybox file may look something like this:
```json5
{
    "schemaVersion": /* version (int) */,
    "type": /* skybox type (string) */,
    "conditions": // conditions object (optional)
    {
        "biomes": /* biomes (string array, optional) */,
        "worlds": /* dimensions (string array, optional) */,
        "weather": /* weathers (string array, optional) */,
        // Here, a "MinMax" type refers to an object containing "min" and "max" keys, both floats
        "xRanges": /* x ranges (MinMax array, optional) */,
        "yRanges": /* y ranges (MinMax array, optional) */,
        "zRanges": /* z ranges (MinMax array, optional) */
    },
    "decorations": // decorations object (optional)
    {
        "sun": /* sun texture path (string, optional) */,
        "moon": /* moon texture path (string, optional) */,
        "showSun": /* render sun (bool, optional) */,
        "showMoon": /* render moon (bool, optional) */,
        "showStars": /* render stars (bool, optional) */,
        "rotation": // rotation object FOR SUN/MOON/STARS (optional)
        {
            // Here, a "Float Vector" type refers to an array of 3 floats
            "static": /* static rotation in degrees (Float Vector, optional) */,
            "axis": /* axis rotation in degrees (Float Vector, optional) */,
            "rotationSpeed": /* speed of rotation (float, optional) */
        }
    },
    "properties": // default properties object
    {
        "priority": /* integer (optional) */,
        "fade": // fade object (optional)
        {
            "startFadeIn": /* fade-in start time in ticks (int) */,
            "endFadeIn": /* fade-in end time in ticks (int) */,
            "startFadeOut": /* fade-out start time in ticks (int) */,
            "endFadeOut": /* fade-out end time in ticks (int) */,
            "alwaysOn": /* always show skybox (bool, optional) */
        },
        "maxAlpha": /* max alpha value (0-1 float, optional) */,
        "transitionSpeed": /* fade in/out speed (0-1 float, optional) */,
        "changeFog": /* change fog color (bool, optional) */,
        "fogColors": // RGBA object for fog color (optional)
        {
            "red": /* amount of red (0-1 float, optional) */,
            "blue": /* amount of blue (0-1 float, optional) */,
            "green": /* amount of green (0-1 float, optional) */,
            "alpha": /* alpha value (0-1 float, optional) */
        },
        "sunSkyTint": /* tint sky yellow during sunrise/sunset (bool, optional) */,
        "shouldRotate": /* rotate skybox (bool, optional) */,
        "rotation": // rotation object FOR SKYBOX (optional)
        {
            // Here, a "Float Vector" type refers to an array of 3 floats
            "static": /* static rotation in degrees (Float Vector, optional) */,
            "axis": /* axis rotation in degrees (Float Vector, optional) */,
            "rotationSpeed": /* speed of rotation (float, optional) */
        }          
    },
    
    // The following objects are for specific types, not all of them should be used
    "color": // RGBA object for sky color (monocolor type only, optional)
    {
        "red": /* amount of red (0-1 float, optional) */,
        "blue": /* amount of blue (0-1 float, optional) */,
        "green": /* amount of green (0-1 float, optional) */,
        "alpha": /* alpha value (0-1 float, optional) */
    },
    "blend": // blend object (textured types only, optional)
    {
        "type": /* blend type (string, optional) */,
        
        // OR
        
        "sFactor": /* sFactor number (int, optional) */,
        "dFactor": /* dFactor number (int, optional) */,
        "equation": /* equation number (int, optional) */
    },
    "textures": // textures object (square-textured type only)
    {
        "north": /* texture to use for north direction (string, optional) */,
        "south": /* texture to use for south direction (string, optional) */,
        "east": /* texture to use for east direction (string, optional) */,
        "west": /* texture to use for west direction (string, optional) */,
        "top": /* texture to use for top direction (string, optional) */,
        "bottom": /* texture to use for bottom direction (string, optional) */
    },
    "texture": /* path to single-sprite texture (string, single-sprite-square-textured type only) */,
    "fps": /* frames per second for animation (float, animated-square-textured, single-sprite-animated-square-textured types only) */,
    "animationTextures": // textures to use for animation (animated-square-textured type only)
    [
        { // textures for first frame
            "north": /* texture to use for north direction (string, optional) */,
            "south": /* texture to use for south direction (string, optional) */,
            "east": /* texture to use for east direction (string, optional) */,
            "west": /* texture to use for west direction (string, optional) */,
            "top": /* texture to use for top direction (string, optional) */,
            "bottom": /* texture to use for bottom direction (string, optional) */
        }
        // ...
    ],
    "animationTextures": // textures to use for animation (single-sprite-animated-square-textured type only)
    [
        /* single sprite texture for first frame (string) */
        // ...
    ]
}
```
Note that this isn't actually a valid json file as some fields are blank and there are comments. Instead, it serves to illustrate the general structure of a skybox file and gives basic descriptions of each item.  


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

Normal textured skyboxes require 6 image files (1 for each direction), and are recommended. Single sprite textured skyboxes only require 1 image file which follows the optifine specification, but they are sometimes buggy.

### Shared Data
All skybox types use these fields

|      Name       |                        Datatype                         |                              Description                              |      Required      |                       Default value                       |
|:---------------:|:-------------------------------------------------------:|:---------------------------------------------------------------------:|:------------------:|:---------------------------------------------------------:|
|  `properties`   | [Default Properties object](#default-properties-object) |      Specifies the properties to be used when rendering a skybox      | :white_check_mark: |                             -                             |
|  `conditions`   |         [Conditions object](#conditions-object)         | Specifies conditions about when and where a skybox should be rendered |        :x:         |                       No conditions                       |
|  `decorations`  |        [Decorations object](#decorations-object)        |          Specifies information about the sun, moon and stars          |        :x:         | Default sun and moon texture with all decorations enabled |
|     `type`      |                         String                          |                Specifies the kind of skybox to be used                | :white_check_mark: |                             -                             |
| `schemaVersion` |                         Integer                         |      Specifies the schema version to be used for deserialization      |        :x:         |                      Falls back to 1                      |

### Mono color skybox
Only the `monocolor` skybox type uses these fields

|  Name   |          Datatype           |            Description            | Required |  Default value   |
|:-------:|:---------------------------:|:---------------------------------:|:--------:|:----------------:|
| `color` | [RGBA Object](#rgba-object) | Specifies the color of the skybox |   :x:    | 0 for each value |

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


## Data types
### Default Properties Object
Specifies common properties used by all types of skyboxes. 

**Specification**

|       Name        |              Datatype               |                                                                                                           Description                                                                                                            |      Required      | Default value                                |
|:-----------------:|:-----------------------------------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:------------------:|----------------------------------------------|
|    `priority`     |               Integer               | Specifies the order which skybox will be rendered. If there are multiple skyboxes with identical priority, those skyboxes are not re-ordered therefore being dependant of Vanilla's alphabetical namespaced identifiers loading. |        :x:         | 0                                            |
|      `fade`       |     [Fade object](#fade-object)     |                                                                    Specifies the time of day in ticks that the skybox should start and end fading in and out.                                                                    | :white_check_mark: | -                                            |
|    `maxAlpha`     |                Float                |                                                                       Specifies the maximum value that the alpha can be. The value must be within 0 and 1.                                                                       |        :x:         | 1.0                                          |
| `transitionSpeed` |                Float                |                                                     Specifies the speed that skybox will fade in or out when valid conditions are changed. The value must be within 0 and 1.                                                     |        :x:         | 1.0                                          |
|    `changeFog`    |               Boolean               |                                                                                    Specifies whether the skybox should change the fog color.                                                                                     |        :x:         | `false`                                      |
|    `fogColors`    |     [RGBA Object](#rgba-object)     |                                                                                        Specifies the colors to be used for rendering fog.                                                                                        |        :x:         | 0 for each value                             |
|   `sunSkyTint`    |               Boolean               |                                                                            Specifies whether the skybox should disable sunrise/set sky color tinting                                                                             |        :x:         | `true`                                       |
|  `shouldRotate`   |               Boolean               |                                                                                     Specifies whether the skybox should rotate on its axis.                                                                                      |        :x:         | `false`                                      |
|    `rotation`     | [Rotation object](#rotation-object) |                                                                                           Specifies the rotation angles of the skybox.                                                                                           |        :x:         | [0,0,0] for static/axis, 1 for rotationSpeed |

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
  "transitionSpeed": 0.8,
  "sunSkyTint": false,
  "changeFog": true,
  "fogColors": {
    "red": 0.2,
    "green": 0.9,
    "blue": 0.6,
    "alpha": 1.0
  },
  "shouldRotate": true,
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
    ]
  }
}
```

### Conditions Object

Specifies when and where a skybox should render. All fields are optional.

**Specification**

|   Name    |                    Datatype                     |                                 Description                                  |            Default value             |
|:---------:|:-----------------------------------------------:|:----------------------------------------------------------------------------:|:------------------------------------:|
| `biomes`  |    Array of [Namespaced Ids](#namespaced-id)    |       Specifies a list of biomes that the skybox should be rendered in       |       Empty Array (all biomes)       |
| `worlds`  |    Array of [Namespaced Ids](#namespaced-id)    |       Specifies a list of worlds that the skybox should be rendered in       |       Empty Array (all worlds)       |
| `weather` |          Array of [Weathers](#weather)          | Specifies a list of weather conditions that the skybox should be rendered in | Empty Array (all weather conditions) |
| `xRanges` | Array of [MinMax Entries](#minmax-entry-object) |  Specifies a list of coordinates that the skybox should be rendered between  |   Empty Array (all x coordinates)    |
| `yRanges` | Array of [MinMax Entries](#minmax-entry-object) |  Specifies a list of coordinates that the skybox should be rendered between  |   Empty Array (all y coordinates)    |
| `zRanges` | Array of [MinMax Entries](#minmax-entry-object) |  Specifies a list of coordinates that the skybox should be rendered between  |   Empty Array (all z coordinates)    |

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
  ]
}
```

### Decorations Object
Stores all specifications for sun and moon configuration. For optimum results, the moon texture should mimic the vanilla moon texture.
The Default value stores the overworld sun and moon textures and sets all enabled to true.

**Specification**

|    Name     |              Datatype               |                               Description                               | Required |                              Default value                              |
|:-----------:|:-----------------------------------:|:-----------------------------------------------------------------------:|:--------:|:-----------------------------------------------------------------------:|
|    `sun`    |   [Namespaced Id](#namespaced-id)   | Specifies the location of the texture to be used for rendering the sun  |   :x:    |     Default sun texture (`minecraft:textures/environment/sun.png`)      |
|   `moon`    |   [Namespaced Id](#namespaced-id)   | Specifies the location of the texture to be used for rendering the moon |   :x:    | Default moon texture (`minecraft:textures/environment/moon_phases.png`) |
|  `showSun`  |               Boolean               |              Specifies whether the sun should be rendered               |   :x:    |                                 `true`                                  |
| `showMoon`  |               Boolean               |              Specifies whether the moon should be rendered              |   :x:    |                                 `true`                                  |
| `showStars` |               Boolean               |               Specifies whether stars should be rendered                |   :x:    |                                 `true`                                  |
| `rotation`  | [Rotation Object](#rotation-object) |               Specifies the rotation of the decorations.                |   :x:    |              [0,0,0] for static/axis, 1 for rotationSpeed               |

**Example**

```json
{
  "sun": "minecraft:textures/environment/sun.png",
  "moon": "minecraft:textures/atlas/blocks.png",
  "showSun": true,
  "showMoon": true,
  "showStars": false,
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
    "rotationSpeed": 1.0
  }
}
```

### RGBA Object
Stores a list of four floating-point literals, each for a specific color. The fourth, alpha, is not required. The value of these literals must be between 0 and 1.
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

|      Name      | Datatype |                       Description                       |      Required      | Default |
|:--------------:|:--------:|:-------------------------------------------------------:|:------------------:|:-------:|
| `startFadeIn`  | Integer  | The times in ticks when a skybox will start to fade in  | :white_check_mark: |    -    |
|  `endFadeIn`   | Integer  |   The times in ticks when a skybox will end fading in   | :white_check_mark: |    -    |
| `startFadeOut` | Integer  | The times in ticks when a skybox will start to fade out | :white_check_mark: |    -    |
|  `endFadeOut`  | Integer  |  The times in ticks when a skybox will end fading out   | :white_check_mark: |    -    |
|   `alwaysOn`   | Boolean  | Whether the skybox should always be at full visibility  |        :x:         |  false  |

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

### Rotation Object
Specifies static and axis rotation for a skybox. 


**Specification** 

|      Name       |           Datatype            |                               Description                                | Required | Default value |
|:---------------:|:-----------------------------:|:------------------------------------------------------------------------:|:--------:|:-------------:|
|    `static`     | [Float Vector](#float-vector) |                 Specifies the static rotation in degrees                 |   :x:    |    [0,0,0]    |
|     `axis`      | [Float Vector](#float-vector) |                  Specifies the axis rotation in degrees                  |   :x:    |    [0,0,0]    |
| `rotationSpeed` |        Floating Point         | Specifies the speed of the skybox rotation, in rotations per 24000 ticks |   :x:    |       1       |

The skybox is initially rotated according to `static`, then is rotated around `axis` `rotationSpeed` times per full, in-game day.  


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
  "rotationSpeed": 1.0
}
```

### Weather
Specifies a kind of weather as a String. 

**Specification**

Does not contain any fields. The value must be one of `clear`, `rain`, `thunder` or `snow`. 

### Namespaced Id
Specifies the location of a file as a string in the format `namespace:path`. The string `namespace:path` translates to `assets/namespace/path` (at least in the scenarios present in FabricSkyboxes). More info can be found on the [Minecraft Wiki](https://minecraft.fandom.com/wiki/Resource_location).  

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

|    Name    | Datatype |                   Description                   | Required |
|:----------:|:--------:|:-----------------------------------------------:|:--------:|
|   `type`   |  String  |        Specifies the type of the blend.         |   :x:    |
| `sFactor`  | Integer  |   Specifies the OpenGL source factor to use.    |   :x:    |
| `dFactor`  | Integer  | Specifies the OpenGL destination factor to use. |   :x:    |
| `equation` | Integer  |   Specifies the OpenGL blend equation to use.   |   :x:    |

Valid types are: `add`, `subtract`, `multiply`, `screen`, `replace`, `alpha`, `dodge`, `burn`, `darken` and `lighten`.

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
  "sFactor": 0,
  "dFactor": 0,
  "equation": 0
}
```

# Full Example
Here is a full skybox file for example purposes:
```json
{
  "schemaVersion": 2,
  "type": "square-textured",
  "properties": {
    "fade": {
      "startFadeIn": 1000,
      "endFadeIn": 2000,
      "startFadeOut": 11000,
      "endFadeOut": 13000,
      "alwaysOn": true
    },
    "maxAlpha": 1.0,
    "transitionSpeed": 1.0,
    "changeFog": true,
    "fogColors": {
	   "red": 0,
	   "green": 0,
	   "blue": 0,
	   "alpha": 0
    },
    "shouldRotate": true,
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
	   "rotationSpeed": 1.0
    }
  },
  "conditions": {
	 "biomes": [],
	 "worlds": [],
	 "weather": [],
	 "xRanges": [],
	 "yRanges": [],
	 "zRanges": []
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
		 "rotationSpeed": 1.0
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
