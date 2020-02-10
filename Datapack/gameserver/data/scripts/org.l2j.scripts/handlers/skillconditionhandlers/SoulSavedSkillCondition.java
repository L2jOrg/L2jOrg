package handlers.skillconditionhandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public class SoulSavedSkillCondition implements SkillCondition {

	public final int amount;
	
	public SoulSavedSkillCondition(StatsSet params)
	{
		amount = params.getInt("amount");
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		return isPlayer(caster) && caster.getActingPlayer().getChargedSouls() >= amount;
	}
}
