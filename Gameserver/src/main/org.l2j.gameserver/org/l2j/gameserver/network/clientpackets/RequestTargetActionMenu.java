package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.actor.instance.Player;

import static java.util.Objects.isNull;

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
        if (isNull(player)) {
            return;
        }

        if (_type == 1) {
            var object = World.getInstance().getVisibleObject(player, _objectId);
            player.setTarget(object);
        }
    }
}
