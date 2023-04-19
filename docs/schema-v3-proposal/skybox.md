```json5
{
  "conditions": { // playing with idea of putting this in main config json, for extra cool points
    "biomes": {
      "whitelist": [
        
      ],
      "blacklist": [
        
      ]
    }
  },
  "properties": {
    "fade": { // Old fade object - minus the alwaysOn, find a way to express that via the numbers
      
    },
    "maxAlpha": 0,
    "transition": {
      "in": 0,
      "out": 0
    }
  },
  "custom": { // everything that is type-specific goes in here
    "textures": {

    },
    "blend": { // Discussion - type specific or not?
      "type": "", // I still like this, low-effort defaults is good
      "blender": { // Maybe we only require one or the other - I think this is powerful, but usually not needed

      }
    },
    "rotation": { // This needs a lot of work - as evidenced by the weeks of discussion about how confusing it is
      "static": {},
      "axis": {}
    }
  }
}
```