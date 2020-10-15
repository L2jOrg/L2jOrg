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

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.Henna;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Zoey76
 */
public final class HennaItemRemoveInfo extends ServerPacket {
    private final Player _activeChar;
    private final Henna _henna;

    public HennaItemRemoveInfo(Henna henna, Player player) {
        _henna = henna;
        _activeChar = player;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.HENNA_UNEQUIP_INFO, buffer );

        buffer.writeInt(_henna.getDyeId()); // symbol Id
        buffer.writeInt(_henna.getDyeItemId()); // item id of dye
        buffer.writeLong(_henna.getCancelCount()); // total amount of dye require
        buffer.writeLong(_henna.getCancelFee()); // total amount of Adena require to remove symbol
        buffer.writeInt(_henna.isAllowedClass(_activeChar.getClassId()) ? 0x01 : 0x00); // able to remove or not
        buffer.writeLong(_activeChar.getAdena());
        buffer.writeInt(_activeChar.getINT()); // current INT
        buffer.writeShort((_activeChar.getINT() - _activeChar.getHennaValue(BaseStats.INT))); // equip INT
        buffer.writeInt(_activeChar.getSTR()); // current STR
        buffer.writeShort((_activeChar.getSTR() - _activeChar.getHennaValue(BaseStats.STR))); // equip STR
        buffer.writeInt(_activeChar.getCON()); // current CON
        buffer.writeShort((_activeChar.getCON() - _activeChar.getHennaValue(BaseStats.CON))); // equip CON
        buffer.writeInt(_activeChar.getMEN()); // current MEN
        buffer.writeShort((_activeChar.getMEN() - _activeChar.getHennaValue(BaseStats.MEN))); // equip MEN
        buffer.writeInt(_activeChar.getDEX()); // current DEX
        buffer.writeShort((_activeChar.getDEX() - _activeChar.getHennaValue(BaseStats.DEX))); // equip DEX
        buffer.writeInt(_activeChar.getWIT()); // current WIT
        buffer.writeShort((_activeChar.getWIT() - _activeChar.getHennaValue(BaseStats.WIT))); // equip WIT
        buffer.writeInt(0x00); // current LUC
        buffer.writeShort(0x00); // equip LUC
        buffer.writeInt(0x00); // current CHA
        buffer.writeShort(0x00); // equip CHA
        buffer.writeInt(0x00);
    }

}
