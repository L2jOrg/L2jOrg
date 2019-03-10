package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;

import java.nio.ByteBuffer;

/**
 * This class ...
 *
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestReplyStopPledgeWar extends IClientIncomingPacket {
    private int _answer;

    @Override
    public void readImpl(ByteBuffer packet) {
        readString(packet);
        _answer = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }
        final L2PcInstance requestor = activeChar.getActiveRequester();
        if (requestor == null) {
            return;
        }

        if (_answer == 1) {
            ClanTable.getInstance().deleteclanswars(requestor.getClanId(), activeChar.getClanId());
        } else {
            requestor.sendPacket(SystemMessageId.REQUEST_TO_END_WAR_HAS_BEEN_DENIED);
        }

        activeChar.setActiveRequester(null);
        requestor.onTransactionResponse();
    }
}