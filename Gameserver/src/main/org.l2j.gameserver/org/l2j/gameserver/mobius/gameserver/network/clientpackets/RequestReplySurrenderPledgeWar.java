package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public final class RequestReplySurrenderPledgeWar extends IClientIncomingPacket
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestReplySurrenderPledgeWar.class);
    private String _reqName;
    private int _answer;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _reqName = readString(packet);
        _answer = packet.getInt();
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }
        final L2PcInstance requestor = activeChar.getActiveRequester();
        if (requestor == null)
        {
            return;
        }

        if (_answer == 1)
        {
            ClanTable.getInstance().deleteclanswars(requestor.getClanId(), activeChar.getClanId());
        }
        else
        {
            LOGGER.info(getClass().getSimpleName() + ": Missing implementation for answer: " + _answer + " and name: " + _reqName + "!");
        }
        activeChar.onTransactionRequest(requestor);
    }
}