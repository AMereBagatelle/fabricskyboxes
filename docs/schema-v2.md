# Skybox specification
**Schema Version 2 (DRAFT)**

This specification defines a format for a set of rules for the purpose of custom sky rendering.

**This format will be updated in the future. However, we assure you that it will not break existing skyboxes.**

## Version History
| Version |    Date    |
|:-------:|:----------:|
|    2    | 10/14/2020 |

## Data types
### RGBA Object
Stores a list of four floating-point literals, each for a specific color. The fourth, alpha, is not required. The value of these literals must be between 0 and 1.
The default value for RGBA objects is the RGBA Zero, whose values are zeroes.


**Specification**  

|   Name  |    Datatype    |                                    Description                                   |      Required      | Default |
|:-------:|:--------------:|:--------------------------------------------------------------------------------:|:------------------:|:-------:|
| `red`   | Floating point | Specifies the amount of red color to be used. Must be a value between 0 and 1.   | :white_check_mark: |    -    |
| `green` | Floating point | Specifies the amount of green color to be used. Must be a value between 0 and 1. | :white_check_mark: |    -    |
| `blue`  | Floating point | Specifies the amount of blue color to be used. Must be a value between 0 and 1.  | :white_check_mark: |    -    |
| `alpha` | Floating point | Specifies the amount of alpha to be used. Must be a value between 0 and 1.       |         :x:        |   1.0   |


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
| `endFadeIn`    | Integer  | The times in ticks when a skybox will end fading in     | :white_check_mark: |    -    |
| `startFadeOut` | Integer  | The times in ticks when a skybox will start to fade out | :white_check_mark: |    -    |
| `endFadeOut`   | Integer  | The times in ticks when a skybox will end fading out    | :white_check_mark: |    -    |
| `alwaysOn`     | Boolean  | Whether the skybox should always be at full visibility  |        :x:         |  false  |

**Conversion Table**

| Time in Ticks | Clock Time |
|:-------------:|:----------:|
|       0       |    6 AM    |
|      6000     |    12 PM   |
|     12000     |    6 PM    |
|     18000     |    12 AM   |


**Example**
```json
{
  "startFadeIn": 1000,
  "endFadeIn": 2000,
  "startFadeOut": 3000,
  "endFadeOut": 4000
}
```

### Height Entry Object
Specifies a minimum and maximum height. All fields are required and must be within 0 and 256. 


**Specification**

|  Name |    Datatype    |          Description         |
|:-----:|:--------------:|:----------------------------:|
| `min` | Floating point | Specifies the minimum height |
| `max` | Floating point | Specifies the maximum height |


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

|   Name   |   Datatype   |          Description          |      Required      | Default value |
|:--------:|:------------:|:-----------------------------:|:------------------:|:-------------:|
| `static` | Float Vector | Specifies the static rotation in degrees | :white_check_mark: |       -       |
| `axis`   | Float Vector | Specifies the axis rotation in degrees   |         :x:        |    [0,0,0]    |
| `rotationSpeed` | Floating Point | Specifies the speed of the skybox rotation, as a multiplier of the normal speed | :x: | 1 |


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


### Textures
Specifies a texture for each of the six cardinal directions. 

**Specification**

|   Name   |    Datatype   |                                    Description                                   |
|:--------:|:-------------:|:--------------------------------------------------------------------------------:|
| `north`  | Namespaced Id | Specifies the location of the texture to be used when rendering the skybox north |
| `south`  | Namespaced Id | Specifies the location of the texture to be used when rendering the skybox south |
| `east`   | Namespaced Id | Specifies the location of the texture to be used when rendering the skybox east  |
| `west`   | Namespaced Id | Specifies the location of the texture to be used when rendering the skybox west  |
| `top`    | Namespaced Id | Specifies the location of the texture to be used when rendering the skybox up    |
| `bottom` | Namespaced Id | Specifies the location of the texture to be used when rendering the skybox down  |

