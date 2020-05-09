package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.util.GameUtils;

/**
 * This class represents Friendly Mobs lying over the world.<br>
 * These friendly mobs should only attack players with karma > 0 and it is always aggro, since it just attacks players with karma.
 */
public class FriendlyMob extends Attackable {

    public FriendlyMob(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2FriendlyMobInstance);
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        if (GameUtils.isPlayer(attacker)) {
            return attacker.getReputation() < 0;
        }

        return super.isAutoAttackable(attacker);
    }

    @Override
    public boolean isAggressive() {
        return true;
    }
}
