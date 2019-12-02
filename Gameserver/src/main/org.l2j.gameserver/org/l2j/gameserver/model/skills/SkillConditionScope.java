package org.l2j.gameserver.model.skills;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author NosBit
 */
public enum SkillConditionScope {
    GENERAL("conditions"),
    TARGET("targetConditions"),
    PASSIVE("passiveConditions");

    private static final Map<String, SkillConditionScope> XML_NODE_NAME_TO_SKILL_CONDITION_SCOPE;

    static {
        XML_NODE_NAME_TO_SKILL_CONDITION_SCOPE = Arrays.stream(values()).collect(Collectors.toMap(SkillConditionScope::getXmlNodeName, Function.identity()));
    }

    private final String _xmlNodeName;

    SkillConditionScope(String xmlNodeName) {
        _xmlNodeName = xmlNodeName;
    }

    public static SkillConditionScope findByXmlNodeName(String xmlNodeName) {
        return XML_NODE_NAME_TO_SKILL_CONDITION_SCOPE.get(xmlNodeName);
    }

    public String getXmlNodeName() {
        return _xmlNodeName;
    }
}
