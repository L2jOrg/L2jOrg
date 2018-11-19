package l2s.gameserver.skills.skillclasses;

import java.util.List;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.network.l2.s2c.RecipeBookItemListPacket;
import l2s.gameserver.templates.StatsSet;

public class Craft extends Skill
{
	private final boolean _dwarven;

	public Craft(StatsSet set)
	{
		super(set);
		_dwarven = set.getBool("isDwarven");
	}

	@Override
	public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first)
	{
		if(!super.checkCondition(activeChar, target, forceUse, dontMove, first))
			return false;

		Player p = (Player) activeChar;
		if(p.isInStoreMode() || p.isProcessingRequest())
			return false;

		return true;
	}

	@Override
	public void onEndCast(Creature activeChar, List<Creature> targets)
	{
		super.onEndCast(activeChar, targets);
		activeChar.sendPacket(new RecipeBookItemListPacket((Player) activeChar, _dwarven));
	}
}