package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.world.zone.ZoneType;

import static java.util.Objects.isNull;
import static org.l2j.commons.util.Util.zeroIfNullOrElse;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Player Can Create Base condition implementation.
 *
 * @author Adry_85
 */
public class ConditionPlayerCanCreateBase extends Condition {
    private final boolean _val;

    public ConditionPlayerCanCreateBase(boolean val) {
        _val = val;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (!isPlayer(effector)) {
            return !_val;
        }

        final Player player = effector.getActingPlayer();
        boolean canCreateBase = true;
        if (player.isAlikeDead() || (player.getClan() == null)) {
            canCreateBase = false;
        }

        final Castle castle = CastleManager.getInstance().getCastle(player);
        final SystemMessage sm;
        if ((castle == null)) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addSkillName(skill);
            player.sendPacket(sm);
            canCreateBase = false;
        } else if (!castle.getSiege().isInProgress()) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addSkillName(skill);
            player.sendPacket(sm);
            canCreateBase = false;
        } else if (isNull(castle.getSiege().getAttackerClan(player.getClan()))) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addSkillName(skill);
            player.sendPacket(sm);
            canCreateBase = false;
        } else if (!player.isClanLeader()) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addSkillName(skill);
            player.sendPacket(sm);
            canCreateBase = false;
        } else if (zeroIfNullOrElse(castle.getSiege().getAttackerClan(player.getClan()), SiegeClanData::getNumFlags) >= SiegeManager.getInstance().getFlagMaxCount()) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addSkillName(skill);
            player.sendPacket(sm);
            canCreateBase = false;
        } else if (!player.isInsideZone(ZoneType.HQ)) {
            player.sendPacket(SystemMessageId.YOU_CAN_T_BUILD_HEADQUARTERS_HERE);
            canCreateBase = false;
        }
        return (_val == canCreateBase);
    }
}
