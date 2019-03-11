/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.model.skills.Skill;

import java.util.ArrayList;

/**
 * The Class ConditionPlayerHasClanHall.
 *
 * @author MrPoke
 */
public final class ConditionPlayerHasClanHall extends Condition {
    private final ArrayList<Integer> _clanHall;

    /**
     * Instantiates a new condition player has clan hall.
     *
     * @param clanHall the clan hall
     */
    public ConditionPlayerHasClanHall(ArrayList<Integer> clanHall) {
        _clanHall = clanHall;
    }

    /**
     * Test impl.
     *
     * @return true, if successful
     */
    @Override
    public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
        if (effector.getActingPlayer() == null) {
            return false;
        }

        final L2Clan clan = effector.getActingPlayer().getClan();
        if (clan == null) {
            return ((_clanHall.size() == 1) && (_clanHall.get(0) == 0));
        }

        // All Clan Hall
        if ((_clanHall.size() == 1) && (_clanHall.get(0) == -1)) {
            return clan.getHideoutId() > 0;
        }
        return _clanHall.contains(clan.getHideoutId());
    }
}
