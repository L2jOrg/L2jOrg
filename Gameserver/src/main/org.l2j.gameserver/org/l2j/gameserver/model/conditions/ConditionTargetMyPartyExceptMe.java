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

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * Target My Party Except Me condition implementation.
 *
 * @author Adry_85
 */
public class ConditionTargetMyPartyExceptMe extends Condition {
    private final boolean _val;

    public ConditionTargetMyPartyExceptMe(boolean val) {
        _val = val;
    }

    @Override
    public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
        boolean isPartyMember = true;
        final L2PcInstance player = effector.getActingPlayer();
        if ((player == null) || (effected == null) || !effected.isPlayer()) {
            isPartyMember = false;
        } else if (player == effected) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THIS_ON_YOURSELF);
            isPartyMember = false;
        } else if (!player.isInParty() || !player.getParty().equals(effected.getParty())) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addSkillName(skill);
            player.sendPacket(sm);
            isPartyMember = false;
        }
        return _val == isPartyMember;
    }
}
