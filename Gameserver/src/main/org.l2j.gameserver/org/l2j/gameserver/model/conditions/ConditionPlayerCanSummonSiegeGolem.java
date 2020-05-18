package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.network.SystemMessageId;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Player Can Summon Siege Golem implementation.
 *
 * @author Adry_85
 */
public class ConditionPlayerCanSummonSiegeGolem extends Condition {
    private final boolean _val;

    public ConditionPlayerCanSummonSiegeGolem(boolean val) {
        _val = val;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (!isPlayer(effector)) {
            return !_val;
        }

        final Player player = effector.getActingPlayer();
        boolean canSummonSiegeGolem = true;
        if (player.isAlikeDead()  || (player.getClan() == null)) {
            canSummonSiegeGolem = false;
        }

        final Castle castle = CastleManager.getInstance().getCastle(player);
        if ((castle == null)) {
            canSummonSiegeGolem = false;
        }

        if (castle != null && castle.getId() == 0) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            canSummonSiegeGolem = false;
        } else if (nonNull(castle) && !castle.getSiege().isInProgress()) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            canSummonSiegeGolem = false;
        } else if (player.getClanId() != 0 &&  nonNull(castle) && isNull(castle.getSiege().getAttackerClan(player.getClanId())) ) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            canSummonSiegeGolem = false;
        }
        return (_val == canSummonSiegeGolem);
    }
}
