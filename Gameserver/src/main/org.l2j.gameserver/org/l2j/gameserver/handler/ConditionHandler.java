package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.conditions.ConditionFactory;
import org.l2j.gameserver.model.conditions.ICondition;
import org.l2j.gameserver.engine.scripting.ScriptEngineManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Sdw
 */
public final class ConditionHandler {
    private final Map<String, Function<StatsSet, ICondition>> factories = new HashMap<>();

    private ConditionHandler() {
    }

    public void registerFactory(ConditionFactory handler) {
        factories.put(handler.conditionName(), handler::create);
    }

    public Function<StatsSet, ICondition> getHandlerFactory(String name) {
        return factories.get(name);
    }

    public int size() {
        return factories.size();
    }

    public static ConditionHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        private static final ConditionHandler INSTANCE = new ConditionHandler();
    }
}
