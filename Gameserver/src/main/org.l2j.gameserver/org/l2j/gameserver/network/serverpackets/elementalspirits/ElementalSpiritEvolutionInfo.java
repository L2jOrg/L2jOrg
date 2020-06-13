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
package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.ServerExPacketId.EX_ELEMENTAL_SPIRIT_EVOLUTION_INFO;

public class ElementalSpiritEvolutionInfo extends ServerPacket {

    private final byte type;

    public ElementalSpiritEvolutionInfo(byte type) {
        this.type = type;
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(EX_ELEMENTAL_SPIRIT_EVOLUTION_INFO);

        var player = client.getPlayer();
        var spirit = player.getElementalSpirit(ElementalType.of(type));

        if(isNull(spirit)) {
            writeByte(0);
            writeInt(0);
            return;
        }

        writeByte(type);
        writeInt(spirit.getNpcId());
        writeInt(0x01); // unk
        writeInt(spirit.getStage());
        writeDouble(100); // chance ??

        var items = spirit.getItemsToEvolve();
        writeInt(items.size());
        for (ItemHolder item : items) {
            writeInt(item.getId());
            writeLong(item.getCount());
        }
    }
}
