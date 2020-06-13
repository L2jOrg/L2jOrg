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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.HENNA_UNEQUIP_INFO);

        writeInt(_henna.getDyeId()); // symbol Id
        writeInt(_henna.getDyeItemId()); // item id of dye
        writeLong(_henna.getCancelCount()); // total amount of dye require
        writeLong(_henna.getCancelFee()); // total amount of Adena require to remove symbol
        writeInt(_henna.isAllowedClass(_activeChar.getClassId()) ? 0x01 : 0x00); // able to remove or not
        writeLong(_activeChar.getAdena());
        writeInt(_activeChar.getINT()); // current INT
        writeShort((short) (_activeChar.getINT() - _activeChar.getHennaValue(BaseStats.INT))); // equip INT
        writeInt(_activeChar.getSTR()); // current STR
        writeShort((short) (_activeChar.getSTR() - _activeChar.getHennaValue(BaseStats.STR))); // equip STR
        writeInt(_activeChar.getCON()); // current CON
        writeShort((short) (_activeChar.getCON() - _activeChar.getHennaValue(BaseStats.CON))); // equip CON
        writeInt(_activeChar.getMEN()); // current MEN
        writeShort((short) (_activeChar.getMEN() - _activeChar.getHennaValue(BaseStats.MEN))); // equip MEN
        writeInt(_activeChar.getDEX()); // current DEX
        writeShort((short) (_activeChar.getDEX() - _activeChar.getHennaValue(BaseStats.DEX))); // equip DEX
        writeInt(_activeChar.getWIT()); // current WIT
        writeShort((short) (_activeChar.getWIT() - _activeChar.getHennaValue(BaseStats.WIT))); // equip WIT
        writeInt(0x00); // current LUC
        writeShort((short) 0x00); // equip LUC
        writeInt(0x00); // current CHA
        writeShort((short) 0x00); // equip CHA
        writeInt(0x00);
    }

}
