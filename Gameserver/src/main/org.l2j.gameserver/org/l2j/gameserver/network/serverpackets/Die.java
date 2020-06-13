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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isAttackable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid, Nos, Mobius
 * @author JoeAlisson
 */
public class Die extends ServerPacket {
    private final int objectId;
    private final boolean isSweepable;
    private int flags = 0;

    public Die(Creature creature) {
        objectId = creature.getObjectId();
        isSweepable = isAttackable(creature) && creature.isSweepActive();

        if (isPlayer(creature)) {
            final var player = creature.getActingPlayer();
            final Clan clan = player.getClan();
            boolean isInCastleDefense = false;

            SiegeClanData siegeClan = null;
            final Castle castle = CastleManager.getInstance().getCastle(creature);
            if ((castle != null) && castle.getSiege().isInProgress()) {
                siegeClan = castle.getSiege().getAttackerClan(clan);
                isInCastleDefense = (siegeClan == null) && castle.getSiege().checkIsDefender(clan);
            }

            flags += nonNull(clan) && clan.getHideoutId() > 0 ? 2 : 0; // clan hall
            flags += (nonNull(clan) && (clan.getCastleId() > 0)) || isInCastleDefense ? 4 : 0; // castle
                                                                                              // 8  fortress
            flags += nonNull(siegeClan) && !siegeClan.getFlags().isEmpty() ? 16 : 0; // outpost
            flags += creature.getAccessLevel().allowFixedRes() || player.getInventory().haveItemForSelfResurrection() ? 32 : 0; // feather
        }
    }


    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.DIE);

        writeInt(objectId);
        writeInt(flags);
        writeInt(0);
        writeInt(isSweepable);
        writeInt(0);
        writeInt(0);
        writeInt(0);
        writeInt(0);
        writeByte(0);
    }
}
