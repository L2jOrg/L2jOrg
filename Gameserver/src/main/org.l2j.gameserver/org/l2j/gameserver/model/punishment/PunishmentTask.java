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
package org.l2j.gameserver.model.punishment;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.database.dao.PunishmentDAO;
import org.l2j.gameserver.data.database.data.PunishmentData;
import org.l2j.gameserver.handler.IPunishmentHandler;
import org.l2j.gameserver.handler.PunishmentHandler;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;

import static org.l2j.commons.database.DatabaseAccess.getDAO;


/**
 * @author UnAfraid
 */
public class PunishmentTask implements Runnable {

    private final PunishmentData data;
    private boolean _isStored;
    private ScheduledFuture<?> _task = null;

    public PunishmentTask(Object key, PunishmentAffect affect, PunishmentType type, long expirationTime, String reason, String punishedBy) {
        this(0, key, affect, type, expirationTime, reason, punishedBy, false);
    }

    public PunishmentTask(PunishmentData data, boolean stored) {
        this.data = data;
        this._isStored = stored;
    }

    public PunishmentTask(int id, Object key, PunishmentAffect affect, PunishmentType type, long expirationTime, String reason, String punishedBy, boolean isStored) {
        this(PunishmentData.of(String.valueOf(key), affect, type, expirationTime, reason, punishedBy), isStored);
        this.data.setId(id);
        startPunishment();
    }

    /**
     * @return affection value charId, account, ip, etc..
     */
    public Object getKey() {
        return data.getKey();
    }

    /**
     * @return {@link PunishmentAffect} affection type, account, character, ip, etc..
     */
    public PunishmentAffect getAffect() {
        return data.getAffect();
    }

    /**
     * @return {@link PunishmentType} type of current punishment.
     */
    public PunishmentType getType() {
        return data.getType();
    }

    /**
     * @return milliseconds to the end of the current punishment, -1 for infinity.
     */
    public final long getExpirationTime() {
        return data.getExpiration();
    }

    /**
     * @return the reason for this punishment.
     */
    public String getReason() {
        return data.getReason();
    }

    /**
     * @return name of the punishment issuer.
     */
    public String getPunishedBy() {
        return data.getPunisher();
    }

    /**
     * @return {@code true} if current punishment task has expired, {@code false} otherwise.
     */
    public final boolean isExpired() {
        return (data.getExpiration() > 0) && (System.currentTimeMillis() > data.getExpiration());
    }

    /**
     * Activates the punishment task.
     */
    private void startPunishment() {
        if (isExpired()) {
            return;
        }

        onStart();
        if (data.getExpiration() > 0) // Has expiration?
        {
            _task = ThreadPool.schedule(this, (data.getExpiration() - System.currentTimeMillis()));
        }
    }

    /**
     * Stops the punishment task.
     */
    public final void stopPunishment() {
        abortTask();
        onEnd();
    }

    /**
     * Aborts the scheduled task.
     */
    private void abortTask() {
        if (_task != null) {
            if (!_task.isCancelled() && !_task.isDone()) {
                _task.cancel(false);
            }
            _task = null;
        }
    }

    /**
     * Store and activate punishment upon start.
     */
    private void onStart() {
        if (!_isStored) {
            _isStored = getDAO(PunishmentDAO.class).save(data);
        }

        final IPunishmentHandler handler = PunishmentHandler.getInstance().getHandler(data.getType());
        if (handler != null) {
            handler.onStart(this);
        }
    }

    /**
     * Remove and deactivate punishment when it ends.
     */
    private void onEnd() {
        if (_isStored) {
            getDAO(PunishmentDAO.class).updateExpiration(data.getId(), System.currentTimeMillis());
        }

        if (data.getType() == PunishmentType.CHAT_BAN && data.getAffect() == PunishmentAffect.CHARACTER) {
            final Player player = World.getInstance().findPlayer(Integer.parseInt(data.getKey()));
            if (player != null) {
                player.getEffectList().stopAbnormalVisualEffect(AbnormalVisualEffect.NO_CHAT);
            }
        }

        final IPunishmentHandler handler = PunishmentHandler.getInstance().getHandler(data.getType());
        if (handler != null) {
            handler.onEnd(this);
        }
    }

    /**
     * Runs when punishment task ends in order to stop and remove it.
     */
    @Override
    public final void run() {
        PunishmentManager.getInstance().stopPunishment(data.getKey(), data.getAffect(), data.getType());
    }
}
