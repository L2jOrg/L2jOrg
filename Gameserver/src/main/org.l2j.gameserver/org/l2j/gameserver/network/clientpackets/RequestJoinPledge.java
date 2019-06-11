package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.AskJoinPledge;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.nio.ByteBuffer;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.4 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestJoinPledge extends IClientIncomingPacket {
    private int _target;
    private int _pledgeType;

    @Override
    public void readImpl() {
        _target = readInt();
        _pledgeType = readInt();
    }

    private void scheduleDeny(L2PcInstance player, String name) {
        if (player != null) {
            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DID_NOT_RESPOND_INVITATION_TO_THE_CLAN_HAS_BEEN_CANCELLED);
            sm.addString(name);
            player.sendPacket(sm);
            player.onTransactionResponse();
        }
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final L2Clan clan = activeChar.getClan();
        if (clan == null) {
            return;
        }

        final L2PcInstance target = L2World.getInstance().getPlayer(_target);
        if (target == null) {
            activeChar.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
            return;
        }

        if (!clan.checkClanJoinCondition(activeChar, target, _pledgeType)) {
            return;
        }

        if (!activeChar.getRequest().setRequest(target, this)) {
            return;
        }

        final String pledgeName = activeChar.getClan().getName();
        target.sendPacket(new AskJoinPledge(activeChar, _pledgeType, pledgeName));
    }

    public int getPledgeType() {
        return _pledgeType;
    }
}
