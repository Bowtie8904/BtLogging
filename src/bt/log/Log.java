package bt.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.File;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;

/**
 * A convinience wrapper for the global logger implementation.
 * <p>
 * Any logging calls made to this class will be forwarded to the logger implementation with the name GLOBAL.
 * <p>
 * This class also offers some additional configuration methods to setup the default JDK logging framwork if
 * no other implementation should be used.
 *
 * @author Lukas Hartwig
 * @since 15.12.2021
 */
public class Log
{
    private static final String GLOBAL_LOGGER_NAME = "GLOBAL";
    private static final Logger GLOBAL_LOGGER = LoggerFactory.getLogger(GLOBAL_LOGGER_NAME);
    private static final String DEFAULT_LOG_FOLDER = "./logs";

    static
    {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
        {
            public void uncaughtException(Thread t, Throwable e)
            {
                Log.error("Uncaught exception on thread " + t.getName(), e);
            }
        });
    }

    public static void createDefaultLogFolder()
    {
        createLogFolder(DEFAULT_LOG_FOLDER);
    }

    public static void createLogFolder(String path)
    {
        File folder = new File(path);
        folder.mkdirs();
    }

    /**
     * Configures the JDK global logger with a loglevel ALL and a standard {@link ConsoleLoggerHandler}.
     * <p>
     * This setup means that the logger will only log to System.out and System.err.
     */
    public static void configureDefaultJDKLogger()
    {
        configureDefaultJDKLogger(new LoggerConfiguration().level(Level.ALL));
    }

    /**
     * Configures the JDK global logger with the given loglevel and a standard {@link ConsoleLoggerHandler}.
     * <p>
     * This setup means that the logger will only log to System.out and System.err.
     *
     * @param logLevel The log level for this logger and the created console logger handler.
     */
    public static void configureDefaultJDKLogger(Level logLevel)
    {
        configureDefaultJDKLogger(new LoggerConfiguration().level(logLevel));
    }

    /**
     * Configures the JDK global logger.
     * A custom {@link ConsoleLoggerHandler} will be created which will use the given configuration.
     * <p>
     * This setup means that the logger will only log to System.out and System.err.
     *
     * @param config The configuration that should be used by the created console logger handler.
     */
    public static void configureDefaultJDKLogger(LoggerConfiguration config)
    {
        configureDefaultJDKLogger(config.level, new ConsoleLoggerHandler(config));
    }

    /**
     * Configures the JDK global logger with the given logger handlers and log level ALL.
     * <p>
     * With this setup no default {@link ConsoleLoggerHandler} will be created. If you wish to log to the
     * console then you need to create your own and pass it to this method.
     *
     * @param firstHandler The first logger handler.
     * @param handlers     Additional logger handlers.
     */
    public static void configureDefaultJDKLogger(Handler firstHandler, Handler... handlers)
    {
        configureDefaultJDKLogger(Level.ALL, firstHandler, handlers);
    }

    /**
     * Configures the JDK global logger with the given logger handlers and log level.
     * <p>
     * The log level is only relevant for the logger itsself, meaning the logs that are being handed to the handlers.
     * Logger handlers will use their own log level and further restrict output.
     * <p>
     * With this setup no default {@link ConsoleLoggerHandler} will be created. If you wish to log to the
     * console then you need to create your own and pass it to this method.
     *
     * @param logLevel     The log level of the logger.
     * @param firstHandler The first logger handler.
     * @param handlers     Additional logger handlers.
     */
    public static void configureDefaultJDKLogger(Level logLevel, Handler firstHandler, Handler... handlers)
    {
        // remove the default handler from the root logger
        LogManager.getLogManager().reset();

        java.util.logging.Logger globalJdkLogger = java.util.logging.Logger.getLogger(GLOBAL_LOGGER_NAME);

        globalJdkLogger.setLevel(logLevel);
        globalJdkLogger.addHandler(firstHandler);

        for (var handler : handlers)
        {
            globalJdkLogger.addHandler(handler);
        }
    }

    private static String valueToString(Object value)
    {
        String ret = "";

        if (value != null)
        {
            if (value.getClass().isArray())
            {
                if (value.getClass() == boolean[].class)
                {
                    ret += Arrays.toString((boolean[])value);
                }
                else if (value.getClass() == byte[].class)
                {
                    ret += Arrays.toString((byte[])value);
                }
                else if (value.getClass() == short[].class)
                {
                    ret += Arrays.toString((short[])value);
                }
                else if (value.getClass() == int[].class)
                {
                    ret += Arrays.toString((int[])value);
                }
                else if (value.getClass() == long[].class)
                {
                    ret += Arrays.toString((long[])value);
                }
                else if (value.getClass() == float[].class)
                {
                    ret += Arrays.toString((float[])value);
                }
                else if (value.getClass() == double[].class)
                {
                    ret += Arrays.toString((double[])value);
                }
                else if (value.getClass() == char[].class)
                {
                    ret += Arrays.toString((char[])value);
                }
                else
                {
                    ret += Arrays.toString((Object[])value);
                }
            }
            else
            {
                ret += value;
            }
        }
        else
        {
            ret = "null";
        }

        return ret;
    }

    private static String formatParameterValues(Object... values)
    {
        String ret = "";

        var stack = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                               .walk(s -> s.skip(2)
                                           .findFirst())
                               .get();

        Class cls = stack.getDeclaringClass();
        String methodName = stack.getMethodName();
        MethodType type = stack.getMethodType();

        try
        {
            Method m = cls.getDeclaredMethod(methodName, type.parameterArray());
            m.setAccessible(true);

            Parameter p;

            for (int i = 0; i < m.getParameters().length; i++)
            {
                p = m.getParameters()[i];
                ret += "[" + p.getName() + " = ";

                if (i < values.length)
                {
                    ret += valueToString(values[i]);
                }

                ret += "]";
            }
        }
        catch (NoSuchMethodException | SecurityException e)
        {
            error("Failed to format parameter values for entry call", e);
        }

        return ret;
    }

    /**
     * Creates a TRACE level entry log message.
     */
    public static void entry()
    {
        trace("ENTRY");
    }

    /**
     * Creates a TRACE level entry log message with the given parameter.
     *
     * @param parameterValue The value for the parameter of the calling method.
     */
    public static void entry(Object parameterValue)
    {
        trace("ENTRY " + formatParameterValues(new Object[] { parameterValue }));
    }

    /**
     * Creates a TRACE level entry log message with the given parameters.
     *
     * @param parameterValue1 The value for the first parameter of the calling method.
     * @param parameterValues Additional values for other parameters of the calling method.
     */
    public static void entry(Object parameterValue1, Object... parameterValues)
    {
        Object[] params = null;

        if (parameterValues == null)
        {
            params = new Object[] { parameterValue1 };
        }
        else
        {
            params = new Object[parameterValues.length + 1];
            params[0] = parameterValue1;

            for (int i = 1; i < params.length; i++)
            {
                params[i] = parameterValues[i - 1];
            }
        }

        trace("ENTRY " + formatParameterValues(params));
    }

    /**
     * Creates a TRACE level exit log message with the given return value.
     *
     * @param returnValue
     */
    public static void exit(Object returnValue)
    {
        trace("EXIT [return = " + valueToString(returnValue) + "]");
    }

    /**
     * Creates a TRACE level exit log message.
     */
    public static void exit()
    {
        trace("EXIT");
    }

    /**
     * @see Logger#getName()
     */
    public static String getName()
    {
        return GLOBAL_LOGGER.getName();
    }

    /**
     * @see Logger#isTraceEnabled()
     */
    public static boolean isTraceEnabled()
    {
        return GLOBAL_LOGGER.isTraceEnabled();
    }

    /**
     * @see Logger#trace(String)
     */
    public static void trace(String s)
    {
        GLOBAL_LOGGER.trace(s);
    }

    /**
     * @see Logger#trace(String, Object)
     */
    public static void trace(String s, Object o)
    {
        GLOBAL_LOGGER.trace(s, o);
    }

    /**
     * @see Logger#trace(String, Object, Object)
     */
    public static void trace(String s, Object o, Object o1)
    {
        GLOBAL_LOGGER.trace(s, o, o1);
    }

    /**
     * @see Logger#trace(String, Object...)
     */
    public static void trace(String s, Object... objects)
    {
        GLOBAL_LOGGER.trace(s, objects);
    }

    /**
     * @see Logger#trace(String, Throwable)
     */
    public static void trace(String s, Throwable throwable)
    {
        GLOBAL_LOGGER.trace(s, throwable);
    }

    /**
     * @see Logger#isTraceEnabled(Marker)
     */
    public static boolean isTraceEnabled(Marker marker)
    {
        return GLOBAL_LOGGER.isTraceEnabled(marker);
    }

    /**
     * @see Logger#trace(Marker, String)
     */
    public static void trace(Marker marker, String s)
    {
        GLOBAL_LOGGER.trace(marker, s);
    }

    /**
     * @see Logger#trace(Marker, String, Object)
     */
    public static void trace(Marker marker, String s, Object o)
    {
        GLOBAL_LOGGER.trace(marker, s, o);
    }

    /**
     * @see Logger#trace(Marker, String, Object, Object)
     */
    public static void trace(Marker marker, String s, Object o, Object o1)
    {
        GLOBAL_LOGGER.trace(marker, s, o, o1);
    }

    /**
     * @see Logger#trace(Marker, String, Object...)
     */
    public static void trace(Marker marker, String s, Object... objects)
    {
        GLOBAL_LOGGER.trace(marker, s, objects);
    }

    /**
     * @see Logger#trace(Marker, String, Throwable)
     */
    public static void trace(Marker marker, String s, Throwable throwable)
    {
        GLOBAL_LOGGER.trace(marker, s, throwable);
    }

    /**
     * @see Logger#isDebugEnabled()
     */
    public static boolean isDebugEnabled()
    {
        return GLOBAL_LOGGER.isDebugEnabled();
    }

    /**
     * @see Logger#debug(String)
     */
    public static void debug(String s)
    {
        GLOBAL_LOGGER.debug(s);
    }

    /**
     * @see Logger#debug(String, Object)
     */
    public static void debug(String s, Object o)
    {
        GLOBAL_LOGGER.debug(s, o);
    }

    /**
     * @see Logger#debug(String, Object, Object)
     */
    public static void debug(String s, Object o, Object o1)
    {
        GLOBAL_LOGGER.debug(s, o, o1);
    }

    /**
     * @see Logger#debug(String, Object...)
     */
    public static void debug(String s, Object... objects)
    {
        GLOBAL_LOGGER.debug(s, objects);
    }

    /**
     * @see Logger#debug(String, Throwable)
     */
    public static void debug(String s, Throwable throwable)
    {
        GLOBAL_LOGGER.debug(s, throwable);
    }

    /**
     * @see Logger#isDebugEnabled(Marker)
     */
    public static boolean isDebugEnabled(Marker marker)
    {
        return GLOBAL_LOGGER.isDebugEnabled(marker);
    }

    /**
     * @see Logger#debug(Marker, String)
     */
    public static void debug(Marker marker, String s)
    {
        GLOBAL_LOGGER.debug(marker, s);
    }

    /**
     * @see Logger#debug(Marker, String, Object)
     */
    public static void debug(Marker marker, String s, Object o)
    {
        GLOBAL_LOGGER.debug(marker, s, o);
    }

    /**
     * @see Logger#debug(Marker, String, Object, Object)
     */
    public static void debug(Marker marker, String s, Object o, Object o1)
    {
        GLOBAL_LOGGER.debug(marker, s, o, o1);
    }

    /**
     * @see Logger#debug(Marker, String, Object...)
     */
    public static void debug(Marker marker, String s, Object... objects)
    {
        GLOBAL_LOGGER.debug(marker, s, objects);
    }

    /**
     * @see Logger#debug(Marker, String, Throwable)
     */
    public static void debug(Marker marker, String s, Throwable throwable)
    {
        GLOBAL_LOGGER.debug(marker, s, throwable);
    }

    /**
     * @see Logger#isInfoEnabled()
     */
    public static boolean isInfoEnabled()
    {
        return GLOBAL_LOGGER.isInfoEnabled();
    }

    /**
     * @see Logger#info(String)
     */
    public static void info(String s)
    {
        GLOBAL_LOGGER.info(s);
    }

    /**
     * @see Logger#info(String, Object)
     */
    public static void info(String s, Object o)
    {
        GLOBAL_LOGGER.info(s, o);
    }

    /**
     * @see Logger#info(String, Object, Object)
     */
    public static void info(String s, Object o, Object o1)
    {
        GLOBAL_LOGGER.info(s, o, o1);
    }

    /**
     * @see Logger#info(String, Object...)
     */
    public static void info(String s, Object... objects)
    {
        GLOBAL_LOGGER.info(s, objects);
    }

    /**
     * @see Logger#info(String, Throwable)
     */
    public static void info(String s, Throwable throwable)
    {
        GLOBAL_LOGGER.info(s, throwable);
    }

    /**
     * @see Logger#isInfoEnabled(Marker)
     */
    public static boolean isInfoEnabled(Marker marker)
    {
        return GLOBAL_LOGGER.isInfoEnabled(marker);
    }

    /**
     * @see Logger#info(Marker, String)
     */
    public static void info(Marker marker, String s)
    {
        GLOBAL_LOGGER.info(marker, s);
    }

    /**
     * @see Logger#info(Marker, String, Object)
     */
    public static void info(Marker marker, String s, Object o)
    {
        GLOBAL_LOGGER.info(marker, s, o);
    }

    /**
     * @see Logger#info(Marker, String, Object, Object)
     */
    public static void info(Marker marker, String s, Object o, Object o1)
    {
        GLOBAL_LOGGER.info(marker, s, o, o1);
    }

    /**
     * @see Logger#info(Marker, String, Object...)
     */
    public static void info(Marker marker, String s, Object... objects)
    {
        GLOBAL_LOGGER.info(marker, s, objects);
    }

    /**
     * @see Logger#info(Marker, String, Throwable)
     */
    public static void info(Marker marker, String s, Throwable throwable)
    {
        GLOBAL_LOGGER.info(marker, s, throwable);
    }

    /**
     * @see Logger#isWarnEnabled()
     */
    public static boolean isWarnEnabled()
    {
        return GLOBAL_LOGGER.isWarnEnabled();
    }

    /**
     * @see Logger#warn(String)
     */
    public static void warn(String s)
    {
        GLOBAL_LOGGER.warn(s);
    }

    /**
     * @see Logger#warn(String, Object)
     */
    public static void warn(String s, Object o)
    {
        GLOBAL_LOGGER.warn(s, o);
    }

    /**
     * @see Logger#warn(String, Object...)
     */
    public static void warn(String s, Object... objects)
    {
        GLOBAL_LOGGER.warn(s, objects);
    }

    /**
     * @see Logger#warn(String, Object, Object)
     */
    public static void warn(String s, Object o, Object o1)
    {
        GLOBAL_LOGGER.warn(s, o, o1);
    }

    /**
     * @see Logger#warn(String, Throwable)
     */
    public static void warn(String s, Throwable throwable)
    {
        GLOBAL_LOGGER.warn(s, throwable);
    }

    /**
     * @see Logger#isWarnEnabled(Marker)
     */
    public static boolean isWarnEnabled(Marker marker)
    {
        return GLOBAL_LOGGER.isWarnEnabled(marker);
    }

    /**
     * @see Logger#warn(Marker, String)
     */
    public static void warn(Marker marker, String s)
    {
        GLOBAL_LOGGER.warn(marker, s);
    }

    /**
     * @see Logger#warn(Marker, String, Object)
     */
    public static void warn(Marker marker, String s, Object o)
    {
        GLOBAL_LOGGER.warn(marker, s, o);
    }

    /**
     * @see Logger#warn(Marker, String, Object, Object)
     */
    public static void warn(Marker marker, String s, Object o, Object o1)
    {
        GLOBAL_LOGGER.warn(marker, s, o, o1);
    }

    /**
     * @see Logger#warn(Marker, String, Object...)
     */
    public static void warn(Marker marker, String s, Object... objects)
    {
        GLOBAL_LOGGER.warn(marker, s, objects);
    }

    /**
     * @see Logger#warn(Marker, String, Throwable)
     */
    public static void warn(Marker marker, String s, Throwable throwable)
    {
        GLOBAL_LOGGER.warn(marker, s, throwable);
    }

    /**
     * @see Logger#isErrorEnabled()
     */
    public static boolean isErrorEnabled()
    {
        return GLOBAL_LOGGER.isErrorEnabled();
    }

    /**
     * @see Logger#error(String)
     */
    public static void error(String s)
    {
        GLOBAL_LOGGER.error(s);
    }

    /**
     * @see Logger#error(String, Object)
     */
    public static void error(String s, Object o)
    {
        GLOBAL_LOGGER.error(s, o);
    }

    /**
     * @see Logger#error(String, Object, Object)
     */
    public static void error(String s, Object o, Object o1)
    {
        GLOBAL_LOGGER.error(s, o, o1);
    }

    /**
     * @see Logger#error(String, Object...)
     */
    public static void error(String s, Object... objects)
    {
        GLOBAL_LOGGER.error(s, objects);
    }

    /**
     * @see Logger#error(String, Throwable)
     */
    public static void error(String s, Throwable throwable)
    {
        GLOBAL_LOGGER.error(s, throwable);
    }

    /**
     * @see Logger#isErrorEnabled(Marker)
     */
    public static boolean isErrorEnabled(Marker marker)
    {
        return GLOBAL_LOGGER.isErrorEnabled(marker);
    }

    /**
     * @see Logger#error(Marker, String)
     */
    public static void error(Marker marker, String s)
    {
        GLOBAL_LOGGER.error(marker, s);
    }

    /**
     * @see Logger#error(Marker, String, Object)
     */
    public static void error(Marker marker, String s, Object o)
    {
        GLOBAL_LOGGER.error(marker, s, o);
    }

    /**
     * @see Logger#error(Marker, String, Object, Object)
     */
    public static void error(Marker marker, String s, Object o, Object o1)
    {
        GLOBAL_LOGGER.error(marker, s, o, o1);
    }

    /**
     * @see Logger#error(Marker, String, Object...)
     */
    public static void error(Marker marker, String s, Object... objects)
    {
        GLOBAL_LOGGER.error(marker, s, objects);
    }

    /**
     * @see Logger#error(Marker, String, Throwable)
     */
    public static void error(Marker marker, String s, Throwable throwable)
    {
        GLOBAL_LOGGER.error(marker, s, throwable);
    }
}
