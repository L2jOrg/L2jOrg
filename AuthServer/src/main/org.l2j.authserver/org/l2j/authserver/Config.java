package org.l2j.authserver;

public final class Config {
    public static final String HECACHE_FILE = getCurrentPath("config/ehcache.xml");
    public static final String ASYNC_MMOCORE_FILE = getCurrentPath("config/async-mmocore.properties");
    public static final String DATABASE_FILE = getCurrentPath("config/database.properties");
    public static final String BANNED_IP_FILE = getCurrentPath("banned_ip.cfg");
    public static final String SERVERNAME_SCHEMA_FILE = getCurrentPath("servername.xsd");
    public static final String SERVERNAME_FILE = getCurrentPath("servername.xml");


    public static String getCurrentPath(String path) {
        boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().
                getInputArguments().toString().contains("jdwp");
        return (isDebug ? "src/main/resources/" + path : path);


    }
}
