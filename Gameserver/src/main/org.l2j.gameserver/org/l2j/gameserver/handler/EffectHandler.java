package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.scripting.ScriptEngineManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author BiggBoss, UnAfraid
 */
public final class EffectHandler {

    private final Map<String, Function<StatsSet, AbstractEffect>> _effectHandlerFactories = new HashMap<>();

    private EffectHandler() {

    }

    public void registerHandler(String name, Function<StatsSet, AbstractEffect> handlerFactory) {
        _effectHandlerFactories.put(name, handlerFactory);
    }

    public Function<StatsSet, AbstractEffect> getHandlerFactory(String name) {
        return _effectHandlerFactories.get(name);
    }

    public int size() {
        return _effectHandlerFactories.size();
    }

    public void executeScript() {
        try {
            ScriptEngineManager.getInstance().executeEffectMasterHandler();
        } catch (Exception e) {
            throw new Error("Problems while running EffectMansterHandler", e);
        }
    }

    public static EffectHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        protected static final EffectHandler INSTANCE = new EffectHandler();
    }
}
