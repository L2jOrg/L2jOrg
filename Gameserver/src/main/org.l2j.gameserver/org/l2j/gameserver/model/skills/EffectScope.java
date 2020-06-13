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
