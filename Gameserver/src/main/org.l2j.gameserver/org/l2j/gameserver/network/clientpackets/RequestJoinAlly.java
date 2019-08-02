package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.AskJoinAlly;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

public final class RequestJoinAlly extends ClientPacket {
    private int _objectId;

    @Override
    public void readImpl() {
        _objectId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final Player target = World.getInstance().findPlayer(_objectId);

        if (target == null) {
            activeChar.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
            return;
        }

        final Clan clan = activeChar.getClan();

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
