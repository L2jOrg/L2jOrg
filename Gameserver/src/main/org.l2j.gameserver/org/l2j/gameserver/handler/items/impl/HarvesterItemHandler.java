package org.l2j.gameserver.handler.items.impl;

import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.instances.MonsterInstance;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.components.SystemMsg;

public class HarvesterItemHandler extends DefaultItemHandler
{
	private static final int HARVESTER_SKILL_ID = 2098;

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		Player player;
		if(playable.isPlayer())
			player = (Player) playable;
		else if(playable.isPet())
			player = playable.getPlayer();
		else
			return false;

		GameObject target = player.getTarget();
		if(target == null || !target.isMonster())
		{
			player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}

		MonsterInstance monster = (MonsterInstance) player.getTarget();

		if(!monster.isDead())
		{
			player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
			return false;
		}

		Skill skill = SkillHolder.getInstance().getSkill(HARVESTER_SKILL_ID, 1);
		if(skill != null && skill.checkCondition(player, monster, false, false, true))
		{
			player.getAI().Cast(skill, monster);
			return true;
		}
		return false;
	}
}