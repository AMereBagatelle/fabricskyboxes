package io.github.amerebagatelle.fabricskyboxes;

import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class Test implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        String prop = System.getProperty("fabrickskyboxes.runTests");
        if ("true".equals(prop)) {
            Path configDir = FabricLoader.getInstance().getConfigDir();
            System.exit(0);
        }
    }
}
