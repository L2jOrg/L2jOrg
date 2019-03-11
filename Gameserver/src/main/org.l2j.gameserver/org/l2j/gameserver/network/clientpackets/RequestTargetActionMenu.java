package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

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
            for (L2Object object : L2World.getInstance().getVisibleObjects(player, L2Object.class)) {
                if (_objectId == object.getObjectId()) {
                    player.setTarget(object);
                    break;
                }
            }
        }
    }
}
