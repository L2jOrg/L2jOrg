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

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author BiggBoss, UnAfraid
 * @author JoeAlisson
 */
public final class EffectHandler {

    private final Map<String, Function<StatsSet, AbstractEffect>> factories = new HashMap<>();

    private EffectHandler() {
    }

    public void registerFactory(SkillEffectFactory factory) {
        factories.put(factory.effectName(), factory::create);
    }

    public Function<StatsSet, AbstractEffect> getHandlerFactory(String name) {
        return factories.get(name);
    }


    public static EffectHandler getInstance() {
        return Singleton.INSTANCE;
    }


    private static final class Singleton {
        protected static final EffectHandler INSTANCE = new EffectHandler();
    }
}
