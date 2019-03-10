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
package org.l2j.gameserver.mobius.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.events.EventType;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;

/**
 * An instantly executed event when L2Attackable is attacked by L2PcInstance.
 *
 * @author UnAfraid
 */
public class OnAttackableAttack implements IBaseEvent {
    private final L2PcInstance _attacker;
    private final L2Attackable _target;
    private final int _damage;
    private final Skill _skill;
    private final boolean _isSummon;

    public OnAttackableAttack(L2PcInstance attacker, L2Attackable target, int damage, Skill skill, boolean isSummon) {
        _attacker = attacker;
        _target = target;
        _damage = damage;
        _skill = skill;
        _isSummon = isSummon;
    }

    public final L2PcInstance getAttacker() {
        return _attacker;
    }

    public final L2Attackable getTarget() {
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