package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author -Wooden-
 */
public final class SnoopQuit extends ClientPacket {
    private int _snoopID;

    @Override
    public void readImpl() {
        _snoopID = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = World.getInstance().findPlayer(_snoopID);
        if (player == null) {
            return;
        }

        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        player.removeSnooper(activeChar);
        activeChar.removeSnooped(player);

    }
}
