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

    public int size() {
        return skillConditionHandlerFactories.size();
    }

    public static SkillConditionHandler getInstance() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        private static final SkillConditionHandler INSTANCE = new SkillConditionHandler();
    }
}
