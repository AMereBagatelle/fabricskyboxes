# Blend Mode
The mod uses [glBlendFunc(sourceFactor, destinationFactor)](https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glBlendFunc.xhtml) and [glBlendEquation(equation)](https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glBlendEquation.xhtml) to blend the textured sky boxes.
[[Online Visualize Blending Tool](https://www.andersriggelsen.dk/glblendfunc.php)]

Using the example below to achieve the burn blend effect.

##### Burn Blend Mode
```
glBlendFunc(ZERO, ONE_MINUS_SRC_COLOR);
glBlendEquation(ADD);
```

```json
{
  "sFactor": 0,
  "dFactor": 769,
  "equation": 32774
}
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

