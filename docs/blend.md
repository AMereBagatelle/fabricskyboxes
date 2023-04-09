# Blend Mode Custom Blender

The mod
uses [glBlendFunc](https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glBlendFunc.xhtml)/[glBlendFuncSeparate](https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glBlendFuncSeparate.xhtml)
and [glBlendEquation](https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glBlendEquation.xhtml) to blend the
textured sky boxes. To get a better understanding of the blending functions and equations, you can use
an [Online Visualize Blending Tool](https://www.andersriggelsen.dk/glblendfunc.php).

In FabricSkyboxes, you must specify an integer value corresponding to the `sourceFactor` and `destinationFactor`
from `glBlendFunc`, and `equation` from `glBlendEquation`, respectively. A table of supported enums and their
corresponding integer values is provided below.
If `separateFunction` is enabled, you must also fill `sourceFactorAlpha` and `destinationFactorAlpha`
from `glBlendFuncSeparate`.

Here's an example of how to achieve the burn blend effect in FabricSkyboxes:

#### Burn Blend Mode

In the `"blend"` property of FabricSkyboxes, specify the following JSON:

```json
{
  "separateFunction": false,
  "sourceFactor": 0,
  "destinationFactor": 769,
  "equation": 32774,
  "sourceFactorAlpha": 0,
  "destinationFactorAlpha": 0,
  "redAlphaEnabled": true,
  "greenAlphaEnabled": true,
  "blueAlphaEnabled": true,
  "alphaEnabled": false
}
```

This corresponds to the following OpenGL code:

```java
glBlendFunc(ZERO,ONE_MINUS_SRC_COLOR);
glBlendEquation(ADD);
setShaderColor(RED,GREEN,BLUE,ALPHA);  // The `redAlphaEnabled`, `greenAlphaEnabled`, `blueAlphaEnabled`, and `alphaEnabled` values will determine whether the internal alpha state or a predetermined value of 1.0 will be used for the corresponding parameters.
```

Note that unlike in normal OpenGL, FabricSkyboxes does not support enums. You must specify an integer value.

#### Example decorations blender

```json
{
  "separateFunction": true,
  "sourceFactor": 770,
  "destinationFactor": 1,
  "equation": 32774,
  "sourceFactorAlpha": 1,
  "destinationFactorAlpha": 0,
  "redAlphaEnabled": false,
  "greenAlphaEnabled": false,
  "blueAlphaEnabled": false,
  "alphaEnabled": true
}
```

This corresponds to the following OpenGL code:

```java
glBlendFuncSeparate(SRC_ALPHA,ONE,ONE,ZERO);
glBlendEquation(ADD);
setShaderColor(RED,GREEN,BLUE,ALPHA);  // The `redAlphaEnabled`, `greenAlphaEnabled`, `blueAlphaEnabled`, and `alphaEnabled` values will determine whether the internal alpha state or a predetermined value of 1.0 will be used for the corresponding parameters.
```

### Source/Destination Factor

| Parameter                  | Value |
|----------------------------|-------|
| `CONSTANT_ALPHA`           | 32771 |
| `CONSTANT_COLOR`           | 32769 |
| `DST_ALPHA`                | 772   |
| `DST_COLOR`                | 774   |
| `ONE`                      | 1     |
| `ONE_MINUS_CONSTANT_ALPHA` | 32772 |
| `ONE_MINUS_CONSTANT_COLOR` | 32770 |
| `ONE_MINUS_DST_ALPHA`      | 773   |
| `ONE_MINUS_DST_COLOR`      | 775   |
| `ONE_MINUS_SRC_ALPHA`      | 771   |
| `ONE_MINUS_SRC_COLOR`      | 769   |
| `SRC_ALPHA`                | 770   |
| `SRC_ALPHA_SATURATE`       | 776   |
| `SRC_COLOR`                | 768   |
| `ZERO`                     | 0     |

### Equation

| Parameter          | Value |
|--------------------|-------|
| `ADD`              | 32774 |
| `SUBTRACT`         | 32778 |
| `REVERSE_SUBTRACT` | 32779 |
| `MIN`              | 32775 |
| `MAX`              | 32776 |
