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
package handlers.usercommandhandlers;

import org.l2j.gameserver.handler.IUserCommandHandler;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * Party Info user command.
 * @author Tempy
 */
public class PartyInfo implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		81
	};
	
	@Override
	public boolean useUserCommand(int id, Player activeChar)
	{
		if (id != COMMAND_IDS[0])
		{
			return false;
		}
		
		activeChar.sendPacket(SystemMessageId.PARTY_INFORMATION);
		if (activeChar.isInParty())
		{
			final Party party = activeChar.getParty();
			switch (party.getDistributionType())
			{
				case FINDERS_KEEPERS:
				{
					activeChar.sendPacket(SystemMessageId.LOOTING_METHOD_FINDERS_KEEPERS);
					break;
				}
				case RANDOM:
				{
					activeChar.sendPacket(SystemMessageId.LOOTING_METHOD_RANDOM);
					break;
				}
				case RANDOM_INCLUDING_SPOIL:
				{
					activeChar.sendPacket(SystemMessageId.LOOTING_METHOD_RANDOM_INCLUDING_SPOIL);
					break;
				}
				case BY_TURN:
				{
					activeChar.sendPacket(SystemMessageId.LOOTING_METHOD_BY_TURN);
					break;
				}
				case BY_TURN_INCLUDING_SPOIL:
				{
					activeChar.sendPacket(SystemMessageId.LOOTING_METHOD_BY_TURN_INCLUDING_SPOIL);
					break;
				}
			}
			
			// Not used in Infinite Odissey
			// if (!party.isLeader(activeChar))
			// {
			// final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.PARTY_LEADER_C1);
			// sm.addPcName(party.getLeader());
			// activeChar.sendPacket(sm);
			// }
		}
		activeChar.sendPacket(SystemMessageId.END_LIST);
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}
