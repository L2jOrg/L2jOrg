package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.engine.scripting.ScriptEngineManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author NosBit
 */
public final class SkillConditionHandler {
    private final Map<String, Function<StatsSet, ISkillCondition>> _skillConditionHandlerFactories = new HashMap<>();

    private SkillConditionHandler() {
    }

    public void registerHandler(String name, Function<StatsSet, ISkillCondition> handlerFactory) {
        _skillConditionHandlerFactories.put(name, handlerFactory);
    }

    public Function<StatsSet, ISkillCondition> getHandlerFactory(String name) {
        return _skillConditionHandlerFactories.get(name);
    }

    public int size() {
        return _skillConditionHandlerFactories.size();
    }

    public void executeScript() {
        try {
            ScriptEngineManager.getInstance().executeSkillConditionMasterHandler();
        } catch (Exception e) {
            throw new Error("Problems while running SkillMasterHandler", e);
        }
    }

    public static SkillConditionHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        private static final SkillConditionHandler INSTANCE = new SkillConditionHandler();
    }
}
