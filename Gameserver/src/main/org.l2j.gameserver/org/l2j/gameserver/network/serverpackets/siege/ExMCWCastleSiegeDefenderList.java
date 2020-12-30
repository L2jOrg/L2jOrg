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
package org.l2j.gameserver.network.serverpackets.siege;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.engine.clan.ClanEngine;
import org.l2j.gameserver.engine.siege.Siege;
import org.l2j.gameserver.enums.SiegeClanType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class ExMCWCastleSiegeDefenderList extends AbstractSiegeClanList {

    public ExMCWCastleSiegeDefenderList(Siege siege) {
        super(siege);
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) throws Exception {
        writeId(ServerExPacketId.EX_MERCENARY_CASTLEWAR_CASTLE_SIEGE_DEFENDER_LIST, buffer);

        var defenders = siege.getDefenderClans();
        var size = defenders.size();
        var owner = siege.getCastle().getOwner();

        if(nonNull(owner)) {
            size++;
        }

        writeHeader(buffer, size);

        if(nonNull(owner)) {
            writeClanInfo(buffer, owner);
            buffer.writeInt(SiegeClanType.OWNER.ordinal());
            writeAllianceInfo(buffer, owner);
        }

        for (SiegeClanData defender : defenders) {
            var clan = ClanEngine.getInstance().getClan(defender.getClanId());
            writeClanInfo(buffer, clan);
            buffer.writeInt(defender.getType().ordinal());
            writeAllianceInfo(buffer, clan);
        }
    }
}
