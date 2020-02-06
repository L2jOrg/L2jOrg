package handlers.skillconditionhandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public class CheckSexSkillCondition implements ISkillCondition {

	private final boolean female;
	
	public CheckSexSkillCondition(StatsSet params)
	{
		female = params.getBoolean("isFemale");
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		return isPlayer(caster) && (caster.getActingPlayer().getAppearance().isFemale() == female);
	}
}
