package org.l2j.gameserver.engine.skill.api;

import java.util.function.Consumer;

/**
 * @author JoeAlisson
 */
public enum SkillType {
    PHYSIC,
    MAGIC,
    STATIC,
    DANCE;

    private static final SkillType[] CACHE = values();

    public static void forEach(Consumer<SkillType> action) {
        for (SkillType type : CACHE) {
            action.accept(type);
        }
    }
}
