package l2s.gameserver.handler.items.impl;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;

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