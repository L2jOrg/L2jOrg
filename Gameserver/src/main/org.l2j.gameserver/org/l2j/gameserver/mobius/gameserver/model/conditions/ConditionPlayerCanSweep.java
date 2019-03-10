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
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Item;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Checks Sweeper conditions:
 * <ul>
 * <li>Minimum checks, player not null, skill not null.</li>
 * <li>Checks if the target isn't null, is dead and spoiled.</li>
 * <li>Checks if the sweeper player is the target spoiler, or is in the spoiler party.</li>
 * <li>Checks if the corpse is too old.</li>
 * <li>Checks inventory limit and weight max load won't be exceed after sweep.</li>
 * </ul>
 * If two or more conditions aren't meet at the same time, one message per condition will be shown.
 *
 * @author Zoey76
 */
public class ConditionPlayerCanSweep extends Condition {
    private final boolean _val;

    public ConditionPlayerCanSweep(boolean val) {
        _val = val;
    }

    @Override
    public boolean testImpl(L2Character effector, L2Character effected, Skill skill, L2Item item) {
        final AtomicBoolean canSweep = new AtomicBoolean(false);
        if (effector.getActingPlayer() != null) {
            final L2PcInstance sweeper = effector.getActingPlayer();
            if (skill != null) {
                skill.forEachTargetAffected(sweeper, effected, o ->
                {
                    if ((o != null) && o.isAttackable()) {
                        final L2Attackable target = (L2Attackable) o;
                        if (target.isDead()) {
                            if (target.isSpoiled()) {
                                canSweep.set(target.checkSpoilOwner(sweeper, true));
                                if (canSweep.get()) {
                                    canSweep.set(!target.isOldCorpse(sweeper, Config.CORPSE_CONSUME_SKILL_ALLOWED_TIME_BEFORE_DECAY, true));
                                }
                                if (canSweep.get()) {
                                    canSweep.set(sweeper.getInventory().checkInventorySlotsAndWeight(target.getSpoilLootItems(), true, true));
                                }
                            } else {
                                sweeper.sendPacket(SystemMessageId.SWEEPER_FAILED_TARGET_NOT_SPOILED);
                            }
                        }
                    }
                });
            }
        }
        return (_val == canSweep.get());
    }
}
