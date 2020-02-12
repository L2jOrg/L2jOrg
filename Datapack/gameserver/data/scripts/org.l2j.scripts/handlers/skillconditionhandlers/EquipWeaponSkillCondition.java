package handlers.skillconditionhandlers;

import java.util.Arrays;
import java.util.List;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.w3c.dom.Node;

import static java.util.Objects.nonNull;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class EquipWeaponSkillCondition implements SkillCondition {
	public int mask;

	private EquipWeaponSkillCondition(int mask) {
		this.mask = mask;
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		final ItemTemplate weapon = caster.getActiveWeaponItem();
		return nonNull(weapon) && (weapon.getItemMask() & mask) != 0;
	}

	public static final class Factory extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			int mask = Arrays.stream(xmlNode.getFirstChild().getTextContent().split(Util.SPACE))
					.mapToInt(s -> WeaponType.valueOf(s).mask()).reduce(0, (a, b) -> a | b);
			return new EquipWeaponSkillCondition(mask);
		}

		@Override
		public String conditionName() {
			return "weapon";
		}
	}
}
