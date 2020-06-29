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

import org.l2j.commons.database.DatabaseAccess;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.sql.impl.OfflineTradersTable;
import org.l2j.gameserver.datatables.ReportTable;
import org.l2j.gameserver.datatables.SchemeBufferTable;
import org.l2j.gameserver.engine.autoplay.AutoPlayEngine;
import org.l2j.gameserver.engine.olympiad.OlympiadEngine;
import org.l2j.gameserver.instancemanager.*;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.gs2as.OnlineStatus;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.WorldTimeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * This class provides the functions for shutting down and restarting the server.<br>
 * It closes all open client connections and saves all data.
 *
 * @author JoeAlisson
 */
public class Shutdown extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(Shutdown.class);

    private static Shutdown counterInstance = null;
    private int secondsShut;
    private ShutdownMode shutdownMode;

    /**
     * Default constructor is only used internal to create the shutdown-hook instance
     */
    private Shutdown() {
        secondsShut = -1;
        shutdownMode = ShutdownMode.SIGTERM;
    }

    /**
     * This creates a countdown instance of Shutdown.
     *
     * @param seconds how many seconds until shutdown
     * @param restart true is the server shall restart after shutdown
     *
     */
    public Shutdown(int seconds, boolean restart) {
        if (seconds < 0) {
            seconds = 0;
        }
        secondsShut = seconds;
        shutdownMode = restart ? ShutdownMode.GM_RESTART : ShutdownMode.GM_SHUTDOWN;
    }

    /**
     * This function is called, when a new thread starts if this thread is the thread of getInstance, then this is the shutdown hook and we save all data and disconnect all clients.<br>
     * After this thread ends, the server will completely exit.
     * If this is not the thread of getInstance, then this is a countdown thread.<br>
     * We start the countdown, and when we finished it, and it was not aborted, we tell the shutdown-hook why we call exit, and then call exit when the exit status of the server is 1, startServer.sh / startServer.bat will restart the server.
     */
    @Override
    public void run() {
        if (this == getInstance()) {
            saveDataAndReleaseResources();
        } else {
            // gm shutdown: send warnings and then call exit to start shutdown sequence
            countdown();
            switch (shutdownMode) {
                case GM_SHUTDOWN -> {
                    getInstance().setMode(ShutdownMode.GM_SHUTDOWN);
                    System.exit(0);
                }
                case GM_RESTART -> {
                    getInstance().setMode(ShutdownMode.GM_RESTART);
                    System.exit(2);
                }
                case ABORT -> AuthServerCommunication.getInstance().sendPacket(new OnlineStatus(true));
            }
        }
    }

    private void saveDataAndReleaseResources() {
        switch (shutdownMode) {
            case SIGTERM -> LOGGER.info("SIGTERM received. Shutting down NOW!");
            case GM_SHUTDOWN -> LOGGER.info("GM shutdown received. Shutting down NOW!");
            case GM_RESTART -> LOGGER.info("GM restart received. Restarting NOW!");
        }

        // last byebye, save all data and quit this server
        saveData();

        // saveData sends messages to exit players, so shutdown selector after it
        try {
            GameServer.getInstance().getConnectionHandler().shutdown();
            LOGGER.info("Game Server: Networking has been shut down.");

            AutoPlayEngine.getInstance().shutdown();
            LOGGER.info("Auto Play Engine has been shut down.");

            WorldTimeController.getInstance().stopTimer();
            LOGGER.info("Game Time Controller: Timer stopped.");

            AuthServerCommunication.getInstance().shutdown();
            LOGGER.info("Auth server Communication: Thread interrupted.");

            DatabaseAccess.shutdown();
            LOGGER.info("Database connection has been shut down.");

            ThreadPool.getInstance().shutdown();
            LOGGER.info("Thread Pool Manager: Manager has been shut down.");
        } catch (Throwable t) {
            LOGGER.warn(t.getMessage(), t);
        }

        LOGGER.info("The server has been successfully shut down .");

        // server will quit, when this function ends.
        if (getInstance().shutdownMode == ShutdownMode.GM_RESTART) {
            Runtime.getRuntime().halt(2);
        } else {
            Runtime.getRuntime().halt(0);
        }

    }

    /**
     * This functions starts a shutdown countdown.
     *
     * @param player GM who issued the shutdown command
     * @param seconds    seconds until shutdown
     * @param restart    true if the server will restart after shutdown
     */
    public void startShutdown(Player player, int seconds, boolean restart) {
        shutdownMode = restart ? ShutdownMode.GM_RESTART : ShutdownMode.GM_SHUTDOWN;

        if (player != null) {
            LOGGER.warn("GM: {} issued shutdown command. {} in {} seconds!", player, shutdownMode.description, seconds);
        } else {
            LOGGER.warn("Server scheduled restart issued shutdown command. Restart in {} seconds!", seconds);
        }

        if (counterInstance != null) {
            counterInstance._abort();
        }

        // the main instance should only run for shutdown hook, so we start a new instance
        counterInstance = new Shutdown(seconds, restart);
        counterInstance.start();
    }

    /**
     * This function starts a shutdown count down from Telnet (Copied from Function startShutdown())
     *
     * @param seconds seconds until shutdown
     */
    private void SendServerQuit(int seconds) {
        Broadcast.toAllOnlinePlayers(getSystemMessage(SystemMessageId.THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECOND_S_PLEASE_FIND_A_SAFE_PLACE_TO_LOG_OUT).addInt(seconds));
    }

    /**
     * This function aborts a running countdown.
     *
     * @param player GM who issued the abort command
     */
    public void abort(Player player) {
        LOGGER.warn("GM: {} issued shutdown ABORT. {} has been stopped!", player, shutdownMode.description);
        Util.doIfNonNull(counterInstance, counter -> {
            counter._abort();
            Broadcast.toAllOnlinePlayers("Server aborts " + shutdownMode.description + " and continues normal operation!", false);
        });
    }

    /**
     * Set the shutdown mode.
     *
     * @param mode what mode shall be set
     */
    private void setMode(ShutdownMode mode) {
        shutdownMode = mode;
    }

    /**
     * Set shutdown mode to ABORT.
     */
    private void _abort() {
        shutdownMode = ShutdownMode.ABORT;
    }

    /**
     * This counts the countdown and reports it to all players countdown is aborted if mode changes to ABORT.
     */
    private void countdown() {
        try {
            var countDelay = 1000;
            while (secondsShut > 0) {
                switch (secondsShut) {
                    case 540, 480, 420, 360, 300, 240, 180, 120, 30, 10, 5, 1 -> SendServerQuit(secondsShut);
                    case 60 -> {
                        AuthServerCommunication.getInstance().sendPacket(new OnlineStatus(false));
                        SendServerQuit(60);
                    }
                }

                secondsShut--;
                Thread.sleep(countDelay);

                if (shutdownMode == ShutdownMode.ABORT) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            // this will never happen
        }
    }

    /**
     * This sends a last byebye, disconnects all players and saves data.
     */
    private void saveData() {
        try {
            if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS && !Config.STORE_OFFLINE_TRADE_IN_REALTIME) {
                OfflineTradersTable.getInstance().storeOffliners();
            }
        } catch (Throwable t) {
            LOGGER.warn("Error saving offline shops.", t);
        }

        disconnectAllCharacters();
        LOGGER.info("All players disconnected and saved.");

        // Save all raidboss and GrandBoss status ^_^
        DBSpawnManager.getInstance().cleanUp();
        LOGGER.info("RaidBossSpawnManager: All raidboss info saved.");

        GrandBossManager.getInstance().cleanUp();
        LOGGER.info("GrandBossManager: All Grand Boss info saved.");

        ItemAuctionManager.getInstance().shutdown();
        LOGGER.info("Item Auction Manager: All tasks stopped.");

        OlympiadEngine.getInstance().saveOlympiadStatus();
        LOGGER.info("Olympiad System: Data saved.");

        Hero.getInstance().shutdown();
        LOGGER.info("Hero System: Data saved.");

        ClanTable.getInstance().shutdown();
        LOGGER.info("Clan System: Data saved.");

        // Save all manor data
        CastleManorManager.getInstance().storeMe();
        LOGGER.info("Castle Manor Manager: Data saved.");

        // Save all global (non-player specific) Quest data that needs to persist after reboot
        QuestManager.getInstance().save();
        LOGGER.info("Quest Manager: Data saved.");

        // Save all global variables data
        GlobalVariablesManager.getInstance().storeMe();
        LOGGER.info("Global Variables Manager: Variables saved.");

        // Schemes save.
        SchemeBufferTable.getInstance().saveSchemes();
        LOGGER.info("SchemeBufferTable data has been saved.");

        // Save items on ground before closing
        if (getSettings(GeneralSettings.class).saveDroppedItems()) {
            ItemsOnGroundManager.getInstance().saveInDb();
            LOGGER.info("Items On Ground Manager: Data saved.");
            ItemsOnGroundManager.getInstance().cleanUp();
            LOGGER.info("Items On Ground Manager: Cleaned up.");
        }

        // Save bot reports to database
        if (Config.BOTREPORT_ENABLE) {
            ReportTable.getInstance().saveReportedCharData();
            LOGGER.info("Bot Report Table: Successfully saved reports to database!");
        }
    }

    /**
     * This disconnects all clients from the server.
     */
    private void disconnectAllCharacters() {
        World.getInstance().forEachPlayer(player -> Disconnection.of(player).defaultSequence(true));
    }

    private enum  ShutdownMode {
        SIGTERM("SIGTERM"),
        GM_SHUTDOWN("SHUTTING DOWN"),
        GM_RESTART("RESTARTING"),
        ABORT("ABORTING");

        private final String description;

        ShutdownMode(String description) {
            this.description = description;
        }
    }

    /**
     * Get the shutdown-hook instance the shutdown-hook instance is created by the first call of this function, but it has to be registered externally.<br>
     *
     * @return instance of Shutdown, to be used as shutdown hook
     */
    public static Shutdown getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final Shutdown INSTANCE = new Shutdown();
    }
}
