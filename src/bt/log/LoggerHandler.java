package bt.log;

import java.lang.invoke.MethodType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

/**
 * @author Lukas Hartwig
 * @since 15.12.2021
 */
public abstract class LoggerHandler extends Handler
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

    protected LoggerConfiguration config;

    public LoggerHandler(LoggerConfiguration config)
    {
        this.config = config;
        this.invalidCallerClasses = initializeInvalidCallerClasses();
        addInvalidCallerClasses(config.invalidCallerClasses);
        this.invalidCallerPackages = initializeInvalidCallerPackages();
        addInvalidCallerPackages(config.invalidCallerPackages);
        setLevel(config.level);
    }

    protected List<Class<?>> initializeInvalidCallerClasses()
    {
        return new ArrayList<Class<?>>();
    }

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

    protected List<String> initializeInvalidCallerPackages()
    {
        var list = new ArrayList<String>();
        list.add("org.slf4j");
        list.add("bt.log");
        list.add("java.util.logging");
        return list;
    }

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

    private boolean isCallerStack(StackWalker.StackFrame stk)
    {
        return !this.invalidCallerClasses.stream().anyMatch(cls -> cls.getName().equals(stk.getClassName()))
                && !this.invalidCallerPackages.stream().anyMatch(pkg -> stk.getClassName().startsWith(pkg));
    }

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

    protected String getDateString()
    {
        return "[" + DateTimeFormatter.ofPattern("dd-MM-yyyy HH24:mm:ss.SSS").format(LocalDateTime.now()) + "]";
    }

    protected String getLogLevelString(Level logLevel)
    {
        return " [" + logLevel.getName() + "]";
    }

    protected String getThreadNameString()
    {
        return " [" + Thread.currentThread().getName() + "]";
    }

    protected String getPrefix(LogRecord record)
    {
        String prefix = "";

        if (this.config.printTimestamp)
        {
            prefix += getDateString();
        }

        if (this.config.printLogLevel)
        {
            prefix += getLogLevelString(record.getLevel());
        }

        if (this.config.printThreadName)
        {
            prefix += getThreadNameString();
        }

        if (this.config.printCaller)
        {
            prefix += getCallerString();
        }

        return prefix;
    }

    @Override
    public void publish(LogRecord record)
    {
        if (record.getLevel().intValue() >= getLevel().intValue())
        {
            doLog(getPrefix(record), record);
        }
    }

    @Override
    public void flush()
    {
        System.out.println("flushing");
    }

    @Override
    public void close() throws SecurityException
    {
        System.out.println("closing");
    }

    protected abstract void doLog(String prefix, LogRecord record);
}