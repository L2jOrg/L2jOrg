package org.l2j.gameserver.handler;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.scripting.ScriptEngineManager;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author NosBit
 */
public final class SkillConditionHandler {
    private final Map<String, Function<StatsSet, SkillCondition>> _skillConditionHandlerFactories = new HashMap<>();

    private SkillConditionHandler() {
    }

    public void registerConditionFactory(String name, Function<Node, SkillCondition> handlerFactory) {

    }

    public void registerHandler(String name, Function<StatsSet, SkillCondition> handlerFactory) {
        _skillConditionHandlerFactories.put(name, handlerFactory);
    }

    public Function<StatsSet, SkillCondition> getHandlerFactory(String name) {
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
