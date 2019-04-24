package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.L2ControllableAirShipInstance;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * MyTargetSelected server packet implementation.
 *
 * @author UnAfraid
 */
public class MyTargetSelected extends IClientOutgoingPacket {
    private final int _objectId;
    private final int _color;

    public MyTargetSelected(L2PcInstance player, L2Character target) {
        _objectId = (target instanceof L2ControllableAirShipInstance) ? ((L2ControllableAirShipInstance) target).getHelmObjectId() : target.getObjectId();
        _color = target.isAutoAttackable(player) ? (player.getLevel() - target.getLevel()) : 0;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.MY_TARGET_SELECTED.writeId(packet);

        packet.putInt(0x01); // Grand Crusade
        packet.putInt(_objectId);
        packet.putShort((short) _color);
        packet.putInt(0x00); // Mode 0x00 - Standard; 0x03 Context Menu
    }
}
