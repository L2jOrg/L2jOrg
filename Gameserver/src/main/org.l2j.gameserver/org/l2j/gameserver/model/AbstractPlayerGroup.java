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
package org.l2j.gameserver.model;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.MathUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Battlecruiser
 */
public abstract class AbstractPlayerGroup {
    /**
     * @return a list of all members of this group
     */
    public abstract List<Player> getMembers();

    /**
     * @return the leader of this group
     */
    public abstract Player getLeader();

    /**
     * @return the leader's object ID
     */
    public int getLeaderObjectId() {
        return getLeader().getObjectId();
    }

    /**
     * Check if a given player is the leader of this group.
     *
     * @param player the player to check
     * @return {@code true} if the specified player is the leader of this group, {@code false} otherwise
     */
    public boolean isLeader(Player player) {
        return getLeader().getObjectId() == player.getObjectId();
    }

    /**
     * @return the count of all players in this group
     */
    public int getMemberCount() {
        return getMembers().size();
    }

    /**
     * Broadcast a packet to every member of this group.
     *
     * @param packet the packet to broadcast
     */
    public void broadcastPacket(ServerPacket packet) {
        checkEachMember(m ->
        {
            if (m != null) {
                m.sendPacket(packet);
            }
            return true;
        });
    }

    /**
     * Broadcast a system message to this group.
     *
     * @param message the system message to broadcast
     */
    public void broadcastMessage(SystemMessageId message) {
        broadcastPacket(SystemMessage.getSystemMessage(message));
    }

    public void broadcastCreatureSay(CreatureSay msg, Player broadcaster) {
        checkEachMember(m ->
        {
            if ((m != null) && !BlockList.isBlocked(m, broadcaster)) {
                m.sendPacket(msg);
            }
            return true;
        });
    }

    /**
     * Iterates over the group and executes procedure on each member
     *
     * @param procedure the prodecure to be executed on each member.<br>
     *                  If executing the procedure on a member returns {@code true}, the loop continues to the next member, otherwise it breaks the loop
     * @return {@code true} if the procedure executed correctly, {@code false} if the loop was broken prematurely
     */
    public boolean checkEachMember(Function<Player, Boolean> procedure) {
        for (Player player : getMembers()) {
            if (!procedure.apply(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof AbstractPlayerGroup) {
            return getLeaderObjectId() == ((AbstractPlayerGroup) obj).getLeaderObjectId();
        }

        return false;
    }
}
