package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * The Class ConditionPlayerHasCastle.
 *
 * @author MrPoke
 */
public final class ConditionPlayerHasCastle extends Condition {
    public final int _castle;

    /**
     * Instantiates a new condition player has castle.
     *
     * @param castle the castle
     */
    public ConditionPlayerHasCastle(int castle) {
        _castle = castle;
    }

    /**
     * Test impl.
     *
     * @return true, if successful
     */
    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (effector.getActingPlayer() == null) {
            return false;
        }

        final Clan clan = effector.getActingPlayer().getClan();
        if (clan == null) {
            return _castle == 0;
        }

        // Any castle
        if (_castle == -1) {
            return clan.getCastleId() > 0;
        }
        return clan.getCastleId() == _castle;
    }
}
