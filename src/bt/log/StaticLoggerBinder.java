package org.slf4j.impl;

import bt.log.BtLoggerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 * @author Lukas Hartwig
 * @since 22.01.2022
 */
public class StaticLoggerBinder implements LoggerFactoryBinder
{
    private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();
    private static final String loggerFactoryClassStr = BtLoggerFactory.class.getName();
    public static String REQUESTED_API_VERSION = "1.6.99";
    private final ILoggerFactory loggerFactory = new BtLoggerFactory();

    private StaticLoggerBinder()
    {
    }

    public static final StaticLoggerBinder getSingleton()
    {
        return SINGLETON;
    }

    public ILoggerFactory getLoggerFactory()
    {
        return this.loggerFactory;
    }

    public String getLoggerFactoryClassStr()
    {
        return loggerFactoryClassStr;
    }
}