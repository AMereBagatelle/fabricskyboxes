# Api Documentation
### Permanent skyboxes
FabricSkyBoxes provides an api that allows you to register skyboxes in code. 
These skyboxes will never be cleared after a resource reload and should often only be used to provide custom sky rendering to your custom dimension or biome.

To add permanent skyboxes, simply call `SkyboxManager.getInstance().addPermanentSkybox(mySkybox)` in your client mod initializer. 
In theory, this it can be called anywhere. It is recommended that it should be called at startup since the skybox cannot be disabled once enabled.

You can find an example of a permanent skybox in the [testmod](../src/testmod/java/io/github/amerebagatelle/fabricskyboxes/TestClientModInitializer.java)
