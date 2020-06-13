/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.type.WeaponType;

import java.util.ArrayList;
import java.util.List;

import static org.l2j.gameserver.model.item.BodyPart.RIGHT_HAND;
import static org.l2j.gameserver.model.item.BodyPart.TWO_HAND;

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
