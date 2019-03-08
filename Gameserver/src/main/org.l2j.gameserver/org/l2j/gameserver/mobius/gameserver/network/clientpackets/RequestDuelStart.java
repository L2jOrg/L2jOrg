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

import org.l2j.commons.concurrent.ThreadPool;
import org.l2j.commons.network.PacketReader;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.FakePlayerData;
import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExDuelAskStart;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;

/**
 * Format:(ch) Sd
 * @author -Wooden-
 */
public final class RequestDuelStart extends IClientIncomingPacket
{
	private String _player;
	private int _partyDuel;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_player = readString(packet);
		_partyDuel = packet.getInt();
		return true;
	}
	
	private void scheduleDeny(L2PcInstance player, String name)
	{
		if (player != null)
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL);
			sm.addString(name);
			player.sendPacket(sm);
			player.onTransactionResponse();
		}
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (FakePlayerData.getInstance().isTalkable(_player))
		{
			final String name = FakePlayerData.getInstance().getProperName(_player);
			if (activeChar.isInsideZone(ZoneId.PVP) || activeChar.isInsideZone(ZoneId.PEACE) || activeChar.isInsideZone(ZoneId.SIEGE))
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_AN_AREA_WHERE_DUEL_IS_NOT_ALLOWED_AND_YOU_CANNOT_APPLY_FOR_A_DUEL);
				sm.addString(name);
				activeChar.sendPacket(sm);
				return;
			}
			boolean npcInRange = false;
			for (L2Npc npc : L2World.getInstance().getVisibleObjectsInRange(activeChar, L2Npc.class, 250))
			{
				if (npc.getName().equals(name))
				{
					npcInRange = true;
				}
			}
			if (!npcInRange)
			{
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_TOO_FAR_AWAY_TO_RECEIVE_A_DUEL_CHALLENGE);
				sm.addString(name);
				activeChar.sendPacket(sm);
				return;
			}
			if (activeChar.isProcessingRequest())
			{
				final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER);
				msg.addString(name);
				activeChar.sendPacket(msg);
				return;
			}
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_BEEN_CHALLENGED_TO_A_DUEL);
			sm.addString(name);
			activeChar.sendPacket(sm);
			ThreadPoolManager.getInstance().schedule(() -> scheduleDeny(activeChar, name), 10000);
			activeChar.blockRequest();
			return;
		}
		
		final L2PcInstance targetChar = L2World.getInstance().getPlayer(_player);
		if (targetChar == null)
		{
			activeChar.sendPacket(SystemMessageId.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL);
			return;
		}
		if (activeChar == targetChar)
		{
			activeChar.sendPacket(SystemMessageId.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL);
			return;
		}
		
		// Check if duel is possible
		if (!activeChar.canDuel())
		{
			activeChar.sendPacket(SystemMessageId.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
			return;
		}
		else if (!targetChar.canDuel())
		{
			activeChar.sendPacket(targetChar.getNoDuelReason());
			return;
		}
		// Players may not be too far apart
		else if (!activeChar.isInsideRadius2D(targetChar, 250))
		{
			final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_TOO_FAR_AWAY_TO_RECEIVE_A_DUEL_CHALLENGE);
			msg.addString(targetChar.getName());
			activeChar.sendPacket(msg);
			return;
		}
		
		// Duel is a party duel
		if (_partyDuel == 1)
		{
			// Player must be in a party & the party leader
			final L2Party party = activeChar.getParty();
			if ((party == null) || !party.isLeader(activeChar))
			{
				activeChar.sendMessage("You have to be the leader of a party in order to request a party duel.");
				return;
			}
			// Target must be in a party
			else if (!targetChar.isInParty())
			{
				activeChar.sendPacket(SystemMessageId.SINCE_THE_PERSON_YOU_CHALLENGED_IS_NOT_CURRENTLY_IN_A_PARTY_THEY_CANNOT_DUEL_AGAINST_YOUR_PARTY);
				return;
			}
			// Target may not be of the same party
			else if (activeChar.getParty().containsPlayer(targetChar))
			{
				activeChar.sendMessage("This player is a member of your own party.");
				return;
			}
			
			// Check if every player is ready for a duel
			for (L2PcInstance temp : activeChar.getParty().getMembers())
			{
				if (!temp.canDuel())
				{
					activeChar.sendMessage("Not all the members of your party are ready for a duel.");
					return;
				}
			}
			L2PcInstance partyLeader = null; // snatch party leader of targetChar's party
			for (L2PcInstance temp : targetChar.getParty().getMembers())
			{
				if (partyLeader == null)
				{
					partyLeader = temp;
				}
				if (!temp.canDuel())
				{
					activeChar.sendPacket(SystemMessageId.THE_OPPOSING_PARTY_IS_CURRENTLY_UNABLE_TO_ACCEPT_A_CHALLENGE_TO_A_DUEL);
					return;
				}
			}
			
			// Send request to targetChar's party leader
			if (partyLeader != null)
			{
				if (!partyLeader.isProcessingRequest())
				{
					activeChar.onTransactionRequest(partyLeader);
					partyLeader.sendPacket(new ExDuelAskStart(activeChar.getName(), _partyDuel));
					
					SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_S_PARTY_HAS_BEEN_CHALLENGED_TO_A_DUEL);
					msg.addString(partyLeader.getName());
					activeChar.sendPacket(msg);
					
					msg = SystemMessage.getSystemMessage(SystemMessageId.C1_S_PARTY_HAS_CHALLENGED_YOUR_PARTY_TO_A_DUEL);
					msg.addString(activeChar.getName());
					targetChar.sendPacket(msg);
				}
				else
				{
					final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER);
					msg.addString(partyLeader.getName());
					activeChar.sendPacket(msg);
				}
			}
		}
		else
		// 1vs1 duel
		{
			if (!targetChar.isProcessingRequest())
			{
				activeChar.onTransactionRequest(targetChar);
				targetChar.sendPacket(new ExDuelAskStart(activeChar.getName(), _partyDuel));
				
				SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_BEEN_CHALLENGED_TO_A_DUEL);
				msg.addString(targetChar.getName());
				activeChar.sendPacket(msg);
				
				msg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_CHALLENGED_YOU_TO_A_DUEL);
				msg.addString(activeChar.getName());
				targetChar.sendPacket(msg);
			}
			else
			{
				final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER);
				msg.addString(targetChar.getName());
				activeChar.sendPacket(msg);
			}
		}
	}
}
