package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * The Class ConditionPlayerIsHero.
 *
 * @author JoeAlisson
 */
public class ConditionPlayerIsHero extends Condition {

    private static final ConditionPlayerIsHero HERO = new ConditionPlayerIsHero(true);
    private static final ConditionPlayerIsHero NO_HERO = new ConditionPlayerIsHero(false);

    public final boolean isHero;

    /**
     * Instantiates a new condition player is hero.
     *
     * @param isHero the val
     */
    private ConditionPlayerIsHero(boolean isHero) {
        this.isHero = isHero;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (effector.getActingPlayer() == null) {
            return false;
        }
        return (effector.getActingPlayer().isHero() == isHero);
    }

    public static ConditionPlayerIsHero of(boolean hero) {
        return hero ? HERO : NO_HERO;
    }
}
