package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * The Class ConditionPlayerSex.
 *
 * @author JoeAlisson
 */
public class ConditionPlayerSex extends Condition {

    private static final ConditionPlayerSex MALE = new ConditionPlayerSex(0);
    private static final ConditionPlayerSex FEMALE = new ConditionPlayerSex(1);

    // male 0 female 1
    public final int _sex;

    /**
     * Instantiates a new condition player sex.
     *
     * @param sex the sex
     */
    private ConditionPlayerSex(int sex) {
        _sex = sex;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (effector.getActingPlayer() == null) {
            return false;
        }
        return (effector.getActingPlayer().getAppearance().isFemale() ? 1 : 0) == _sex;
    }

    public static ConditionPlayerSex of(int sex) {
        return sex == 0 ? MALE : FEMALE;
    }
}
