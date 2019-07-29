package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author ShanSoft
 * structure chddSdS
 */
public final class RequestModifyBookMarkSlot extends ClientPacket {
    private int id;
    private int icon;
    private String name;
    private String tag;

    @Override
    public void readImpl() {
        id = readInt();
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
        activeChar.teleportBookmarkModify(id, icon, tag, name);
    }
}
