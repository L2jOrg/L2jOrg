/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.handler;

import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author NosBit
 * @author JoeAlisson
 */
public final class SkillConditionHandler {
    private final Map<String, Function<Node, SkillCondition>> skillConditionHandlerFactories = new HashMap<>();

    private SkillConditionHandler() {
    }

    public void registerFactory(SkillConditionFactory skillConditionFactory) {
        skillConditionHandlerFactories.put(skillConditionFactory.conditionName(), skillConditionFactory::create);
    }

    public Function<Node, SkillCondition> getHandlerFactory(String name) {
        return skillConditionHandlerFactories.get(name);
    }

    public static SkillConditionHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        private static final SkillConditionHandler INSTANCE = new SkillConditionHandler();
    }
}