**Example**
```json
{
  "west": "minecraft:textures/atlas/blocks.png",
  "top": "minecraft:textures/atlas/blocks.png",
  "bottom": "minecraft:textures/atlas/blocks.png",
  "north": "minecraft:textures/atlas/blocks.png",
  "south": "minecraft:textures/atlas/blocks.png",
  "east": "minecraft:textures/atlas/blocks.png"
}
```

### Blend

Specifies the blend type or equation.

**Specification**

|   Name   |    Datatype   |              Description               |  Required |
|:--------:|:-------------:|:--------------------------------------:|:---------:|
|  `type`  |    String     |   Specifies the type of the blend.     |    :x:    |
| `sFactor` | Integer | Specifies the OpenGL source factor to use.  |    :x:    |
| `dFactor` | Integer | Specifies the OpenGL destination factor to use.  |    :x:    |
| `equation` | Integer | Specifies the OpenGL blend equation to use. | :x: |

Valid types are: `add`, `subtract`, `multiply`, `screen`, and `replace`.

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

### Conditions Object

Specifies when and where a skybox should render. All fields are optional.

**Specification**

|    Name   |         Datatype        |                                  Description                                  |             Default value            |
|:---------:|:-----------------------:|:-----------------------------------------------------------------------------:|:------------------------------------:|
| `biomes`  | Array of Namespaced Ids | Specifies a list of biomes that the skybox should be rendered in              |       Empty Array (all biomes)       |
| `worlds`  | Array of Namespaced Ids | Specifies a list of worlds that the skybox should be rendered in              |       Empty Array (all worlds)       |
| `weather` | Array of Weathers       | Specifies a list of weather conditions that the skybox should be rendered in  | Empty Array (all weather conditions) |
| `heights` | Array of Height Entries | Specifies a list of height entries that the skybox should be rendered between |       Empty Array (all heights)      |


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
  "heights": [
    {
      "min": 50.0,
      "max": 60.0
    },
    {
      "min": 100.0,
      "max": 110.0
    }
  ]
}
```


### Decorations Object
Stores all specifications for sun and moon configuration. For optimum results, the moon texture should be a 4 wide, 2 high stacked texture.
The Default value stores the overworld sun and moon textures and sets all enabled to true.

**Specification**

|     Name    |    Datatype   |                               Description                               | Required |                              Default value                              |
|:-----------:|:-------------:|:-----------------------------------------------------------------------:|:--------:|:-----------------------------------------------------------------------:|
| `sun`       | Namespaced Id | Specifies the location of the texture to be used for rendering the sun  |   :x:    |      Default sun texture (`minecraft:textures/environment/sun.png`)     |
| `moon`      | Namespaced Id | Specifies the location of the texture to be used for rendering the moon |   :x:    | Default moon texture (`minecraft:textures/environment/moon_phases.png`) |
| `showSun`   | Boolean       | Specifies whether the sun should be rendered                            |   :x:    |                                  `true`                                 |
| `showMoon`  | Boolean       | Specifies whether the moon should be rendered                           |   :x:    |                                  `true`                                 |
| `showStars` | Boolean       | Specifies whether stars should be rendered                              |   :x:    |                                  `true`                                 |
| `rotation`  | Rotation Object | Specifies the rotation of the decorations.                            |   :x:    |                        [0, 0, 0] for each value                         |

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

### Default Properties Object
Specifies common properties used by most kinds of skyboxes. 

**Specification**

|        Name       |     Datatype    |                                                        Description                                                       |      Required      | Default value          |
|:-----------------:|:---------------:|:------------------------------------------------------------------------------------------------------------------------:|:------------------:|------------------------|
| `fade`            | Fade object     | Specifies the time of day in ticks that the skybox should start and end fading in and out.                               | :white_check_mark: |            -           |
| `maxAlpha`        | Float           | Specifies the maximum value that the alpha can be. The value must be within 0 and 1.                                     |         :x:        |           1.0          |
| `transitionSpeed` | Float           | Specifies the speed that skybox will fade in or out when valid conditions are changed. The value must be within 0 and 1. |         :x:        |           1.0          |
| `changeFog`       | Boolean         | Specifies whether the skybox should change the fog color.                                                                |         :x:        |         `false`        |
| `fogColors`       | RGBA Object     | Specifies the colors to be used for rendering fog.                                                                       |         :x:        |    0 for each value    |
| `sunSkyTint`      | Boolean         | Specifies whether the skybox should disable sunrise/set sky color tinting                                                |         :x:        |         `true`         |
| `shouldRotate`    | Boolean         | Specifies whether the skybox should rotate on its axis.                                                                  |         :x:        |         `false`        |
| `rotation`        | Rotation object | Specifies the rotation angles of the skybox.                                                                             |         :x:        | [0,0,0] for each value |

**Example**
```json
{
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

## Skyboxes
### Shared Data
All kinds of skyboxes use these fields

|       Name      |          Datatype         |                              Description                              |      Required      |                       Default value                       |
|:---------------:|:-------------------------:|:---------------------------------------------------------------------:|:------------------:|:---------------------------------------------------------:|
| `properties`    | Default Properties object | Specifies the properties to be used when rendering a skybox           | :white_check_mark: |                             -                             |
| `conditions`    | Conditions object         | Specifies conditions about when and where a skybox should be rendered |         :x:        |                       No conditions                       |
| `decorations`   | Decorations object        | Specifies information about the sun, moon and stars                   |         :x:        | Default sun and moon texture with all decorations enabled |
| `type`          | String                    | Specifies the kind of skybox to be used                               | :white_check_mark: |                             -                             |
| `schemaVersion` | Integer                   | Specifies the schema version to be used for deserialization           |         :x:        |                      Falls back to 1                      |

### Mono color skybox
Only the `monocolor` skybox type uses these fields

|   Name  |   Datatype  |            Description            | Required |   Default value  |
|:-------:|:-----------:|:---------------------------------:|:--------:|:----------------:|
| `color` | RGBA Object | Specifies the color of the skybox |    :x:   | 0 for each value |

### Textured skyboxes

Only the `-textured` skybox types use these fields

|   Name  | Datatype |                       Description                      |      Required      |   Default value  |
|:-------:|:--------:|:------------------------------------------------------:|:------------------:|:----------------:|
| `blend` | Blend Object | Specifies how the skybox should blend into the sky | :white_check_mark: |         -        |

### Square Textured skybox
Only the `square-textured` skybox type uses these fields

|    Name    |     Datatype    |                      Description                     |      Required      | Default value |
|:----------:|:---------------:|:----------------------------------------------------:|:------------------:|:-------------:|
| `textures` | Textures object | Specifies the textures to be used for each direction | :white_check_mark: |       -       |

### Single sprite Square Textured skybox
Only the `single-sprite-square-textured` skybox type uses these fields

|    Name   |    Datatype   |                    Description                   |      Required      | Default value |
|:---------:|:-------------:|:------------------------------------------------:|:------------------:|:-------------:|
| `texture` | Namespaced Id | Specifies the location of the texture to be used | :white_check_mark: |       -       |

### Animated skyboxes
Animated skybox types (`animated-square-textured` and `single-sprite-animated-square-textured`) use these fields

|         Name        |          Datatype         |                          Description                     |      Required      | Default value |
|:-------------------:|:-------------------------:|:--------------------------------------------------------:|:------------------:|:-------------:|
| `fps`               | Floating Point            | Specifies the number of frames to be rendered per second | :white_check_mark: |       -       |


### Animated Square Textured skybox
Only the `animated-square-textured` skybox type uses these fields

|         Name        |          Datatype         |                          Description                         |      Required      | Default value |
|:-------------------:|:-------------------------:|:------------------------------------------------------------:|:------------------:|:-------------:|
| `animationTextures` | Array of Textures objects | Specifies the list of textures to be used for each direction | :white_check_mark: |       -       |

### Single sprite Animated Square Textured skybox
Only the `single-spriteanimated-square-textured` skybox type uses these fields

|         Name        |          Datatype         |                      Description                     |      Required      | Default value |
|:-------------------:|:-------------------------:|:----------------------------------------------------:|:------------------:|:-------------:|
| `animationTextures` |  Array of Namespaced Ids  | Specifies a list of locations to textures to be used | :white_check_mark: |       -       |
