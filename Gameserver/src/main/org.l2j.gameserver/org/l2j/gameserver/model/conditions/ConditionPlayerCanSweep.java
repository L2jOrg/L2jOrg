/*
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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.network.SystemMessageId;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.l2j.gameserver.util.GameUtils.isAttackable;

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
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        final AtomicBoolean canSweep = new AtomicBoolean(false);
        if (effector.getActingPlayer() != null) {
            final Player sweeper = effector.getActingPlayer();
            if (skill != null) {
                skill.forEachTargetAffected(sweeper, effected, o ->
                {
                    if (isAttackable(o)) {
                        final Attackable target = (Attackable) o;
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
