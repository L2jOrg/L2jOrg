package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * This class ...
 *
 * @version $Revision: 1.4.2.1.2.3 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestReplyStopPledgeWar extends ClientPacket {
    private int _answer;

    @Override
    public void readImpl() {
        readString();
        _answer = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }
        final Player requestor = activeChar.getActiveRequester();
        if (requestor == null) {
            return;
        }

        if (_answer == 1) {
            ClanTable.getInstance().deleteClanWars(requestor.getClanId(), activeChar.getClanId());
        } else {
            requestor.sendPacket(SystemMessageId.REQUEST_TO_END_WAR_HAS_BEEN_DENIED);
        }

        activeChar.setActiveRequester(null);
        requestor.onTransactionResponse();
    }
}