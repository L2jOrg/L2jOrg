package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.mobius.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.base.ClassId;
import org.l2j.gameserver.mobius.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * @author Gnacik
 */
public class ExListPartyMatchingWaitingRoom extends IClientOutgoingPacket {
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_LIST_PARTY_MATCHING_WAITING_ROOM.writeId(packet);

        packet.putInt(_size);
        packet.putInt(_players.size());
        for (L2PcInstance player : _players) {
            writeString(player.getName(), packet);
            packet.putInt(player.getClassId().getId());
            packet.putInt(player.getLevel());
            final Instance instance = InstanceManager.getInstance().getPlayerInstance(player, false);
            packet.putInt((instance != null) && (instance.getTemplateId() >= 0) ? instance.getTemplateId() : -1);
            final Map<Integer, Long> _instanceTimes = InstanceManager.getInstance().getAllInstanceTimes(player);
            packet.putInt(_instanceTimes.size());
            for (Entry<Integer, Long> entry : _instanceTimes.entrySet()) {
                final long instanceTime = TimeUnit.MILLISECONDS.toSeconds(entry.getValue() - System.currentTimeMillis());
                packet.putInt(entry.getKey());
                packet.putInt((int) instanceTime);
            }
        }
    }
}
