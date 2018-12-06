package org.l2j.gameserver;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.sf.ehcache.CacheManager;
import org.l2j.commons.database.L2DatabaseFactory;
import org.l2j.commons.lang.StatsUtils;
import org.l2j.commons.listener.Listener;
import org.l2j.commons.listener.ListenerList;
import org.l2j.gameserver.cache.CrestCache;
import org.l2j.gameserver.cache.ImagesCache;
import org.l2j.gameserver.config.templates.HostInfo;
import org.l2j.gameserver.config.xml.ConfigParsers;
import org.l2j.gameserver.config.xml.holder.HostsConfigHolder;
import org.l2j.gameserver.dao.CharacterDAO;
import org.l2j.gameserver.dao.CustomHeroDAO;
import org.l2j.gameserver.dao.HidenItemsDAO;
import org.l2j.gameserver.dao.ItemsDAO;
import org.l2j.gameserver.data.BoatHolder;
import org.l2j.gameserver.data.xml.Parsers;
import org.l2j.gameserver.data.xml.holder.EventHolder;
import org.l2j.gameserver.data.xml.holder.ResidenceHolder;
import org.l2j.gameserver.data.xml.holder.StaticObjectHolder;
import org.l2j.gameserver.database.UpdatesInstaller;
import org.l2j.gameserver.geodata.GeoEngine;
import org.l2j.gameserver.handler.admincommands.AdminCommandHandler;
import org.l2j.gameserver.handler.bbs.BbsHandlerHolder;
import org.l2j.gameserver.handler.bypass.BypassHolder;
import org.l2j.gameserver.handler.dailymissions.DailyMissionHandlerHolder;
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
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.model.entity.MonsterRace;
import org.l2j.gameserver.model.entity.olympiad.Olympiad;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.network.l2.GamePacketHandler;
import org.l2j.gameserver.scripts.Scripts;
import org.l2j.gameserver.security.HWIDBan;
import org.l2j.gameserver.tables.ClanTable;
import org.l2j.gameserver.tables.EnchantHPBonusTable;
import org.l2j.gameserver.tables.FakePlayersTable;
import org.l2j.gameserver.taskmanager.AutomaticTasks;
import org.l2j.gameserver.taskmanager.ItemsAutoDestroy;
import org.l2j.gameserver.utils.OnlineTxtGenerator;
import org.l2j.gameserver.utils.Strings;
import org.l2j.gameserver.utils.TradeHelper;
import org.l2j.mmocore.ConnectionBuilder;
import org.l2j.mmocore.ConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class GameServer {

    private static final String LOG4J_CONFIGURATION_FILE = "log4j.configurationFile";

    public static final String UPDATE_NAME = "Classic: Saviors (Zaken)";
    public static boolean DEVELOP = false;
    public static final int AUTH_SERVER_PROTOCOL = 2;

    private static Logger _log;
    private final ConnectionHandler<GameClient> connectionHandler;

    public static GameServer _instance;

    private String version;

    private final GameServerListenerList _listeners;

    private long _serverStartTimeMillis;
    private final String _licenseHost;
    private final int _onlineLimit;

    @SuppressWarnings("unchecked")
    public GameServer() throws Exception {
        _instance = this;
        _serverStartTimeMillis = System.currentTimeMillis();
        _listeners = new GameServerListenerList();

        logVersionInfo();

        // Initialize config
        ConfigParsers.parseAll();
        Config.load();

        HostInfo[] hosts = HostsConfigHolder.getInstance().getGameServerHosts();
        if (hosts.length == 0)
            throw new Exception("Server hosts list is empty!");

        // TODO Remove this, there is no need to have a lot of hosts in a single Execution. We need a solution more scalable
        final TIntSet ports = new TIntHashSet();
        for (HostInfo host : hosts) {
            if (host.getIP() != null || host.getInnerIP() != null)
                ports.add(host.getPort());
        }

        int[] portsArray = ports.toArray();

        if (portsArray.length == 0)
            throw new Exception("Server ports list is empty!");

        // Check binding address
        checkFreePorts(portsArray);
        _licenseHost = Config.EXTERNAL_HOSTNAME;
        _onlineLimit = Config.MAXIMUM_ONLINE_USERS;
        if (_onlineLimit == 0)
            throw new Exception("Server online limit is zero!");

        // Initialize database
        Class.forName(Config.DATABASE_DRIVER).getDeclaredConstructor().newInstance();
        L2DatabaseFactory.getInstance().getConnection().close();

        // TODO remove this
        UpdatesInstaller.checkAndInstall();

        IdFactory _idFactory = IdFactory.getInstance();
        if (!_idFactory.isInitialized()) {
            _log.error("Could not read object IDs from DB. Please Check Your Data.");
            throw new Exception("Could not initialize the ID factory");
        }

        CacheManager.getInstance();

        ThreadPoolManager.getInstance();

        BotCheckManager.loadBotQuestions();

        HidenItemsDAO.LoadAllHiddenItems();

        // TODO Remove This
        CustomHeroDAO.getInstance();

        HWIDBan.getInstance().load();

        ItemHandler.getInstance();

        DailyMissionHandlerHolder.getInstance();

        Scripts.getInstance();

        GeoEngine.load();

        // TODO Remove This
        Strings.reload();

        GameTimeController.getInstance();

        World.init();

        // TODO remove this
        Parsers.parseAll();

        ItemsDAO.getInstance();

        CrestCache.getInstance();

        ImagesCache.getInstance();

        CharacterDAO.getInstance();

        ClanTable.getInstance();

        EnchantHPBonusTable.getInstance();

        SpawnManager.getInstance().spawnAll();

        StaticObjectHolder.getInstance().spawnAll();

        RaidBossSpawnManager.getInstance();

        Scripts.getInstance().init();

        Announcements.getInstance();

        PlayerMessageStack.getInstance();

        if (Config.AUTODESTROY_ITEM_AFTER > 0)
            ItemsAutoDestroy.getInstance();

        MonsterRace.getInstance();

        if (Config.ENABLE_OLYMPIAD) {
            Olympiad.load();
            Hero.getInstance();
        }

        PetitionManager.getInstance();

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

        _log.info("=[Events]=========================================");
        ResidenceHolder.getInstance().callInit();
        EventHolder.getInstance().callInit();
        _log.info("==================================================");

        BoatHolder.getInstance().spawnAll();

        Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());

        _log.info("IdFactory: Free ObjectID's remaining: " + IdFactory.getInstance().size());

        MiniGameScoreManager.getInstance();

        ClanSearchManager.getInstance().load();

        BotReportManager.getInstance();

        TrainingCampManager.getInstance().init();

        Shutdown.getInstance().schedule(Config.RESTART_AT_TIME, Shutdown.RESTART);

        _log.info("GameServer Started");
        _log.info("Maximum Numbers of Connected Players: " + getOnlineLimit());

        var bindAddress = getInetSocketAddress();
        final GamePacketHandler gph = new GamePacketHandler();
        connectionHandler = ConnectionBuilder.create(bindAddress, gph, gph, gph).build();
        connectionHandler.start();

        getListeners().onStart();

        // TODO remove this
        if (Config.BUFF_STORE_ENABLED) {
            _log.info("Restoring offline buffers...");
            int count = TradeHelper.restoreOfflineBuffers();
            _log.info("Restored " + count + " offline buffers.");
        }

        // TODO remove This
        if (Config.SERVICES_OFFLINE_TRADE_RESTORE_AFTER_RESTART) {
            _log.info("Restoring offline traders...");
            int count = TradeHelper.restoreOfflineTraders();
            _log.info("Restored " + count + " offline traders.");
        }

        // TODO remove this
        FakePlayersTable.getInstance();

        // TODO remove this
        if (Config.ONLINE_GENERATOR_ENABLED)
            ThreadPoolManager.getInstance().scheduleAtFixedRate(new OnlineTxtGenerator(), 5000L, Config.ONLINE_GENERATOR_DELAY * 60 * 1000L);

        ThreadPoolManager.getInstance().execute(AuthServerCommunication.getInstance());

        _log.info("=================================================");
        String memUsage = String.valueOf(StatsUtils.getMemUsage());
        for (String line : memUsage.split("\n"))
            _log.info(line);

        _log.info("=================================================");
    }

    private void logVersionInfo() {
        try {

            var versionProperties = new Properties();
            versionProperties.load(ClassLoader.getSystemResourceAsStream("version.properties"));
            version = versionProperties.getProperty("version");
            _log.info("======================================================================");
            _log.info("Build Version: ........... {}", version);
            _log.info("Build Revision: .......... {}", versionProperties.getProperty("revision"));
            _log.info("Update: .................. {}", UPDATE_NAME);
            _log.info("Build date: .............. {}", versionProperties.getProperty("buildDate"));
            _log.info("Compiler JDK version: .... {}", versionProperties.getProperty("compilerVersion"));
            _log.info("======================================================================");
        } catch (IOException e) {
            _log.warn(e.getLocalizedMessage(), e);
        }
    }

    private InetSocketAddress getInetSocketAddress() {
        return new InetSocketAddress(Config.PORT_GAME);
    }


    public GameServerListenerList getListeners() {
        return _listeners;
    }

    public static GameServer getInstance() {
        return _instance;
    }

    public <T extends GameListener> boolean addListener(T listener) {
        return _listeners.add(listener);
    }

    public <T extends GameListener> boolean removeListener(T listener) {
        return _listeners.remove(listener);
    }

    private void checkFreePorts(int[] ports) {
        for (int port : ports) {
            while (!checkFreePort(port)) {
                _log.warn("Port " + port + " is allready binded. Please free it and restart server.");
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ie) {
                }
            }
        }
    }

    private static boolean checkFreePort(int port) {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
        } catch (Exception e) {
            return false;
        } finally {
            try {
                ss.close();
            } catch (Exception e) {
            }
        }

        return true;
    }

    private static boolean checkOpenPort(String ip, int port) {
        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 100);
        } catch (Exception e) {
            return false;
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
            }
        }

        return true;
    }

    public static void main(String[] args) {
        for (String arg : args)
            if (arg.equalsIgnoreCase("-dev"))
                DEVELOP = true;
        configureLogger();
        try {
            new GameServer();
        } catch (Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }

    public long getServerStartTime() {
        return _serverStartTimeMillis;
    }

    public String getLicenseHost() {
        return _licenseHost;
    }

    public int getOnlineLimit() {
        return _onlineLimit;
    }

    public void shutdown() {
        connectionHandler.shutdown();
    }

    private static void configureLogger() {
        String logConfigurationFile = System.getProperty(LOG4J_CONFIGURATION_FILE);
        if (logConfigurationFile == null || logConfigurationFile.isEmpty()) {
            System.setProperty(LOG4J_CONFIGURATION_FILE, "log4j.xml");
        }
        _log = LoggerFactory.getLogger(GameServer.class);
    }

    public String getVersion() {
        return version;
    }

    public class GameServerListenerList extends ListenerList<GameServer> {
        public void onStart() {
            for (Listener<GameServer> listener : getListeners())
                if (OnStartListener.class.isInstance(listener))
                    ((OnStartListener) listener).onStart();
        }

        public void onShutdown() {
            for (Listener<GameServer> listener : getListeners())
                if (OnShutdownListener.class.isInstance(listener))
                    ((OnShutdownListener) listener).onShutdown();
        }
    }
}