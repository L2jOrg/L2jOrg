package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;

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
    protected final void writeImpl()
    {
        writeD(_chaId);
        writeD(_targetId);
        writeD(_distance);

        writeD(_x);
        writeD(_y);
        writeD(_z);

        writeD(_tx);
        writeD(_ty);
        writeD(_tz);
    }
}