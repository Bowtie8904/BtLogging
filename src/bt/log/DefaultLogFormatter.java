package bt.log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.invoke.MethodType;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Lukas Hartwig
 * @since 15.12.2021
 */
public class DefaultLogFormatter extends Formatter
{
    /**
     * A list of classes that should be skipped when looking for the caller name.
     * <p>
     * This is used to properly log the caller method of the logging framework and
     * not the caller method of the getCallerString method.
     */
    protected List<Class<?>> invalidCallerClasses;

    /**
     * A list of packages that should be skipped when looking for the caller name.
     * <p>
     * This is used to properly log the caller method of the logging framework and
     * not the caller method of the getCallerString method.
     */
    protected List<String> invalidCallerPackages;

    /**
     * The configuration of this handler.
     */
    protected LoggerConfiguration config;

    /**
     * Creates a new instance with the given configuration.
     *
     * @param config
     */
    public DefaultLogFormatter(LoggerConfiguration config)
    {
        this.config = config;
        this.invalidCallerClasses = initializeInvalidCallerClasses();
        addInvalidCallerClasses(config.getInvalidCallerClasses());
        this.invalidCallerPackages = initializeInvalidCallerPackages();
        addInvalidCallerPackages(config.getInvalidCallerPackages());
    }

    /**
     * Creates a new instance with a default configuration.
     */
    public DefaultLogFormatter()
    {
        this(new LoggerConfiguration());
    }

    /**
     * Creates and potentially initially fills the list of invalid caller classes.
     * <p>
     * This is used to properly log the caller method of the logging framework and
     * not the caller method of the getCallerString method.
     *
     * @return
     */
    protected List<Class<?>> initializeInvalidCallerClasses()
    {
        var list = new ArrayList<Class<?>>();

        list.add(getClass());

        return list;
    }

    /**
     * Adds the given classes to the existing list of invalid caller classes.
     * <p>
     * This is used to properly log the caller method of the logging framework and
     * not the caller method of the getCallerString method.
     */
    public void addInvalidCallerClasses(Class<?>... classes)
    {
        if (classes != null)
        {
            for (Class<?> cls : classes)
            {
                this.invalidCallerClasses.add(cls);
            }
        }
    }

    /**
     * Creates and potentially initially fills the list of invalid caller packages.
     * <p>
     * This is used to properly log the caller method of the logging framework and
     * not the caller method of the getCallerString method.
     *
     * @return
     */
    protected List<String> initializeInvalidCallerPackages()
    {
        var list = new ArrayList<String>();
        list.add("org.slf4j");
        list.add("bt.log");
        list.add("java.util.logging");
        return list;
    }

    /**
     * Adds the given package names to the existing list of invalid caller packages.
     * <p>
     * This is used to properly log the caller method of the logging framework and
     * not the caller method of the getCallerString method.
     */
    public void addInvalidCallerPackages(String... packages)
    {
        if (packages != null)
        {
            for (String pkg : packages)
            {
                this.invalidCallerPackages.add(pkg);
            }
        }
    }

    /**
     * Checks if the given stack is a valid caller stack by comparing it to the invalid
     * caller classes and invalid caller packages.
     *
     * @param stk
     *
     * @return true if the stack is a valid caller stack, false otherwise.
     */
    protected boolean isCallerStack(StackWalker.StackFrame stk)
    {
        return !this.invalidCallerClasses.stream().anyMatch(cls -> cls.getName().equals(stk.getClassName()))
                && !this.invalidCallerPackages.stream().anyMatch(pkg -> stk.getClassName().startsWith(pkg));
    }

    /**
     * Formats a String of the caller information. The String should contain information about the method
     * that called the logging framework, so that it is clear where the log entry was created from.
     * <p>
     * The default implementation will create a String with the following format:
     * <p>
     * [package.subpackage.class.method(paramType1, paramType2) : lineNumber]
     *
     * @return A formatted representation of the log caller.
     */
    protected String getCallerString()
    {
        StackWalker.StackFrame stack = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                                                  .walk(stream -> stream.filter(this::isCallerStack).findFirst())
                                                  .get();

        String className = stack.getClassName();
        String methodName = stack.getMethodName();
        MethodType type = stack.getMethodType();

        var str = new StringBuilder();
        str.append("[");
        str.append(className);
        str.append(".");
        str.append(methodName);
        str.append("(");

        for (Class<?> param : type.parameterArray())
        {
            str.append(param.getSimpleName());
            str.append(", ");
        }

        String ret = str.toString();

        if (type.parameterCount() > 0)
        {
            ret = ret.substring(0, ret.length() - 2);
        }

        str.setLength(0);
        str.append(ret);
        str.append(") : ");
        str.append(stack.getLineNumber());
        str.append("]");

        return " " + str;
    }

