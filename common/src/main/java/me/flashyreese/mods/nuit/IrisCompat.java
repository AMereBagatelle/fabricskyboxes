package me.flashyreese.mods.nuit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class IrisCompat {
    private static boolean irisPresent;
    private static Object apiInstance;
    private static Method shaderPackInUseMethod;
    private static Method sunPathRotationMethod;

    static {
        try {
            Class<?> api = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            apiInstance = api.cast(api.getDeclaredMethod("getInstance").invoke(null));
            shaderPackInUseMethod = findMethod(api, "isShaderPackInUse");
            sunPathRotationMethod = findMethod(api, "getSunPathRotation");
            irisPresent = true;
        } catch (ReflectiveOperationException | LinkageError | RuntimeException exception) {
            irisPresent = false;
        }
    }

    private IrisCompat() {
    }

    public static boolean isIrisPresent() {
        return irisPresent;
    }

    public static boolean isShaderPackInUse() {
        if (irisPresent && shaderPackInUseMethod != null) {
            try {
                return (boolean) shaderPackInUseMethod.invoke(apiInstance);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                NuitClient.getLogger().debug("Failed to query Iris shader pack state", exception);
            }
        }
        return false;
    }

    public static float getSunPathRotation() {
        if (irisPresent && sunPathRotationMethod != null) {
            try {
                return (float) sunPathRotationMethod.invoke(apiInstance);
            } catch (IllegalAccessException | InvocationTargetException exception) {
                NuitClient.getLogger().debug("Failed to query Iris sun path rotation", exception);
            }
        }
        return 0.0F;
    }

    private static Method findMethod(Class<?> type, String methodName) {
        try {
            return type.getMethod(methodName);
        } catch (NoSuchMethodException exception) {
            return null;
        }
    }
}
