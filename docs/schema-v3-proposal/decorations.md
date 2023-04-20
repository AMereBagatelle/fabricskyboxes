# Decorations

## Decoration Objects

### Decoration

Defines the state of a decoration.

**Specification**

| Name      | Datatype                              | Description                                | Required           | Default Value                                  |
|-----------|---------------------------------------|--------------------------------------------|--------------------|------------------------------------------------|
| `visible` | Boolean                               | Whether the decoration should render.      | :white_check_mark: |                                                |
| `texture` | [Identifier](schema-v3.md#identifier) | The texture to be used for the decoration. | :x:                | `"minecraft:textures/environment/element.png"` |

**Example**

```json
{
  "visible": true,
  "texture": "minecraft:textures/environment/sun.png"
}
```

## Decorations

Defines the state of the sun, moon, and stars.

**Specification**

| Name          | Datatype                  | Description                                 | Required           |
|---------------|---------------------------|---------------------------------------------|--------------------|
| `sun`         | [Decoration](#decoration) | Describes the sun.                          | :white_check_mark: |
| `moon_phases` | [Decoration](#decoration) | Describes the moon phases.                  | :white_check_mark: |
| `stars`       | [Decoration](#decoration) | Describes the stars.  Textures do not work. | :white_check_mark: |

**Example**

```json
{
  "sun": {
    "visible": true,
    "texture": "minecraft:sun",
    "rotation": {
      "static": [],
      "axis": []
    }
  },
  "moon_phases": {
    "visible": false
  },
  "stars": {
    "visible": true
  }
}
```