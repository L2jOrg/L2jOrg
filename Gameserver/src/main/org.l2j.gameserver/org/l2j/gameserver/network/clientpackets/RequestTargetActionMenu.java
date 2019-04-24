package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

import static java.util.Objects.nonNull;

/**
 * @author Mobius
 */
public class RequestTargetActionMenu extends IClientIncomingPacket {
    private int _objectId;
    private int _type;

    @Override
    public void readImpl(ByteBuffer packet) {
        _objectId = packet.getInt();
        _type = packet.getShort(); // action?
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        if (_type == 1) {
            var object = L2World.getInstance().getVisibleObject(player, _objectId);
            player.setTarget(object);
        }
    }
}
