package io.github.amerebagatelle.fabricskyboxes.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerUtil {
    private static final Logger LOGGER = LogManager.getLogger("FabricSkyboxes");

    public static void logInfo(Object message) {
        LOGGER.info(formatLogMessage(message));
    }

    public static void logDebug(Object message) {
        LOGGER.debug(formatLogMessage(message));
    }

    public static void logWarn(Object message) {
        LOGGER.warn(formatLogMessage(message));
    }

    private static String formatLogMessage(Object message) {
        return String.format("[%s] %s", LOGGER.getName(), message.toString());
    }
}
