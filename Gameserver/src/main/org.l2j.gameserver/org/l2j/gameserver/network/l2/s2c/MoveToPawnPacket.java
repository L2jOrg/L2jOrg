package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class MoveToPawnPacket extends L2GameServerPacket
{
    private int _chaId, _targetId, _distance;
    private int _x, _y, _z, _tx, _ty, _tz;

    public MoveToPawnPacket(Creature cha, Creature target, int distance)
    {
        _chaId = cha.getObjectId();
        _targetId = target.getObjectId();
        _distance = distance;
        _x = cha.getX();
        _y = cha.getY();
        _z = cha.getZ();
        _tx = target.getX();
        _ty = target.getY();
        _tz = target.getZ();
    }

    @Override
    protected final void writeImpl(GameClient client, ByteBuffer buffer)
    {
        buffer.putInt(_chaId);
        buffer.putInt(_targetId);
        buffer.putInt(_distance);

        buffer.putInt(_x);
        buffer.putInt(_y);
        buffer.putInt(_z);

        buffer.putInt(_tx);
        buffer.putInt(_ty);
        buffer.putInt(_tz);
    }
}