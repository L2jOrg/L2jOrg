package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.MoveType;
import org.l2j.gameserver.model.stats.Stat;

/**
 * StatByMoveType effect implementation.
 * @author UnAfraid
 */
public class StatByMoveType extends AbstractEffect {
	private final Stat stat;
	private final MoveType type;
	private final double value;
	
	public StatByMoveType(StatsSet params) {
		stat = params.getEnum("stat", Stat.class);
		type = params.getEnum("type", MoveType.class);
		value = params.getDouble("value");
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		effected.getStats().mergeMoveTypeValue(stat, type, value);
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.getStats().mergeMoveTypeValue(stat, type, -value);
	}
	
	@Override
	public boolean onActionTime(Creature effector, Creature effected, Skill skill, Item item)
	{
		return skill.isPassive() || skill.isToggle();
	}
}
