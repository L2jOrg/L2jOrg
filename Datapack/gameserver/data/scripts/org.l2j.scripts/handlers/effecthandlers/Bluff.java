package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.network.serverpackets.StartRotation;
import org.l2j.gameserver.network.serverpackets.StopRotation;

/**
 * Bluff effect implementation.
 * @author decad
 * @author JoeAlisson
 */
public final class Bluff extends AbstractEffect {

	private final int power;
	
	private Bluff(StatsSet params) {
		power = params.getInt("power", 100);
	}
	
	@Override
	public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
	{
		return Formulas.calcProbability(power, effector, effected, skill);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		// Headquarters NPC should not rotate
		if (effected.getId() == 35062 || effected.isRaid() || effected.isRaidMinion()) {
			return;
		}
		
		effected.broadcastPacket(new StartRotation(effected.getObjectId(), effected.getHeading(), 1, 65535));
		effected.broadcastPacket(new StopRotation(effected.getObjectId(), effector.getHeading(), 65535));
		effected.setHeading(effector.getHeading());
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new Bluff(data);
		}

		@Override
		public String effectName() {
			return "Bluff";
		}
	}

}
