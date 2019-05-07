package org.l2j.gameserver.network.serverpackets.mentoring;

import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author UnAfraid
 */
public class ListMenteeWaiting extends IClientOutgoingPacket {
    private final int PLAYERS_PER_PAGE = 64;
    private final List<L2PcInstance> _possibleCandiates = new ArrayList<>();
    private final int _page;

    public ListMenteeWaiting(int page, int minLevel, int maxLevel) {
        _page = page;
        for (L2PcInstance player : L2World.getInstance().getPlayers()) {
            if ((player.getLevel() >= minLevel) && (player.getLevel() <= maxLevel) && !player.isMentee() && !player.isMentor() && !player.isInCategory(CategoryType.SIXTH_CLASS_GROUP)) {
                _possibleCandiates.add(player);
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.LIST_MENTEE_WAITING.writeId(packet);

        packet.putInt(0x01); // always 1 in retail
        if (_possibleCandiates.isEmpty()) {
            packet.putInt(0x00);
            packet.putInt(0x00);
            return;
        }

        packet.putInt(_possibleCandiates.size());
        packet.putInt(_possibleCandiates.size() % PLAYERS_PER_PAGE);

        for (L2PcInstance player : _possibleCandiates) {
            if ((1 <= (PLAYERS_PER_PAGE * _page)) && (1 > (PLAYERS_PER_PAGE * (_page - 1)))) {
                writeString(player.getName(), packet);
                packet.putInt(player.getActiveClass());
                packet.putInt(player.getLevel());
            }
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 17 + _possibleCandiates.size() * 10 + _possibleCandiates.stream().mapToInt(c -> c.getName().length() * 2).sum();
    }
}
