package org.l2j.gameserver.network.clientpackets.mentoring;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.mentoring.ListMenteeWaiting;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class RequestMenteeWaitingList extends IClientIncomingPacket {
    private int _page;
    private int _minLevel;
    private int _maxLevel;

    @Override
    public void readImpl(ByteBuffer packet) {
        _page = packet.getInt();
        _minLevel = packet.getInt();
        _maxLevel = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }
        client.sendPacket(new ListMenteeWaiting(_page, _minLevel, _maxLevel));
    }
}
