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
import org.l2j.gameserver.mobius.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.mobius.gameserver.model.L2ClanMember;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;

public class RequestGiveNickName extends IClientIncomingPacket
{
	private String _target;
	private String _title;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_target = readString(packet);
		_title = readString(packet);
		return true;
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		// Noblesse can bestow a title to themselves
		if (activeChar.isNoble() && _target.equalsIgnoreCase(activeChar.getName()))
		{
			activeChar.setTitle(_title);
			client.sendPacket(SystemMessageId.YOUR_TITLE_HAS_BEEN_CHANGED);
			activeChar.broadcastTitleInfo();
		}
		else
		{
			// Can the player change/give a title?
			if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_GIVE_TITLE))
			{
				client.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			
			if (activeChar.getClan().getLevel() < 3)
			{
				client.sendPacket(SystemMessageId.A_PLAYER_CAN_ONLY_BE_GRANTED_A_TITLE_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE);
				return;
			}
			
			final L2ClanMember member1 = activeChar.getClan().getClanMember(_target);
			if (member1 != null)
			{
				final L2PcInstance member = member1.getPlayerInstance();
				if (member != null)
				{
					// is target from the same clan?
					member.setTitle(_title);
					member.sendPacket(SystemMessageId.YOUR_TITLE_HAS_BEEN_CHANGED);
					member.broadcastTitleInfo();
				}
				else
				{
					client.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
				}
			}
			else
			{
				client.sendPacket(SystemMessageId.THE_TARGET_MUST_BE_A_CLAN_MEMBER);
			}
		}
	}
}
