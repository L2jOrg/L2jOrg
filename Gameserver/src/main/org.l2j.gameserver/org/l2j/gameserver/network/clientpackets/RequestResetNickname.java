package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author KenM
 */
public class RequestResetNickname extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        activeChar.getAppearance().setTitleColor(0xFFFF77);
        activeChar.setTitle("");
        activeChar.broadcastTitleInfo();
    }
}
