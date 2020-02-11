package handlers.skillconditionhandlers;

import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class TargetRaceSkillCondition implements SkillCondition {

	public final Race race;

	protected TargetRaceSkillCondition(Race race) {
		this.race = race;
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		if (!isCreature(target)) {
			return false;
		}
		final Creature targetCreature = (Creature) target;
		return targetCreature.getRace() == race;
	}
}
