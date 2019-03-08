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
import org.l2j.gameserver.mobius.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.clan.entry.PledgeWaitingInfo;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Sdw
 */
public class RequestPledgeDraftListApply extends IClientIncomingPacket
{
	private int _applyType;
	private int _karma;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_applyType = packet.getInt();
		_karma = packet.getInt();
		return true;
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = client.getActiveChar();
		
		if ((activeChar == null) || (activeChar.getClan() != null))
		{
			return;
		}
		
		if (activeChar.getClan() != null)
		{
			client.sendPacket(SystemMessageId.ONLY_THE_CLAN_LEADER_OR_SOMEONE_WITH_RANK_MANAGEMENT_AUTHORITY_MAY_REGISTER_THE_CLAN);
			return;
		}
		
		switch (_applyType)
		{
			case 0: // remove
			{
				if (ClanEntryManager.getInstance().removeFromWaitingList(activeChar.getObjectId()))
				{
					client.sendPacket(SystemMessageId.ENTRY_APPLICATION_CANCELLED_YOU_MAY_APPLY_TO_A_NEW_CLAN_AFTER_5_MINUTES);
				}
				break;
			}
			case 1: // add
			{
				final PledgeWaitingInfo pledgeDraftList = new PledgeWaitingInfo(activeChar.getObjectId(), activeChar.getLevel(), _karma, activeChar.getClassId().getId(), activeChar.getName());
				
				if (ClanEntryManager.getInstance().addToWaitingList(activeChar.getObjectId(), pledgeDraftList))
				{
					client.sendPacket(SystemMessageId.YOU_HAVE_JOINED_THE_WAITING_LIST_CHARACTERS_ARE_AUTOMATICALLY_DELETED_FROM_THE_LIST_AFTER_30_DAYS_IF_EXIT_WAITING_LIST_IS_USED_YOU_CANNOT_JOIN_THE_WAITING_LIST_FOR_5_MINUTES);
				}
				else
				{
					final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_MAY_APPLY_FOR_ENTRY_AFTER_S1_MINUTE_S_DUE_TO_CANCELLING_YOUR_APPLICATION);
					sm.addLong(ClanEntryManager.getInstance().getPlayerLockTime(activeChar.getObjectId()));
					client.sendPacket(sm);
				}
				break;
			}
		}
	}
}