    /**
     * Formats a String with the log entry timestamp information.
     * <p>
     * The default implementation will create a timestamp with the following format and UTC timezone:
     * <p>
     * [dd-MM-yyyy HH:mm:ss.SSS]
     *
     * @param record
     *
     * @return
     */
    protected String getTimestampString(LogRecord record)
    {
        return "[" + DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS").format(LocalDateTime.ofInstant(record.getInstant(), ZoneOffset.UTC)) + "]";
    }

    /**
     * Translates JDK log levels to unified SLF4j levels.
     *
     * @param logLevel
     *
     * @return
     */
    protected org.slf4j.event.Level unifyLogLevel(Level logLevel)
    {
        if (logLevel.equals(Level.SEVERE))
        {
            return org.slf4j.event.Level.ERROR;
        }
        else if (logLevel.equals(Level.WARNING))
        {
            return org.slf4j.event.Level.WARN;
        }
        else if (logLevel.equals(Level.INFO) || logLevel.equals(Level.CONFIG))
        {
            return org.slf4j.event.Level.INFO;
        }
        else if (logLevel.equals(Level.FINE) || logLevel.equals(Level.FINER))
        {
            return org.slf4j.event.Level.DEBUG;
        }
        else if (logLevel.equals(Level.FINEST))
        {
            return org.slf4j.event.Level.TRACE;
        }
        else
        {
            return org.slf4j.event.Level.DEBUG;
        }
    }

    /**
     * Formats a String with the information about the used log level.
     * <p>
     * The default implementation will unify the log level via {@link #unifyLogLevel(Level)} and
     * create a String with the following format:
     * <p>
     * [logLevel]
     * <p>
     * The log level will be padded to 5 characters.
     *
     * @param logLevel
     *
     * @return
     */
    protected String getLogLevelString(Level logLevel)
    {
        String levelStr = unifyLogLevel(logLevel).toString();

        if (levelStr.length() < 5)
        {
            int oldLength = levelStr.length();

            for (int i = 0; i < 5 - oldLength; i++)
            {
                levelStr += " ";
            }
        }

        return " [" + levelStr + "]";
    }

    /**
     * Formats a String containing information about the logging thread.
     * <p>
     * The default implementation will create a String with following format:
     * <p>
     * [threadName]
     *
     * @return
     */
    protected String getThreadNameString()
    {
        return " [" + Thread.currentThread().getName() + "]";
    }

    /**
     * Formats the prefix before the actual log message containing all additionally desired information.
     * <p>
     * The default implementation will create a String with the following format:
     * <p>
     * timestamp logLevel threadName caller
     * <p>
     * Checking the set configuration for each of these values.
     *
     * @param record The record that is requested to be logged.
     *
     * @return
     */
    protected String getPrefix(LogRecord record)
    {
        String prefix = "";

        if (this.config.isPrintTimestamp())
        {
            prefix += getTimestampString(record);
        }

        if (this.config.isPrintLogLevel())
        {
            prefix += getLogLevelString(record.getLevel());
        }

        if (this.config.isPrintThreadName())
        {
            prefix += getThreadNameString();
        }

        if (this.config.isPrintCaller())
        {
            prefix += getCallerString();
        }

        return prefix;
    }

    /**
     * Formates the text of the given throwable.
     * <p>
     * The default iplementation will return the stacktrace of the throwable as a String.
     *
     * @param t
     *
     * @return
     *
     * @throws IOException
     */
    protected String getThrowableText(Throwable t) throws IOException
    {
        String trace = null;

        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw))
        {
            t.printStackTrace(pw);
            trace = sw.toString();
        }

        return trace;
    }

    /**
     * Formats the message text of the given record.
     * <p>
     * The default implementation will simply return the message of the record.
     *
     * @param record
     *
     * @return
     */
    protected String getMessageText(LogRecord record)
    {
        return record.getMessage();
    }

    @Override
    public String format(LogRecord record)
    {
        String prefix = getPrefix(record);
        String text = getMessageText(record) + System.lineSeparator();

        if (record.getThrown() != null)
        {
            try
            {
                text += getThrowableText(record.getThrown());
            }
            catch (IOException e)
            {
                Log.error("Failed to log exception", e);
            }
        }

        String finalText = "";

        for (String line : text.split(System.lineSeparator() + "|\n"))
        {
            finalText += prefix + " " + line + System.lineSeparator();
        }

        return finalText;
    }
}
