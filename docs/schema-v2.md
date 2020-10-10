# Skybox specification
**Schema Version: 2**

This specification defines a format for a set of rules for the purpose of custom sky rendering.

**This format will be updated in the future. However, we assure you that it will not break existing skyboxes.**

## Version History
| Version |    Date    |
|:-------:|:----------:|
|    2    | 29/21/2020 |

## Data types
### RGBA Object
Stores a list of four floating-point literals, each for a specific color. The fourth, alpha, is not required. The value of these literals must be between 0 and 1.
The default value for RGBA objects is the RGBA Zero, whose values are zeroes.
Example:-
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
Conversion table:-  

| Time in Ticks | Clock Time |
|:-------------:|:----------:|
|       0       |    6 AM    |
|      6000     |    12 PM   |
|     12000     |    6 PM    |
|     18000     |    12 AM   |

Example:-
```json
{
  "startFadeIn": 1000,
  "endFadeIn": 2000,
  "startFadeOut": 3000,
  "endFadeOut": 4000
}
```

### Height range Object
Stores two floating-point literals named `min` and `max`. `min` must be lesser than `max`.
Example:-
```json
{
  "min": 30,
  "max": 70.5
}
```

### Textures Object
Stores six identifiers of textures for each cardinal direction (north, west, south, east, up, down)
Example:-
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

### Decoration Object 
Stores all specifications for sun and moon configuration. For optimum results, the moon texture should be a 4 wide, 2 high stacked texture. 
The Default value stores the overworld sun and moon textures and sets all enabled to true.
Example:-
```json
{
  "sun": "minecraft:textures/environment/sun.png",
  "moon": "minecraft:textures/atlas/blocks.png",
  "sunEnabled": true,
  "moonEnabled": true,
  "starsEnabled": true
}
```


## Schema 
### Fields required by all skybox types
These must be present in all skybox json files. 

| Name              | Json Datatype             | Description                                     |
|-------------------|---------------------------|-------------------------------------------------|
| `schemaVersion`   | Integer                   | Specifies the version of the format being used. |
| `type`            | String                    | Specifies the type of skybox to be used.        |

### Shared data
These are used by both the `square-textured` and `monocolored` skybox type. 

| Name                 | Json Datatype              | Description                                                                                                                                                                                               |      Required      |         Default value         |
|----------------------|----------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------:|:-----------------------------:|
| `fade`               | Fade object                | Specifies the time of day that the skybox should start and end fading in and out.                                                                                                                         | :white_check_mark: |               -               |
| `maxAlpha`           | Float                      | Specifies the maximum value that the alpha can be. The value must be within 0 and 1.                                                                                                                      | :x:                |               1               |
| `transitionSpeed`    | Float                      | Specifies the speed that skybox will fade in or out when valid conditions are changed. The value must be within 0 and 1 as a percentage.                                                                  | :x:                |               1               |
| `changeFog`          | Boolean                    | Specifies whether the skybox should change the fog color.                                                                                                                                                 | :x:                |            `false`            |
| `fogColors`          | RGBA object                | Specifies the colors to be used for fog.                                                                                                                                                                  | :x:                |           RGBA Zero           |
| `shouldRotate`       | Boolean                    | Specifies whether the skybox should rotate on its axis.                                                                                                                                                   | :x:                |            `false`            |
| `decorations`        | Boolean                    | Specifies whether the sun and moon should be rendered on top of the skybox                                                                                                                                | :x:                |            `false`            |
| `weather`            | String array               | Specifies a list of whether conditions that the skybox should be rendered in. Values must be one of clear, rain, thunder or snow. The skybox is rendered in all weather conditions if the array is empty. | :x:                |          Empty array          |
| `biomes`             | String array               | Specifies a list of identifiers of biomes that the skybox should be rendered in. The skybox is rendered in all biomes if the array is empty.                                                              | :x:                |          Empty array          |
| `dimensions`         | String array               | Specifies a list of identifiers or **worlds**, not dimension types, that the skybox should be rendered in. The skybox is rendered in all worlds if the array is empty.                                    | :x:                |          Empty array          |
| `heightRanges`       | Height range object array  | Specifies a list of height ranges that the skybox should be rendered in. The skybox is rendered at all heights if the array is empty.                                                                     | :x:                |          Empty array          |
| `decorationTextures` | Decoration Textures Object | Specifies the custom sun and moon texture to be used while rendering the skybox.                                                                                                                          | :x:                | Vanilla sun and moon textures |

## Skybox Specifics

### All Textured Skyboxes
| Name       | Json Datatype   | Description                                                                                             | Required           | Default value |
|------------|-----------------|---------------------------------------------------------------------------------------------------------|--------------------|---------------|
| `axis`     | Float array     | Specifies the rotation angles of the skybox. Must have three values, each between 0 and 180.            | :x:                |    [0,0,0]    |
| `blend`    | Boolean         | Specifies whether the skybox should fully blend into the sky texture. Will replace it if set to `false` | :x:                |    `false`    |

### Extra fields used by `square-textured`
Only the `square-textured` skybox type uses these fields

| Name       | Json Datatype   | Description                                                                                             | Required           | Default value |
|------------|-----------------|---------------------------------------------------------------------------------------------------------|--------------------|---------------|
| `textures` | Textures object | Specifies the textures to be used for each cardinal direction.                                          | :white_check_mark: |       -       |

### Extra fields used by `monocolor`
Only the `monocolor` skybox type uses these fields

| Name    | Json Datatype | Description                       | Required           | Default value |
|---------|---------------|-----------------------------------|--------------------|---------------|
| `color` | RGBA Object   | Specifies the color of the skybox | :white_check_mark: |       -       |