package de.markustieger.tigxa.http.cookie;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class PlatformLogger implements System.Logger {

    public enum Level {
        // The name and value must match that of {@code java.lang.System.Level}s.
        // Declare in ascending order of the given value
        ALL(System.Logger.Level.ALL),
        FINEST(System.Logger.Level.TRACE),
        FINER(System.Logger.Level.TRACE),
        FINE(System.Logger.Level.DEBUG),
        INFO(System.Logger.Level.INFO),
        WARNING(System.Logger.Level.WARNING),
        SEVERE(System.Logger.Level.ERROR),
        OFF(System.Logger.Level.OFF);

        final System.Logger.Level systemLevel;

        Level(System.Logger.Level systemLevel) {
            this.systemLevel = systemLevel;
        }
    }

    private System.Logger.Level getSystemLoggerLevel(PlatformLogger.Level l) {
        switch (l) {
            case ALL:
                return System.Logger.Level.ALL;
            case FINEST:
                return System.Logger.Level.TRACE;
            case FINER:
                return System.Logger.Level.TRACE;
            case FINE:
                return System.Logger.Level.DEBUG;
            case INFO:
                return System.Logger.Level.INFO;
            case WARNING:
                return System.Logger.Level.WARNING;
            case SEVERE:
                return System.Logger.Level.ERROR;
            case OFF:
                return System.Logger.Level.OFF;
            default:
                return System.Logger.Level.ALL;
        }
    }


    // Table of known loggers.  Maps names to PlatformLoggers.
    private static final Map<String, WeakReference<PlatformLogger>> loggers =
            new HashMap<>();

    /**
     * Returns a PlatformLogger of a given name.
     *
     * @param name the name of the logger
     * @return a PlatformLogger
     */
    public static synchronized PlatformLogger getLogger(String name) {
        PlatformLogger log = null;
        WeakReference<PlatformLogger> ref = loggers.get(name);
        if (ref != null) {
            log = ref.get();
        }
        if (log == null) {
            log = new PlatformLogger(System.getLogger(name));
            loggers.put(name, new WeakReference<>(log));
        }
        return log;
    }


    private final System.Logger loggerProxy;

    protected PlatformLogger(System.Logger loggerProxy) {
        this.loggerProxy = loggerProxy;
    }

    // ------------------------------------------------------------------------
    //          From System.Logger interface
    // ------------------------------------------------------------------------

    /**
     * Gets the name for this platform logger.
     *
     * @return the name of the platform logger.
     */
    @Override
    public String getName() {
        return loggerProxy.getName();
    }

    @Override
    public boolean isLoggable(System.Logger.Level level) {
        return loggerProxy.isLoggable(level);
    }

    @Override
    public void log(System.Logger.Level level, ResourceBundle bundle, String format, Object... params) {
        loggerProxy.log(level, bundle, format, params);
    }

    @Override
    public void log(System.Logger.Level level, ResourceBundle bundle, String msg, Throwable thrown) {
        loggerProxy.log(level, bundle, msg, thrown);
    }

    // ------------------------------------------------------------------------


    /**
     * Returns true if a message of the given level would actually
     * be logged by this logger.
     *
     * @param level the level
     * @return whether a message of that level would be logged
     */
    public boolean isLoggable(PlatformLogger.Level level) {
        if (level == null) {
            throw new NullPointerException();
        }

        return loggerProxy.isLoggable(getSystemLoggerLevel(level));
    }

    /**
     * Logs a SEVERE message.
     *
     * @param msg the message
     */
    public void severe(String msg) {
        if (!loggingEnabled) return;
        loggerProxy.log(System.Logger.Level.ERROR, msg, (Object[]) null);
    }

    public void severe(String msg, Throwable t) {
        if (!loggingEnabled) return;
        loggerProxy.log(System.Logger.Level.ERROR, msg, t);
    }

    public void severe(String msg, Object... params) {
        if (!loggingEnabled) return;
        loggerProxy.log(System.Logger.Level.ERROR, msg, params);
    }

    /**
     * Logs a WARNING message.
     *
     * @param msg the message
     */
    public void warning(String msg) {
        if (!loggingEnabled) return;
        loggerProxy.log(System.Logger.Level.WARNING, msg, (Object[]) null);
    }

    public void warning(String msg, Throwable t) {
        if (!loggingEnabled) return;
        loggerProxy.log(System.Logger.Level.WARNING, msg, t);
    }

    public void warning(String msg, Object... params) {
        if (!loggingEnabled) return;
        loggerProxy.log(System.Logger.Level.WARNING, msg, params);
    }

    /**
     * Logs an INFO message.
     *
     * @param msg the message
     */
    public void info(String msg) {
        if (!loggingEnabled) return;
        loggerProxy.log(System.Logger.Level.INFO, msg, (Object[]) null);
    }

    public void info(String msg, Throwable t) {
        if (!loggingEnabled) return;
        loggerProxy.log(System.Logger.Level.INFO, msg, t);
    }

    public void info(String msg, Object... params) {
        if (!loggingEnabled) return;
        loggerProxy.log(System.Logger.Level.INFO, msg, params);
    }

    /**
     * Logs a FINE message.
     *
     * @param msg the message
     */
    public void fine(String msg) {
        if (!loggingEnabled) return;
        loggerProxy.log(System.Logger.Level.DEBUG, msg, (Object[]) null);
    }

    public void fine(String msg, Throwable t) {
        if (!loggingEnabled) return;
        loggerProxy.log(System.Logger.Level.DEBUG, msg, t);
    }

    public void fine(String msg, Object... params) {
        if (!loggingEnabled) return;
        loggerProxy.log(System.Logger.Level.DEBUG, msg, params);
    }

    /**
     * Logs a FINER message.
     *
     * @param msg the message
     */
    public void finer(String msg) {
        if (!loggingEnabled) return;
        loggerProxy.log(System.Logger.Level.TRACE, msg, (Object[]) null);
    }

    public void finer(String msg, Throwable t) {
        if (!loggingEnabled) return;
        loggerProxy.log(System.Logger.Level.TRACE, msg, t);
    }

    public void finer(String msg, Object... params) {
        if (!loggingEnabled) return;
        loggerProxy.log(System.Logger.Level.TRACE, msg, params);
    }

    /**
     * Logs a FINEST message.
     *
     * @param msg the message
     */
    public void finest(String msg) {
        if (!loggingEnabled) return;
        loggerProxy.log(System.Logger.Level.TRACE, msg, (Object[]) null);
    }

    public void finest(String msg, Throwable t) {
        if (!loggingEnabled) return;
        loggerProxy.log(System.Logger.Level.TRACE, msg, t);
    }

    public void finest(String msg, Object... params) {
        if (!loggingEnabled) return;
        loggerProxy.log(System.Logger.Level.TRACE, msg, params);
    }

    // Methods for unit tests
    private boolean loggingEnabled = true;

    public void enableLogging() {
        loggingEnabled = true;
    }

    public void disableLogging() {
        loggingEnabled = false;
    }

}
