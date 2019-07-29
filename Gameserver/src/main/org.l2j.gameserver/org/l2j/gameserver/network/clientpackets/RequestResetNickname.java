package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author KenM
 */
public class RequestResetNickname extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        activeChar.getAppearance().setTitleColor(0xFFFF77);
        activeChar.setTitle("");
        activeChar.broadcastTitleInfo();
    }
}
