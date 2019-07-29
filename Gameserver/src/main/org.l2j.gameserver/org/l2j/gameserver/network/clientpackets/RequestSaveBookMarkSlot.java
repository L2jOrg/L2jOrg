package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author ShanSoft
 * @structure chdSdS
 */
public final class RequestSaveBookMarkSlot extends ClientPacket {
    private int icon;
    private String name;
    private String tag;

    @Override
    public void readImpl() {
        name = readString();
        icon = readInt();
        tag = readString();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }
        activeChar.teleportBookmarkAdd(activeChar.getX(), activeChar.getY(), activeChar.getZ(), icon, tag, name);
    }
}
