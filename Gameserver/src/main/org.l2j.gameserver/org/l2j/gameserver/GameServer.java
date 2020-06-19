/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver;

import io.github.joealisson.mmocore.ConnectionBuilder;
import io.github.joealisson.mmocore.ConnectionHandler;
import org.l2j.commons.cache.CacheFactory;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.DeadLockDetector;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.database.RankManager;
import org.l2j.gameserver.data.database.announce.manager.AnnouncementsManager;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.sql.impl.*;
import org.l2j.gameserver.data.xml.*;
import org.l2j.gameserver.data.xml.impl.*;
import org.l2j.gameserver.datatables.ReportTable;
import org.l2j.gameserver.datatables.SchemeBufferTable;
import org.l2j.gameserver.engine.costume.CostumeEngine;
import org.l2j.gameserver.engine.elemental.ElementalSpiritEngine;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.engine.mission.MissionEngine;
import org.l2j.gameserver.engine.scripting.ScriptEngineManager;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.engine.upgrade.UpgradeItemEngine;
import org.l2j.gameserver.engine.vip.VipEngine;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.instancemanager.*;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.model.olympiad.Olympiad;
import org.l2j.gameserver.model.votereward.VoteSystem;
import org.l2j.gameserver.network.ClientPacketHandler;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.taskmanager.TaskManager;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.world.World;
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
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * @author JoeAlisson
 */
public class GameServer {

    private static final String LOG4J_CONFIGURATION_FILE = "log4j.configurationFile";

    private static Logger LOGGER;
    private static GameServer INSTANCE;
    public static String fullVersion;
    private final ConnectionHandler<GameClient> connectionHandler;

    public GameServer() throws Exception {
        final var serverLoadStart = Instant.now();

        printSection("Identity Factory");
        if (!IdFactory.getInstance().isInitialized()) {
            LOGGER.error("Could not read object IDs from database. Please check your configuration.");
            throw new Exception("Could not initialize the Identity factory!");
        }

        printSection("World");
        World.init();

        printSection("Skills");
        SkillEngine.init();

        printSection("Items");
        ItemEngine.init();

        printSection("NPCs");
        NpcData.init();

        printSection("Castle Data");
        CastleManager.init();

        printSection("Class Categories");
        CategoryManager.init();

        printSection("Scripts");
        ScriptEngineManager.getInstance().executeScriptLoader();

        printSection("Spawns");
        SpawnsData.init();
        DBSpawnManager.init();
        ThreadPool.executeForked(SpawnsData.getInstance()::spawnAll);

        printSection("Server Data");
        GlobalVariablesManager.init();
        ActionManager.init();
        BuyListData.init();
        MultisellData.getInstance();
        RecipeData.getInstance();
        ArmorSetsData.getInstance();
        FishingData.getInstance();
        HennaData.getInstance();
        ShuttleData.getInstance();
        GraciaSeedsManager.getInstance();

        printSection("Features");
        AnnouncementsManager.init();
        SecondaryAuthManager.init();
        ClanRewardManager.init();
        MissionEngine.init();
        VipEngine.init();
        ElementalSpiritEngine.init();
        TeleportEngine.init();
        PrimeShopData.getInstance();
        LCoinShopData.getInstance();
        CommissionManager.getInstance();
        LuckyGameData.getInstance();
        AttendanceRewardData.getInstance();
        CostumeEngine.init();
        UpgradeItemEngine.init();
        CombinationItemsManager.init();
        RankManager.init();
        BeautyShopData.getInstance();
        ExtendDropData.getInstance();
        ItemAuctionManager.getInstance();
        SchemeBufferTable.getInstance();
        GrandBossManager.getInstance();

        printSection("Characters");
        ClassListData.getInstance();
        InitialEquipmentData.getInstance();
        InitialShortcutData.getInstance();
        LevelData.init();
        KarmaData.getInstance();
        HitConditionBonusData.getInstance();
        PlayerTemplateData.getInstance();
        PlayerNameTable.getInstance();
        AdminData.getInstance();
        PetDataTable.getInstance();
        CubicData.getInstance();
        PlayerSummonTable.getInstance().init();
        MentorManager.getInstance();

        printSection("Clans");
        ClanTable.init();
        ResidenceFunctionsData.getInstance();
        ClanHallManager.init();
        ClanHallAuctionManager.getInstance();
        ClanEntryManager.getInstance();
        WalkingManager.getInstance();
        StaticObjectData.getInstance();

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
        TransformData.getInstance();
        ReportTable.getInstance();
        if (Config.SELLBUFF_ENABLED) {
            SellBuffsManager.getInstance();
        }

        printSection("Event Engine");
        EventEngineData.getInstance();
        VoteSystem.initialize();

        printSection("Siege");
        SiegeManager.getInstance().getSieges();
        CastleManager.getInstance().activateInstances();
        SiegeScheduleData.getInstance();
        CastleManorManager.getInstance();
        SiegeGuardManager.getInstance();
        QuestManager.getInstance().report();

        var generalSettings = getSettings(GeneralSettings.class);
        if (generalSettings.saveDroppedItems()) {
            ItemsOnGroundManager.init();
        }

        if (generalSettings.autoDestroyItemTime() > 0 || generalSettings.autoDestroyHerbTime() > 0) {
            ItemsAutoDestroy.getInstance();
        }

        TaskManager.getInstance();

        AntiFeedManager.getInstance().registerEvent(AntiFeedManager.GAME_ID);

        if (generalSettings.allowMail()) {
            MailEngine.init();
        }

        PunishmentManager.getInstance();

        Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());

