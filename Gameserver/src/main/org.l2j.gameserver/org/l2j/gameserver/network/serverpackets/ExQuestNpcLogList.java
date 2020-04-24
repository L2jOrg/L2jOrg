package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.holders.NpcLogListHolder;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.ServerExPacketId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author UnAfraid
 */
public class ExQuestNpcLogList extends ServerPacket {
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
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_QUEST_NPC_LOG_LIST);

        writeInt(_questId);
        writeByte((byte) _npcLogList.size());
        for (NpcLogListHolder holder : _npcLogList) {
            writeInt(holder.isNpcString() ? holder.getId() : holder.getId() + 1000000);
            writeByte((byte)( holder.isNpcString() ? 0x01 : 0x00));
            writeInt(holder.getCount());
        }
    }

}