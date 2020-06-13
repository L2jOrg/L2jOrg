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

import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.enums.Race;
import org.w3c.dom.Node;

/**
 * @author JoeAlisson
 */
public class SkillConditionTargetFactory extends SkillConditionFactory {

    @Override
    public SkillCondition create(Node xmlNode) {
        var child = xmlNode.getFirstChild();
        return switch (child.getNodeName()) {
            case "door" -> doorTargetCondition(child);
            case "npc" -> npcTargetCondition(child);
            case "party" -> partyTargetCondition(child);
            case "race" -> raceTargetCondition(child);
            default -> null;
        };
    }

    private SkillCondition raceTargetCondition(Node raceNode) {
        return new TargetRaceSkillCondition(parseEnum(raceNode.getAttributes(), Race.class, "name"));
    }

    private SkillCondition partyTargetCondition(Node partyNode) {
        return new TargetMyPartySkillCondition(parseBoolean(partyNode.getAttributes(), "include-caster"));
    }

    private SkillCondition npcTargetCondition(Node npcNode) {
        return new OpTargetNpcSkillCondition(parseIntSet(npcNode.getFirstChild()));
    }

    private SkillCondition doorTargetCondition(Node doorNode) {
        return new OpTargetDoorSkillCondition(parseIntSet(doorNode.getFirstChild()));
    }

    @Override
    public String conditionName() {
        return "target";
    }
}
