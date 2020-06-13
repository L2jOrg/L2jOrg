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
package org.l2j.gameserver.taskmanager;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.settings.GeneralSettings;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.util.Util.falseIfNullOrElse;

/**
 * @author JoeAlisson
 */
public class SaveTaskManager {

    private final Map<Player, Long> playerSaveStamp = Collections.synchronizedMap(new WeakHashMap<>());
    private ScheduledFuture<?> scheduledTask;

    private SaveTaskManager() {
    }

    public void registerPlayer(Player player) {
        var scheduleTime = getSettings(GeneralSettings.class).autoSavePlayerTime();
        if(playerSaveStamp.isEmpty() && (isNull(scheduledTask) || scheduledTask.isDone())) {
            scheduledTask = ThreadPool.scheduleAtFixedDelay(this::saveTask, scheduleTime, scheduleTime, TimeUnit.MINUTES);
        }
        playerSaveStamp.put(player, nextSave(scheduleTime));
    }

    protected long nextSave(int scheduleTime) {
        return System.currentTimeMillis() + Duration.ofMinutes(scheduleTime).toMillis();
    }

    private void saveTask() {
        final var now = System.currentTimeMillis();
        final var nextSave = nextSave(getSettings(GeneralSettings.class).autoSavePlayerTime());

        synchronized (playerSaveStamp) {
            playerSaveStamp.entrySet().stream()
                    .filter(entry -> falseIfNullOrElse(entry, e -> e.getValue() < now))
                    .forEach(entry -> save(nextSave, entry));
        }
    }

    private void save(long nextSave, Map.Entry<Player, Long> entry) {
        entry.getKey().storeMe();
        entry.setValue(nextSave);
    }

    public void remove(Player player) {
        playerSaveStamp.remove(player);
        if(playerSaveStamp.isEmpty() && nonNull(scheduledTask) && !scheduledTask.isDone()) {
            scheduledTask.cancel(false);
            scheduledTask = null;
        }
    }

    public static SaveTaskManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        private static final SaveTaskManager INSTANCE = new SaveTaskManager();
    }
}
