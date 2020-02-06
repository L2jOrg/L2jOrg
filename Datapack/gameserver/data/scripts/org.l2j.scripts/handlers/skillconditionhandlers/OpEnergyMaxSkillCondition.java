package handlers.skillconditionhandlers;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * @author UnAfraid
 */
public class OpEnergyMaxSkillCondition implements ISkillCondition {

	public final int charges;
	
	public OpEnergyMaxSkillCondition(StatsSet params) {
		charges = params.getInt("amount");
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		if (caster.getActingPlayer().getCharges() >= charges) {
			caster.sendPacket(SystemMessageId.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY);
			return false;
		}
		return true;
	}
}
