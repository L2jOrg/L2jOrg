package handlers.skillconditionhandlers;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public class TargetMyPartySkillCondition implements ISkillCondition {

	public final boolean _includeMe;
	
	public TargetMyPartySkillCondition(StatsSet params)
	{
		_includeMe = params.getBoolean("includeMe");
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {

		if (!isPlayer(target)) {
			return false;
		}
		
		final Party party = caster.getParty();
		final Party targetParty = target.getActingPlayer().getParty();
		return ((party == null) ? (_includeMe && (caster == target)) : (_includeMe ? party == targetParty : (party == targetParty) && (caster != target)));
	}
}
