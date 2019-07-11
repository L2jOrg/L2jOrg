package org.l2j.gameserver.network.serverpackets.mentoring;

import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author UnAfraid
 */
public class ListMenteeWaiting extends ServerPacket {
    private final int PLAYERS_PER_PAGE = 64;
    private final List<Player> _possibleCandiates = new ArrayList<>();
    private final int _page;

    public ListMenteeWaiting(int page, int minLevel, int maxLevel) {
        _page = page;
        for (Player player : World.getInstance().getPlayers()) {
            if ((player.getLevel() >= minLevel) && (player.getLevel() <= maxLevel) && !player.isMentee() && !player.isMentor() && !player.isInCategory(CategoryType.SIXTH_CLASS_GROUP)) {
                _possibleCandiates.add(player);
            }
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.LIST_MENTEE_WAITING);

        writeInt(0x01); // always 1 in retail
        if (_possibleCandiates.isEmpty()) {
            writeInt(0x00);
            writeInt(0x00);
            return;
        }

        writeInt(_possibleCandiates.size());
        writeInt(_possibleCandiates.size() % PLAYERS_PER_PAGE);

        for (Player player : _possibleCandiates) {
            if ((1 <= (PLAYERS_PER_PAGE * _page)) && (1 > (PLAYERS_PER_PAGE * (_page - 1)))) {
                writeString(player.getName());
                writeInt(player.getActiveClass());
                writeInt(player.getLevel());
            }
        }
    }

}
