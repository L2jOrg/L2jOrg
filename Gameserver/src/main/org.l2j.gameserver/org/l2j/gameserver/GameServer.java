package org.l2j.gameserver;

import io.github.joealisson.mmocore.ConnectionBuilder;
import io.github.joealisson.mmocore.ConnectionHandler;
import org.l2j.commons.cache.CacheFactory;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.commons.lang.StatsUtils;
import org.l2j.commons.listener.Listener;
import org.l2j.commons.listener.ListenerList;
import org.l2j.gameserver.cache.CrestCache;
import org.l2j.gameserver.cache.ImagesCache;
import org.l2j.gameserver.dao.ItemsDAO;
import org.l2j.gameserver.data.BoatHolder;
import org.l2j.gameserver.data.xml.Parsers;
import org.l2j.gameserver.data.xml.holder.EventHolder;
import org.l2j.gameserver.data.xml.holder.ResidenceHolder;
import org.l2j.gameserver.data.xml.holder.StaticObjectHolder;
import org.l2j.gameserver.geodata.GeoEngine;
import org.l2j.gameserver.handler.admincommands.AdminCommandHandler;
import org.l2j.gameserver.handler.bbs.BbsHandlerHolder;
import org.l2j.gameserver.handler.bypass.BypassHolder;
import org.l2j.gameserver.handler.items.ItemHandler;
import org.l2j.gameserver.handler.onshiftaction.OnShiftActionHolder;
import org.l2j.gameserver.handler.usercommands.UserCommandHandler;
import org.l2j.gameserver.handler.voicecommands.VoicedCommandHandler;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.instancemanager.*;
import org.l2j.gameserver.instancemanager.clansearch.ClanSearchManager;
import org.l2j.gameserver.instancemanager.games.MiniGameScoreManager;
import org.l2j.gameserver.listener.GameListener;
import org.l2j.gameserver.listener.game.OnShutdownListener;
import org.l2j.gameserver.listener.game.OnStartListener;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.model.entity.olympiad.Olympiad;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.network.l2.GamePacketHandler;
import org.l2j.gameserver.scripts.Scripts;
import org.l2j.gameserver.security.HWIDBan;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.tables.ClanTable;
import org.l2j.gameserver.tables.EnchantHPBonusTable;
import org.l2j.gameserver.taskmanager.AutomaticTasks;
import org.l2j.gameserver.taskmanager.ItemsAutoDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.isNullOrEmpty;

public class GameServer {

    private static final String LOG4J_CONFIGURATION_FILE = "log4j.configurationFile";

    public static final String UPDATE_NAME = "Classic: Saviors (Zaken)";

    private static Logger logger;
    private static GameServer instance;
    private static String version;

    private final ConnectionHandler<GameClient> connectionHandler;
    private final GameServerListenerList listeners;

    private final String licenseHost;

    private int onlineLimit;

    public GameServer() throws Exception {
        instance = this;
        listeners = new GameServerListenerList();

        var serverSettings = getSettings(ServerSettings.class);

        licenseHost = serverSettings.externalAddress();

        if (!IdFactory.getInstance().isInitialized()) {
            logger.error("Could not read object IDs from DB. Please Check Your Data.");
            throw new Exception("Could not initialize the ID factory");
        }

        GeoEngine.load();
        GameTimeController.getInstance();

        ItemHandler.getInstance();

        Scripts.getInstance();

        Parsers.parseAll();

        ItemsDAO.getInstance();

        ImagesCache.getInstance();

        CrestCache.getInstance();
        ClanTable.getInstance();

        EnchantHPBonusTable.getInstance();

        SpawnManager.getInstance().spawnAll();
        StaticObjectHolder.getInstance().spawnAll();
        HWIDBan.getInstance().load();
        RaidBossSpawnManager.getInstance();

        Scripts.getInstance().init();

        Announcements.getInstance();

        if (Config.AUTODESTROY_ITEM_AFTER > 0)
            ItemsAutoDestroy.getInstance();

        if (Config.ENABLE_OLYMPIAD) {
            Olympiad.load();
            Hero.getInstance();
        }

        if (Config.ALLOW_WEDDING)
            CoupleManager.getInstance();

        AdminCommandHandler.getInstance().log();
        UserCommandHandler.getInstance().log();
        VoicedCommandHandler.getInstance().log();
        BbsHandlerHolder.getInstance().log();
        BypassHolder.getInstance().log();
        OnShiftActionHolder.getInstance().log();

        AutomaticTasks.init();

        ClanTable.getInstance().checkClans();

        logger.info("=[Events]=========================================");
        ResidenceHolder.getInstance().callInit();
        EventHolder.getInstance().callInit();
        logger.info("==================================================");

        BoatHolder.getInstance().spawnAll();

        logger.info("IdFactory: Free ObjectID's remaining: " + IdFactory.getInstance().size());

        MiniGameScoreManager.getInstance();

        ClanSearchManager.getInstance().load();

        BotCheckManager.loadBotQuestions();
        BotReportManager.getInstance();

        TrainingCampManager.getInstance().init();

        Shutdown.getInstance().schedule(Config.RESTART_AT_TIME, Shutdown.RESTART);

        logger.info("GameServer Started");
        onlineLimit = serverSettings.maximumOnlineUsers();
        logger.info("Maximum Numbers of Connected Players: {}", onlineLimit);

        final GamePacketHandler gph = new GamePacketHandler();
        connectionHandler = ConnectionBuilder.create(new InetSocketAddress(serverSettings.port()), gph, gph, gph).bufferLargeSize(24 * 1024).build();
        connectionHandler.start();

        getListeners().onStart();
    }

