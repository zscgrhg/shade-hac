package org.apache.http.client.fluent;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.Logger;

class LoggerBuddy {
    public static final LoggerContext LOGGER_CONTEXT = createFromClasspathResource();
    public static final String CONFIG_NAME = "hac.xml";

    public static Logger of(Class<?> clazz) {
        return LOGGER_CONTEXT.getLogger(clazz);
    }


    private static LoggerContext createFromClasspathResource() {
        LoggerContext context = new LoggerContext();
        try {
            JoranConfigurator configurator = new JoranConfigurator();

            configurator.setContext(context);
            configurator.doConfigure(LoggerBuddy.class
                    .getClassLoader()
                    .getResourceAsStream(CONFIG_NAME));
        } catch (JoranException je) {

        }
        return context;
    }
}
