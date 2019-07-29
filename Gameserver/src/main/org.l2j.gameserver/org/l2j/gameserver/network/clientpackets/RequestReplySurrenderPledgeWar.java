package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RequestReplySurrenderPledgeWar extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestReplySurrenderPledgeWar.class);
    private String _reqName;
    private int _answer;

    @Override
    public void readImpl() {
        _reqName = readString();
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
            LOGGER.info(getClass().getSimpleName() + ": Missing implementation for answer: " + _answer + " and name: " + _reqName + "!");
        }
        activeChar.onTransactionRequest(requestor);
    }
}