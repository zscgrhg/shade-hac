package org.apache.http.client.fluent;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.Logger;

class LoggerBuddy {
    public static final LoggerContext LOGGER_CONTEXT = createFromClasspathResource();
    public static final String CONFIG_NAME = "hac.xml";
    public static final String CONFIG_NAME_DEBUG = "hac-debug.xml";

    public static Logger of(Class<?> clazz) {
        return LOGGER_CONTEXT.getLogger(clazz);
    }


    private static LoggerContext createFromClasspathResource() {
        LoggerContext context = new LoggerContext();
        try {
            JoranConfigurator configurator = new JoranConfigurator();
            String filename = "true".equalsIgnoreCase(getSystemProperty("hac.debug"))
                    ? CONFIG_NAME_DEBUG : CONFIG_NAME;
            configurator.setContext(context);
            configurator.doConfigure(LoggerBuddy.class
                    .getClassLoader()
                    .getResourceAsStream(filename));
        } catch (JoranException je) {

        }
        return context;
    }

    private static String getSystemProperty(String name) {
        try {
            return trim(System.getProperty(name));
        } catch (Exception e) {
            return null;
        }
    }

    private static String trim(String str) {
        if (str != null) {
            return str.trim();
        } else {
            return null;
        }
    }
}
