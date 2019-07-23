package org.l2j.gameserver;

import io.github.joealisson.mmocore.ConnectionBuilder;
import io.github.joealisson.mmocore.ConnectionHandler;
import org.l2j.commons.cache.CacheFactory;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.commons.util.DeadLockDetector;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.database.dao.CharacterDAO;
import org.l2j.gameserver.data.elemental.ElementalSpiritManager;
import org.l2j.gameserver.data.sql.impl.*;
import org.l2j.gameserver.data.xml.impl.*;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.datatables.ReportTable;
import org.l2j.gameserver.datatables.SchemeBufferTable;
import org.l2j.gameserver.geoengine.GeoEngine;
import org.l2j.gameserver.handler.ConditionHandler;
import org.l2j.gameserver.handler.DailyMissionHandler;
import org.l2j.gameserver.handler.EffectHandler;
import org.l2j.gameserver.handler.SkillConditionHandler;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.instancemanager.*;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.model.olympiad.Olympiad;
import org.l2j.gameserver.model.votereward.VoteSystem;
import org.l2j.gameserver.network.ClientPacketHandler;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.scripting.ScriptEngineManager;
import org.l2j.gameserver.taskmanager.TaskManager;
import org.l2j.gameserver.util.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.isNullOrEmpty;

public class GameServer {

    public static final String UPDATE_NAME = "Classic - Secret of Empire";
    private static final String LOG4J_CONFIGURATION_FILE = "log4j.configurationFile";

    private static Logger LOGGER;
    private static GameServer INSTANCE;
    public static String fullVersion;
    private final DeadLockDetector _deadDetectThread;
    private final ConnectionHandler<GameClient> connectionHandler;

