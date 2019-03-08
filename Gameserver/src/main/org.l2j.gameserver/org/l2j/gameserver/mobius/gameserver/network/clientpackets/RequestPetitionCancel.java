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

import com.l2jmobius.Config;
import org.l2j.commons.network.PacketReader;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.mobius.gameserver.enums.ChatType;
import org.l2j.gameserver.mobius.gameserver.instancemanager.PetitionManager;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;

/**
 * <p>
 * Format: (c) d
 * <ul>
 * <li>d: Unknown</li>
 * </ul>
 * </p>
 * @author -Wooden-, TempyIncursion
 */
public final class RequestPetitionCancel extends IClientIncomingPacket
{
	
	// private int _unknown;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		// _unknown = packet.getInt(); This is pretty much a trigger packet.
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
		
		if (PetitionManager.getInstance().isPlayerInConsultation(activeChar))
		{
			if (activeChar.isGM())
			{
				PetitionManager.getInstance().endActivePetition(activeChar);
			}
			else
			{
				activeChar.sendPacket(SystemMessageId.YOUR_PETITION_IS_BEING_PROCESSED);
			}
		}
		else if (PetitionManager.getInstance().isPlayerPetitionPending(activeChar))
		{
			if (PetitionManager.getInstance().cancelActivePetition(activeChar))
			{
				final int numRemaining = Config.MAX_PETITIONS_PER_PLAYER - PetitionManager.getInstance().getPlayerTotalPetitionCount(activeChar);
				
				final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_PETITION_WAS_CANCELED_YOU_MAY_SUBMIT_S1_MORE_PETITION_S_TODAY);
				sm.addString(String.valueOf(numRemaining));
				activeChar.sendPacket(sm);
				
				// Notify all GMs that the player's pending petition has been cancelled.
				final String msgContent = activeChar.getName() + " has canceled a pending petition.";
				AdminData.getInstance().broadcastToGMs(new CreatureSay(activeChar.getObjectId(), ChatType.HERO_VOICE, "Petition System", msgContent));
			}
			else
			{
				activeChar.sendPacket(SystemMessageId.FAILED_TO_CANCEL_PETITION_PLEASE_TRY_AGAIN_LATER);
			}
		}
		else
		{
			activeChar.sendPacket(SystemMessageId.YOU_HAVE_NOT_SUBMITTED_A_PETITION);
		}
	}
}
