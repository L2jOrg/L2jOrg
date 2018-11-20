package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.xml.holder.SkillAcquireHolder;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.base.AcquireType;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.instances.VillageMasterPledgeBypasses;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.model.pledge.SubUnit;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import org.l2j.gameserver.skills.SkillEntry;
import org.l2j.gameserver.templates.item.data.ItemData;
import org.l2j.gameserver.utils.ItemFunctions;
import org.l2j.gameserver.utils.MulticlassUtils;

public class RequestAquireSkill extends L2GameClientPacket
{
	private AcquireType _type;
	private int _id, _level, _subUnit;

	@Override
	protected void readImpl()
	{
		_id = readD();
		_level = readD();
		_type = AcquireType.getById(readD());
		if(_type == AcquireType.SUB_UNIT)
			_subUnit = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null || player.isTransformed() || _type == null)
			return;

		NpcInstance trainer = player.getLastNpc();
		if((trainer == null || !player.checkInteractionDistance(trainer)) && !player.isGM())
			trainer = null;

		SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(_id, _level);
		if(skillEntry == null)
			return;

		ClassId selectedMultiClassId = player.getSelectedMultiClassId();
		if(_type == AcquireType.MULTICLASS)
		{
			if(selectedMultiClassId == null)
				return;
		}
		else
			selectedMultiClassId = null;

		if(!SkillAcquireHolder.getInstance().isSkillPossible(player, selectedMultiClassId, skillEntry.getTemplate(), _type))
			return;

		SkillLearn skillLearn = SkillAcquireHolder.getInstance().getSkillLearn(player, selectedMultiClassId, _id, _level, _type);
		if(skillLearn == null)
			return;

		if(skillLearn.getMinLevel() > player.getLevel())
			return;

