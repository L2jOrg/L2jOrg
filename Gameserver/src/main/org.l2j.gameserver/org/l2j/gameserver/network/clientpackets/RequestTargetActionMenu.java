package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.world.World;

import static org.l2j.commons.util.Util.doIfNonNull;

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
        var player = client.getPlayer();
        doIfNonNull(World.getInstance().findVisibleObject(player, _objectId), player::setTarget);
    }
}
