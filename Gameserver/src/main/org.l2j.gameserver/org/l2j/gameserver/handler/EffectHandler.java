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

    public int size() {
        return factories.size();
    }


    public static EffectHandler getInstance() {
        return Singleton.INSTANCE;
    }


    private static final class Singleton {
        protected static final EffectHandler INSTANCE = new EffectHandler();
    }
}
