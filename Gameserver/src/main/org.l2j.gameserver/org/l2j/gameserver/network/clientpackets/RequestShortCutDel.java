package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.database.data.Shortcut;

/**
 * @author JoeAlisson
 */
public final class RequestShortCutDel extends ClientPacket {
    private int room;

    @Override
    public void readImpl() {
        room = readInt();
    }

    @Override
    public void runImpl() {
        if(room < 0 || (room > Shortcut.MAX_ROOM && room != Shortcut.AUTO_POTION_ROOM)) {
            return;
        }

        client.getPlayer().deleteShortcut(room);
    }
}
