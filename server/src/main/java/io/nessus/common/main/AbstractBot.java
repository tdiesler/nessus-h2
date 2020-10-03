package io.nessus.common.main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import io.nessus.common.Config;
import io.nessus.common.ConfigSupport;
import io.nessus.common.BasicConfig;

public abstract class AbstractBot<T extends AbstractOptions> extends ConfigSupport {

    private static String implVersion;
    private static String implBuild;

    public AbstractBot(URL cfgurl) throws IOException {
        this(new BasicConfig(cfgurl));
    }

    public AbstractBot(Config config) {
        super(config);
    }

    protected abstract T createOptions();

    public void start(String... args) {
        
        try (Connection con = createConnection()) {
            try {
                
                T options = parseArguments(args);
                
                prepare(options);
                
                doStart(options);
                
            } catch (Exception ex) {
                
                logError(ex);
            }
        } catch (Throwable th) {
            
            logError(th);
        }
    }
    
    protected void prepare(T options) throws Exception {
        
    }

    protected abstract void doStart(T options) throws Exception;

    @SuppressWarnings("unchecked")
    protected T parseArguments(String... args) throws CmdLineException {
        
        T options = createOptions();
        CmdLineParser parser = new CmdLineParser(options);

        // Obtain version information from the manifest that contains the options class
        Class<T> clazz = (Class<T>) options.getClass();
        String className = clazz.getSimpleName() + ".class";
        String classPath = clazz.getResource(className).toString();
        if (classPath.startsWith("jar")) {
            String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 2) + JarFile.MANIFEST_NAME;
            try (InputStream ins = new URL(manifestPath).openStream()) {
                Manifest manifest = new Manifest(ins);
                Attributes attribs = manifest.getMainAttributes();
                implVersion = attribs.getValue("Implementation-Version");
                implBuild = attribs.getValue("Implementation-Build");
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }

        try {
            parser.parseArgument(args);
        } catch (CmdLineException ex) {
            helpScreen(options);
            throw ex;
        }

        if (options.help) {
            helpScreen(options);
            System.exit(0);
        }

        if (options.version) {
            System.out.println(getVersionString());
            System.exit(0);
        }

        options.initDefaults(getConfig());
        return options;
    }

    public static String getImplVersion() {
        return implVersion;
    }

    public static String getImplBuild() {
        return implBuild;
    }

    public static String getVersionString() {
        if (implVersion != null && implVersion.endsWith("SNAPSHOT"))
            return String.format("%s (%s)", implVersion, implBuild);
        else
            return String.format("%s (%s)", implVersion, implBuild);
    }

    protected void helpScreen(T options) {
        System.err.println(options.cmd + " [options...]");
        CmdLineParser parser = new CmdLineParser(options);
        parser.printUsage(System.err);
    }
}
