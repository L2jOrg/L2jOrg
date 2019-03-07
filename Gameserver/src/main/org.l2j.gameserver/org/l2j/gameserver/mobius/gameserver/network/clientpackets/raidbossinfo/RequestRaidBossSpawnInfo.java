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
package org.l2j.gameserver.mobius.gameserver.network.clientpackets.raidbossinfo;

import com.l2jmobius.commons.network.PacketReader;
import com.l2jmobius.gameserver.instancemanager.DBSpawnManager;
import com.l2jmobius.gameserver.instancemanager.DBSpawnManager.DBStatusType;
import com.l2jmobius.gameserver.instancemanager.GrandBossManager;
import com.l2jmobius.gameserver.network.L2GameClient;
import com.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;
import com.l2jmobius.gameserver.network.serverpackets.raidbossinfo.ExRaidBossSpawnInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mobius
 */
public class RequestRaidBossSpawnInfo implements IClientIncomingPacket
{
	private final List<Integer> _bossIds = new ArrayList<>();
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		final int count = packet.readD();
		for (int i = 0; i < count; i++)
		{
			final int bossId = packet.readD();
			if (DBSpawnManager.getInstance().getNpcStatusId(bossId) == DBStatusType.ALIVE)
			{
				_bossIds.add(bossId);
			}
			else if (GrandBossManager.getInstance().getBossStatus(bossId) == 0)
			{
				_bossIds.add(bossId);
			}
			/*
			 * else { String message = "Could not find spawn info for boss " + bossId; final L2NpcTemplate template = NpcData.getInstance().getTemplate(bossId); if (template != null) { message += " - " + template.getName() + "."; } else { message += " - NPC template not found."; }
			 * System.out.println(message); }
			 */
		}
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		client.sendPacket(new ExRaidBossSpawnInfo(_bossIds));
	}
}
