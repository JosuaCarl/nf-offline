package groovy.qbic.offline.logging

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import groovy.util.logging.Slf4j
import org.slf4j.LoggerFactory

/**
 * An adaptor class for modifying logging
 */
@Slf4j
class LoggingAdapter {
    LoggerContext loggerContext

    LoggingAdapter(
            LoggerContext loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    ) {
        this.loggerContext = loggerContext
    }

    /**
     * Adds the ROOT appenders to the current scope
     *
     * @param scope Name of the scope, defaults to 'qbic.offline' Logger
     */
    void addRootAppendersToLogger(
            Logger logger = loggerContext.getLogger('qbic.offline')
    ) {
        // Ensure appenders are present
        Iterator<Appender> appenders = logger.iteratorForAppenders()
        if (appenders.size() == 0) {
            Iterator<Appender<ILoggingEvent>> rootAppenders = loggerContext.getLogger('ROOT').iteratorForAppenders()
            for (Appender rootAppender : rootAppenders) {
                logger.addAppender(rootAppender)
            }
            logger.setAdditive(false)
        }
    }
}