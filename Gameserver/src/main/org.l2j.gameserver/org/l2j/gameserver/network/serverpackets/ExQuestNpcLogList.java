package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.holders.NpcLogListHolder;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author UnAfraid
 */
public class ExQuestNpcLogList extends IClientOutgoingPacket {
    private final int _questId;
    private final List<NpcLogListHolder> _npcLogList = new ArrayList<>();

    public ExQuestNpcLogList(int questId) {
        _questId = questId;
    }

    public void addNpc(int npcId, int count) {
        _npcLogList.add(new NpcLogListHolder(npcId, false, count));
    }

    public void addNpcString(NpcStringId npcStringId, int count) {
        _npcLogList.add(new NpcLogListHolder(npcStringId.getId(), true, count));
    }

    public void add(NpcLogListHolder holder) {
        _npcLogList.add(holder);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_QUEST_NPC_LOG_LIST.writeId(packet);

        packet.putInt(_questId);
        packet.put((byte) _npcLogList.size());
        for (NpcLogListHolder holder : _npcLogList) {
            packet.putInt(holder.isNpcString() ? holder.getId() : holder.getId() + 1000000);
            packet.put((byte)( holder.isNpcString() ? 0x01 : 0x00));
            packet.putInt(holder.getCount());
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 10 + _npcLogList.size() * 9;
    }
}