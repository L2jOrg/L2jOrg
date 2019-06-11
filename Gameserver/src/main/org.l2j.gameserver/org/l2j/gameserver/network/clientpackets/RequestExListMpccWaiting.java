package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.ExListMpccWaiting;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class RequestExListMpccWaiting extends IClientIncomingPacket {
    private int _page;
    private int _location;
    private int _level;

    @Override
    public void readImpl() {
        _page = readInt();
        _location = readInt();
        _level = readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        activeChar.sendPacket(new ExListMpccWaiting(_page, _location, _level));
    }
}
