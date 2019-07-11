package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.StaticWorldObject;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author KenM
 */
public class StaticObject extends ServerPacket {
    private final int _staticObjectId;
    private final int _objectId;
    private final int _type;
    private final boolean _isTargetable;
    private final int _meshIndex;
    private final boolean _isClosed;
    private final boolean _isEnemy;
    private final int _maxHp;
    private final int _currentHp;
    private final boolean _showHp;
    private final int _damageGrade;

    public StaticObject(StaticWorldObject staticObject) {
        _staticObjectId = staticObject.getId();
        _objectId = staticObject.getObjectId();
        _type = 0;
        _isTargetable = true;
        _meshIndex = staticObject.getMeshIndex();
        _isClosed = false;
        _isEnemy = false;
        _maxHp = 0;
        _currentHp = 0;
        _showHp = false;
        _damageGrade = 0;
    }

    public StaticObject(Door door, boolean targetable) {
        _staticObjectId = door.getId();
        _objectId = door.getObjectId();
        _type = 1;
        _isTargetable = door.isTargetable() || targetable;
        _meshIndex = door.getMeshIndex();
        _isClosed = !door.isOpen();
        _isEnemy = door.isEnemy();
        _maxHp = door.getMaxHp();
        _currentHp = (int) door.getCurrentHp();
        _showHp = door.getIsShowHp();
        _damageGrade = door.getDamage();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.STATIC_OBJECT);

        writeInt(_staticObjectId);
        writeInt(_objectId);
        writeInt(_type);
        writeInt(_isTargetable ? 1 : 0);
        writeInt(_meshIndex);
        writeInt(_isClosed ? 1 : 0);
        writeInt(_isEnemy ? 1 : 0);
        writeInt(_currentHp);
        writeInt(_maxHp);
        writeInt(_showHp ? 1 : 0);
        writeInt(_damageGrade);
    }

}
