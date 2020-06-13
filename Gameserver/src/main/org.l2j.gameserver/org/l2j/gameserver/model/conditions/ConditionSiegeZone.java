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
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.item.ItemTemplate;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * The Class ConditionSiegeZone.
 *
 * @author Gigiikun
 */
public final class ConditionSiegeZone extends Condition {
    // conditional values
    public static final int COND_NOT_ZONE = 0x0001;
    public static final int COND_CAST_ATTACK = 0x0002;
    public static final int COND_CAST_DEFEND = 0x0004;
    public static final int COND_CAST_NEUTRAL = 0x0008;
    public static final int COND_FORT_ATTACK = 0x0010;
    public static final int COND_FORT_DEFEND = 0x0020;
    public static final int COND_FORT_NEUTRAL = 0x0040;

    private final int _value;
    private final boolean _self;

    /**
     * Instantiates a new condition siege zone.
     *
     * @param value the value
     * @param self  the self
     */
    public ConditionSiegeZone(int value, boolean self) {
        _value = value;
        _self = self;
    }

    /**
     * Check if ok.
     *
     * @param activeChar the active char
     * @param castle     the castle
     * @param value      the value
     * @return true, if successful
     */
    public static boolean checkIfOk(Creature activeChar, Castle castle, int value) {
        if (!isPlayer(activeChar)) {
            return false;
        }

        final Player player = (Player) activeChar;

        if (((castle == null) || (castle.getId() <= 0))) {
            if ((value & COND_NOT_ZONE) != 0) {
                return true;
            }
        } else if (!castle.getZone().isActive()) {
            if ((value & COND_NOT_ZONE) != 0) {
                return true;
            }
        } else if (((value & COND_CAST_ATTACK) != 0) && player.isRegisteredOnThisSiegeField(castle.getId()) && (player.getSiegeState() == 1)) {
            return true;
        } else if (((value & COND_CAST_DEFEND) != 0) && player.isRegisteredOnThisSiegeField(castle.getId()) && (player.getSiegeState() == 2)) {
            return true;
        } else if (((value & COND_CAST_NEUTRAL) != 0) && (player.getSiegeState() == 0)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        final Creature target = _self ? effector : effected;
        final Castle castle = CastleManager.getInstance().getCastle(target);

        if (castle == null) {
            return (_value & COND_NOT_ZONE) != 0;
        }
        return checkIfOk(target, castle, _value);
    }

}
