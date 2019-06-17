package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.ExPledgeWaitingList;

/**
 * @author Sdw
 */
public class RequestPledgeWaitingList extends ClientPacket {
    private int _clanId;

    @Override
    public void readImpl() {
        _clanId = readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if ((activeChar == null) || (activeChar.getClanId() != _clanId)) {
            return;
        }

        client.sendPacket(new ExPledgeWaitingList(_clanId));
    }
}
