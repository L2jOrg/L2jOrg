package org.l2j.gameserver.engine.skill.api;

import org.l2j.commons.xml.XmlParser;
import org.w3c.dom.Node;

public abstract class SkillConditionFactory extends XmlParser {

    public abstract SkillCondition create(Node xmlNode);

    public abstract String conditionName();
}
