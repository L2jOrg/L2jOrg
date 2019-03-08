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
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.EnchantSkillGroupsData;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.mobius.gameserver.enums.CategoryType;
import org.l2j.gameserver.mobius.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.mobius.gameserver.enums.SkillEnchantType;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.holders.EnchantSkillHolder;
import org.l2j.gameserver.mobius.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExEnchantSkillInfo;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExEnchantSkillInfoDetail;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExEnchantSkillResult;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author -Wooden-
 */
public final class RequestExEnchantSkill extends IClientIncomingPacket
{
	private static final Logger LOGGER = Logger.getLogger(RequestExEnchantSkill.class.getName());
	private static final Logger LOGGER_ENCHANT = Logger.getLogger("enchant.skills");
	
	private SkillEnchantType _type;
	private int _skillId;
	private int _skillLvl;
	private int _skillSubLvl;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		final int type = packet.getInt();
		if ((type < 0) || (type >= SkillEnchantType.values().length))
		{
			LOGGER.log(Level.WARNING, "Client: " + client + " send incorrect type " + type + " on packet: " + getClass().getSimpleName());
			return false;
		}
		
		_type = SkillEnchantType.values()[type];
		_skillId = packet.getInt();
		_skillLvl = packet.getShort();
		_skillSubLvl = packet.getShort();
		return true;
	}
	
	@Override
	public void runImpl()
	{
		if ((_skillId <= 0) || (_skillLvl <= 0) || (_skillSubLvl < 0))
		{
			return;
		}
		
		final L2PcInstance player = client.getActiveChar();
		if (player == null)
		{
			return;
		}
		
		if (!player.isInCategory(CategoryType.SIXTH_CLASS_GROUP))
		{
			return;
		}
		
		if (!player.isAllowedToEnchantSkills())
		{
			return;
		}
		
		if (player.isSellingBuffs())
		{
			return;
		}
		
		if (player.isInOlympiadMode())
		{
			return;
		}
		
		if (player.getPrivateStoreType() != PrivateStoreType.NONE)
		{
			return;
		}
		
		Skill skill = player.getKnownSkill(_skillId);
		if (skill == null)
		{
			return;
		}
		
		if (!skill.isEnchantable())
		{
			return;
		}
		
		if (skill.getLevel() != _skillLvl)
		{
			return;
		}
		
		if (skill.getSubLevel() > 0)
		{
			if (_type == SkillEnchantType.CHANGE)
			{
				final int group1 = (_skillSubLvl % 1000);
				final int group2 = (skill.getSubLevel() % 1000);
				if (group1 != group2)
				{
					LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Client: " + client + " send incorrect sub level group: " + group1 + " expected: " + group2);
					return;
				}
			}
			else if ((skill.getSubLevel() + 1) != _skillSubLvl)
			{
				LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Client: " + client + " send incorrect sub level: " + _skillSubLvl + " expected: " + skill.getSubLevel() + 1);
				return;
			}
		}
		
		final EnchantSkillHolder enchantSkillHolder = EnchantSkillGroupsData.getInstance().getEnchantSkillHolder(_skillSubLvl % 1000);
		
		// Verify if player has all the ingredients
		for (ItemHolder holder : enchantSkillHolder.getRequiredItems(_type))
		{
			if (player.getInventory().getInventoryItemCount(holder.getId(), 0) < holder.getCount())
			{
				player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
				return;
			}
		}
		
		// Consume all ingredients
		for (ItemHolder holder : enchantSkillHolder.getRequiredItems(_type))
		{
			if (!player.destroyItemByItemId("Skill enchanting", holder.getId(), holder.getCount(), player, true))
			{
				return;
			}
		}
		
		if (player.getSp() < enchantSkillHolder.getSp(_type))
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL);
			return;
		}
		
		player.getStat().removeExpAndSp(0, enchantSkillHolder.getSp(_type), false);
		
		switch (_type)
		{
			case BLESSED:
			case NORMAL:
			case IMMORTAL:
			{
				if (Rnd.get(100) <= enchantSkillHolder.getChance(_type))
				{
					final Skill enchantedSkill = SkillData.getInstance().getSkill(_skillId, _skillLvl, _skillSubLvl);
					if (Config.LOG_SKILL_ENCHANTS)
					{
						LOGGER_ENCHANT.log(Level.INFO, "Success, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", +" + enchantedSkill.getLevel() + " " + enchantedSkill.getSubLevel() + " - " + enchantedSkill.getName() + " (" + enchantedSkill.getId() + "), " + enchantSkillHolder.getChance(_type));
					}
					player.addSkill(enchantedSkill, true);
					
					final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.SKILL_ENCHANT_WAS_SUCCESSFUL_S1_HAS_BEEN_ENCHANTED);
					sm.addSkillName(_skillId);
					player.sendPacket(sm);
					
					player.sendPacket(ExEnchantSkillResult.STATIC_PACKET_TRUE);
				}
				else
				{
					final int newSubLevel = ((skill.getSubLevel() > 0) && (enchantSkillHolder.getEnchantFailLevel() > 0)) ? ((skill.getSubLevel() - (skill.getSubLevel() % 1000)) + enchantSkillHolder.getEnchantFailLevel()) : 0;
					final Skill enchantedSkill = SkillData.getInstance().getSkill(_skillId, _skillLvl, _type == SkillEnchantType.NORMAL ? newSubLevel : skill.getSubLevel());
					if (_type == SkillEnchantType.NORMAL)
					{
						player.addSkill(enchantedSkill, true);
						player.sendPacket(SystemMessageId.SKILL_ENCHANT_FAILED_THE_SKILL_WILL_BE_INITIALIZED);
					}
					else if (_type == SkillEnchantType.BLESSED)
					{
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SKILL_ENCHANT_FAILED_CURRENT_LEVEL_OF_ENCHANT_SKILL_S1_WILL_REMAIN_UNCHANGED).addSkillName(skill));
					}
					player.sendPacket(ExEnchantSkillResult.STATIC_PACKET_FALSE);
					
					if (Config.LOG_SKILL_ENCHANTS)
					{
						LOGGER_ENCHANT.log(Level.INFO, "Failed, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", +" + enchantedSkill.getLevel() + " " + enchantedSkill.getSubLevel() + " - " + enchantedSkill.getName() + " (" + enchantedSkill.getId() + "), " + enchantSkillHolder.getChance(_type));
					}
				}
				break;
			}
			case CHANGE:
			{
				if (Rnd.get(100) <= enchantSkillHolder.getChance(_type))
				{
					final Skill enchantedSkill = SkillData.getInstance().getSkill(_skillId, _skillLvl, _skillSubLvl);
					if (Config.LOG_SKILL_ENCHANTS)
					{
						LOGGER_ENCHANT.info("Success, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", +" + enchantedSkill.getLevel() + " " + enchantedSkill.getSubLevel() + " - " + enchantedSkill.getName() + " (" + enchantedSkill.getId() + "), " + enchantSkillHolder.getChance(_type));
					}
					player.addSkill(enchantedSkill, true);
					
					final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.ENCHANT_SKILL_ROUTE_CHANGE_WAS_SUCCESSFUL_LV_OF_ENCHANT_SKILL_S1_WILL_REMAIN);
					sm.addSkillName(_skillId);
					player.sendPacket(sm);
					
					player.sendPacket(ExEnchantSkillResult.STATIC_PACKET_TRUE);
				}
				else
				{
					final Skill enchantedSkill = SkillData.getInstance().getSkill(_skillId, _skillLvl, enchantSkillHolder.getEnchantFailLevel());
					player.addSkill(enchantedSkill, true);
					player.sendPacket(SystemMessageId.SKILL_ENCHANT_FAILED_THE_SKILL_WILL_BE_INITIALIZED);
					player.sendPacket(ExEnchantSkillResult.STATIC_PACKET_FALSE);
					
					if (Config.LOG_SKILL_ENCHANTS)
					{
						LOGGER_ENCHANT.log(Level.INFO, "Failed, Character:" + player.getName() + " [" + player.getObjectId() + "] Account:" + player.getAccountName() + " IP:" + player.getIPAddress() + ", +" + enchantedSkill.getLevel() + " " + enchantedSkill.getSubLevel() + " - " + enchantedSkill.getName() + " (" + enchantedSkill.getId() + "), " + enchantSkillHolder.getChance(_type));
					}
				}
				break;
			}
		}
		
		player.broadcastUserInfo();
		player.sendSkillList();
		
		skill = player.getKnownSkill(_skillId);
		player.sendPacket(new ExEnchantSkillInfo(skill.getId(), skill.getLevel(), skill.getSubLevel(), skill.getSubLevel()));
		player.sendPacket(new ExEnchantSkillInfoDetail(_type, skill.getId(), skill.getLevel(), Math.min(skill.getSubLevel() + 1, EnchantSkillGroupsData.MAX_ENCHANT_LEVEL), player));
		player.updateShortCuts(skill.getId(), skill.getLevel(), skill.getSubLevel());
	}
}
