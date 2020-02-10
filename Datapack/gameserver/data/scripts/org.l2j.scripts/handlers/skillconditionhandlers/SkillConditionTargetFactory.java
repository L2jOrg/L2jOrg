package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
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
            case "npc" -> null;
            default -> null;
        };
    }

    private SkillCondition doorTargetCondition(Node doorNode) {
        return new OpTargetDoorSkillCondition(parseIntSet(doorNode.getFirstChild()));
    }

    @Override
    public String conditionName() {
        return "target";
    }
}
