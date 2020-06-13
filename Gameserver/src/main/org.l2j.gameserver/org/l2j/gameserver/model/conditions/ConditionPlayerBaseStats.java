/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.ItemTemplate;

enum BaseStat {
    Int,
    Str,
    Con,
    Dex,
    Men,
    Wit
}

/**
 * The Class ConditionPlayerBaseStats.
 *
 * @author mkizub
 */
public class ConditionPlayerBaseStats extends Condition {

    private final BaseStat _stat;
    private final int _value;

    /**
     * Instantiates a new condition player base stats.
     *
     * @param player the player
     * @param stat   the stat
     * @param value  the value
     */
    public ConditionPlayerBaseStats(Creature player, BaseStat stat, int value) {
        super();
        _stat = stat;
        _value = value;
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
        final Player player = effector.getActingPlayer();
        switch (_stat) {
            case Int: {
                return player.getINT() >= _value;
            }
            case Str: {
                return player.getSTR() >= _value;
            }
            case Con: {
                return player.getCON() >= _value;
            }
            case Dex: {
                return player.getDEX() >= _value;
            }
            case Men: {
                return player.getMEN() >= _value;
            }
            case Wit: {
                return player.getWIT() >= _value;
            }
        }
        return false;
    }
}
