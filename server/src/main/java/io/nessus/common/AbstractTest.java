package io.nessus.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import io.nessus.common.service.LogService;
import io.nessus.common.service.Service;
import io.nessus.common.utils.DateUtils;

public abstract class AbstractTest extends LogSupport {

	protected Logger LOG = LoggerFactory.getLogger(getClass().getPackage().getName());

    private Config config;
    
    @Before
    public void before() throws Exception {
        setRootLevel(Level.WARN);
    }

    @After
    public void after() throws Exception {
    }

    protected void setRootLevel(Level level) {
        getConfig().putParameter("logger.io.nessus", level);
    }

    protected <T extends Service> T getService(Class<T> type) {
        return getConfig().getService(type);
    }

    protected String getSimpleName() {
        String name = getClass().getSimpleName();
        if (name.endsWith("Test")) {
            name = name.substring(0, name.length() - 4);
        }
        return name;
    }
    
    protected TimeRange getTimeRange(Date endTime, int days) {
        Date tstart = DateUtils.subTime(endTime, days, TimeUnit.DAYS);
        return new TimeRange(tstart, endTime);
    }

    protected TimeRange getTimeRange(String endTime, int days) {
        return getTimeRange(DateUtils.parse(endTime), days);
    }
    
    protected Path getOutPath() {
        Path outdir = Paths.get("target", getSimpleName());
        outdir.toFile().mkdirs();
        return outdir;
    }
    
    protected PrintStream getPrintStream() throws IOException {
        return getPrintStream(getSimpleName());
    }

    protected PrintStream getPrintStream(String fname) throws IOException {
    	File file = Paths.get("target", fname + ".txt").toFile();
        return new PrintStream(new FileOutputStream(file));
    }
    
    protected Config createConfig() {
        Config config = new BasicConfig(new Parameters());
        config.addService(new LogService());
        return config;
    }
    
    @Override
    public Config getConfig() {
        if (config == null) {
            config = createConfig();
            config.initServices();
        }
        return config;
    }
    
    protected void sleepSafe(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            // ignore
        }
    }
}
