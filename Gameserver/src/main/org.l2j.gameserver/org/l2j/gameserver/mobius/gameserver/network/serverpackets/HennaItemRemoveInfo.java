/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.commons.network.PacketWriter;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Henna;
import org.l2j.gameserver.mobius.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author Zoey76
 */
public final class HennaItemRemoveInfo implements IClientOutgoingPacket
{
	private final L2PcInstance _activeChar;
	private final L2Henna _henna;
	
	public HennaItemRemoveInfo(L2Henna henna, L2PcInstance player)
	{
		_henna = henna;
		_activeChar = player;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.HENNA_UNEQUIP_INFO.writeId(packet);
		
		packet.writeD(_henna.getDyeId()); // symbol Id
		packet.writeD(_henna.getDyeItemId()); // item id of dye
		packet.writeQ(_henna.getCancelCount()); // total amount of dye require
		packet.writeQ(_henna.getCancelFee()); // total amount of Adena require to remove symbol
		packet.writeD(_henna.isAllowedClass(_activeChar.getClassId()) ? 0x01 : 0x00); // able to remove or not
		packet.writeQ(_activeChar.getAdena());
		packet.writeD(_activeChar.getINT()); // current INT
		packet.writeH(_activeChar.getINT() - _activeChar.getHennaValue(BaseStats.INT)); // equip INT
		packet.writeD(_activeChar.getSTR()); // current STR
		packet.writeH(_activeChar.getSTR() - _activeChar.getHennaValue(BaseStats.STR)); // equip STR
		packet.writeD(_activeChar.getCON()); // current CON
		packet.writeH(_activeChar.getCON() - _activeChar.getHennaValue(BaseStats.CON)); // equip CON
		packet.writeD(_activeChar.getMEN()); // current MEN
		packet.writeH(_activeChar.getMEN() - _activeChar.getHennaValue(BaseStats.MEN)); // equip MEN
		packet.writeD(_activeChar.getDEX()); // current DEX
		packet.writeH(_activeChar.getDEX() - _activeChar.getHennaValue(BaseStats.DEX)); // equip DEX
		packet.writeD(_activeChar.getWIT()); // current WIT
		packet.writeH(_activeChar.getWIT() - _activeChar.getHennaValue(BaseStats.WIT)); // equip WIT
		packet.writeD(0x00); // current LUC
		packet.writeH(0x00); // equip LUC
		packet.writeD(0x00); // current CHA
		packet.writeH(0x00); // equip CHA
		packet.writeD(0x00);
		return true;
	}
}
