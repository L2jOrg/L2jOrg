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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.Henna;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.ArrayList;
import java.util.List;

/**
 * This server packet sends the player's henna information.
 *
 * @author Zoey76
 */
public final class HennaInfo extends ServerPacket {
    private final Player _activeChar;
    private final List<Henna> _hennas = new ArrayList<>();

    public HennaInfo(Player player) {
        _activeChar = player;
        for (Henna henna : _activeChar.getHennaList()) {
            if (henna != null) {
                _hennas.add(henna);
            }
        }
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.HENNA_INFO, buffer );

        buffer.writeShort(_activeChar.getHennaValue(BaseStats.INT)); // equip INT
        buffer.writeShort(_activeChar.getHennaValue(BaseStats.STR)); // equip STR
        buffer.writeShort(_activeChar.getHennaValue(BaseStats.CON)); // equip CON
        buffer.writeShort(_activeChar.getHennaValue(BaseStats.MEN)); // equip MEN
        buffer.writeShort(_activeChar.getHennaValue(BaseStats.DEX)); // equip DEX
        buffer.writeShort(_activeChar.getHennaValue(BaseStats.WIT)); // equip WIT
        buffer.writeShort(0x00); // equip LUC
        buffer.writeShort(0x00); // equip CHA
        buffer.writeInt(3 - _activeChar.getHennaEmptySlots()); // Slots
        buffer.writeInt(_hennas.size()); // Size
        for (Henna henna : _hennas) {
            buffer.writeInt(henna.getDyeId());
            buffer.writeInt(henna.isAllowedClass(_activeChar.getClassId()) ? 0x01 : 0x00);
        }
        buffer.writeInt(0x00); // Premium Slot Dye ID
        buffer.writeInt(0x00); // Premium Slot Dye Time Left
        buffer.writeInt(0x00); // Premium Slot Dye ID isValid
    }

}
