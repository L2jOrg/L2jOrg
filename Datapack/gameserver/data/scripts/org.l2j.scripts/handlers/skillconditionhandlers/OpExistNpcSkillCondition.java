package handlers.skillconditionhandlers;

import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.world.World;
import org.w3c.dom.Node;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class OpExistNpcSkillCondition implements SkillCondition {

	public final IntSet npcIds;
	public final int range;
	public final boolean isAround;

	private OpExistNpcSkillCondition(IntSet npcs, int range, boolean around) {
		this.npcIds = npcs;
		this.range = range;
		this.isAround = around;
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		return isAround == World.getInstance().hasAnyVisibleObjectInRange(caster, Npc.class, range, npc -> npcIds.contains(npc.getId()));
	}


	public static final class Factory extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			var attr = xmlNode.getAttributes();
			var npcs  = parseIntSet(xmlNode.getFirstChild());
			return new OpExistNpcSkillCondition(npcs, parseInt(attr, "range"), parseBoolean(attr, "around"));
		}

		@Override
		public String conditionName() {
			return "exists-npc";
		}
	}
}