        LOGGER.info("IdFactory: Free ObjectID's remaining: " + IdFactory.getInstance().size());

        if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS) {
            OfflineTradersTable.getInstance().restoreOfflineTraders();
        }

        var serverSettings = getSettings(ServerSettings.class);
        if (serverSettings.scheduleRestart()) {
            ServerRestartManager.getInstance();
        }

        LOGGER.info("Maximum number of connected players is configured to {}", serverSettings.maximumOnlineUsers());
        LOGGER.info("Server loaded in {} seconds", serverLoadStart.until(Instant.now(), ChronoUnit.SECONDS));

        printSection("Setting All characters to offline status!");
        getDAO(PlayerDAO.class).setAllCharactersOffline();

        connectionHandler = ConnectionBuilder.create(new InetSocketAddress(serverSettings.port()), GameClient::new, new ClientPacketHandler(), ThreadPool::execute).build();
        connectionHandler.start();
    }

    public static void main(String[] args) throws Exception {
        configureLogger();
        configureCache();
        logVersionInfo();
        configureDatabase();
        configureNetworkPackets();

        printSection("Server Configuration");
        Config.load(); // TODO remove this

        printSection("Scripting Engine");
        ScriptEngineManager.init();

        var settings = getSettings(ServerSettings.class);
        ThreadPool.init(settings.threadPoolSize() ,settings.scheduledPoolSize());

        INSTANCE = new GameServer();

        ThreadPool.execute(AuthServerCommunication.getInstance());

        if (settings.useDeadLockDetector()) {
            DeadLockDetector deadLockDetector = new DeadLockDetector(Duration.ofSeconds(settings.deadLockDetectorInterval()), () -> {
                if (settings.restartOnDeadLock()) {
                    Broadcast.toAllOnlinePlayers("Server has stability issues - restarting now.");
                    Shutdown.getInstance().startShutdown(null, 60, true);
                }
            });
            deadLockDetector.start();
        }
    }

    private static void configureCache() {
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
        printSection("Datasource Settings");
        System.setProperty("hikaricp.configurationFile", "config/database.properties");
        if (!DatabaseAccess.initialize()) {
            throw new Exception("Database Access could not be initialized");
        }
    }

    private static void logVersionInfo() {
        try( var versionFile = ClassLoader.getSystemResourceAsStream("version.properties")) {
            if (nonNull(versionFile)) {
                var versionProperties = new Properties();
                versionProperties.load(versionFile);
                var version = versionProperties.getProperty("version");
                var updateName = versionProperties.getProperty("update");

                fullVersion = String.format("%s: %s-%s (%s)", updateName, version, versionProperties.getProperty("revision"), versionProperties.getProperty("buildDate"));
                var protocol = getSettings(ServerSettings.class).acceptedProtocols();
                printSection("Server Info Version");
                LOGGER.info("Update: .................. {}", updateName);
                LOGGER.info("Protocol: ................ {}", protocol);
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
        LOGGER.info("{}=[ {} ]", "-".repeat(64 - s.length()), s);
    }

    public static GameServer getInstance() {
        return INSTANCE;
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
