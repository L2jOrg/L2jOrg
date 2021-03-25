package org.l2j.gameserver.network.serverpackets.collections;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExCollectionReward extends ServerPacket {
    private final Player _player;

    public ExCollectionReward(Player player)
    {
        _player = player;
    }


    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_COLLECTION_RECEIVE_REWARD, buffer);
        //buffer.writeShort(); //todo collectionId
        //buffer.writeByte(); //todo boolean success
    }
}
