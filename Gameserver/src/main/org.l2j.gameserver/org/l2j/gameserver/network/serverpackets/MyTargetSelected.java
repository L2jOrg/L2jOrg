package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.L2ControllableAirShipInstance;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * MyTargetSelected server packet implementation.
 *
 * @author UnAfraid
 */
public class MyTargetSelected extends ServerPacket {
    private final int _objectId;
    private final int _color;

    public MyTargetSelected(Player player, Creature target) {
        _objectId = (target instanceof L2ControllableAirShipInstance) ? ((L2ControllableAirShipInstance) target).getHelmObjectId() : target.getObjectId();
        _color = target.isAutoAttackable(player) ? (player.getLevel() - target.getLevel()) : 0;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.MY_TARGET_SELECTED);

        writeInt(0x01); // Grand Crusade
        writeInt(_objectId);
        writeShort((short) _color);
        writeInt(0x00); // Mode 0x00 - Standard; 0x03 Context Menu
    }

}
