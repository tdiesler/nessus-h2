package io.nessus.common.service;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.event.Level;

import io.nessus.common.Config;
import io.nessus.common.Parameters;

public final class LogService implements Service {
    
    static final ThreadLocal<PrintStream> streamAssociation = new ThreadLocal<>();
    
    private Map<String, Level> consoleLevelMapping;
    
    public static PrintStream getPrintStream() {
        return streamAssociation.get();
    }
    
    public static PrintStream setPrintStream(PrintStream out) {
        PrintStream prev = streamAssociation.get();
        streamAssociation.set(out);
        return prev;
    }
    
    @Override
    public String getType() {
    	return LogService.class.getName();
    }
    
    @Override
	public void init(Config config) {
        Parameters params = config.getParameters();
        Map<String, Level> mapping = params.keys().stream()
            .filter(k -> k.startsWith("logger."))
            .collect(Collectors.toMap(k -> k.substring(7), k -> Level.valueOf(params.get(k, String.class))));
        consoleLevelMapping = Collections.synchronizedMap(new TreeMap<>(mapping));
	}

	public void log(Logger log, Level level, Throwable th, String msg, Object[] args) {
        
        if (level == Level.ERROR) 
            logError(log, th, msg, args);
        
        else if (level == Level.WARN) 
            logWarn(log, msg, args);
        
        else if (level == Level.INFO) 
            logInfo(log, msg, args);
        
        else if (level == Level.DEBUG) 
            logDebug(log, msg, args);
        
        else if (level == Level.TRACE) 
            logTrace(log, msg, args);
    }
    
    public void logError(Logger log, Throwable th, String msg, Object... args) {
        logConsole(log, Level.ERROR, th, msg, args);
        log.error(msg, th);
    }
    
    public void logWarn(Logger log, String msg, Object... args) {
        logConsole(log, Level.WARN, null, msg, args);
        log.warn(msg, args);
    }
    
    public void logInfo(Logger log, String msg, Object... args) {
        logConsole(log, Level.INFO, null, msg, args);
        log.info(msg, args);
    }

    public void logDebug(Logger log, String msg, Object... args) {
        logConsole(log, Level.DEBUG, null, msg, args);
        log.debug(msg, args);
    }
    
    public void logTrace(Logger log, String msg, Object... args) {
        logConsole(log, Level.TRACE, null, msg, args);
        log.trace(msg, args);
    }
    
    private void logConsole(Logger log, Level level, Throwable th, String msg, Object... args) {
        
        Consumer<PrintStream> consumer = (ps) -> {
            if (ps != null) {
                
                if (msg != null) {
                    String fmsg = format(msg, args);
                    ps.println(fmsg);
                }
                
                if (th != null) 
                    th.printStackTrace(ps);
            }
        };
        
        if (isConsoleLogEnabled(log, level)) {
            
            if (Level.ERROR.toInt() <= level.toInt()) {
                consumer.accept(System.err);
            } else { 
                consumer.accept(System.out);
            }
        }
        
        if (Level.INFO.toInt() <= level.toInt()) {
            PrintStream out = getPrintStream();
            consumer.accept(out);
        }
    }
    
    private boolean isConsoleLogEnabled(Logger log, Level level) {
        String key = log.getName();
        Level target = consoleLevelMapping.get(key);
        if (target == null) {
            int idx = key.lastIndexOf('.');
            while (target == null && idx > 0) {
                key = key.substring(0, idx);
                idx = key.lastIndexOf('.');
                target = consoleLevelMapping.get(key);
            }
            if (target == null) target = Level.INFO;
            consoleLevelMapping.put(log.getName(), target);
        }
        return target.toInt() <= level.toInt();
    }
    
    static String format(String msg, Object... args) {
    	if (args.length > 0) {
            msg = msg.replace("%", "%%");
            msg = msg.replace("{}", "%s");
            msg = String.format(msg, args);
    	}
        return msg;
    }
}
