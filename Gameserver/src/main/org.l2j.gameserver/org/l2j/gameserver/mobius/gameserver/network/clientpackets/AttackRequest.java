package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.PcCondOverride;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.mobius.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.mobius.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;

import java.nio.ByteBuffer;

public final class AttackRequest extends IClientIncomingPacket {
    // cddddc
    private int _objectId;
    @SuppressWarnings("unused")
    private int _originX;
    @SuppressWarnings("unused")
    private int _originY;
    @SuppressWarnings("unused")
    private int _originZ;
    @SuppressWarnings("unused")
    private int _attackId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _objectId = packet.getInt();
        _originX = packet.getInt();
        _originY = packet.getInt();
        _originZ = packet.getInt();
        _attackId = packet.get(); // 0 for simple click 1 for shift-click
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final BuffInfo info = activeChar.getEffectList().getFirstBuffInfoByAbnormalType(AbnormalType.BOT_PENALTY);
        if (info != null) {
            for (AbstractEffect effect : info.getEffects()) {
                if (!effect.checkCondition(-1)) {
                    activeChar.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED);
                    activeChar.sendPacket(ActionFailed.STATIC_PACKET);
                    return;
                }
            }
        }

        // avoid using expensive operations if not needed
        final L2Object target;
        if (activeChar.getTargetId() == _objectId) {
            target = activeChar.getTarget();
        } else {
            target = L2World.getInstance().findObject(_objectId);
        }

        if (target == null) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        } else if ((!target.isTargetable() || activeChar.isTargetingDisabled()) && !activeChar.canOverrideCond(PcCondOverride.TARGET_ALL)) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        // Players can't attack objects in the other instances
        else if (target.getInstanceWorld() != activeChar.getInstanceWorld()) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        // Only GMs can directly attack invisible characters
        else if (!target.isVisibleFor(activeChar)) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (activeChar.getTarget() != target) {
            target.onAction(activeChar);
        } else if ((target.getObjectId() != activeChar.getObjectId()) && (activeChar.getPrivateStoreType() == PrivateStoreType.NONE) && (activeChar.getActiveRequester() == null)) {
            target.onForcedAttack(activeChar);
        } else {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }
}
