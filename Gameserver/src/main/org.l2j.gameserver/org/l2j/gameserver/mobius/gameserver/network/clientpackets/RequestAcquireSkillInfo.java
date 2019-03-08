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
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.mobius.gameserver.enums.CategoryType;
import org.l2j.gameserver.mobius.gameserver.enums.Race;
import org.l2j.gameserver.mobius.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.mobius.gameserver.model.L2SkillLearn;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.AcquireSkillInfo;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExAcquireSkillInfo;

/**
 * Request Acquire Skill Info client packet implementation.
 * @author Zoey76
 */
public final class RequestAcquireSkillInfo extends IClientIncomingPacket
{
	private int _id;
	private int _level;
	private AcquireSkillType _skillType;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_id = packet.getInt();
		_level = packet.getInt();
		_skillType = AcquireSkillType.getAcquireSkillType(packet.getInt());
		return true;
	}
	
	@Override
	public void runImpl()
	{
		if ((_id <= 0) || (_level <= 0))
		{
			LOGGER.warning(RequestAcquireSkillInfo.class.getSimpleName() + ": Invalid Id: " + _id + " or level: " + _level + "!");
			return;
		}
		
		final L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		final L2Npc trainer = activeChar.getLastFolkNPC();
		if ((_skillType != AcquireSkillType.CLASS) && ((trainer == null) || !trainer.isNpc() || (!trainer.canInteract(activeChar) && !activeChar.isGM())))
		{
			return;
		}
		
		final Skill skill = SkillData.getInstance().getSkill(_id, _level);
		if (skill == null)
		{
			LOGGER.warning("Skill Id: " + _id + " level: " + _level + " is undefined. " + RequestAcquireSkillInfo.class.getName() + " failed.");
			return;
		}
		
		// Hack check. Doesn't apply to all Skill Types
		final int prevSkillLevel = activeChar.getSkillLevel(_id);
		if ((prevSkillLevel > 0) && !((_skillType == AcquireSkillType.TRANSFER) || (_skillType == AcquireSkillType.SUBPLEDGE)))
		{
			if (prevSkillLevel == _level)
			{
				LOGGER.warning(RequestAcquireSkillInfo.class.getSimpleName() + ": Player " + activeChar.getName() + " is requesting info for a skill that already knows, Id: " + _id + " level: " + _level + "!");
			}
			else if (prevSkillLevel != (_level - 1))
			{
				LOGGER.warning(RequestAcquireSkillInfo.class.getSimpleName() + ": Player " + activeChar.getName() + " is requesting info for skill Id: " + _id + " level " + _level + " without knowing it's previous level!");
			}
		}
		
		final L2SkillLearn s = SkillTreesData.getInstance().getSkillLearn(_skillType, _id, _level, activeChar);
		if (s == null)
		{
			return;
		}
		
		switch (_skillType)
		{
			case TRANSFORM:
			case FISHING:
			case SUBCLASS:
			case COLLECT:
			case TRANSFER:
			case DUALCLASS:
			{
				client.sendPacket(new AcquireSkillInfo(_skillType, s));
				break;
			}
			case CLASS:
			{
				client.sendPacket(new ExAcquireSkillInfo(activeChar, s));
				break;
			}
			case PLEDGE:
			{
				if (!activeChar.isClanLeader())
				{
					return;
				}
				client.sendPacket(new AcquireSkillInfo(_skillType, s));
				break;
			}
			case SUBPLEDGE:
			{
				if (!activeChar.isClanLeader() || !activeChar.hasClanPrivilege(ClanPrivilege.CL_TROOPS_FAME))
				{
					return;
				}
				client.sendPacket(new AcquireSkillInfo(_skillType, s));
				break;
			}
			case ALCHEMY:
			{
				if (activeChar.getRace() != Race.ERTHEIA)
				{
					return;
				}
				client.sendPacket(new AcquireSkillInfo(_skillType, s));
				break;
			}
			case REVELATION:
			{
				if ((activeChar.getLevel() < 85) || !activeChar.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
				{
					return;
				}
				client.sendPacket(new AcquireSkillInfo(_skillType, s));
				break;
			}
			case REVELATION_DUALCLASS:
			{
				if (!activeChar.isSubClassActive() || !activeChar.isDualClassActive())
				{
					return;
				}
				client.sendPacket(new AcquireSkillInfo(_skillType, s));
				break;
			}
		}
	}
}
