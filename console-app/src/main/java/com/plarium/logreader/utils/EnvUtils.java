package com.plarium.logreader.utils;

import javax.naming.ConfigurationException;
import java.util.Map;

public class EnvUtils {

    public static int getEnvInt(final Map<String, ?> env, final String propertyName, final int defaultValue) throws ConfigurationException {
        final Object propertyValue = env.get(propertyName);
        if (propertyValue == null) {
            return defaultValue;
        }
        if (propertyValue instanceof Number) {
            return ((Number) propertyValue).intValue();
        }
        final String str = propertyValue.toString();
        final String resultStr = str.trim();

        try {
            return Integer.parseInt(resultStr);
        } catch (NumberFormatException e) {
            throw new ConfigurationException(String.format("Invalid format of property: %s, it should be int not a %s", propertyName, resultStr));
        }
    }

    public static String getRequiredEnvString(final Map<String, ?> env, final String propertyName) throws ConfigurationException {
        final Object property = env.get(propertyName);
        if (property == null) {
            throw new ConfigurationException(String.format("Property: %s, is not allowed to be empty", propertyName));
        }
        return property.toString().trim();
    }

    public static boolean getEnvBool(Map<String, ?> env, final String propertyName, boolean defaultValue) {
        final Object property = env.get(propertyName);
        if (property == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(property.toString());
    }
}
