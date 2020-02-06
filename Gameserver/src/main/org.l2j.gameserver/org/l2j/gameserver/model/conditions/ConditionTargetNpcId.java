package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;

import java.util.List;

import static org.l2j.gameserver.util.GameUtils.isDoor;
import static org.l2j.gameserver.util.GameUtils.isNpc;

/**
 * The Class ConditionTargetNpcId.
 */
public class ConditionTargetNpcId extends Condition {
    private final List<Integer> _npcIds;

    /**
     * Instantiates a new condition target npc id.
     *
     * @param npcIds the npc ids
     */
    public ConditionTargetNpcId(List<Integer> npcIds) {
        _npcIds = npcIds;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        return (isNpc(effected) || isDoor(effected)) && _npcIds.contains(effected.getId());
    }
}
