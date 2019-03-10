package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.instancemanager.BoatManager;
import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2BoatInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.MoveToLocationInVehicle;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.StopMoveInVehicle;

import java.nio.ByteBuffer;

public final class RequestMoveToLocationInVehicle extends IClientIncomingPacket {
    private int _boatId;
    private int _targetX;
    private int _targetY;
    private int _targetZ;
    private int _originX;
    private int _originY;
    private int _originZ;

    @Override
    public void readImpl(ByteBuffer packet) {
        _boatId = packet.getInt(); // objectId of boat
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

        if ((Config.PLAYER_MOVEMENT_BLOCK_TIME > 0) && !activeChar.isGM() && (activeChar.getNotMoveUntil() > System.currentTimeMillis())) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_MOVE_WHILE_SPEAKING_TO_AN_NPC_ONE_MOMENT_PLEASE);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if ((_targetX == _originX) && (_targetY == _originY) && (_targetZ == _originZ)) {
            client.sendPacket(new StopMoveInVehicle(activeChar, _boatId));
            return;
        }

        if (activeChar.isAttackingNow() && (activeChar.getActiveWeaponItem() != null) && (activeChar.getActiveWeaponItem().getItemType() == WeaponType.BOW)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (activeChar.isSitting() || activeChar.isMovementDisabled()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (activeChar.hasSummon()) {
            client.sendPacket(SystemMessageId.YOU_SHOULD_RELEASE_YOUR_SERVITOR_SO_THAT_IT_DOES_NOT_FALL_OFF_OF_THE_BOAT_AND_DROWN);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (activeChar.isTransformed()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_BOAT);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final L2BoatInstance boat;
        if (activeChar.isInBoat()) {
            boat = activeChar.getBoat();
            if (boat.getObjectId() != _boatId) {
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
        } else {
            boat = BoatManager.getInstance().getBoat(_boatId);
            if ((boat == null) || !boat.isInsideRadius3D(activeChar, 300)) {
                client.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            activeChar.setVehicle(boat);
        }

        final Location pos = new Location(_targetX, _targetY, _targetZ);
        final Location originPos = new Location(_originX, _originY, _originZ);
        activeChar.setInVehiclePosition(pos);
        activeChar.broadcastPacket(new MoveToLocationInVehicle(activeChar, pos, originPos));
    }
}
