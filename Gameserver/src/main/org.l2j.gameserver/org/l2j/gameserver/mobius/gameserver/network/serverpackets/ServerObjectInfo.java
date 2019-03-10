package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author devScarlet, mrTJO
 */
public final class ServerObjectInfo extends IClientOutgoingPacket {
    private final L2Npc _activeChar;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _heading;
    private final int _idTemplate;
    private final boolean _isAttackable;
    private final double _collisionHeight;
    private final double _collisionRadius;
    private final String _name;

    public ServerObjectInfo(L2Npc activeChar, L2Character actor) {
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SERVER_OBJECT_INFO.writeId(packet);

        packet.putInt(_activeChar.getObjectId());
        packet.putInt(_idTemplate + 1000000);
        writeString(_name, packet); // name
        packet.putInt(_isAttackable ? 1 : 0);
        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
        packet.putInt(_heading);
        packet.putDouble(1.0); // movement multiplier
        packet.putDouble(1.0); // attack speed multiplier
        packet.putDouble(_collisionRadius);
        packet.putDouble(_collisionHeight);
        packet.putInt((int) (_isAttackable ? _activeChar.getCurrentHp() : 0));
        packet.putInt(_isAttackable ? _activeChar.getMaxHp() : 0);
        packet.putInt(0x01); // object type
        packet.putInt(0x00); // special effects
    }
}
