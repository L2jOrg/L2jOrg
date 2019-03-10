package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.base.ClassId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExListPartyMatchingWaitingRoom;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Gnacik
 */
public class RequestListPartyMatchingWaitingRoom extends IClientIncomingPacket {
    private int _page;
    private int _minLevel;
    private int _maxLevel;
    private List<ClassId> _classId; // 1 - waitlist 0 - room waitlist
    private String _query;

    @Override
    public void readImpl(ByteBuffer packet) {
        _page = packet.getInt();
        _minLevel = packet.getInt();
        _maxLevel = packet.getInt();
        final int size = packet.getInt();

        if ((size > 0) && (size < 128)) {
            _classId = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                _classId.add(ClassId.getClassId(packet.getInt()));
            }
        }
        if (packet.hasRemaining()) {
            _query = readString(packet);
        }
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        client.sendPacket(new ExListPartyMatchingWaitingRoom(activeChar, _page, _minLevel, _maxLevel, _classId, _query));
    }
}