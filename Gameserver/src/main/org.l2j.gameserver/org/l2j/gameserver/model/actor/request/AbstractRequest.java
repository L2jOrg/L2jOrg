/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.model.actor.request;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.nonNull;

/**
 * @author UnAfraid
 */
public abstract class AbstractRequest {
    private volatile long timestamp = 0;
    private volatile boolean isProcessing;
    protected final Player player;
    private ScheduledFuture<?> timeOutTask;

    public AbstractRequest(Player player) {
        Objects.requireNonNull(player);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void scheduleTimeout(long delay) {
        timeOutTask = ThreadPool.schedule(this::onTimeout, delay);
    }

    public void cancelTimeout() {
        if(nonNull(timeOutTask)) {
            timeOutTask.cancel(false);
        }
    }

    public boolean isProcessing() {
        return isProcessing;
    }

    public void setProcessing(boolean isProcessing) {
        this.isProcessing = isProcessing;
    }

    public boolean canWorkWith(AbstractRequest request) {
        return true;
    }

    public boolean isItemRequest() {
        return false;
    }

    public abstract boolean isUsingItem(int objectId);

    public void onTimeout() {

    }
}
