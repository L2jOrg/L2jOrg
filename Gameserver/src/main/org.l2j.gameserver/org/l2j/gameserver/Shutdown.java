package org.l2j.gameserver;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.sql.impl.OfflineTradersTable;
import org.l2j.gameserver.datatables.ReportTable;
import org.l2j.gameserver.datatables.SchemeBufferTable;
import org.l2j.gameserver.instancemanager.*;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.model.olympiad.Olympiad;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.gs2as.OnlineStatus;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.commons.configuration.Configurator.getSettings;


/**
 * This class provides the functions for shutting down and restarting the server.<br>
 * It closes all open client connections and saves all data.
 *
 * @version $Revision: 1.2.4.5 $ $Date: 2005/03/27 15:29:09 $
 */
public class Shutdown extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(Shutdown.class);
    private static final int SIGTERM = 0;
    private static final int GM_SHUTDOWN = 1;
    private static final int GM_RESTART = 2;
    private static final int ABORT = 3;
    private static final String[] MODE_TEXT =
            {
                    "SIGTERM",
                    "shutting down",
                    "restarting",
                    "aborting"
            };
    private static Shutdown _counterInstance = null;
    private int _secondsShut;
    private int _shutdownMode;

    /**
     * Default constructor is only used internal to create the shutdown-hook instance
     */
    private Shutdown() {
        _secondsShut = -1;
        _shutdownMode = SIGTERM;
    }

    /**
     * This creates a countdown instance of Shutdown.
     *
     * @param seconds how many seconds until shutdown
     * @param restart true is the server shall restart after shutdown
     *
     * TODO Remove this Constructor. Should be only one Shutdown Instance on Runtime
     */
    public Shutdown(int seconds, boolean restart) {
        if (seconds < 0) {
            seconds = 0;
        }
        _secondsShut = seconds;
        _shutdownMode = restart ? GM_RESTART : GM_SHUTDOWN;
    }

    /**
     * Get the shutdown-hook instance the shutdown-hook instance is created by the first call of this function, but it has to be registered externally.<br>
     *
     * @return instance of Shutdown, to be used as shutdown hook
     */
    public static Shutdown getInstance() {
        return Singleton.INSTANCE;
    }

    /**
     * This function starts a shutdown count down from Telnet (Copied from Function startShutdown())
     *
     * @param seconds seconds until shutdown
     */
    private void SendServerQuit(int seconds) {
        final SystemMessage sysm = SystemMessage.getSystemMessage(SystemMessageId.THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECOND_S_PLEASE_FIND_A_SAFE_PLACE_TO_LOG_OUT);
        sysm.addInt(seconds);
        Broadcast.toAllOnlinePlayers(sysm);
    }

    /**
     * This function is called, when a new thread starts if this thread is the thread of getInstance, then this is the shutdown hook and we save all data and disconnect all clients.<br>
     * After this thread ends, the server will completely exit if this is not the thread of getInstance, then this is a countdown thread.<br>
     * We start the countdown, and when we finished it, and it was not aborted, we tell the shutdown-hook why we call exit, and then call exit when the exit status of the server is 1, startServer.sh / startServer.bat will restart the server.
     */
    @Override
    public void run() {
        if (this == getInstance()) {
            final TimeCounter tc = new TimeCounter();
            final TimeCounter tc1 = new TimeCounter();

            try {
                if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS && !Config.STORE_OFFLINE_TRADE_IN_REALTIME) {
                    OfflineTradersTable.getInstance().storeOffliners();
                    LOGGER.info("Offline Traders Table: Offline shops stored(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
                }
            } catch (Throwable t) {
                LOGGER.warn("Error saving offline shops.", t);
            }

            try {
                disconnectAllCharacters();
                LOGGER.info("All players disconnected and saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
            } catch (Throwable t) {
                // ignore
            }

            // ensure all services are stopped
            try {
                WorldTimeController.getInstance().stopTimer();
                LOGGER.info("Game Time Controller: Timer stopped(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
            } catch (Throwable t) {
                // ignore
            }

            // stop all thread pools
            try {
                ThreadPool.getInstance().shutdown();
                LOGGER.info("Thread Pool Manager: Manager has been shut down(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
            } catch (Throwable t) {
                // ignore
            }

            try {
                AuthServerCommunication.getInstance().shutdown();
                LOGGER.info("Authserver Communication: Thread interruped(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
            } catch (Throwable t) {
                // ignore
            }

            // last byebye, save all data and quit this server
            saveData();
            tc.restartCounter();

            // saveData sends messages to exit players, so shutdown selector after it
            try {
                GameServer.getInstance().getConnectionHandler().shutdown();
                LOGGER.info("Game Server: Networking has been shut down(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
            } catch (Throwable t) {
                // ignore
            }

            // commit data, last chance
            try {
                DatabaseFactory.getInstance().shutdown();
                LOGGER.info("Database Factory: Database connection has been shut down(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
            } catch (Throwable t) {
            }

            // server will quit, when this function ends.
            if (getInstance()._shutdownMode == GM_RESTART) {
                Runtime.getRuntime().halt(2);
            } else {
                Runtime.getRuntime().halt(0);
            }

            LOGGER.info("The server has been successfully shut down in " + (tc1.getEstimatedTime() / 1000) + "seconds.");
        } else {
            // gm shutdown: send warnings and then call exit to start shutdown sequence
            countdown();
            // last point where logging is operational :(
            LOGGER.warn("GM shutdown countdown is over. " + MODE_TEXT[_shutdownMode] + " NOW!");
            switch (_shutdownMode) {
                case GM_SHUTDOWN: {
                    getInstance().setMode(GM_SHUTDOWN);
                    System.exit(0);
                    break;
                }
                case GM_RESTART: {
                    getInstance().setMode(GM_RESTART);
                    System.exit(2);
                    break;
                }
                case ABORT: {
                    AuthServerCommunication.getInstance().sendPacket(new OnlineStatus(true));
                    break;
                }
            }
        }
    }

    /**
     * This functions starts a shutdown countdown.
     *
     * @param activeChar GM who issued the shutdown command
     * @param seconds    seconds until shutdown
     * @param restart    true if the server will restart after shutdown
     */
    public void startShutdown(Player activeChar, int seconds, boolean restart) {
        _shutdownMode = restart ? GM_RESTART : GM_SHUTDOWN;

        if (activeChar != null) {
            LOGGER.warn("GM: " + activeChar.getName() + "(" + activeChar.getObjectId() + ") issued shutdown command. " + MODE_TEXT[_shutdownMode] + " in " + seconds + " seconds!");
        } else {
            LOGGER.warn("Server scheduled restart issued shutdown command. Restart in " + seconds + " seconds!");
        }

        if (_shutdownMode > 0) {
            switch (seconds) {
                case 540:
                case 480:
                case 420:
                case 360:
                case 300:
                case 240:
                case 180:
                case 120:
                case 60:
                case 30:
                case 10:
                case 5:
                case 4:
                case 3:
                case 2:
                case 1: {
                    break;
                }
                default: {
                    SendServerQuit(seconds);
                }
            }
        }

        if (_counterInstance != null) {
            _counterInstance._abort();
        }

        // the main instance should only run for shutdown hook, so we start a new instance
        _counterInstance = new Shutdown(seconds, restart);
        _counterInstance.start();
    }

    /**
     * This function aborts a running countdown.
     *
     * @param activeChar GM who issued the abort command
     */
    public void abort(Player activeChar) {
        LOGGER.warn("GM: " + (activeChar != null ? activeChar.getName() + "(" + activeChar.getObjectId() + ") " : "") + "issued shutdown ABORT. " + MODE_TEXT[_shutdownMode] + " has been stopped!");
        if (_counterInstance != null) {
            _counterInstance._abort();
            Broadcast.toAllOnlinePlayers("Server aborts " + MODE_TEXT[_shutdownMode] + " and continues normal operation!", false);
        }
    }

    /**
     * Set the shutdown mode.
     *
     * @param mode what mode shall be set
     */
    private void setMode(int mode) {
        _shutdownMode = mode;
    }

    /**
     * Set shutdown mode to ABORT.
     */
    private void _abort() {
        _shutdownMode = ABORT;
    }

    /**
     * This counts the countdown and reports it to all players countdown is aborted if mode changes to ABORT.
     */
    private void countdown() {
        try {
            while (_secondsShut > 0) {
                switch (_secondsShut) {
                    case 540: {
                        SendServerQuit(540);
                        break;
                    }
                    case 480: {
                        SendServerQuit(480);
                        break;
                    }
                    case 420: {
                        SendServerQuit(420);
                        break;
                    }
                    case 360: {
                        SendServerQuit(360);
                        break;
                    }
                    case 300: {
                        SendServerQuit(300);
                        break;
                    }
                    case 240: {
                        SendServerQuit(240);
                        break;
                    }
                    case 180: {
                        SendServerQuit(180);
                        break;
                    }
                    case 120: {
                        SendServerQuit(120);
                        break;
                    }
                    case 60: {
                        AuthServerCommunication.getInstance().sendPacket(new OnlineStatus(false));
                        SendServerQuit(60);
                        break;
                    }
                    case 30: {
                        SendServerQuit(30);
                        break;
                    }
                    case 10: {
                        SendServerQuit(10);
                        break;
                    }
                    case 5: {
                        SendServerQuit(5);
                        break;
                    }
                    case 4: {
                        SendServerQuit(4);
                        break;
                    }
                    case 3: {
                        SendServerQuit(3);
                        break;
                    }
                    case 2: {
                        SendServerQuit(2);
                        break;
                    }
                    case 1: {
                        SendServerQuit(1);
                        break;
                    }
                }

                _secondsShut--;

                final int delay = 1000; // milliseconds
                Thread.sleep(delay);

                if (_shutdownMode == ABORT) {
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
        switch (_shutdownMode) {
            case SIGTERM: {
                LOGGER.info("SIGTERM received. Shutting down NOW!");
                break;
            }
            case GM_SHUTDOWN: {
                LOGGER.info("GM shutdown received. Shutting down NOW!");
                break;
            }
            case GM_RESTART: {
                LOGGER.info("GM restart received. Restarting NOW!");
                break;
            }
        }

        final TimeCounter tc = new TimeCounter();

        // Save all raidboss and GrandBoss status ^_^
        DBSpawnManager.getInstance().cleanUp();
        LOGGER.info("RaidBossSpawnManager: All raidboss info saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
        GrandBossManager.getInstance().cleanUp();
        LOGGER.info("GrandBossManager: All Grand Boss info saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
        ItemAuctionManager.getInstance().shutdown();
        LOGGER.info("Item Auction Manager: All tasks stopped(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
        Olympiad.getInstance().saveOlympiadStatus();
        LOGGER.info("Olympiad System: Data saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
        CeremonyOfChaosManager.getInstance().stopScheduler();
        LOGGER.info("CeremonyOfChaosManager: Scheduler stopped(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");

        Hero.getInstance().shutdown();
        LOGGER.info("Hero System: Data saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
        ClanTable.getInstance().shutdown();
        LOGGER.info("Clan System: Data saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");

        // Save Cursed Weapons data before closing.
        CursedWeaponsManager.getInstance().saveData();
        LOGGER.info("Cursed Weapons Manager: Data saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");

        // Save all manor data
        if (!Config.ALT_MANOR_SAVE_ALL_ACTIONS) {
            CastleManorManager.getInstance().storeMe();
            LOGGER.info("Castle Manor Manager: Data saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
        }

        // Save all global (non-player specific) Quest data that needs to persist after reboot
        QuestManager.getInstance().save();
        LOGGER.info("Quest Manager: Data saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");

        // Save all global variables data
        GlobalVariablesManager.getInstance().storeMe();
        LOGGER.info("Global Variables Manager: Variables saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");

        // Schemes save.
        SchemeBufferTable.getInstance().saveSchemes();
        LOGGER.info("SchemeBufferTable data has been saved.");

        // Save items on ground before closing
        if (getSettings(GeneralSettings.class).saveDroppedItems()) {
            ItemsOnGroundManager.getInstance().saveInDb();
            LOGGER.info("Items On Ground Manager: Data saved(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
            ItemsOnGroundManager.getInstance().cleanUp();
            LOGGER.info("Items On Ground Manager: Cleaned up(" + tc.getEstimatedTimeAndRestartCounter() + "ms).");
        }

        // Save bot reports to database
        if (Config.BOTREPORT_ENABLE) {
            ReportTable.getInstance().saveReportedCharData();
            LOGGER.info("Bot Report Table: Successfully saved reports to database!");
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // never happens :p
        }
    }

    /**
     * This disconnects all clients from the server.
     */
    private void disconnectAllCharacters() {
        for (Player player : World.getInstance().getPlayers()) {
            Disconnection.of(player).defaultSequence(true);
        }
    }

    public long getSecondsToRestart() {
        return _secondsShut;
    }

    /**
     * A simple class used to track down the estimated time of method executions.<br>
     * Once this class is created, it saves the start time, and when you want to get the estimated time, use the getEstimatedTime() method.
     */
    private static final class TimeCounter {
        private long _startTime;

        protected TimeCounter() {
            restartCounter();
        }

        protected void restartCounter() {
            _startTime = System.currentTimeMillis();
        }

        protected long getEstimatedTimeAndRestartCounter() {
            final long toReturn = System.currentTimeMillis() - _startTime;
            restartCounter();
            return toReturn;
        }

        protected long getEstimatedTime() {
            return System.currentTimeMillis() - _startTime;
        }
    }

    private static class Singleton {
        private static final Shutdown INSTANCE = new Shutdown();
    }
}
