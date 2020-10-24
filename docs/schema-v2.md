# Skybox specification
**Schema Version 2 (DRAFT)**

This specification defines a format for a set of rules for the purpose of custom sky rendering.

**This format will be updated in the future. However, we assure you that it will not break existing skyboxes.**

## Version History
| Version |    Date    |
|:-------:|:----------:|
|    1    |   Unknown  |
|    2    | 10/14/2020 |

## Data types
### RGBA Object
Stores a list of four floating-point literals, each for a specific color. The fourth, alpha, is not required. The value of these literals must be between 0 and 1.
The default value for RGBA objects is the RGBA Zero, whose values are zeroes.


**Specification**  

|   Name  |    Datatype    |                                    Description                                   |      Required      | Default |
|:-------:|:--------------:|:--------------------------------------------------------------------------------:|:------------------:|:-------:|
| `red`   | Floating point | Specifies the amount of red color to be used. Must be a value between 0 and 1.   | :white_check_mark: |    -    |
| `green` | Floating point | Specifies the amount of green color to be used. Must be a value between 0 and 1. |  :white_check_mark |    -    |
| `blue`  | Floating point | Specifies the amount of blue color to be used. Must be a value between 0 and 1.  | :white_check_mark: |    -    |
| `alpha` | Floating point | Specifies the amount of alpha to be used. Must be a value between 0 and 1.       |         :x         |   1.0   |


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

### Decoration Object
Stores all specifications for sun and moon configuration. For optimum results, the moon texture should be a 4 wide, 2 high stacked texture.
The Default value stores the overworld sun and moon textures and sets all enabled to true.


**Specification**
|     Name    |    Datatype   |                               Description                               |      Required      | Default |
|:-----------:|:-------------:|:-----------------------------------------------------------------------:|:------------------:|:-------:|
| `sun`       | Namespaced Id | Specifies the location of the texture to be used for rendering the sun  | :white_check_mark: |    -    |
| `moon`      | Namespaced Id | Specifies the location of the texture to be used for rendering the moon | :white_check_mark: |    -    |
| `showSun`   | Boolean       | Specifies whether the sun should be rendered                            |         :x:        |  `true` |
| `showMoon`  | Boolean       | Specifies whether the moon should be rendered                           |         :x:        |  `true` |
| `showStars` | Boolean       | Specifies whether stars should be rendered                              |         :x:        |  `true` |


**Example**

```json
{
  "sun": "minecraft:textures/environment/sun.png",
  "moon": "minecraft:textures/atlas/blocks.png",
  "showSun": true,
  "showMoon": true,
  "showStars": true
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

### Float Vector Object
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
| `static` | Float Vector | Specifies the static rotation | :white_check:mark: |       -       |
| `axis`   | Float Vector | Specifies the axis rotation   |         :x:        |    [0,0,0]    |


**Example**
```json
{
  "static": [
    0.0,
    0.9,
    0.4
  ],
  "axis": [
    0.5,
    0.5,
    0.5
  ]
}
```

