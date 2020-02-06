package handlers.skillconditionhandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;

import java.util.List;

/**
 * @author UnAfraid
 */
public class OpExistNpcSkillCondition implements ISkillCondition {

	public final List<Integer> npcIds;
	public final int range;
	public final boolean isAround;
	
	public OpExistNpcSkillCondition(StatsSet params) {
		npcIds = params.getList("npcIds", Integer.class);
		range = params.getInt("range");
		isAround = params.getBoolean("isAround");
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		return isAround == World.getInstance().hasAnyVisibleObjectInRange(caster, Npc.class, range, npc -> npcIds.contains(npc.getId()));
	}
}
