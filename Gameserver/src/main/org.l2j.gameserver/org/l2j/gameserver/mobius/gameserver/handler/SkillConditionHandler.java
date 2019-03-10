package org.l2j.gameserver.mobius.gameserver.handler;

import org.l2j.gameserver.mobius.gameserver.model.StatsSet;
import org.l2j.gameserver.mobius.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.mobius.gameserver.scripting.ScriptEngineManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author NosBit
 */
public final class SkillConditionHandler {
    private final Map<String, Function<StatsSet, ISkillCondition>> _skillConditionHandlerFactories = new HashMap<>();

    public static SkillConditionHandler getInstance() {
        return SingletonHolder._instance;
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

    private static final class SingletonHolder {
        protected static final SkillConditionHandler _instance = new SkillConditionHandler();
    }
}
