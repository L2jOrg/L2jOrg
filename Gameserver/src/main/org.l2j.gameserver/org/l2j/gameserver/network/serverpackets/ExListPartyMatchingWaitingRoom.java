package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * @author Gnacik
 */
public class ExListPartyMatchingWaitingRoom extends ServerPacket {
    private static final int NUM_PER_PAGE = 64;
    private final int _size;
    private final List<L2PcInstance> _players = new LinkedList<>();

    public ExListPartyMatchingWaitingRoom(L2PcInstance player, int page, int minLevel, int maxLevel, List<ClassId> classIds, String query) {
        final List<L2PcInstance> players = MatchingRoomManager.getInstance().getPlayerInWaitingList(minLevel, maxLevel, classIds, query);

        _size = players.size();
        final int startIndex = (page - 1) * NUM_PER_PAGE;
        int chunkSize = _size - startIndex;
        if (chunkSize > NUM_PER_PAGE) {
            chunkSize = NUM_PER_PAGE;
        }
        for (int i = startIndex; i < (startIndex + chunkSize); i++) {
            _players.add(players.get(i));
        }
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_LIST_PARTY_MATCHING_WAITING_ROOM);

        writeInt(_size);
        writeInt(_players.size());
        for (L2PcInstance player : _players) {
            writeString(player.getName());
            writeInt(player.getClassId().getId());
            writeInt(player.getLevel());
            final Instance instance = InstanceManager.getInstance().getPlayerInstance(player, false);
            writeInt((instance != null) && (instance.getTemplateId() >= 0) ? instance.getTemplateId() : -1);
            final Map<Integer, Long> _instanceTimes = InstanceManager.getInstance().getAllInstanceTimes(player);
            writeInt(_instanceTimes.size());
            for (Entry<Integer, Long> entry : _instanceTimes.entrySet()) {
                final long instanceTime = TimeUnit.MILLISECONDS.toSeconds(entry.getValue() - System.currentTimeMillis());
                writeInt(entry.getKey());
                writeInt((int) instanceTime);
            }
        }
    }

}
