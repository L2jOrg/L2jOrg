package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.ExPrivateStoreSetWholeMsg;
import org.l2j.gameserver.util.Util;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public class SetPrivateStoreWholeMsg extends IClientIncomingPacket {
    private static final int MAX_MSG_LENGTH = 29;

    private String _msg;

    @Override
    public void readImpl(ByteBuffer packet) {
        _msg = readString(packet);
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if ((player == null) || (player.getSellList() == null)) {
            return;
        }

        if ((_msg != null) && (_msg.length() > MAX_MSG_LENGTH)) {
            Util.handleIllegalPlayerAction(player, "Player " + player.getName() + " tried to overflow private store whole message", Config.DEFAULT_PUNISH);
            return;
        }

        player.getSellList().setTitle(_msg);
        client.sendPacket(new ExPrivateStoreSetWholeMsg(player));
    }

}
