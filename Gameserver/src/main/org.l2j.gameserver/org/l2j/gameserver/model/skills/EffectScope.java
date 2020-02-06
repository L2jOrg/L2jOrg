package org.l2j.gameserver.model.skills;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author NosBit
 */
public enum EffectScope {
    GENERAL("effects"),
    START("startEffects"),
    SELF("selfEffects"),
    CHANNELING("channelingEffects"),
    PVP("pvpEffects"),
    PVE("pveEffects"),
    END("endEffects");

    private static final Map<String, EffectScope> XML_NODE_NAME_TO_EFFECT_SCOPE;

    static {
        XML_NODE_NAME_TO_EFFECT_SCOPE = Arrays.stream(values()).collect(Collectors.toMap(EffectScope::getXmlNodeName, e -> e));
    }

    private final String _xmlNodeName;

    EffectScope(String xmlNodeName) {
        _xmlNodeName = xmlNodeName;
    }

    public static EffectScope findByXmlNodeName(String xmlNodeName) {
        return XML_NODE_NAME_TO_EFFECT_SCOPE.get(xmlNodeName);
    }

    public String getXmlNodeName() {
        return _xmlNodeName;
    }
}
