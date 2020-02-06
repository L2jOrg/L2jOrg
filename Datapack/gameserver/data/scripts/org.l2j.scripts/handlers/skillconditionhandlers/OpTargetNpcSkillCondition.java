package handlers.skillconditionhandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;

import java.util.List;

import static org.l2j.gameserver.util.GameUtils.isNpc;

/**
 * @author Sdw
 */
public class OpTargetNpcSkillCondition implements ISkillCondition
{
	public final List<Integer> _npcId;
	
	public OpTargetNpcSkillCondition(StatsSet params)
	{
		_npcId = params.getList("npcIds", Integer.class);
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		return isNpc(target) && _npcId.contains(target.getId());
	}
}
