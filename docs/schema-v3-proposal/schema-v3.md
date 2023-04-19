# Schema V3

This specification aims to set a standard for custom sky rendering.  
This schema envelops the entire file structure of a resource pack being used to load skyboxes.

## File Structure

The basic structure of a resource pack implementing custom skyboxes will be as follows:
```
assets/namespace/sky...
    /skyboxes.json
    /skyboxes/...
        (all skybox JSON files here)
    /decorations/...
        (all decoration JSON files here)
    /fog/
        (all fog JSON files here)
    /images/...
        (all images here)
```

The overarching `skyboxes.json` file will define the layer priority for all skyboxes.
It also defines several general conflict resolution strategies for multiple skyboxes.

The details for the files in each of the subfolders can be found in their respectively named docs:
- skybox.md
- decorations.md
- fog.md
- images.md

## Skybox Configuration JSON

```json5
{
  "skyboxes": [
    {
      "file": "skybox1.json",
      "type": "square-textured"
    },
  ],
  "decorations": [
    {
      "file": "decorations.json",
      "when": "skybox1.json" // optional - active at all times unless specified
    },
  ],
  "fog": [
    "fog.json"
  ]
}
```

(Developer's Notes Below)
The reason that the type is in the skybox description in the skyboxes.json is to facilitate easier parsing.
Instead of having to get the metadata of the skybox and then parsing it again for the rest of the data - it can all be done once.

The idea of the skyboxes.json file is that it defines what is being applied when and in what order, while the files themselves describe specific effects.