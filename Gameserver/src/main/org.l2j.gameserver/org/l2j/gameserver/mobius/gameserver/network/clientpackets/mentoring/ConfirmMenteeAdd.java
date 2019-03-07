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
package org.l2j.gameserver.mobius.gameserver.network.clientpackets.mentoring;

import com.l2jmobius.commons.database.DatabaseFactory;
import com.l2jmobius.commons.network.PacketReader;
import com.l2jmobius.gameserver.enums.CategoryType;
import com.l2jmobius.gameserver.instancemanager.MentorManager;
import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.events.EventDispatcher;
import com.l2jmobius.gameserver.model.events.impl.character.player.OnPlayerMenteeAdd;
import com.l2jmobius.gameserver.network.L2GameClient;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.clientpackets.IClientIncomingPacket;
import com.l2jmobius.gameserver.network.serverpackets.SystemMessage;
import com.l2jmobius.gameserver.network.serverpackets.mentoring.ExMentorList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Level;

/**
 * @author Gnacik, UnAfraid
 */
public class ConfirmMenteeAdd implements IClientIncomingPacket
{
	// public final static int MENTEE_CERT = 33800;
	
	private int _confirmed;
	private String _mentor;
	
	@Override
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_confirmed = packet.readD();
		_mentor = packet.readS();
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		final L2PcInstance mentee = client.getActiveChar();
		if (mentee == null)
		{
			return;
		}
		
		final L2PcInstance mentor = L2World.getInstance().getPlayer(_mentor);
		if (mentor == null)
		{
			return;
		}
		
		if (_confirmed == 0)
		{
			mentee.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_DECLINED_S1_S_MENTORING_OFFER).addString(mentor.getName()));
			mentor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_DECLINED_BECOMING_YOUR_MENTEE).addString(mentee.getName()));
		}
		else if (validate(mentor, mentee))
		{
			try (Connection con = DatabaseFactory.getConnection();
				PreparedStatement statement = con.prepareStatement("INSERT INTO character_mentees (charId, mentorId) VALUES (?, ?)"))
			{
				statement.setInt(1, mentee.getObjectId());
				statement.setInt(2, mentor.getObjectId());
				statement.execute();
				
				MentorManager.getInstance().addMentor(mentor.getObjectId(), mentee.getObjectId());
				
				// Notify to scripts
				EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMenteeAdd(mentor, mentee), mentor);
				
				mentor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FROM_NOW_ON_S1_WILL_BE_YOUR_MENTEE).addString(mentee.getName()));
				mentor.sendPacket(new ExMentorList(mentor));
				
				mentee.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FROM_NOW_ON_S1_WILL_BE_YOUR_MENTOR).addString(mentor.getName()));
				mentee.sendPacket(new ExMentorList(mentee));
			}
			catch (Exception e)
			{
				LOGGER.log(Level.WARNING, e.getMessage(), e);
			}
		}
	}
	
	/**
	 * @param mentor
	 * @param mentee
	 * @return
	 */
	public static boolean validate(L2PcInstance mentor, L2PcInstance mentee)
	{
		if ((mentor == null) || (mentee == null))
		{
			return false;
		}
		else if (!mentee.isOnline())
		{
			mentor.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
			return false;
		}
		else if (!mentor.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
		{
			mentor.sendPacket(SystemMessageId.YOU_MUST_AWAKEN_IN_ORDER_TO_BECOME_A_MENTOR);
			return false;
		}
		else if (MentorManager.getInstance().getMentorPenalty(mentor.getObjectId()) > System.currentTimeMillis())
		{
			long remainingTime = (MentorManager.getInstance().getMentorPenalty(mentor.getObjectId()) - System.currentTimeMillis()) / 1000;
			final int days = (int) (remainingTime / 86400);
			remainingTime = remainingTime % 86400;
			final int hours = (int) (remainingTime / 3600);
			remainingTime = remainingTime % 3600;
			final int minutes = (int) (remainingTime / 60);
			final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_CAN_BOND_WITH_A_NEW_MENTEE_IN_S1_DAY_S_S2_HOUR_S_S3_MINUTE_S);
			msg.addInt(days);
			msg.addInt(hours);
			msg.addInt(minutes);
			mentor.sendPacket(msg);
			return false;
		}
		else if (mentor.getObjectId() == mentee.getObjectId())
		{
			mentor.sendPacket(SystemMessageId.YOU_CANNOT_BECOME_YOUR_OWN_MENTEE);
			return false;
		}
		else if (mentee.getLevel() >= 86)
		{
			mentor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_ABOVE_LEVEL_85_AND_CANNOT_BECOME_A_MENTEE).addString(mentee.getName()));
			return false;
		}
		else if (mentee.isSubClassActive())
		{
			mentor.sendPacket(SystemMessageId.INVITATION_CAN_OCCUR_ONLY_WHEN_THE_MENTEE_IS_IN_MAIN_CLASS_STATUS);
			return false;
		}
		
		// else if (mentee.getInventory().getItemByItemId(MENTEE_CERT) == null)
		// {
		// mentor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DOES_NOT_HAVE_THE_ITEM_NEEDED_TO_BECOME_A_MENTEE).addString(mentee));
		// return false;
		// }
		else if ((MentorManager.getInstance().getMentees(mentor.getObjectId()) != null) && (MentorManager.getInstance().getMentees(mentor.getObjectId()).size() >= 3))
		{
			mentor.sendPacket(SystemMessageId.A_MENTOR_CAN_HAVE_UP_TO_3_MENTEES_AT_THE_SAME_TIME);
			return false;
		}
		else if (mentee.isMentee())
		{
			mentor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ALREADY_HAS_A_MENTOR).addString(mentee.getName()));
			return false;
		}
		return true;
	}
}
