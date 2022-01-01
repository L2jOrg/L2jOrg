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
import org.l2j.gameserver.data.database.data.Mercenary;
import org.l2j.gameserver.engine.siege.Siege;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.World;

import java.util.Collection;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

/**
 * @author JoeAlisson
 */
public class ExPledgeMercenaryMemberList extends ServerPacket {

    private final Siege siege;
    private final Clan clan;
    private final Collection<Mercenary> mercenaries;

    public ExPledgeMercenaryMemberList(Siege siege, Clan clan, Collection<Mercenary> mercenaries) {
        this.siege = requireNonNull(siege);
        this.clan = requireNonNull(clan);
        this.mercenaries = requireNonNull(mercenaries);
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) throws Exception {
        writeId(ServerExPacketId.EX_PLEDGE_MERCENARY_MEMBER_LIST, buffer);
        buffer.writeInt(siege.getCastle().getId());
        buffer.writeInt(clan.getId());

        buffer.writeInt(mercenaries.size());
        for (Mercenary mercenary : mercenaries) {
            buffer.writeInt(mercenary.getId() == client.getPlayer().getObjectId());
            buffer.writeInt(nonNull(World.getInstance().findPlayer(mercenary.getId()))); // TODO improve it
            buffer.writeSizedString(mercenary.getName());
            buffer.writeInt(mercenary.getClassId().getId());
        }
    }
}
