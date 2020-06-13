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
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.w3c.dom.Node;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.MathUtil.calculateDistanceSq2D;
import static org.l2j.gameserver.util.MathUtil.calculateDistanceSq3D;

/**
 * @author JoeAlisson
 */
public class CheckRangeSkillCondition implements SkillCondition {

    private final int min;
    private final int max;
    private final boolean check3D;

    public CheckRangeSkillCondition(int min, int max, boolean check3D) {
        this.min = min * min;
        this.max = max > 0 ?  max * max : max;
        this.check3D = check3D;
    }

    @Override
    public boolean canUse(Creature caster, Skill skill, WorldObject target) {
        if(isNull(target)) {
            return false;
        }
        final var radius = caster.getCollisionRadius() + ( target instanceof Creature creature ? creature.getCollisionRadius() : 0);
        final var distance = (check3D ? calculateDistanceSq3D(caster, target) : calculateDistanceSq2D(caster, target) ) - (radius * radius);
        return distance >= min && (max <= 0 || distance <= max);
    }

    public static final class Factory extends SkillConditionFactory {

        @Override
        public SkillCondition create(Node xmlNode) {
            var attr = xmlNode.getAttributes();
            return new CheckRangeSkillCondition(parseInt(attr, "min"), parseInt(attr,"max"), parseBoolean(attr, "check-3d"));
        }

        @Override
        public String conditionName() {
            return "check-range";
        }
    }
}

