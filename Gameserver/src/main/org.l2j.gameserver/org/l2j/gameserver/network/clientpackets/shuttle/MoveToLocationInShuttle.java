package org.l2j.gameserver.network.clientpackets.shuttle;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.shuttle.ExMoveToLocationInShuttle;
import org.l2j.gameserver.network.serverpackets.shuttle.ExStopMoveInShuttle;

/**
 * @author UnAfraid
 */
public final class MoveToLocationInShuttle extends ClientPacket {
    private int _boatId;
    private int _targetX;
    private int _targetY;
    private int _targetZ;
    private int _originX;
    private int _originY;
    private int _originZ;

    @Override
    public void readImpl() {
        _boatId = readInt(); // objectId of boat
        _targetX = readInt();
        _targetY = readInt();
        _targetZ = readInt();
        _originX = readInt();
        _originY = readInt();
        _originZ = readInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if ((_targetX == _originX) && (_targetY == _originY) && (_targetZ == _originZ)) {
            activeChar.sendPacket(new ExStopMoveInShuttle(activeChar, _boatId));
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

        activeChar.setInVehiclePosition(new Location(_targetX, _targetY, _targetZ));
        activeChar.broadcastPacket(new ExMoveToLocationInShuttle(activeChar, _originX, _originY, _originZ));
    }
}
