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
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ValidateLocation;
import org.l2j.gameserver.mobius.gameserver.util.Broadcast;
import org.l2j.gameserver.mobius.gameserver.util.Util;

/**
 * Fromat:(ch) dddddc
 * @author -Wooden-
 */
public final class RequestExMagicSkillUseGround extends IClientIncomingPacket
{
	private int _x;
	private int _y;
	private int _z;
	private int _skillId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_x = packet.getInt();
		_y = packet.getInt();
		_z = packet.getInt();
		_skillId = packet.getInt();
		_ctrlPressed = packet.getInt() != 0;
		_shiftPressed = packet.get() != 0;
		return true;
	}
	
	@Override
	public void runImpl()
	{
		// Get the current L2PcInstance of the player
		final L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		// Get the level of the used skill
		final int level = activeChar.getSkillLevel(_skillId);
		if (level <= 0)
		{
			client.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Get the L2Skill template corresponding to the skillID received from the client
		final Skill skill = SkillData.getInstance().getSkill(_skillId, level);
		
		// Check the validity of the skill
		if (skill != null)
		{
			activeChar.setCurrentSkillWorldPosition(new Location(_x, _y, _z));
			
			// normally magicskilluse packet turns char client side but for these skills, it doesn't (even with correct target)
			activeChar.setHeading(Util.calculateHeadingFrom(activeChar.getX(), activeChar.getY(), _x, _y));
			Broadcast.toKnownPlayers(activeChar, new ValidateLocation(activeChar));
			
			activeChar.useMagic(skill, null, _ctrlPressed, _shiftPressed);
		}
		else
		{
			client.sendPacket(ActionFailed.STATIC_PACKET);
			LOGGER.warning("No skill found with id " + _skillId + " and level " + level + " !!");
		}
	}
}
