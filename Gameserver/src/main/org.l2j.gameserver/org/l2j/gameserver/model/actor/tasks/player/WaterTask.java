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
package org.l2j.gameserver.model.actor.tasks.player;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.model.DamageInfo.DamageType;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * Task dedicated to make damage to the player while drowning.
 *
 * @author UnAfraid
 */
public class WaterTask implements Runnable {
    private final Player player;

    public WaterTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (nonNull(player)) {
            double reduceHp = Math.min(1, player.getMaxHp() / 100.0);
            player.reduceCurrentHp(reduceHp, null, null, false, true, false, false, DamageType.DROWN);
            player.sendPacket(getSystemMessage(SystemMessageId.YOU_HAVE_TAKEN_S1_DAMAGE_BECAUSE_YOU_WERE_UNABLE_TO_BREATHE).addInt((int) reduceHp));
        }
    }
}
