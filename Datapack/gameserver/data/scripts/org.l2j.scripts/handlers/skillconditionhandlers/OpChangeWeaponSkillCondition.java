package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.Weapon;
import org.w3c.dom.Node;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class OpChangeWeaponSkillCondition implements SkillCondition {

	private OpChangeWeaponSkillCondition() {
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		final Weapon weaponItem = caster.getActiveWeaponItem();
		if (weaponItem == null)
		{
			return false;
		}
		
		if (weaponItem.getChangeWeaponId() == 0)
		{
			return false;
		}

		return !caster.getActingPlayer().hasItemRequest();
	}

	public static final class Factory extends SkillConditionFactory {
		private static final OpChangeWeaponSkillCondition INSTANCE = new OpChangeWeaponSkillCondition();

		@Override
		public SkillCondition create(Node xmlNode) {
			return INSTANCE;
		}

		@Override
		public String conditionName() {
			return "OpChangeWeapon";
		}
	}
}
