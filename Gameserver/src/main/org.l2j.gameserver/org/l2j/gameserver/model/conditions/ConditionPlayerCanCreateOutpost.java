package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.ZoneType;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Player Can Create Outpost condition implementation.
 *
 * @author Adry_85
 */
public class ConditionPlayerCanCreateOutpost extends Condition {
    private final boolean _val;

    public ConditionPlayerCanCreateOutpost(boolean val) {
        _val = val;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (!isPlayer(effector)) {
            return !_val;
        }

        final Player player = effector.getActingPlayer();
        boolean canCreateOutpost = true;
        if (player.isAlikeDead() || (player.getClan() == null)) {
            canCreateOutpost = false;
        }

        final Castle castle = CastleManager.getInstance().getCastle(player);
        if (castle == null) {
            canCreateOutpost = false;
        }

        if ((castle != null) && (castle.getId() == 0)) {
            player.sendMessage("You must be on fort or castle ground to construct an outpost or flag.");
            canCreateOutpost = false;
        } else if ((castle != null) && !castle.getZone().isActive()) {
            player.sendMessage("You can only construct an outpost or flag on siege field.");
            canCreateOutpost = false;
        } else if (!player.isClanLeader()) {
            player.sendMessage("You must be a clan leader to construct an outpost or flag.");
            canCreateOutpost = false;
        } else if (!player.isInsideZone(ZoneType.HQ)) {
            player.sendPacket(SystemMessageId.YOU_CAN_T_BUILD_HEADQUARTERS_HERE);
            canCreateOutpost = false;
        }
        return (_val == canCreateOutpost);
    }
}
