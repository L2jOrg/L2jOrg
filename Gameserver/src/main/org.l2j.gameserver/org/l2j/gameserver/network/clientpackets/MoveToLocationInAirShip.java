package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.L2AirShipInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ExMoveToLocationInAirShip;
import org.l2j.gameserver.network.serverpackets.StopMoveInVehicle;

import java.nio.ByteBuffer;

/**
 * format: ddddddd X:%d Y:%d Z:%d OriginX:%d OriginY:%d OriginZ:%d
 *
 * @author GodKratos
 */
public class MoveToLocationInAirShip extends IClientIncomingPacket {
    private int _shipId;
    private int _targetX;
    private int _targetY;
    private int _targetZ;
    private int _originX;
    private int _originY;
    private int _originZ;

    @Override
    public void readImpl(ByteBuffer packet) {
        _shipId = packet.getInt();
        _targetX = packet.getInt();
        _targetY = packet.getInt();
        _targetZ = packet.getInt();
        _originX = packet.getInt();
        _originY = packet.getInt();
        _originZ = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if ((_targetX == _originX) && (_targetY == _originY) && (_targetZ == _originZ)) {
            activeChar.sendPacket(new StopMoveInVehicle(activeChar, _shipId));
            return;
        }

        if (activeChar.isAttackingNow() && (activeChar.getActiveWeaponItem() != null) && (activeChar.getActiveWeaponItem().getItemType() == WeaponType.BOW)) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (activeChar.isSitting() || activeChar.isMovementDisabled()) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (!activeChar.isInAirShip()) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final L2AirShipInstance airShip = activeChar.getAirShip();
        if (airShip.getObjectId() != _shipId) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        activeChar.setInVehiclePosition(new Location(_targetX, _targetY, _targetZ));
        activeChar.broadcastPacket(new ExMoveToLocationInAirShip(activeChar));
    }
}
