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
package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.commons.network.PacketReader;
import org.l2j.gameserver.mobius.gameserver.enums.SkillEnchantType;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExEnchantSkillInfoDetail;

/**
 * @author -Wooden-
 */
public final class RequestExEnchantSkillInfoDetail extends IClientIncomingPacket
{
	private SkillEnchantType _type;
	private int _skillId;
	private int _skillLvl;
	private int _skillSubLvl;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_type = SkillEnchantType.values()[packet.getInt()];
		_skillId = packet.getInt();
		_skillLvl = packet.getShort();
		_skillSubLvl = packet.getShort();
		return true;
	}
	
	@Override
	public void runImpl()
	{
		if ((_skillId <= 0) || (_skillLvl <= 0) || (_skillSubLvl < 0))
		{
			return;
		}
		
		final L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		activeChar.sendPacket(new ExEnchantSkillInfoDetail(_type, _skillId, _skillLvl, _skillSubLvl, activeChar));
	}
}
