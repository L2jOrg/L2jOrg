package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author Mobius
 */
public class RequestTargetActionMenu extends ClientPacket {
    private int _objectId;
    private int _type;

    @Override
    public void readImpl() {
        _objectId = readInt();
        _type = readShort(); // action?
    }

    @Override
    public void runImpl() {
        final Player player = client.getActiveChar();
        if (player == null) {
            return;
        }

        if (_type == 1) {
            var object = L2World.getInstance().getVisibleObject(player, _objectId);
            player.setTarget(object);
        }
    }
}
