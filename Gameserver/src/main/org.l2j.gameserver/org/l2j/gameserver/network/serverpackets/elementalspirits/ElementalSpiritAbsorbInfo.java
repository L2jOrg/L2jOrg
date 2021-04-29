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
package org.l2j.gameserver.network.serverpackets.elementalspirits;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.engine.elemental.AbsorbItem;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.ServerExPacketId.EX_ELEMENTAL_SPIRIT_ABSORB_INFO;

public class ElementalSpiritAbsorbInfo extends ServerPacket {

    private final byte type;

    public ElementalSpiritAbsorbInfo(byte type) {
        this.type = type;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(EX_ELEMENTAL_SPIRIT_ABSORB_INFO, buffer);

        var player = client.getPlayer();
        var spirit = player.getElementalSpirit(ElementalType.of(type));

        if(isNull(spirit)) {
            buffer.writeByte(0x00);
            buffer.writeByte(0x00);
            return;
        }

        buffer.writeByte(0x01);
        buffer.writeByte(type);
        buffer.writeByte(spirit.getStage());
        buffer.writeLong(spirit.getExperience());
        buffer.writeLong(spirit.getExperienceToNextLevel()); //NextExp
        buffer.writeLong(spirit.getExperienceToNextLevel()); //MaxExp
        buffer.writeInt(spirit.getLevel());
        buffer.writeInt(spirit.getMaxLevel());

        var absorbItems = spirit.getAbsorbItems();

        buffer.writeInt(absorbItems.size()); //AbsorbCount
        for (AbsorbItem absorbItem : absorbItems) {
            buffer.writeInt(absorbItem.getId());
            buffer.writeInt((int) Util.zeroIfNullOrElseLong( player.getInventory().getItemByItemId(absorbItem.getId()), Item::getCount));
            buffer.writeInt(absorbItem.getExperience());
        }
    }
}
