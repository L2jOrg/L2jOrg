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
import org.l2j.gameserver.mobius.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.mobius.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.mobius.gameserver.model.ClanWar;
import org.l2j.gameserver.mobius.gameserver.model.ClanWar.ClanWarState;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2ClanMember;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;

import java.util.Objects;

public final class RequestSurrenderPledgeWar extends IClientIncomingPacket
{
	private String _pledgeName;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_pledgeName = readString(packet);
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
		
		final L2Clan myClan = activeChar.getClan();
		if (myClan == null)
		{
			return;
		}
		
		if (myClan.getMembers().stream().filter(Objects::nonNull).filter(L2ClanMember::isOnline).map(L2ClanMember::getPlayerInstance).anyMatch(p -> !p.isInCombat()))
		{
			activeChar.sendPacket(SystemMessageId.A_CEASE_FIRE_DURING_A_CLAN_WAR_CAN_NOT_BE_CALLED_WHILE_MEMBERS_OF_YOUR_CLAN_ARE_ENGAGED_IN_BATTLE);
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final L2Clan targetClan = ClanTable.getInstance().getClanByName(_pledgeName);
		if (targetClan == null)
		{
			activeChar.sendMessage("No such clan.");
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		else if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_PLEDGE_WAR))
		{
			client.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final ClanWar clanWar = myClan.getWarWith(targetClan.getId());
		
		if (clanWar == null)
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_NOT_DECLARED_A_CLAN_WAR_AGAINST_THE_CLAN_S1);
			sm.addString(targetClan.getName());
			activeChar.sendPacket(sm);
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (clanWar.getState() == ClanWarState.BLOOD_DECLARATION)
		{
			activeChar.sendPacket(SystemMessageId.YOU_CANNOT_DECLARE_DEFEAT_AS_IT_HAS_NOT_BEEN_7_DAYS_SINCE_STARTING_A_CLAN_WAR_WITH_CLAN_S1);
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		clanWar.cancel(activeChar, myClan);
	}
}