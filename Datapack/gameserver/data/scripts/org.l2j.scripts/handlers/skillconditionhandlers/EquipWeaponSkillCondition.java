package handlers.skillconditionhandlers;

import java.util.List;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;

import static java.util.Objects.nonNull;

/**
 * @author Sdw
 */
public class EquipWeaponSkillCondition implements ISkillCondition {
	public int mask = 0;
	
	public EquipWeaponSkillCondition(StatsSet params) {
		final List<WeaponType> weaponTypes = params.getEnumList("weaponType", WeaponType.class);
		if (nonNull(weaponTypes)) {
			for (WeaponType weaponType : weaponTypes) {
				mask |= weaponType.mask();
			}
		}
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		final ItemTemplate weapon = caster.getActiveWeaponItem();
		return nonNull(weapon) && (weapon.getItemMask() & mask) != 0;
	}
}
