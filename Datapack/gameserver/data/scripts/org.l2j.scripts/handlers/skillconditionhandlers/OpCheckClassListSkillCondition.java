package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.enums.SkillConditionAffectType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class OpCheckClassListSkillCondition implements SkillCondition {

	public final Set<ClassId> classIds;
	public final SkillConditionAffectType affectType;

	private OpCheckClassListSkillCondition(Set<ClassId> classIds, SkillConditionAffectType affect) {
		this.classIds = classIds;
		this.affectType = affect;
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		return switch (affectType) {
			case CASTER -> isPlayer(caster) && classIds.contains(caster.getActingPlayer().getClassId());
			case TARGET -> isPlayer(target) && classIds.contains(target.getActingPlayer().getClassId());
			default ->  false;
		};
	}

	public static final class Factory extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			var affect = parseEnum(xmlNode.getAttributes(), SkillConditionAffectType.class, "affect");
			var listNode = xmlNode.getFirstChild();
			Set<ClassId> classIds = Collections.emptySet();
			if(nonNull(listNode)) {
				classIds = Arrays.stream(listNode.getTextContent().split(" ")).map(ClassId::valueOf).collect(Collectors.toSet());
			}
			return new OpCheckClassListSkillCondition(classIds, affect);
		}

		@Override
		public String conditionName() {
			return "class";
		}
	}
}