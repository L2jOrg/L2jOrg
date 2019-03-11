package org.l2j.gameserver.instancemanager;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.Shutdown;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 * @author Gigi
 */
public class ServerRestartManager {
    static final Logger LOGGER = Logger.getLogger(ServerRestartManager.class.getName());

    private String nextRestartTime = "unknown";

    protected ServerRestartManager() {
        try {
            final Calendar currentTime = Calendar.getInstance();
            final Calendar restartTime = Calendar.getInstance();
            Calendar lastRestart = null;
            long delay = 0;
            long lastDelay = 0;

            for (String scheduledTime : Config.SERVER_RESTART_SCHEDULE) {
                final String[] splitTime = scheduledTime.trim().split(":");
                restartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTime[0]));
                restartTime.set(Calendar.MINUTE, Integer.parseInt(splitTime[1]));
                restartTime.set(Calendar.SECOND, 00);

                if (restartTime.getTimeInMillis() < currentTime.getTimeInMillis()) {
                    restartTime.add(Calendar.DAY_OF_MONTH, 1);
                }

                delay = restartTime.getTimeInMillis() - currentTime.getTimeInMillis();
                if (lastDelay == 0) {
                    lastDelay = delay;
                    lastRestart = restartTime;
                }
                if (delay < lastDelay) {
                    lastDelay = delay;
                    lastRestart = restartTime;
                }
            }

            if (lastRestart != null) {
                nextRestartTime = new SimpleDateFormat("HH:mm").format(lastRestart.getTime());
                ThreadPoolManager.getInstance().schedule(new ServerRestartTask(), lastDelay - (Config.SERVER_RESTART_SCHEDULE_COUNTDOWN * 1000));
                LOGGER.info("Scheduled server restart at " + lastRestart.getTime() + ".");
            }
        } catch (Exception e) {
            LOGGER.info("The scheduled server restart config is not set properly, please correct it!");
        }
    }

    public static ServerRestartManager getInstance() {
        return SingletonHolder._instance;
    }

    public String getNextRestartTime() {
        return nextRestartTime;
    }

    private static class SingletonHolder {
        protected static final ServerRestartManager _instance = new ServerRestartManager();
    }

    class ServerRestartTask implements Runnable {
        @Override
        public void run() {
            Shutdown.getInstance().startShutdown(null, Config.SERVER_RESTART_SCHEDULE_COUNTDOWN, true);
        }
    }
}