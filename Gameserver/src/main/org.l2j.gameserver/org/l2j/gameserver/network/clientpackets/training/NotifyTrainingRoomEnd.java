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
package org.l2j.gameserver.network.clientpackets.training;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.TrainingHolder;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.training.ExTrainingZone_Leaving;

/**
 * @author Sdw
 */
public class NotifyTrainingRoomEnd extends ClientPacket {
    @Override
    public void readImpl() {
        // Nothing to read
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final TrainingHolder holder = activeChar.getTraingCampInfo();
        if (holder == null) {
            return;
        }

        if (holder.isTraining()) {
            holder.setEndTime(System.currentTimeMillis());
            activeChar.setTraingCampInfo(holder);
            activeChar.enableAllSkills();
            activeChar.setIsInvul(false);
            activeChar.setInvisible(false);
            activeChar.setIsImmobilized(false);
            activeChar.teleToLocation(activeChar.getLastLocation());
            activeChar.sendPacket(ExTrainingZone_Leaving.STATIC_PACKET);
            holder.setEndTime(System.currentTimeMillis());
            activeChar.setTraingCampInfo(holder);
        }
    }
}
