package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public class ExSpawnEmitter extends IClientOutgoingPacket {
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SPAWN_EMITTER.writeId(packet);

        packet.putInt(_npcObjectId);
        packet.putInt(_playerObjectId);
        packet.putInt(0x00); // ?
    }

    @Override
    protected int size(L2GameClient client) {
        return 17;
    }
}