		if(!checkSpellbook(player, _type, skillLearn))
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_NECESSARY_MATERIALS_OR_PREREQUISITES_TO_LEARN_THIS_SKILL);
			return;
		}

		switch(_type)
		{
			case NORMAL:
				learnSimpleNextLevel(player, _type, skillLearn, skillEntry, true);
				break;
			case FISHING:
				if(trainer != null)
				{
					learnSimpleNextLevel(player, _type, skillLearn, skillEntry, false);
					NpcInstance.showFishingSkillList(player);
				}
				break;
			case CLAN:
				if(trainer != null)
					learnClanSkill(player, skillLearn, trainer, skillEntry);
				break;
			case SUB_UNIT:
				if(trainer != null)
					learnSubUnitSkill(player, skillLearn, trainer, skillEntry, _subUnit);
				break;
			case MULTICLASS:
				learnSimpleNextLevel(player, _type, skillLearn, skillEntry, true);
				MulticlassUtils.showMulticlassAcquireList(player, selectedMultiClassId);
				break;
			case CUSTOM:
				player.getListeners().onLearnCustomSkill(skillLearn);
				break;
		}
	}

	/**
	 * Изучение следующего возможного уровня скилла
	 */
	private static void learnSimpleNextLevel(Player player, AcquireType type, SkillLearn skillLearn, SkillEntry skillEntry, boolean normal)
	{
		final int skillLevel = player.getSkillLevel(skillLearn.getId(), 0);
		if(skillLevel != skillLearn.getLevel() - 1)
			return;

		learnSimple(player, type, skillLearn, skillEntry, normal);
	}

	private static void learnSimple(Player player, AcquireType type, SkillLearn skillLearn, SkillEntry skillEntry, boolean normal)
	{
		if(player.getSp() < skillLearn.getCost())
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_THIS_SKILL);
			return;
		}

		player.getInventory().writeLock();
		try
		{
			for(ItemData item : skillLearn.getRequiredItemsForLearn(type))
			{
				if(!ItemFunctions.haveItem(player, item.getId(), item.getCount()))
					return;
			}
			for(ItemData item : skillLearn.getRequiredItemsForLearn(type))
				ItemFunctions.deleteItem(player, item.getId(), item.getCount(), true);
		}
		finally
		{
			player.getInventory().writeUnlock();
		}

		player.sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skillEntry.getId(), skillEntry.getLevel()));

		player.setSp(player.getSp() - skillLearn.getCost());
		player.addSkill(skillEntry, true);

		if(normal)
			player.rewardSkills(false);

		player.sendUserInfo();
		player.updateStats();

		player.sendSkillList(skillEntry.getId());

		player.updateSkillShortcuts(skillEntry.getId(), skillEntry.getLevel());
	}

	private static void learnClanSkill(Player player, SkillLearn skillLearn, NpcInstance trainer, SkillEntry skillEntry)
	{
		if(!player.isClanLeader())
		{
			player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			return;
		}

		Clan clan = player.getClan();
		final int skillLevel = clan.getSkillLevel(skillLearn.getId(), 0);
		if(skillLevel != skillLearn.getLevel() - 1) // можно выучить только следующий уровень
			return;
		if(clan.getReputationScore() < skillLearn.getCost())
		{
			player.sendPacket(SystemMsg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
			return;
		}

		player.getInventory().writeLock();
		try
		{
			for(ItemData item : skillLearn.getRequiredItemsForLearn(AcquireType.CLAN))
			{
				if(!ItemFunctions.haveItem(player, item.getId(), item.getCount()))
					return;
			}
			for(ItemData item : skillLearn.getRequiredItemsForLearn(AcquireType.CLAN))
				ItemFunctions.deleteItem(player, item.getId(), item.getCount(), true);
		}
		finally
		{
			player.getInventory().writeUnlock();
		}

		clan.incReputation(-skillLearn.getCost(), false, "AquireSkill: " + skillLearn.getId() + ", lvl " + skillLearn.getLevel());
		clan.addSkill(skillEntry, true);
		clan.broadcastToOnlineMembers(new SystemMessagePacket(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skillEntry.getTemplate()));

		VillageMasterPledgeBypasses.showClanSkillList(trainer, player);
	}

	private static void learnSubUnitSkill(Player player, SkillLearn skillLearn, NpcInstance trainer, SkillEntry skillEntry, int id)
	{
		Clan clan = player.getClan();
		if(clan == null)
			return;
		SubUnit sub = clan.getSubUnit(id);
		if(sub == null)
			return;

		if((player.getClanPrivileges() & Clan.CP_CL_TROOPS_FAME) != Clan.CP_CL_TROOPS_FAME)
		{
			player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}

		int lvl = sub.getSkillLevel(skillLearn.getId(), 0);
		if(lvl >= skillLearn.getLevel())
		{
			player.sendPacket(SystemMsg.THIS_SQUAD_SKILL_HAS_ALREADY_BEEN_ACQUIRED);
			return;
		}

		if(lvl != (skillLearn.getLevel() - 1))
		{
			player.sendPacket(SystemMsg.THE_PREVIOUS_LEVEL_SKILL_HAS_NOT_BEEN_LEARNED);
			return;
		}

		if(clan.getReputationScore() < skillLearn.getCost())
		{
			player.sendPacket(SystemMsg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
			return;
		}

		player.getInventory().writeLock();
		try
		{
			for(ItemData item : skillLearn.getRequiredItemsForLearn(AcquireType.SUB_UNIT))
			{
				if(!ItemFunctions.haveItem(player, item.getId(), item.getCount()))
					return;
			}
			for(ItemData item : skillLearn.getRequiredItemsForLearn(AcquireType.SUB_UNIT))
				ItemFunctions.deleteItem(player, item.getId(), item.getCount(), true);
		}
		finally
		{
			player.getInventory().writeUnlock();
		}

		clan.incReputation(-skillLearn.getCost(), false, "AquireSkill2: " + skillLearn.getId() + ", lvl " + skillLearn.getLevel());
		sub.addSkill(skillEntry, true);
		player.sendPacket(new SystemMessagePacket(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skillEntry.getTemplate()));

		if(trainer != null)
			NpcInstance.showSubUnitSkillList(player);
	}

	private static boolean checkSpellbook(Player player, AcquireType type, SkillLearn skillLearn)
	{
		for(ItemData item : skillLearn.getRequiredItemsForLearn(type))
		{
			if(!ItemFunctions.haveItem(player, item.getId(), item.getCount()))
				return false;
		}

		return true;
	}
}