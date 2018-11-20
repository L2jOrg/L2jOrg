package org.l2j.gameserver.handler.items.impl;

import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;

public class PetSummonItemHandler extends DefaultItemHandler
{
	private static final int SUMMON_SKILL_ID = 2046;

	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(playable == null || !playable.isPlayer())
			return false;
		Player player = (Player) playable;

		player.setPetControlItem(item);
		player.getAI().Cast(SkillHolder.getInstance().getSkill(SUMMON_SKILL_ID, 1), player, false, true);
		return true;
	}
}