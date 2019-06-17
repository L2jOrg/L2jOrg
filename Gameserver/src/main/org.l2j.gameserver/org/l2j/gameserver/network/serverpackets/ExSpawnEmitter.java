package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author KenM
 */
public class ExSpawnEmitter extends ServerPacket {
    private final int _playerObjectId;
    private final int _npcObjectId;

    public ExSpawnEmitter(int playerObjectId, int npcObjectId) {
        _playerObjectId = playerObjectId;
        _npcObjectId = npcObjectId;
    }

    public ExSpawnEmitter(L2PcInstance player, L2Npc npc) {
        this(player.getObjectId(), npc.getObjectId());
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_SPAWN_EMITTER);

        writeInt(_npcObjectId);
        writeInt(_playerObjectId);
        writeInt(0x00); // ?
    }

}
