package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.MoveType;
import org.l2j.gameserver.model.stats.Stat;

/**
 * StatByMoveType effect implementation.
 * @author UnAfraid
 * @author JoeAlisson
 */
public class StatByMoveType extends AbstractEffect {
	private final Stat stat;
	private final MoveType type;
	private final double power;
	
	private StatByMoveType(StatsSet params) {
		stat = params.getEnum("stat", Stat.class);
		type = params.getEnum("type", MoveType.class);
		power = params.getDouble("power");
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		effected.getStats().mergeMoveTypeValue(stat, type, power);
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.getStats().mergeMoveTypeValue(stat, type, -power);
	}
	
	@Override
	public boolean onActionTime(Creature effector, Creature effected, Skill skill, Item item)
	{
		return skill.isPassive() || skill.isToggle();
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new StatByMoveType(data);
		}

		@Override
		public String effectName() {
			return "stat-by-move-type";
		}
	}
}
