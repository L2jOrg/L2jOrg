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
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2j.gameserver.mobius.gameserver.util.Util;

/**
 * Lets drink to code!
 * @author zabbix, HorridoJoho
 */
public final class RequestLinkHtml extends IClientIncomingPacket
{
	private String _link;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_link = readString(packet);
		return true;
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance actor = client.getActiveChar();
		if (actor == null)
		{
			return;
		}
		
		if (_link.isEmpty())
		{
			LOGGER.warning("Player " + actor.getName() + " sent empty html link!");
			return;
		}
		
		if (_link.contains(".."))
		{
			LOGGER.warning("Player " + actor.getName() + " sent invalid html link: link " + _link);
			return;
		}
		
		final int htmlObjectId = actor.validateHtmlAction("link " + _link);
		if (htmlObjectId == -1)
		{
			LOGGER.warning("Player " + actor.getName() + " sent non cached  html link: link " + _link);
			return;
		}
		
		if ((htmlObjectId > 0) && !Util.isInsideRangeOfObjectId(actor, htmlObjectId, L2Npc.INTERACTION_DISTANCE))
		{
			// No logging here, this could be a common case
			return;
		}
		
		final String filename = "data/html/" + _link;
		final NpcHtmlMessage msg = new NpcHtmlMessage(htmlObjectId);
		msg.setFile(actor, filename);
		actor.sendPacket(msg);
	}
}
