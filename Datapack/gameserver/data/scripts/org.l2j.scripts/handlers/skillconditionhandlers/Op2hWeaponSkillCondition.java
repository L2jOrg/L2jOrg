package handlers.skillconditionhandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.Weapon;
import org.l2j.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.Skill;

import java.util.ArrayList;
import java.util.List;

import static org.l2j.gameserver.model.items.BodyPart.RIGHT_HAND;
import static org.l2j.gameserver.model.items.BodyPart.TWO_HAND;

/**
 * @author UnAfraid
 */
public class Op2hWeaponSkillCondition implements SkillCondition {

    public final List<WeaponType> _weaponTypes = new ArrayList<>();

    public Op2hWeaponSkillCondition(StatsSet params) {
        final List<String> weaponTypes = params.getList("weaponType", String.class);
        if (weaponTypes != null) {
            weaponTypes.stream().map(WeaponType::valueOf).forEach(_weaponTypes::add);
        }
    }

    @Override
    public boolean canUse(Creature caster, Skill skill, WorldObject target) {
        final Weapon weapon = caster.getActiveWeaponItem();
        if (weapon == null) {
            return false;
        }
        return _weaponTypes.stream().anyMatch(weaponType -> weapon.getItemType() == weaponType && weapon.getBodyPart().isAnyOf(TWO_HAND, RIGHT_HAND));
    }
}
