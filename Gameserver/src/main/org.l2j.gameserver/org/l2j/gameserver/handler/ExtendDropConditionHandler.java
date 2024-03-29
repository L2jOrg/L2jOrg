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

import org.l2j.gameserver.model.conditions.ConditionFactory;
import org.l2j.gameserver.engine.item.drop.ExtendDropCondition;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Sdw
 */
public final class ExtendDropConditionHandler {
    private final Map<String, Function<Node, ExtendDropCondition>> factories = new HashMap<>();

    private ExtendDropConditionHandler() {
    }

    public void registerFactory(ConditionFactory handler) {
        factories.put(handler.conditionName(), handler::create);
    }

    public Function<Node, ExtendDropCondition> getHandlerFactory(String name) {
        return factories.get(name);
    }

    public static ExtendDropConditionHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        private static final ExtendDropConditionHandler INSTANCE = new ExtendDropConditionHandler();
    }
}
