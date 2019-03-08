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
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;

public final class RequestMagicSkillUse extends IClientIncomingPacket
{
	private int _magicId;
	private boolean _ctrlPressed;
	private boolean _shiftPressed;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_magicId = packet.getInt(); // Identifier of the used skill
		_ctrlPressed = packet.getInt() != 0; // True if it's a ForceAttack : Ctrl pressed
		_shiftPressed = packet.get() != 0; // True if Shift pressed
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
		Skill skill = activeChar.getKnownSkill(_magicId);
		if (skill == null)
		{
			if ((_magicId == CommonSkill.HAIR_ACCESSORY_SET.getId()) //
				|| ((_magicId > 1565) && (_magicId < 1570))) // subClass change SkillTree
			{
				skill = SkillData.getInstance().getSkill(_magicId, 1);
			}
			else
			{
				activeChar.sendPacket(ActionFailed.STATIC_PACKET);
				if (_magicId > 0)
				{
					LOGGER.warning("Skill Id " + _magicId + " not found in player: " + activeChar);
				}
				return;
			}
		}
		
		// Skill is blocked from player use.
		if (skill.isBlockActionUseSkill())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		// Avoid Use of Skills in AirShip.
		if (activeChar.isInAirShip())
		{
			activeChar.sendPacket(SystemMessageId.THIS_ACTION_IS_PROHIBITED_WHILE_MOUNTED_OR_ON_AN_AIRSHIP);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		activeChar.useMagic(skill, null, _ctrlPressed, _shiftPressed);
	}
}