    private static void logMemoryUsage() {
        logger.info("=================================================");
        String memUsage = String.valueOf(StatsUtils.getMemUsage());
        for (String line : memUsage.split("\n"))
            logger.info(line);

        logger.info("=================================================");
    }

    private static void logVersionInfo() {
        try {
            var versionProperties = new Properties();
            var versionFile = ClassLoader.getSystemResourceAsStream("version.properties");
            if(nonNull(versionFile)) {
                versionProperties.load(versionFile);
                version = versionProperties.getProperty("version");
                logger.info("======================================================================");
                logger.info("Build Version: ........... {}", version);
                logger.info("Build Revision: .......... {}", versionProperties.getProperty("revision"));
                logger.info("Update: .................. {}", UPDATE_NAME);
                logger.info("Build date: .............. {}", versionProperties.getProperty("buildDate"));
                logger.info("Compiler JDK version: .... {}", versionProperties.getProperty("compilerVersion"));
                logger.info("======================================================================");
            }
        } catch (IOException e) {
            logger.warn(e.getLocalizedMessage(), e);
        }
    }

    public GameServerListenerList getListeners() {
        return listeners;
    }

    public static GameServer getInstance() {
        return instance;
    }

    public <T extends GameListener> boolean addListener(T listener) {
        return listeners.add(listener);
    }

    public <T extends GameListener> boolean removeListener(T listener) {
        return listeners.remove(listener);
    }

    public String getLicenseHost() {
        return licenseHost;
    }

    public String getVersion() {
        return version;
    }

    public int getOnlineLimit() {
        return onlineLimit;
    }

    public void shutdown() {
        connectionHandler.shutdown();
    }


    public static void main(String[] args) {
        try {

            initializeResources();

            new GameServer();

            ThreadPoolManager.getInstance().execute(AuthServerCommunication.getInstance());

            Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
            logMemoryUsage();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    private static void initializeResources() throws Exception {
        configureLogger();
        logVersionInfo();
        configureDatabase();
        CacheFactory.getInstance().initialize("config/ehcache.xml");
        // TODO remove this
        Config.load();
        ThreadPoolManager.getInstance();
    }

    private static void configureDatabase() throws Exception {
        System.setProperty("hikaricp.configurationFile", "config/database.properties");
        if(!DatabaseAccess.initialize()) {
            throw new Exception("Database Access could not be initialized");
        }
    }

    private static void configureLogger() {
        var logConfigurationFile = System.getProperty(LOG4J_CONFIGURATION_FILE);
        if (isNullOrEmpty(logConfigurationFile)) {
            System.setProperty(LOG4J_CONFIGURATION_FILE, "log4j.xml");
        }
        logger = LoggerFactory.getLogger(GameServer.class);
    }

    public class GameServerListenerList extends ListenerList<GameServer> {
        public void onStart() {
            for (Listener<GameServer> listener : getListeners())
                if (listener instanceof OnStartListener)
                    ((OnStartListener) listener).onStart();
        }

        public void onShutdown() {
            for (Listener<GameServer> listener : getListeners())
                if (listener instanceof OnShutdownListener)
                    ((OnShutdownListener) listener).onShutdown();
        }
    }
}