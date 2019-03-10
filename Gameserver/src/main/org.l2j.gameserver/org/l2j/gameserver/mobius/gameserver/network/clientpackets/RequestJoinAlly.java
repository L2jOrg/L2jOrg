package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.AskJoinAlly;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;

import java.nio.ByteBuffer;

public final class RequestJoinAlly extends IClientIncomingPacket {
    private int _objectId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _objectId = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final L2PcInstance target = L2World.getInstance().getPlayer(_objectId);

        if (target == null) {
            activeChar.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
            return;
        }

        final L2Clan clan = activeChar.getClan();

        if (clan == null) {
            activeChar.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION);
            return;
        }

        if (!clan.checkAllyJoinCondition(activeChar, target)) {
            return;
        }
        if (!activeChar.getRequest().setRequest(target, this)) {
            return;
        }

        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_LEADER_S2_HAS_REQUESTED_AN_ALLIANCE);
        sm.addString(activeChar.getClan().getAllyName());
        sm.addString(activeChar.getName());
        target.sendPacket(sm);
        target.sendPacket(new AskJoinAlly(activeChar.getObjectId(), activeChar.getClan().getAllyName()));
    }
}
