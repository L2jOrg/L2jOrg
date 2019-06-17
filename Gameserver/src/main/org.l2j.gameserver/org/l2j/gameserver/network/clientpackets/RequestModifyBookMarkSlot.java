package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

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
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }
        activeChar.teleportBookmarkModify(id, icon, tag, name);
    }
}
