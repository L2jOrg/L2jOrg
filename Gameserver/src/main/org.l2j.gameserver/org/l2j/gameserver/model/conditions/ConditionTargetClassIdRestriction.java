package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;

import java.util.List;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * The Class ConditionTargetClassIdRestriction.
 */
public class ConditionTargetClassIdRestriction extends Condition {
    private final List<Integer> _classIds;

    /**
     * Instantiates a new condition target class id restriction.
     *
     * @param classId the class id
     */
    public ConditionTargetClassIdRestriction(List<Integer> classId) {
        _classIds = classId;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        return isPlayer(effected) && (_classIds.contains(effected.getActingPlayer().getClassId().getId()));
    }
}
