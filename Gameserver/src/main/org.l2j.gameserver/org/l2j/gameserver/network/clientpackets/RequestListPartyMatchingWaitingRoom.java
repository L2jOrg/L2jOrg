package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.network.serverpackets.ExListPartyMatchingWaitingRoom;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Gnacik
 */
public class RequestListPartyMatchingWaitingRoom extends ClientPacket {
    private int _page;
    private int _minLevel;
    private int _maxLevel;
    private List<ClassId> _classId; // 1 - waitlist 0 - room waitlist
    private String _query;

    @Override
    public void readImpl() {
        _page = readInt();
        _minLevel = readInt();
        _maxLevel = readInt();
        final int size = readInt();

        if ((size > 0) && (size < 128)) {
            _classId = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                _classId.add(ClassId.getClassId(readInt()));
            }
        }
        if (available() > 0) {
            _query = readString();
        }
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        client.sendPacket(new ExListPartyMatchingWaitingRoom(activeChar, _page, _minLevel, _maxLevel, _classId, _query));
    }
}