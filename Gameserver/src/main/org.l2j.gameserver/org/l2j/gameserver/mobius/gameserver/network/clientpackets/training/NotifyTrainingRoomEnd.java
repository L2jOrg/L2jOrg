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
package org.l2j.gameserver.mobius.gameserver.network.clientpackets.training;

import com.l2jmobius.commons.network.PacketReader;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.TrainingHolder;
import com.l2jmobius.gameserver.network.L2GameClient;
import com.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;
import com.l2jmobius.gameserver.network.serverpackets.training.ExTrainingZone_Leaving;

/**
 * @author Sdw
 */
public class NotifyTrainingRoomEnd implements IClientIncomingPacket
{
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		// Nothing to read
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		final L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		final TrainingHolder holder = activeChar.getTraingCampInfo();
		if (holder == null)
		{
			return;
		}
		
		if (holder.isTraining())
		{
			holder.setEndTime(System.currentTimeMillis());
			activeChar.setTraingCampInfo(holder);
			activeChar.enableAllSkills();
			activeChar.setIsInvul(false);
			activeChar.setInvisible(false);
			activeChar.setIsImmobilized(false);
			activeChar.teleToLocation(activeChar.getLastLocation());
			activeChar.sendPacket(ExTrainingZone_Leaving.STATIC_PACKET);
			holder.setEndTime(System.currentTimeMillis());
			activeChar.setTraingCampInfo(holder);
		}
	}
}
