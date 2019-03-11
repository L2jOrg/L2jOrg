package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;

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
