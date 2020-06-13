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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.world.World;

import static org.l2j.gameserver.util.GameUtils.isCreature;

public class RequestRecordInfo extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        client.sendPacket(new UserInfo(activeChar));

        World.getInstance().forEachVisibleObject(activeChar, WorldObject.class, object ->
        {
            if (object.isVisibleFor(activeChar)) {
                object.sendInfo(activeChar);

                if (isCreature(object)) {
                    // Update the state of the Creature object client
                    // side by sending Server->Client packet
                    // MoveToPawn/CharMoveToLocation and AutoAttackStart to
                    // the Player
                    final Creature obj = (Creature) object;
                    if (obj.getAI() != null) {
                        obj.getAI().describeStateToPlayer(activeChar);
                    }
                }
            }
        });
    }
}
