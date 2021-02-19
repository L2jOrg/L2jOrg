package org.l2j.gameserver.network.serverpackets.collections;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ExCollectionRegister extends ServerPacket {
    private final Player _player;

    public ExCollectionRegister(Player player)
    {
        _player = player;
    }


    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_COLLECTION_REGISTER, buffer);

       // buffer.writeShort(); //todo collectionId
       // buffer.writeByte(); //todo boolean success
        //i = GetCurDecodePos();
        // buffer.writeShort(collection.size());
        //buffer.writeByte(); //todo slotindex
        //buffer.writeInt(); //todo ItemClassId
        //buffer.writeByte(); //todo itemEnchant
        //buffer.writeByte(); //todo boolean isBless
        //buffer.writeByte(); //todo blessCondition
        //buffer.writeInt(); //todo amount
//
        /*if (GetCurDecodePos() - i > nSize)
            return;*/

    }
}
