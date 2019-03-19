package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.conditions.ICondition;
import org.l2j.gameserver.scripting.ScriptEngineManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Sdw
 */
public final class ConditionHandler {
    private final Map<String, Function<StatsSet, ICondition>> _conditionHandlerFactories = new HashMap<>();

    private ConditionHandler() {
    }

    public void registerHandler(String name, Function<StatsSet, ICondition> handlerFactory) {
        _conditionHandlerFactories.put(name, handlerFactory);
    }

    public Function<StatsSet, ICondition> getHandlerFactory(String name) {
        return _conditionHandlerFactories.get(name);
    }

    public int size() {
        return _conditionHandlerFactories.size();
    }

    public void executeScript() {
        try {
            ScriptEngineManager.getInstance().executeConditionMasterHandler();
        } catch (Exception e) {
            throw new Error("Problems while running ConditionMasterHandler", e);
        }
    }

    public static ConditionHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        private static final ConditionHandler INSTANCE = new ConditionHandler();
    }
}
