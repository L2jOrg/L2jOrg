package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author devScarlet, mrTJO
 */
public final class ServerObjectInfo extends ServerPacket {
    private final Npc _activeChar;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _heading;
    private final int _idTemplate;
    private final boolean _isAttackable;
    private final double _collisionHeight;
    private final double _collisionRadius;
    private final String _name;

    public ServerObjectInfo(Npc activeChar, Creature actor) {
        _activeChar = activeChar;
        _idTemplate = _activeChar.getTemplate().getDisplayId();
        _isAttackable = _activeChar.isAutoAttackable(actor);
        _collisionHeight = _activeChar.getCollisionHeight();
        _collisionRadius = _activeChar.getCollisionRadius();
        _x = _activeChar.getX();
        _y = _activeChar.getY();
        _z = _activeChar.getZ();
        _heading = _activeChar.getHeading();
        _name = _activeChar.getTemplate().isUsingServerSideName() ? _activeChar.getTemplate().getName() : "";
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SERVER_OBJECT_INFO);

        writeInt(_activeChar.getObjectId());
        writeInt(_idTemplate + 1000000);
        writeString(_name); // name
        writeInt(_isAttackable ? 1 : 0);
        writeInt(_x);
        writeInt(_y);
        writeInt(_z);
        writeInt(_heading);
        writeDouble(1.0); // movement multiplier
        writeDouble(1.0); // attack speed multiplier
        writeDouble(_collisionRadius);
        writeDouble(_collisionHeight);
        writeInt((int) (_isAttackable ? _activeChar.getCurrentHp() : 0));
        writeInt(_isAttackable ? _activeChar.getMaxHp() : 0);
        writeInt(0x01); // object type
        writeInt(0x00); // special effects
    }

}
