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
