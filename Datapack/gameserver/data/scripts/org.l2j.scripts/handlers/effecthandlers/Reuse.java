package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.util.MathUtil;

/**
 * @author Sdw
 */
public class Reuse extends AbstractEffect {
	private final int magicType;
	private final double amount;
	
	public Reuse(StatsSet params) {
		magicType = params.getInt("magicType", 0);
		amount = params.getDouble("amount", 0);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		effected.getStats().mergeReuseTypeValue(magicType, (amount / 100) + 1, MathUtil::mul);
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.getStats().mergeReuseTypeValue(magicType, (amount / 100) + 1, MathUtil::div);
	}
}