    public GameServer() throws Exception {
        final var serverLoadStart = Instant.now();

        printSection("Identity Factory");
        if (!IdFactory.getInstance().isInitialized()) {
            LOGGER.error("Could not read object IDs from database. Please check your configuration.");
            throw new Exception("Could not initialize the ID factory!");
        }

        printSection("Lineage II World");
        GameTimeController.init(); // start game time control early
        World.getInstance();
        MapRegionManager.getInstance();
        ZoneManager.getInstance();
        DoorData.getInstance();
        FenceData.getInstance();
        AnnouncementsTable.getInstance();
        GlobalVariablesManager.getInstance();

        printSection("Server Data");
        ActionData.getInstance();
        CategoryData.getInstance();
        SecondaryAuthData.getInstance();
        CombinationItemsData.getInstance();
        ClanRewardData.getInstance();
        DailyMissionHandler.getInstance().executeScript();
        DailyMissionData.getInstance();
        VipData.getInstance();
        ElementalSpiritManager.init();

        printSection("Skills");
        SkillConditionHandler.getInstance().executeScript();
        EffectHandler.getInstance().executeScript();
        EnchantSkillGroupsData.getInstance();
        SkillTreesData.getInstance();
        SkillData.getInstance();
        PetSkillData.getInstance();

        printSection("Items");
        ConditionHandler.getInstance().executeScript();
        ItemTable.getInstance();
        EnchantItemGroupsData.getInstance();
        EnchantItemData.getInstance();
        EnchantItemOptionsData.getInstance();
        ItemCrystallizationData.getInstance();
        OptionData.getInstance();
        VariationData.getInstance();
        EnsoulData.getInstance();
        EnchantItemHPBonusData.getInstance();
        BuyListData.getInstance();
        MultisellData.getInstance();
        RecipeData.getInstance();
        ArmorSetsData.getInstance();
        FishingData.getInstance();
        HennaData.getInstance();
        PrimeShopData.getInstance();
        CommissionManager.getInstance();
        LuckyGameData.getInstance();
        AttendanceRewardData.getInstance();

        printSection("Characters");
        ClassListData.getInstance();
        InitialEquipmentData.getInstance();
        InitialShortcutData.getInstance();
        ExperienceData.getInstance();
        PlayerXpPercentLostData.getInstance();
        KarmaData.getInstance();
        HitConditionBonusData.getInstance();
        PlayerTemplateData.getInstance();
        PlayerNameTable.getInstance();
        AdminData.getInstance();
        PetDataTable.getInstance();
        CubicData.getInstance();
        CharSummonTable.getInstance().init();
        BeautyShopData.getInstance();
        MentorManager.getInstance();

        printSection("Clans");
        ClanTable.getInstance();
        ResidenceFunctionsData.getInstance();
        ClanHallData.getInstance();
        ClanHallAuctionManager.getInstance();
        ClanEntryManager.getInstance();

        printSection("Geodata");
        long geodataMemory = getUsedMemoryMB();
        GeoEngine.getInstance();
        geodataMemory = getUsedMemoryMB() - geodataMemory;
        if (geodataMemory < 0) {
            geodataMemory = 0;
        }

        printSection("NPCs");
        NpcData.getInstance();
        ExtendDropData.getInstance();
        SpawnsData.getInstance();
        WalkingManager.getInstance();
        StaticObjectData.getInstance();
        ItemAuctionManager.getInstance();
        CastleManager.getInstance().loadInstances();
        SchemeBufferTable.getInstance();
        GrandBossManager.getInstance();

        printSection("Instance");
        InstanceManager.getInstance();

        printSection("Olympiad");
        Olympiad.getInstance();
        Hero.getInstance();

        // Call to load caches
        printSection("Cache");
        HtmCache.getInstance();
        CrestTable.getInstance();
        TeleportersData.getInstance();
        CursedWeaponsManager.getInstance();
        TransformData.getInstance();
        ReportTable.getInstance();
        if (Config.SELLBUFF_ENABLED) {
            SellBuffsManager.getInstance();
        }

        printSection("Scripts");
        AirShipManager.getInstance();
        ShuttleData.getInstance();
        GraciaSeedsManager.getInstance();

        try {
            LOGGER.info("Loading server scripts:");
            ScriptEngineManager.getInstance().executeScriptInitList();
        } catch (Exception e) {
            LOGGER.warn("Failed to execute script list!", e);
        }

        DBSpawnManager.getInstance();
        SpawnsData.getInstance().init();

        printSection("Event Engine");
        EventEngineData.getInstance();
        VoteSystem.initialize();

        printSection("Siege");
        SiegeManager.getInstance().getSieges();
        CastleManager.getInstance().activateInstances();
        // No fortresses
        // FortDataManager.getInstance().loadInstances();
        // FortDataManager.getInstance().activateInstances();
        // FortSiegeManager.getInstance();
        SiegeScheduleData.getInstance();

        CastleManorManager.getInstance();
        SiegeGuardManager.getInstance();
        QuestManager.getInstance().report();

        if (Config.SAVE_DROPPED_ITEM) {
            ItemsOnGroundManager.getInstance();
        }

        if ((Config.AUTODESTROY_ITEM_AFTER > 0) || (Config.HERB_AUTO_DESTROY_TIME > 0)) {
            ItemsAutoDestroy.getInstance();
        }

        TaskManager.getInstance();

        AntiFeedManager.getInstance().registerEvent(AntiFeedManager.GAME_ID);

        if (Config.ALLOW_MAIL) {
            MailManager.getInstance();
        }

        PunishmentManager.getInstance();

        Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());

        LOGGER.info("IdFactory: Free ObjectID's remaining: " + IdFactory.getInstance().size());

