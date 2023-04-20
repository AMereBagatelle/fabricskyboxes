# Fog

Defines the displayed fog.

**Specification**

| Name         | Description                                                          | Required           |
|--------------|----------------------------------------------------------------------|--------------------|
| `thickFog`   | Specifies whether the fog should be thick.                           | :white_check_mark: |
| `color`      | Specifies the color of the fog.                                      | :white_check_mark: |
| `sunSkyTint` | Specifies whether the sun tints the sky yellow at sunrise and sunset | :white_check_mark: |


```json5
{
  "thickFog": true,
  "color": [0, 255, 0, 255], // rgba, 0-255
  "sunSkyTint": false // sun tint yellow (perhaps better for skybox?)
}
```