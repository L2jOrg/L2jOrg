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
package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * An instantly executed event when Attackable is attacked by Player.
 *
 * @author UnAfraid
 */
public class OnAttackableAttack implements IBaseEvent {
    private final Player _attacker;
    private final Attackable _target;
    private final int _damage;
    private final Skill _skill;
    private final boolean _isSummon;

    public OnAttackableAttack(Player attacker, Attackable target, int damage, Skill skill, boolean isSummon) {
        _attacker = attacker;
        _target = target;
        _damage = damage;
        _skill = skill;
        _isSummon = isSummon;
    }

    public final Player getAttacker() {
        return _attacker;
    }

    public final Attackable getTarget() {
        return _target;
    }

    public int getDamage() {
        return _damage;
    }

    public Skill getSkill() {
        return _skill;
    }

    public boolean isSummon() {
        return _isSummon;
    }

    @Override
    public EventType getType() {
        return EventType.ON_ATTACKABLE_ATTACK;
    }
}