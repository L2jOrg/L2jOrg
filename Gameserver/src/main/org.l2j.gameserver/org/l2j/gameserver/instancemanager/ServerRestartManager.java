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
package org.l2j.gameserver.instancemanager;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.Shutdown;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * @author Gigi
 */
public class ServerRestartManager {
    static final Logger LOGGER = LoggerFactory.getLogger(ServerRestartManager.class.getName());

    private String nextRestartTime = "unknown";

    private ServerRestartManager() {
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
                restartTime.set(Calendar.SECOND, 0);

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
                ThreadPool.schedule(new ServerRestartTask(), lastDelay - (Config.SERVER_RESTART_SCHEDULE_COUNTDOWN * 1000));
                LOGGER.info("Scheduled server restart at " + lastRestart.getTime() + ".");
            }
        } catch (Exception e) {
            LOGGER.info("The scheduled server restart config is not set properly, please correct it!");
        }
    }

    public String getNextRestartTime() {
        return nextRestartTime;
    }

    public static ServerRestartManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ServerRestartManager INSTANCE = new ServerRestartManager();
    }

    class ServerRestartTask implements Runnable {
        @Override
        public void run() {
            Shutdown.getInstance().startShutdown(null, Config.SERVER_RESTART_SCHEDULE_COUNTDOWN, true);
        }
    }
}