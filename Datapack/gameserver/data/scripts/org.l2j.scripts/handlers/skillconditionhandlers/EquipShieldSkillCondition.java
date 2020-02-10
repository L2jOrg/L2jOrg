package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.type.ArmorType;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.w3c.dom.Node;

/**
 * @author UnAfraid
 */
public class EquipShieldSkillCondition implements SkillCondition {

	private EquipShieldSkillCondition(){
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		final ItemTemplate shield = caster.getSecondaryWeaponItem();
		return (shield != null) && (shield.getItemType() == ArmorType.SHIELD);
	}

	public static final class Factory extends SkillConditionFactory{

		private static final EquipShieldSkillCondition INSTANCE = new EquipShieldSkillCondition();

		@Override
		public SkillCondition create(Node xmlNode) {
			return INSTANCE;
		}

		@Override
		public String conditionName() {
			return "EquipShield";
		}
	}
}