package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public enum SkillConditionAlignment {
    LAWFUL,
    CHAOTIC;

    public boolean test(Player player) {
        return switch (this) {
            case LAWFUL ->  player.getReputation() >= 0;
            case CHAOTIC -> player.getReputation() < 0;
        };
    }
}