        if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS) {
            OfflineTradersTable.getInstance().restoreOfflineTraders();
        }

        if (Config.SERVER_RESTART_SCHEDULE_ENABLED) {
            ServerRestartManager.getInstance();
        }

        if (Config.DEADLOCK_DETECTOR) {
            _deadDetectThread = new DeadLockDetector(Duration.ofSeconds(Config.DEADLOCK_CHECK_INTERVAL), () ->
            {
                if (Config.RESTART_ON_DEADLOCK) {
                    Broadcast.toAllOnlinePlayers("Server has stability issues - restarting now.");
                    Shutdown.getInstance().startShutdown(null, 60, true);
                }
            });
            _deadDetectThread.setDaemon(true);
            _deadDetectThread.start();
        } else {
            _deadDetectThread = null;
        }
        System.gc();
        final long totalMem = Runtime.getRuntime().maxMemory() / 1048576;
        LOGGER.info("Started, using {} of {} MB total memory", getUsedMemoryMB(), totalMem);
        LOGGER.info("Geodata use {} MB of memory", geodataMemory);
        LOGGER.info("Maximum number of connected players is {}", Config.MAXIMUM_ONLINE_USERS);
        LOGGER.info("Server loaded in {} seconds", serverLoadStart.until(Instant.now(), ChronoUnit.SECONDS));

        printSection("Setting All characters to offline status!");
        getDAO(CharacterDAO.class).setAllCharactersOffline();

        connectionHandler = ConnectionBuilder.create(new InetSocketAddress(Config.PORT_GAME), GameClient::new, new ClientPacketHandler(), ThreadPoolManager::execute).build();
        connectionHandler.start();
    }

    public static void main(String[] args) throws Exception {
        configureLogger();
        configureCaches();
        logVersionInfo();
        configureDatabase();
        configureNetworkPackets();

        printSection("Server Configuration");
        Config.load(); // TODO remove this

        printSection("Scripting Engine");
        ScriptEngineManager.init();

        ThreadPoolManager.getInstance().schedulePurge();
        INSTANCE = new GameServer();
        ThreadPoolManager.execute(AuthServerCommunication.getInstance());
    }

    private static void configureCaches() {
        CacheFactory.getInstance().initialize("config/ehcache.xml");
    }

    private static void configureNetworkPackets() {
        System.setProperty("async-mmocore.configurationFile", "config/async-mmocore.properties");
    }

    private static void configureLogger() {
        var logConfigurationFile = System.getProperty(LOG4J_CONFIGURATION_FILE);
        if (isNullOrEmpty(logConfigurationFile)) {
            System.setProperty(LOG4J_CONFIGURATION_FILE, "log4j.xml");
        }
        LOGGER = LoggerFactory.getLogger(GameServer.class);
    }

    private static void configureDatabase() throws Exception {
        printSection("Database Connection");
        System.setProperty("hikaricp.configurationFile", "config/database.properties");
        if (!DatabaseAccess.initialize()) {
            throw new Exception("Database Access could not be initialized");
        }
    }

    private static void logVersionInfo() {
        try {
            var versionFile = ClassLoader.getSystemResourceAsStream("version.properties");
            if (nonNull(versionFile)) {

                var versionProperties = new Properties();
                versionProperties.load(versionFile);
                var version = versionProperties.getProperty("version");
                fullVersion = String.format("%s: %s-%s (%s)", UPDATE_NAME, version, versionProperties.getProperty("revision"), versionProperties.getProperty("buildDate"));
                printSection("Server Info Version");
                LOGGER.info("Update: .................. {}", UPDATE_NAME);
                LOGGER.info("Build Version: ........... {}", version);
                LOGGER.info("Build Revision: .......... {}", versionProperties.getProperty("revision"));
                LOGGER.info("Build date: .............. {}", versionProperties.getProperty("buildDate"));
                LOGGER.info("Compiler JDK version: .... {}", versionProperties.getProperty("compilerVersion"));

            }
        } catch (IOException e) {
            LOGGER.warn(e.getLocalizedMessage(), e);
        }
    }

    private static void printSection(String s) {
        String format = "{}=[ {} ]";
        var len = s.length();
        LOGGER.info(format, "-".repeat(64 - len), s);
    }

    public static GameServer getInstance() {
        return INSTANCE;
    }

    public long getUsedMemoryMB() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
    }

    public ConnectionHandler<GameClient> getConnectionHandler() {
        return connectionHandler;
    }

    public String getUptime() {
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
        final long days = TimeUnit.MILLISECONDS.toDays(uptime);
        uptime -= TimeUnit.DAYS.toMillis(days);
        final long hours = TimeUnit.MILLISECONDS.toHours(uptime);
        uptime -= TimeUnit.HOURS.toMillis(hours);
        return String.format("%d Days, %d Hours, %d Minutes", days, hours, TimeUnit.MILLISECONDS.toMinutes(uptime));
    }
}
