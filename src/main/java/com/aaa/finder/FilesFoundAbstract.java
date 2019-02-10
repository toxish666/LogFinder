package com.aaa.finder;

import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public abstract class FilesFoundAbstract implements FinderStrategy {

    protected final ExecutorService pool;
    protected Pattern format;
    protected String textToFind;
    protected final static Logger LOGGER =
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    FilesFoundAbstract(ExecutorService pool){ this.pool = pool; }




}
