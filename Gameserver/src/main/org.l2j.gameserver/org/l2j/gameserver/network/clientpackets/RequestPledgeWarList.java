package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.PledgeReceiveWarList;

import java.nio.ByteBuffer;

/**
 * Format: (ch) dd
 *
 * @author -Wooden-
 */
public final class RequestPledgeWarList extends IClientIncomingPacket {
    @SuppressWarnings("unused")
    private int _page;
    private int _tab;

    @Override
    public void readImpl() {
        _page = readInt();
        _tab = readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.getClan() == null) {
            return;
        }

        activeChar.sendPacket(new PledgeReceiveWarList(activeChar.getClan(), _tab));
    }
}