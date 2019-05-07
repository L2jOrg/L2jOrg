package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class Dice extends IClientOutgoingPacket {
    private final int _charObjId;
    private final int _itemId;
    private final int _number;
    private final int _x;
    private final int _y;
    private final int _z;

    public Dice(int charObjId, int itemId, int number, int x, int y, int z) {
        _charObjId = charObjId;
        _itemId = itemId;
        _number = number;
        _x = x;
        _y = y;
        _z = z;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.DICE.writeId(packet);

        packet.putInt(_charObjId); // object id of player
        packet.putInt(_itemId); // item id of dice (spade) 4625,4626,4627,4628
        packet.putInt(_number); // number rolled
        packet.putInt(_x); // x
        packet.putInt(_y); // y
        packet.putInt(_z); // z
    }

    @Override
    protected int size(L2GameClient client) {
        return 29;
    }
}
