package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;

import static java.util.Objects.isNull;

/**
 * @author Mobius
 * @author JoeAlisson
 */
public class RequestTargetActionMenu extends ClientPacket {
    private int _objectId;

    @Override
    public void readImpl() {
        _objectId = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (isNull(player)) {
            return;
        }

        var object = World.getInstance().findVisibleObject(player, _objectId);
        player.setTarget(object);
    }
}
