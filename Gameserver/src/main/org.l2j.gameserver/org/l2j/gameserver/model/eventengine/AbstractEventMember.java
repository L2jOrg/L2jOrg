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
package org.l2j.gameserver.model.eventengine;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @param <T>
 * @author UnAfraid
 */
public abstract class AbstractEventMember<T extends AbstractEvent<?>> {
    private final T _event;
    private final AtomicInteger _score = new AtomicInteger();
    private final Player player;

    public AbstractEventMember(Player player, T event) {
        this.player = player;
        _event = event;
    }

    public final int getObjectId() {
        return player.getObjectId();
    }

    public Player getPlayer() {
        return player;
    }

    public void sendPacket(ServerPacket... packets) {
        final Player player = getPlayer();
        if (player != null) {
            for (ServerPacket packet : packets) {
                player.sendPacket(packet);
            }
        }
    }

    public int getClassId() {
        final Player player = getPlayer();
        if (player != null) {
            return player.getClassId().getId();
        }
        return 0;
    }

    public int getScore() {
        return _score.get();
    }

    public void setScore(int score) {
        _score.set(score);
    }

    public int incrementScore() {
        return _score.incrementAndGet();
    }

    public int decrementScore() {
        return _score.decrementAndGet();
    }

    public int addScore(int score) {
        return _score.addAndGet(score);
    }

    public final T getEvent() {
        return _event;
    }
}
