## Data types
### RGBA Object
Stores a list of four floating-point literals, each for a specific color. The fourth, alpha, is not required. The value of these literals must be between 0 and 1.
The default value for most RGBA objects is the RGBA Zero, whose values are zeroes.
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
Stores a list of four integers which specify the time to start and end fading the skybox in and out.
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

## Schema 
### Fields required by all skybox types
These must be present in all skybox json files. 

| Name              | Json Datatype             | Description                                     |
|-------------------|---------------------------|-------------------------------------------------|
| `schemaVersion`   | Integer                   | Specifies the version of the format being used. |
| `type`            | String                    | Specifies the type of skybox to be used.        |

### Shared data (between `square-textured` and `monocolored`)
These are used by both the `square-textured` and `monocolored` skybox type. 

| Name              | Json Datatype             | Description                                                                                                                                                                                               |      Required      | Default value |
|-------------------|---------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|:------------------:|:-------------:|
| `fade`            | Fade object               | Specifies the time of day that the skybox should start and end fading in and out.                                                                                                                         | :white_check_mark: |       -       |
| `maxAlpha`        | Float                     | Specifies the maximum value that the alpha can be. The value must be within 0 and 1.                                                                                                                      | :x:                |       1       |
| `transitionSpeed` | Float                     | Specifies the speed that skybox will fade in or out when valid conditions are changed. The value must be within 0 and 1.                                                                                  | :x:                |       1       |
| `changeFog`       | Boolean                   | Specifies whether the skybox should change the fog color.                                                                                                                                                 | :x:                |    `false`    |
| `fogColors`       | RGBA object               | Specifies the colors to be used for fog.                                                                                                                                                                  | :x:                |   RGBA Zero   |
| `shouldRotate`    | Boolean                   | Specifies whether the skybox should rotate on its axis.                                                                                                                                                   | :x:                |    `false`    |
| `decorations`     | Boolean                   | Specifies whether the sun and moon should be rendered on top of the skybox                                                                                                                                | :x:                |    `false`    |
| `weather`         | String array              | Specifies a list of whether conditions that the skybox should be rendered in. Values must be one of clear, rain, thunder or snow. The skybox is rendered in all weather conditions if the array is empty. | :x:                |  Empty array  |
| `biomes`          | String array              | Specifies a list of identifiers of biomes that the skybox should be rendered in. The skybox is rendered in all biomes if the array is empty.                                                              | :x:                |  Empty array  |
| `dimensions`      | String array              | Specifies a list of identifiers or **worlds**, not dimension types, that the skybox should be rendered in. The skybox is rendered in all worlds if the array is empty.                                    | :x:                |  Empty array  |
| `heightRanges`    | Height range object array | Specifies a list of height ranges that the skybox should be rendered in. The skybox is rendered at all heights if the array is empty.                                                                     | :x:                |  Empty array  |
