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
package org.l2j.gameserver.mobius.gameserver.model.conditions;

import com.l2jmobius.Config;
import org.l2j.gameserver.mobius.gameserver.data.sql.impl.CharSummonTable;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Item;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;

/**
 * Player Can Summon condition implementation.
 *
 * @author Zoey76
 */
public class ConditionPlayerCanSummonPet extends Condition {
    private final boolean _val;

    public ConditionPlayerCanSummonPet(boolean val) {
        _val = val;
    }

    @Override
    public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
        final L2PcInstance player = effector.getActingPlayer();
        if (player == null) {
            return false;
        }

        boolean canSummon = true;

        if (Config.RESTORE_PET_ON_RECONNECT && CharSummonTable.getInstance().getPets().containsKey(player.getObjectId())) {
            player.sendPacket(SystemMessageId.YOU_MAY_NOT_SUMMON_MULTIPLE_PETS_AT_THE_SAME_TIME);
            canSummon = false;
        } else if (player.hasPet()) {
            player.sendPacket(SystemMessageId.YOU_MAY_NOT_SUMMON_MULTIPLE_PETS_AT_THE_SAME_TIME);
            canSummon = false;
        } else if (player.isFlyingMounted() || player.isMounted() || player.inObserverMode() || player.isTeleporting()) {
            canSummon = false;
        }
        return (_val == canSummon);
    }
}
