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
import org.l2j.gameserver.enums.SkillConditionAffectType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class OpCheckAbnormalSkillCondition implements SkillCondition {
    public final AbnormalType type;
    public final int level;
    public final boolean hasAbnormal;
    public final SkillConditionAffectType affectType;

    private OpCheckAbnormalSkillCondition(AbnormalType type, int level, boolean affected, SkillConditionAffectType affect) {
        this.type = type;
        this.level = level;
        hasAbnormal = affected;
        affectType = affect;
    }

    @Override
    public boolean canUse(Creature caster, Skill skill, WorldObject target) {
        return switch (affectType) {
            case CASTER -> caster.getEffectList().hasAbnormalType(type, info -> (info.getSkill().getAbnormalLvl() >= level)) == hasAbnormal;
            case TARGET -> isCreature(target) && ((Creature) target).getEffectList().hasAbnormalType(type, info -> (info.getSkill().getAbnormalLvl() >= level)) == hasAbnormal;
            default -> false;
        };
    }

    public static final class Factory extends SkillConditionFactory {

        @Override
        public SkillCondition create(Node xmlNode) {
            var attr = xmlNode.getAttributes();
            return new OpCheckAbnormalSkillCondition(parseEnum(attr, AbnormalType.class, "type"), parseInt(attr, "level"),
                    parseBoolean(attr, "affected"), parseEnum(attr, SkillConditionAffectType.class, "affect"));
        }

        @Override
        public String conditionName() {
            return "abnormal";
        }
    }
}
